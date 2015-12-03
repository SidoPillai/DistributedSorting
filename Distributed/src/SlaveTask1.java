import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
//	QuickSort qsort = new QuickSort();

	// Constructor
	public SlaveTask1(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		comp = new AlphanumComparator();
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
//				qsort.quickSort(inputList, 0, inputList.size());
				// send the sorted list back to the server
				output.writeObject(inputList);
			}

		} catch (Exception e) {
			System.out.println("Exception here " + e.getMessage());
//			e.printStackTrace();
		} finally {
			//				socket.close();
//			System.exit(0);
		}
	}
	
	// Main method
	public static void main(String[] args) throws ClassNotFoundException {
		new SlaveTask1("localhost", 6000).start();
	}
	
}