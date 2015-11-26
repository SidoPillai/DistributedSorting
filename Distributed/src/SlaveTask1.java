import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class SlaveTask1 {

	// Member Variables
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private String serverAddress;
	private int serverPort;
	
	ArrayList<String> inputList;
	ArrayList<String> sortedList;
	
	static Comparator<String> comp;
	static PriorityQueue<String> prq;

	// Constructor
	public SlaveTask1(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		comp = new Heap();
		prq = new PriorityQueue<String>(10,comp);
	}

	@SuppressWarnings("unchecked")
	public void start() throws ClassNotFoundException {

		try {
			socket = new Socket(serverAddress, serverPort);
			System.out.println("Connecting to server at " + serverAddress + " on port " + serverPort);

			input = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());

			while(true) {
				// read the arraylist
				inputList = (ArrayList<String>) input.readObject();
				// sort the array list
				Collections.sort(inputList, comp);
				// send the sorted list back to the server
				output.writeObject(inputList);
			}

		} catch (UnknownHostException e) {
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		} finally {
			try {
				socket.close();
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Sorting of the list takes place here
	public static ArrayList<String> sortList(ArrayList<String>inputList) {
			
		int size = inputList.size();
		int j = 0;
		for(int i = 0; i < size; i++) {
			prq.add(inputList.get(i));
		}
		
		while(!prq.isEmpty()) {
			inputList.set(j, prq.poll());
			j++;
		}
		return inputList;
	}

	// Main method
	public static void main(String[] args) throws ClassNotFoundException {
		new SlaveTask1("localhost", 6000).start();
	}
	
}