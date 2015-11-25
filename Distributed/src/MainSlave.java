import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainSlave {

	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private String serverAddress;
	private int serverPort;

	ArrayList<String> inputList;
	ArrayList<String> sortedList;

	CustomComparator comp = new CustomComparator();

	Comparator<String> comparator = new Comparator<String>() {
		public int compare(String a, String b) {
			return a.compareTo(b);
		}
	};

	
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
//				System.out.println("Waiting for input from Master");
				
				// read the arraylist
				inputList = (ArrayList<String>) input.readObject();

//				receivedData(inputList);

//				System.out.println("Sorting the input...");

				// sort the array list
				Collections.sort(inputList, comp);

//				System.out.println("Sending the sorted list back to Master");

//				receivedData(inputList);
				
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

//	private void printReceivedData(ArrayList<String> list) {
//		for (String str: list) {
//			System.out.print(str + " ");
//		}
//	}

	public static void main(String[] args) throws ClassNotFoundException {
		new MainSlave("localhost", 6000).start();
	}
	
}