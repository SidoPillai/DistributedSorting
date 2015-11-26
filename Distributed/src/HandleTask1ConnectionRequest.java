import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;

public class HandleTask1ConnectionRequest extends Thread{

	// Member variables
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	// Reference of master to store the files
	Master master;

	int index;
	public Deque<ArrayList<String>> queue;

	ArrayList<String> sortedData; 

	// Constructor
	public HandleTask1ConnectionRequest(Socket socket, int index, Master master) {
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
	@SuppressWarnings("unchecked")
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
					sortedData = (ArrayList<String>) ois.readObject();
				} catch(Exception e) {

					// in case if an interupption occurs the load is handled here
					sortedData = inPlaceSort(list);
				}

				// add the sorted data in the list of files
				try {
					master.files.add(master.manageSortedArrays(sortedData));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// In-place sort if an exception occurs
	private ArrayList<String> inPlaceSort(ArrayList<String> list) {
		Comparator<String> comp = new Heap();
		Collections.sort(list, comp);
		return list;
	}
}
