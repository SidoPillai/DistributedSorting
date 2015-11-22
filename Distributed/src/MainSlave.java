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
	
	public void setupConnection() throws IOException{
		input = new ObjectInputStream(socket.getInputStream());
		output = new ObjectOutputStream(socket.getOutputStream());
	}
	
	public void start() throws ClassNotFoundException {

		try {
			socket = new Socket(serverAddress, serverPort);
			setupConnection();

			// read the arraylist
			inputList = (ArrayList<String>) input.readObject();
			
			// sort the array list
			heap = new Heap(inputList);
			sortedList = heap.HeapSort();
			
			// send the sorted list back to the server
			output.writeObject(sortedList);
			
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
