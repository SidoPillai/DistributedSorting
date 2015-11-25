import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

public class HandleTask2ConnectionRequest extends Thread{

	// Member variables
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	// Reference of master to store the files
	MainMaster master;

	int index;
	public Deque<ArrayList<String>> queue;

	//	ArrayList<String> sortedData;
	MapObjects obj;

	/**
	 * Constructor
	 * @param socket
	 */
	public HandleTask2ConnectionRequest(Socket socket, int index, MainMaster master) {
		this.index = index;
		queue = new LinkedList<ArrayList<String>>();
		this.master = master;

		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * The run method
	 */
	public void run() {

		while(true) {

			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			if (!this.queue.isEmpty()) {

				ArrayList<String> list = null;

				try {

					try {
						list = queue.pop();
					} catch(Exception e) {
						System.out.println("Data is not present");
					}

					// technically list at this point should be be null.
					// If its null means it has read all the content of the file
					if(list != null) {

						// sending the list out to sort
						oos.writeObject(list);
					}

					// will wait for the sorted arraylist from the slaves
					obj = (MapObjects) ois.readObject();

				} catch(Exception e) {

					// in case if an interupption occurs the load is handled here
					obj = inPlaceMapper(list);
				}
				master.manageMap(obj);
			}
		}
	}

	// In-place sort if an exception occurs
	private MapObjects inPlaceMapper(ArrayList<String> list) {

		// Sum Map
		HashMap<String, Integer> sum_map = new HashMap<String, Integer>();

		// prefix occurencesmap
		HashMap<String, Integer> count_map = new HashMap<String, Integer>();


		for (int i = 65; i < 91; i++) {
			sum_map.put(String.valueOf((char) i), 0);
			count_map.put(String.valueOf((char) i), 0);
		}

		String s = null;

		for (int i = 0; i < list.size(); i++) {
			s = list.get(i);
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
		return obj;
	}

}
