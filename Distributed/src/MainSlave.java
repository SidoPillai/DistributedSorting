import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainSlave {

	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Heap heap;
	private String serverAddress;
	private int serverPort;

	ArrayList<String> inputList;
	ArrayList<String> sortedList;

	public MainSlave(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}

	@SuppressWarnings("unchecked")
	public void start() throws ClassNotFoundException {

		try {
			socket = new Socket(serverAddress, serverPort);
			System.out.println("Connecting to server at " + serverAddress + " on port " + serverPort);

			input = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());

			while(true) {
				System.out.println("Waiting for input");
				
				// read the arraylist
				inputList = (ArrayList<String>) input.readObject();

				System.out.println("Sorting the input...");
				
				// sort the array list
				heap = new Heap(inputList);
				sortedList = heap.HeapSort();

				System.out.println("Sending the sorted list");

				// send the sorted list back to the server
				output.writeObject(sortedList);
				
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws ClassNotFoundException {
		new MainSlave("localhost", 6030).start();
	}
	
}