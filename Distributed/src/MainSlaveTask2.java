import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainSlaveTask2 {

	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private String serverAddress;
	private int serverPort;

	ArrayList<String> inputList;

	// Sum Map
	HashMap<String, Integer> sum_map = new HashMap<String, Integer>();

	// prefix occurencesmap
	HashMap<String, Integer> count_map = new HashMap<String, Integer>();

	// Constructor
	public MainSlaveTask2(String serverAddress, int serverPort) {
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
				// read the arraylist
				inputList = (ArrayList<String>) input.readObject();

				// everytime resetting the value of the map
				for (int i = 65; i < 91; i++) {
					sum_map.put(String.valueOf((char) i), 0);
					count_map.put(String.valueOf((char) i), 0);
				}

				String s = null;

				for (int i = 0; i < inputList.size(); i++) {
					
					s = inputList.get(i);
					
					String letter = s.substring(0, 1).toUpperCase();

					Integer num = Integer.parseInt(s.substring(1));

					if (sum_map.containsKey(letter)) {
						Integer val = sum_map.get(letter);
						sum_map.put(letter, val + num);
					} else {
						sum_map.put(letter, num);
					}

					if (count_map.containsKey(letter)) {
						Integer count = count_map.get(letter);
						count_map.put(letter, ++count);
					} else {
						count_map.put(letter, 1);
					}

				}

				MapObjects obj = new MapObjects(sum_map, count_map);

				// send the map back to master
				output.writeObject(obj);

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

	// Main method
	public static void main(String[] args) throws ClassNotFoundException {
		new MainSlave("localhost", 6000).start();
	}

}