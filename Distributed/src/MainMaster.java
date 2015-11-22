import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class MainMaster {

	ServerSocket serverSocket;
	final int port = 6000;
	List<String> listOfAvailableHost;
	String subnet;

	// To keep a track of which chunk is taken by which node
	Map<MainMaster, Integer> map;

	List<MainMaster> listOfConnectedSlaves; 

	/* Connection variables to worker node */
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;

	/* Data to sort */
	List<String> dataToSort;
	List<List<String>> chunks = new ArrayList<List<String>>();
	List<List<String>> sortedChunks = new ArrayList<List<String>>();

	public MainMaster() {
		map = new HashMap<MainMaster, Integer>();
	}

	MainMaster(String subnet) {
		this.subnet = subnet;
	}

	public MainMaster(Socket socket) {
		this.socket = socket;
	}

	/* Initialises the data input and data output streams to the worker node */
	public void setupConnection() throws IOException{
		input = new ObjectInputStream(socket.getInputStream());
		output = new ObjectOutputStream(socket.getOutputStream());
	}

	public void start() throws IOException {

		try{
			serverSocket = new ServerSocket(port);

			// check for no of  host in the network
			listOfAvailableHost = checkHosts(subnet);

			// send the source files
			for (String host : listOfAvailableHost) {
				sendFiles(host);
			}

			// compile the slave class 
			// the run these clients
			int clientPort = 4000;

			for (String host : listOfAvailableHost) {
				//  Call the master
				String[] arguments = new String[] { String.valueOf(port), String.valueOf(++clientPort)}; 
				//				MainSlave.main(arguments);
			}

			// wait for the connection requests
			for(int count = 0; count < listOfAvailableHost.size(); ++count) {											
				Socket socket = serverSocket.accept();	
				MainMaster connection = new MainMaster(socket);		// New Connection object for easier communication
				connection.setupConnection();						// Setup Connection to worker node
				listOfConnectedSlaves.add(connection);
				System.out.println("Node: " + (count+1) + " connected");
			}


			// Split the file based on the chunks
			dataToSort = FileHandler.getData();

			// Iterate on a loop on toSort based / #nodes
			int count = 0;

			// calculate the last elements
			int sizeForEachNode = dataToSort.size()/listOfConnectedSlaves.size();

			int totalChuncks = listOfConnectedSlaves.size();
			int j = 0;
			List<String> elements = new ArrayList<String>();

			for(int i = 0; i < dataToSort.size(); i++) {

				// fill the chunk 
				if(count < sizeForEachNode && j < totalChuncks-1) {
					elements.add(dataToSort.get(i));
					count++;
				} 
				// set the chunk
				else if (count == sizeForEachNode && j < totalChuncks-1) {
					chunks.set(j, elements);
					j++;
					count = 0;
					elements.clear();
				} 
				// for last chunk
				else {
					elements.add(dataToSort.get(i));
				}
			}

			// adding the last chunk
			chunks.set(j, elements);

			// Send the chunks to the client for sorting
			for (int i = 0; i < listOfConnectedSlaves.size(); i++) {
				// sending the data to slave
				listOfConnectedSlaves.get(i).writeData((ArrayList<String>)chunks.get(i));
				// keeping a map to keep track
				map.put(listOfConnectedSlaves.get(i), i);
			}

			// Receive the response from the client

			ArrayList<String> mergedSorted = (ArrayList<String>) merge(sortedChunks);

			// write to file


		} catch(IOException e){
			System.out.println("Something went wrong while connecting to a client");
			return;
		} finally {
			serverSocket.close();
		}
	}

	/* Sends list to worker node */
	public void writeData(ArrayList<String> data) throws IOException{
		output.writeObject(data.size());
		output.flush();
	}

	// looks for the host in the network and adds inti the list of online devices 
	public static ArrayList<String> checkHosts(String subnet) throws UnknownHostException, IOException {
		ArrayList<String> onlineHost = new ArrayList<String>();
		int timeout = 1000;

		for (int i = 1;i<254;i++){
			System.out.println(i);
			String host=subnet + "." + i;
			if (InetAddress.getByName(host).isReachable(timeout)){
				//				System.out.println(host + " is reachable");
				onlineHost.add(host);
			}
		}

		return onlineHost;
	}

	// Running this command to send the file  
	public static boolean sendFiles(String hostname) {
		Session session = null;
		ChannelExec channel = null;

		try{
			JSch jsch = new JSch();
			session = jsch.getSession(hostname);
			session.connect();
			channel = (ChannelExec) session.openChannel("exec");                        
			channel.setCommand("scp /home/files ip_server:/Users/siddeshpillai/Documents/workspace/Distributed/src/"); // $> scp file1…fileN IP_OF_HOST:/PATH_TO_YOUR_FOLDER
			channel.connect();
			return true;
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
		return false;
	}

	/* Read boolean from inputstream */
	public void readBoolean() throws IOException {
		input.readBoolean();
	}

	/* Send boolean to worker node */
	public void writeBoolean() throws IOException{
		output.writeBoolean(true);
		output.flush();
	}

	public static <E extends Comparable<? super E>> List<E> merge(Collection<? extends List<? extends E>> lists) {
	    PriorityQueue<CompIterator<E>> queue = new PriorityQueue<CompIterator<E>>();
	    for (List<? extends E> list : lists)
	        if (!list.isEmpty())
	            queue.add(new CompIterator<E>(list.iterator()));

	    List<E> merged = new ArrayList<E>();
	    while (!queue.isEmpty()) {
	        CompIterator<E> next = queue.remove();
	        merged.add(next.next());
	        if (next.hasNext())
	            queue.add(next);
	    }
	    return merged;
	}

	private static class CompIterator<E extends Comparable<? super E>> implements Iterator<E>, Comparable<CompIterator<E>> {
	    E peekElem;
	    Iterator<? extends E> it;

	    public CompIterator(Iterator<? extends E> it) {
	        this.it = it;
	        if (it.hasNext()) peekElem = it.next();
	        else peekElem = null;
	    }

	    public boolean hasNext() {
	        return peekElem != null;
	    }

	    public E next() {
	        E ret = peekElem;
	        if (it.hasNext()) peekElem = it.next();
	        else peekElem = null;
	        return ret;
	    }

	    public void remove() {
	        throw new UnsupportedOperationException();
	    }

	    public int compareTo(CompIterator<E> o) {
	        if (peekElem == null) return 1;
	        else return peekElem.compareTo(o.peekElem);
	    }
	}
}
