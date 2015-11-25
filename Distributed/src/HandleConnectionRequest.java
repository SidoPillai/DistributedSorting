import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleConnectionRequest extends Thread{

	// Member variables
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	// Reference of master to store the files
	MainMaster master;

	// main comparator for in-place sort

	Comparator<String> comparator = new Comparator<String>() {
		public int compare(String a, String b) {
			return a.compareTo(b);
//			Pattern pattern = Pattern.compile("[a-zA-Z]");
//			Matcher match;
//
//			// take the first characters
//			int a_first = (int)a.charAt(0);
//			int b_first = (int)b.charAt(0);
//
//			String sa_1;
//			String sa_2;
//			String sb_1;
//			String sb_2;
//
//			int count;
//
//			// check if the prefix is a number
//			if(48 <= a_first && a_first <= 57) {
//				sa_1 = "";
//				sa_2 = a;
//			}
//
//			else {
//				match = pattern.matcher(a);
//				count = 0;
//				while(match.find()) {
//					count++;
//				}
//				sa_1 = a.substring(0, count);
//				sa_2 = a.substring(count);
//			}
//
//			if(48 <= b_first && b_first <= 57) {
//				sb_1 = "";
//				sb_2 = b;
//			}
//			else {
//				match = pattern.matcher(b);
//				count = 0;
//				while(match.find()) {
//					count++;
//				}
//				sb_1 = b.substring(0, count);
//				sb_2 = b.substring(count);	
//			}
//
//			if(a.compareTo(b) == 0) {
//				return 1;
//			}
//
//			else {
//				if(sa_1 != null && sb_1 != null) {
//					if(sa_1.compareTo(sb_1) < 0) {
//						return 1;
//
//					} else if (sa_1.compareTo(sb_1) > 0) {
//						return -1;
//					}
//
//					else {
//						if(!sa_2.equals("") && !sb_2.equals("")) {
//							int sa_int = Integer.parseInt(sa_2);
//							int sb_int = Integer.parseInt(sb_2);
//
//							if(sa_int < sb_int) {
//								return 1;
//							} 
//							else {
//								return -1;
//							}
//						} else {
//							if(sa_2.equals("")) {
//								return 1;
//							}
//							else {
//								return -1;
//							}
//						}	
//					}		
//				}
//			}
//			return 1;	
		}
	};

	int index;
	public Deque<ArrayList<String>> queue;

	ArrayList<String> sortedData; 

	/**
	 * Constructor
	 * @param socket
	 */
	public HandleConnectionRequest(Socket socket, int index, MainMaster master) {
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

		System.out.println("I am Slave Number " + index);

		while(true) {

			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			if (!this.queue.isEmpty()) {

				System.out.println("Time to send the data now");
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
						System.out.println("Sent the chunks to the client");
					}

					// will wait for the sorted arraylist from the slaves
					sortedData = (ArrayList<String>) ois.readObject();
					System.out.println("Receieved the sorted data");

				} catch(Exception e) {

					// in case if an interupption occurs the load is handled here
					sortedData = inPlaceSort(list);
				}

				System.out.println(sortedData.size() + " Elements in the list--------");
				
				// add the sorted data in the list of files
				try {
					master.files.add(master.manageSortedArrays(sortedData));
				} catch (IOException e) {
					e.printStackTrace();
				}

				System.out.println("Added to sorted data");
			}
		}
	}

	// In-place sort if an exception occurs
	private ArrayList<String> inPlaceSort(ArrayList<String> list) {
		Collections.sort(list, comparator);
		return list;
	}


}
