import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;

public class HandleConnectionRequest extends Thread{
	
	// Member variables
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	
	// main comparator for in-place sort
	Comparator<String> comparator = new Comparator<String>() {
		public int compare(String r1, String r2) {
			return Heap.myComparator(r1, r2);
		}
	};

	MainMaster master;

	int index;
	public Deque<ArrayList<String>> queue;

	ArrayList<String> sortedData; 

	/**
	 * Constructor
	 * @param socket
	 */
	public HandleConnectionRequest(Socket socket, int index, MainMaster master) {
		//			this.connectedSocket = socket;
		this.index = index;
		this.master = master;
		queue = new LinkedList<ArrayList<String>>();

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

		//			try {

		System.out.println("I am Slave Number " + index);
		while(true) {
//			System.out.println("Slave " + index + " Q size " + queue.size());
			
			if (!this.queue.isEmpty()) {
				
				System.out.println("Time to send the data now");
				ArrayList<String> list = null;

				try {

					//	if (filestoSort.size() != 0) {

					try {
						list = queue.pop();//filestoSort.remove(index);
						//							this.flagHC = false;
					} catch(Exception e) {
						System.out.println("Data is not present");
					}
					//}

					// technically list at this point should be be null.
					// If its null means it has read all the content of the file
					if(list != null) {

						// sending the list out to sort
						oos.writeObject(list);
						System.out.println("Sent the chunks to the client");
					}

					// will wait for the sorted arraylist from the slaves
					sortedData = (ArrayList<String>) ois.readObject();
					System.out.println("Receieved the sorted data");

				} catch(Exception e) {

					// in case if an interupption occurs the load is handled here
					sortedData = inPlaceSort(list);
				}

				// add the sorted data in the list of files
//				synchronized (master.files) {
					try {
						master.files.add(master.manageSortedArrays(sortedData));
					} catch (IOException e) {
//						 TODO Auto-generated catch block
						e.printStackTrace();
					}
//				}

				System.out.println("Added to sorted data");
				//						break;
			}

		}

		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			} finally {
		//				try {
		//					// closing the socket
		//					this.serverSocketTCP.close();
		//				} catch (IOException e) {
		//					e.printStackTrace();
		//				}
		//			}
	}

	// In-place sort if an exception occurs
	private ArrayList<String> inPlaceSort(ArrayList<String> list) {
		Collections.sort(list, comparator);
		return list;
	}


}
