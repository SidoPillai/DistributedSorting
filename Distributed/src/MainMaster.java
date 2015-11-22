import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class MainMaster {

	ServerSocket serverSocket;
	final int port = 6000;
	List<String> listOfAvailableHost;
	String subnet;
	
	List<Address> listOfConnectedSlaves; 

	/* Connection variables to worker node */
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	
	/* Data to sort */
	List<String> dataToSort;

	
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
				listOfConnectedSlaves.add(new Address(socket.getPort(), socket.getInetAddress()));
				System.out.println("Node: " + (count+1) + " connected");
			}
			serverSocket.close();
		} catch(IOException e){
			System.out.println("Something went wrong while connecting to a client");
			return;
		}
		
		// Split the file based on the chunks
		dataToSort = FileHandler.getData();

		// Iterate on a loop on toSort based / #nodes
		int count = 0;

		// calculate the last elements
		int sizeForEachNode = dataToSort.size()/listOfConnectedSlaves.size();

		List<List<String>> chunks = new ArrayList<List<String>>();
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

}
