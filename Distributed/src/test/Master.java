package test;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/* Class represents a connection to a worker node */
public class Master {

	/* Connection variables to worker node */
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;

	ArrayList<Master> connections = new ArrayList<Master>();

	/* Constructor that takes in Socket connecting to worker node */
	public Master(Socket socket) {
		this.socket = socket;
	}

	/* Initialises the data input and data output streams to the worker node */
	public void setupConnection() throws IOException{
		input = new DataInputStream(this.socket.getInputStream());
		output = new DataOutputStream(this.socket.getOutputStream());
	}

	/* Send right node information to the worker node */
	public void setRight(Address rightAddress) throws IOException{
		byte[] address = rightAddress.getAddress().getAddress();
		output.writeInt(address.length);
		output.flush();
		output.write(address);
		output.flush();
		output.writeInt(rightAddress.getPort());
		output.flush();
	}

	/* Returns the worker nodes address and port number */
	public Address getAddressInfo() throws IOException{
		Address address = new Address();
		address.setPort(input.readInt());
		address.setAddress(socket.getInetAddress());
		return address;
	}

	/* Sends list to worker node */
	public void writeData(int[] data) throws IOException{
		output.writeInt(data.length);
		output.flush();
		for(int i = 0; i < data.length; i++){
			output.writeInt(data[i]);
			output.flush();
		}
	}

	/* When called, sends nodeType to worker node */
	public void setNodeType(int nodeType) throws IOException{
		output.writeInt(nodeType);
		output.flush();
	}

	/* Reads int from inputstream and returns it */
	public int readInt() throws IOException{
		return input.readInt();
	}

	/* Send boolean to worker node */
	public void writeBoolean() throws IOException{
		output.writeBoolean(true);
		output.flush();
	}

	/* Read boolean from inputstream */
	public void readBoolean() throws IOException {
		input.readBoolean();
	}

	public static void main(String[] args) throws IOException {

		int port;
		int listSize;
		int maxValue;
		int nodeAmount;
		ArrayList<Master> connections = new ArrayList<Master>();
		List<String> toSort;


		// Get connection information from the user
		Scanner scan = new Scanner(System.in);
		System.out.print("Port number: ");
		port = scan.nextInt();
		System.out.print("Amount of nodes connecting?: ");
		nodeAmount = scan.nextInt();
		System.out.println("Listening for node connections...");

		// Creates ServerSocket and takes in the user specified amount of worker node connections
		try{
			ServerSocket serverSocket = new ServerSocket(port);
			for(int count = 0; count < nodeAmount; ++count){											
				Socket socket = serverSocket.accept();	
				Master connection = new Master(socket);		// New Connection object for easier communication
				connection.setupConnection();						// Setup Connection to worker node
				connections.add(connection);
				System.out.println("Node: " + (count+1) + " connected");
			}
			serverSocket.close();
		} catch(IOException e){
			System.out.println("Something went wrong while connecting to a client");
			return;
		}

		// Split the file based on the chunks
		toSort = FileHandler.getData();

		// Iterate on a loop on toSort based / #nodes
		int count = 0;
		List<String> send = new ArrayList<String>();

		// calculate the last elements
		int remainderStrings  = toSort.size()%nodeAmount;

		for(int i = 0; i < toSort.size(); i++) {

			if (toSort.size()/nodeAmount > count) {
				if (remainderStrings > count) {
					send.add(toSort.get(i));
				} else {
					send.add(toSort.get(i));
				}
				count++;
			} else {

				// send the list to slave
				count = 0;
				send.clear();
			}

		} 

		if (remainderStrings != 0) {
			// add the remainder strings in the another list
			// send which is available first
		}


		// Get List information from the user.
		System.out.println();
		System.out.print("Size of List?: ");
		listSize = scan.nextInt();
		System.out.print("Maximum value?: ");
		maxValue = scan.nextInt();
		System.out.println("\nGenerating List.....");
		System.out.print("Unsorted list: [");

		// Generate Random list from user specified values
		int values[] = new int[listSize];
		Random randomGenerator = new Random();
		for (int idx = 0; idx < listSize; ++idx){
			values[idx] = randomGenerator.nextInt(maxValue);
			if(idx < listSize-1){
				System.out.print(values[idx] + ", ");
			}
			else{
				System.out.print(values[idx]);
			}
		}
		System.out.println("]\n");

		double start = System.currentTimeMillis();

		// Work out how many list elements go to each worker node
		int length = values.length;
		int divide = length/nodeAmount;
		int[] per = new int[nodeAmount];
		int mod = length % nodeAmount;
		for(int k = 0; k < nodeAmount; k++){	// Distribute the remainder
			if(mod > 0){
				per[k]++;
				mod--;
				if(k == nodeAmount - 1){
					k = 0;
				}
			} 
			else{
				break;
			}
		}

		int g = 0;

		try {
			for(int i = 1; i < connections.size(); i++){

				Master previous = connections.get(i-1);
				Master current = connections.get(i);

				if(i == 1){  // previous node will be the first node, so set flag.
					previous.setNodeType(1);
					if(connections.size() > 2) 	current.setNodeType(2);
					else current.setNodeType(3);
				}								
				else if(i == connections.size()-1) // current node will be the last node, so set flag.
					current.setNodeType(3);
				else	// centre node
					current.setNodeType(2);			// else, this node is a centre node. 
				previous.setRight(current.getAddressInfo());
				int[] data = new int[per[i-1]+divide];		// Build list of values for the previous node and send them.
				for(int j = 0; j < data.length; j++){
					data[j] = values[g];
					g++;
				}
				previous.writeData(data);

				if(i == connections.size()-1){			// If last node then build list of values to sort and send them.
					int[] data2 = new int[per[i]+divide];
					for(int j = 0; j < data2.length; j++){
						data2[j] = values[g];
						g++;
					}
					current.writeData(data2);
				}
			}

			// Waits till all worker nodes at setup
			for(Master connection: connections){
				connection.readBoolean();	
			}
			// Send boolean to tell workers they can set up right node communication
			for(Master connection: connections){
				connection.writeBoolean();
			}

			// Waits till all workers are ready to start sorting
			for(Master connection: connections){
				connection.readInt();	
			}
			// Sends boolean to tell workers they may start
			for(Master connection: connections){
				connection.writeBoolean();
			}

			// Waits for all workers to be sorted
			for(Master connection: connections){
				connection.readBoolean();	
			}

			// Tells workers that they can start to send sorted list up to the sever  
			ArrayList<Integer> sortedList = new ArrayList<Integer>();
			for(Master connection: connections){
				connection.writeBoolean();
				int slength = connection.readInt();
				for(int j = 0; j < slength; j++){
					int q = connection.readInt();
					sortedList.add(q);
				}	
			}

			// Sends boolean to tell workers they may start
			for(Master connection: connections){
				connection.writeBoolean();
			}

			// Print sorted List
			System.out.println("Sorted List: " + sortedList.toString());


			// Print Statistics 
			double end = System.currentTimeMillis();
			double timeTaken = end-start;

			System.out.println("\n* * * Statistics * * *");
			System.out.println("Number of elements: " + listSize);
			System.out.println("Number of worker nodes: " + nodeAmount);
			System.out.println("Time taken for concurrent shaker sort: " + timeTaken + " msec");
			System.out.println("Average time per element: " + timeTaken/listSize + " msec");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}