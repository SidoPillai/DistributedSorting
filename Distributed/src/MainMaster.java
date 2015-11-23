import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class MainMaster {

	ServerSocket serverSocket;
	final int port = 6000;
	List<String> listOfAvailableHost;
	String subnet;
	static boolean flag = false;

	// To keep a track of which chunk is taken by which node
	Map<String, Integer> map;

	List<MainMaster> listOfConnectedSlaves; 

	List<Socket> listOfSlaves;

	List<Bucket> buckets;

	/* Connection variables to worker node */
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;

	/* Data to sort */
	List<String> dataToSort;
	List<List<String>> chunks = new ArrayList<List<String>>();
	List<List<String>> sortedChunks = new ArrayList<List<String>>();

	public MainMaster() {
		map = new HashMap<String, Integer>();
		listOfSlaves = new ArrayList<Socket>();
		buckets = new ArrayList<Bucket>();
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

	public void start() throws IOException, ClassNotFoundException {

		try{
			serverSocket = new ServerSocket(port);

			System.out.println("Master listening on port " + port);
			// check for no of  host in the network
			//listOfAvailableHost = checkHosts(subnet);

			// send the source files
			//for (String host : listOfAvailableHost) {
			//				sendFiles(host);
			//			}

			// compile the slave class 
			// the run these clients

			// Running the clients 
			//			for (String host : listOfAvailableHost) {
			//  Call the slaves
			//				new MainSlave(serverSocket.getInetAddress().getHostAddress(), port).start();
			//			}

			// wait for the connection requests
			//			for(int count = 0; count < listOfAvailableHost.size(); ++count) {	

			for(int count = 0; count < 3; ++count) {								
				Socket socket = serverSocket.accept();
				System.out.println("Accepted one node");
				//				MainMaster connection = new MainMaster(socket);		// New Connection object for easier communication
				//				connection.setupConnection();						// Setup Connection to worker node
				//				listOfConnectedSlaves.add(connection);
				new HandleTCPRequest(socket, count).start();
				//				buckets.add(new Bucket(socket, startIndex, endIndex, chunkSize)); // recovery
				listOfSlaves.add(socket);
				System.out.println("Node: " + (count+1) + " connected");
			}

			System.out.println("All nodes connected..");

			System.out.println("Sending the data...");

			File file = new File("new_dataset_1B.txt");
			FileInputStream f = new FileInputStream(file);
			int SIZE = (int)file.length();

			int SIZE_CHUNKS = SIZE/3;
			int SIZE_EACH_BLOCK = SIZE_CHUNKS/256;

			System.out.println("File Size ..          " +  SIZE + " Bytes");
			System.out.println("Size of each Chunks.. " + SIZE_CHUNKS + " Bytes");
			System.out.println("No of Blocks..        " + 256);
			System.out.println("Size of each Block..  " + SIZE_EACH_BLOCK + " Bytes");
			System.out.println("Estimated block size" + estimateBestSizeOfBlocks(file));


			//			byte[] getFileParts(int start, int end);

			// Split the file based on the chunks
			dataToSort = FileHandler.getData();

			System.out.println("Size of data.." + dataToSort.size());


			// Iterate on a loop on toSort based / #nodes
			int count = 0;

			// calculate the last elements
			int sizeForEachNode = dataToSort.size()/3; //listOfConnectedSlaves.size();

			int totalChuncks = 3; //listOfConnectedSlaves.size();
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
					chunks.add(j, elements);
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
			chunks.add(j, elements);

			System.out.println("Data Prepared...");

			flag = true;

			// Send the chunks to the client for sorting
			//			for (int i = 0; i < listOfConnectedSlaves.size(); i++) {
			//			for (int i = 0; i < 3; i++) {

			//				Socket soc = listOfSlaves.get(i);
			//				output = new ObjectOutputStream(soc.getOutputStream());
			//				input = new ObjectInputStream(soc.getInputStream());

			//				output.writeObject((ArrayList<String>)chunks.get(i));

			//				writeData((ArrayList<String>)chunks.get(i));

			// sending the data to slave
			//				listOfConnectedSlaves.get(i).writeData((ArrayList<String>)chunks.get(i));
			// keeping a map to keep track
			//				map.put(listOfConnectedSlaves.get(i).serverSocket.getInetAddress().getHostAddress(), i);
			//			}


			// Receive the response from the client
			//			for (int i = 0; i < listOfConnectedSlaves.size(); i++) {
			//			for (int i = 0; i < 3; i++) {
			//				sortedChunks.add(readData());
			//			}

			//			System.out.println("Received the data from the client");

			ArrayList<String> mergedSorted = null;

			while (true) {

				System.out.println("Not there yet.. " + sortedChunks.size());
				if (sortedChunks.size() == 3) {
					mergedSorted = (ArrayList<String>) merge(sortedChunks);
					System.out.println("Merged the data finally...");
					break;
				}
			}

			//			ArrayList<String> mergedSorted = (ArrayList<String>) merged(sortedChunks);

			//			mergedSorted.stream().sorted((str1,str2)-> )

			// write to file
			FileHandler.writeFile(mergedSorted);

			System.out.println("Done");

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

	/* Sends list to worker node */
	public ArrayList<String> readData() throws IOException, ClassNotFoundException {
		ArrayList<String> sortedData = (ArrayList<String>) input.readObject();
		return sortedData; 
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


	public static <T extends Comparable<? super T>> List<T> merged(List<List<T>> lists) {
		int totalSize = 0; // every element in the set
		for (List<T> l : lists) {
			totalSize += l.size();
		}
		List<T> result = new ArrayList<T>(totalSize);
		List<T> lowest;

		while (result.size() < totalSize) { // while we still have something to add
			lowest = null;
			for (List<T> l : lists) {
				if (! l.isEmpty()) {
					if (lowest == null) {
						lowest = l;
					}
					else if (l.get(0).compareTo(lowest.get(0)) <= 0) {
						lowest = l;
					}
				}
			}
			result.add(lowest.get(0));
			lowest.remove(0);
		}
		return result;
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

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		new MainMaster().start();
	}

	/**
	 * This class is responsible to accept the TCP requests.
	 * 
	 * @author Siddesh Pillai
	 */
	private class HandleTCPRequest extends Thread {

		// Member variables
		private Socket serverSocketTCP;
		private ObjectInputStream in;
		private ObjectOutputStream out;

		int slaveNumber;

		ArrayList<String> sortedData; 
		/**
		 * Constructor
		 * @param socket
		 */
		public HandleTCPRequest(Socket socket, int slaveNumber) {
			this.serverSocketTCP = socket;
			this.slaveNumber = slaveNumber;

			try {
				out = new ObjectOutputStream(this.serverSocketTCP.getOutputStream());
				in = new ObjectInputStream(this.serverSocketTCP.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}

		/**
		 * The run method
		 */
		public void run() {

			System.out.println("Waiting for the flag");

			try {

				while(true) {

					//					System.out.println(MainMaster.flag + " is the flag value");

					if (MainMaster.flag) {

						System.out.println("Time to send the data now");
						// get the particular chunk
						out.writeObject((ArrayList<String>)chunks.get(slaveNumber));

						System.out.println("Sent the chunks to the client");

						sortedData = (ArrayList<String>) in.readObject();

						System.out.println("Receieved the sorted data");

						synchronized (sortedChunks) {
							sortedChunks.add(sortedData);
						}

						System.out.println("Added to sorted data");
						break;
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					// closing the socket
					this.serverSocketTCP.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/* Get the Small value between the two string */
	public String getSmallVal(String a , String b ) {
		Pattern pattern = Pattern.compile("[a-zA-Z]");
		Matcher match;

		match = pattern.matcher(a);
		int count =0;
		while(match.find()) {
			count++;
		}
		String sa_1 = a.substring(0,count);
		String sa_2 = a.substring(count);

		match = pattern.matcher(b);
		count = 0;
		while(match.find()) {
			count++;
		}
		String sb_1 = b.substring(0,count);
		String sb_2 = b.substring(count);

		int sa_int = Integer.parseInt(sa_2);
		int sb_int = Integer.parseInt(sb_2);

		if(sa_1.compareTo(sb_1) < 0) {
			return a;
		} else if (sa_1.compareTo(sb_1) > 0) {
			return b;
		} else {
			if(sa_int < sb_int) {
				return a;
			} else {
				return b;
			}
		}
	}

	// dividing the file into small blocks
	public static long estimateBestSizeOfBlocks(File filetobesorted) {
		long sizeoffile = filetobesorted.length();

		// we don't want to open up much more than 1024 temporary files, better run
		// out of memory first. (Even 1024 is stretching it.)
		final int MAXTEMPFILES = 1024;
		long blocksize = sizeoffile / MAXTEMPFILES ;

		// on the other hand, we don't want to create many temporary files
		// for naught. If blocksize is smaller than half the free memory, grow it.
		long freemem = Runtime.getRuntime().freeMemory();
		if( blocksize < freemem/2)
			blocksize = freemem/2;
		else {
			if(blocksize >= freemem) 
				System.err.println("We expect to run out of memory. ");
		}
		return blocksize;
	}

	// merging the sorted file
	public int mergeSortedFiles(List<File> files, File outputfile, final Comparator<String> cmp) throws IOException {
		PriorityQueue<BinaryFileBuffer> pq = new PriorityQueue<BinaryFileBuffer>(11, 
				new Comparator<BinaryFileBuffer>() {

			public int compare(BinaryFileBuffer i, BinaryFileBuffer j) {
				return cmp.compare(i.peek(), j.peek());
			}
		});

		for (File f : files) {
			BinaryFileBuffer bfb = new BinaryFileBuffer(f);
			pq.add(bfb);
		}

		BufferedWriter fbw = new BufferedWriter(new FileWriter(outputfile));
		int rowcounter = 0;

		try {
			while(pq.size()>0) {
				BinaryFileBuffer bfb = pq.poll();
				String r = bfb.pop();
				fbw.write(r);
				fbw.newLine();
				++rowcounter;
				if(bfb.empty()) {
					bfb.fbr.close();
					bfb.originalfile.delete();// we don't need you anymore
				} else {
					pq.add(bfb); // add it back
				}
			}
		} finally { 
			fbw.close();
			for(BinaryFileBuffer bfb : pq ) bfb.close();
		}
		return rowcounter;
	}

	public class BinaryFileBuffer {
		public static final int BUFFERSIZE = 2048;
		public BufferedReader fbr;
		public File originalfile;
		private String cache;
		private boolean empty;

		public BinaryFileBuffer(File f) throws IOException {
			originalfile = f;
			fbr = new BufferedReader(new FileReader(f), BUFFERSIZE);
			reload();
		}

		public boolean empty() {
			return empty;
		}

		private void reload() throws IOException {
			try {
				if((this.cache = fbr.readLine()) == null){
					empty = true;
					cache = null;
				}
				else{
					empty = false;
				}
			} catch(EOFException oef) {
				empty = true;
				cache = null;
			}
		}

		public void close() throws IOException {
			fbr.close();
		}


		public String peek() {
			if(empty()) return null;
			return cache.toString();
		}

		public String pop() throws IOException {
			String answer = peek();
			reload();
			return answer;
		}
	}

	public List<File> sortInBatch(File file, Comparator<String> cmp) throws IOException {
		List<File> files = new ArrayList<File>();
		BufferedReader fbr = new BufferedReader(new FileReader(file));
		long blocksize = estimateBestSizeOfBlocks(file);  // in bytes

		try{
			List<String> tmplist =  new ArrayList<String>();
			String line = "";
			try {
				while(line != null) {
					long currentblocksize = 0;// in bytes
					while((currentblocksize < blocksize) 
							&& ((line = fbr.readLine()) != null)) { // as long as you have 2MB
						tmplist.add(line);
						currentblocksize += line.length(); // 2 + 40; // java uses 16 bits per character + 40 bytes of overhead (estimated)
					}
					files.add(sortAndSave(tmplist,cmp));
					tmplist.clear();
				}
			} catch(EOFException oef) {
				if(tmplist.size()>0) {
					files.add(sortAndSave(tmplist,cmp));
					tmplist.clear();
				}
			}
		} finally {
			fbr.close();
		}
		return files;
	}

	public static File sortAndSave(List<String> tmplist, Comparator<String> cmp) throws IOException  {
		Collections.sort(tmplist,cmp);  // 
		File newtmpfile = File.createTempFile("sortInBatch", "flatfile");
		newtmpfile.deleteOnExit();
		BufferedWriter fbw = new BufferedWriter(new FileWriter(newtmpfile));
		try {
			for(String r : tmplist) {
				fbw.write(r);
				fbw.newLine();
			}
		} finally {
			fbw.close();
		}
		return newtmpfile;
	}

}
