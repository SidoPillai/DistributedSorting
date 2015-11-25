import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainSlave {

	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
//	private Heap heap;
	private String serverAddress;
	private int serverPort;

	ArrayList<String> inputList;
	ArrayList<String> sortedList;

	CustomComparator comp = new CustomComparator();

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

				receivedData(inputList);
				System.out.println("Sorting the input...");
				
				Collections.sort(inputList, comp);
				// sort the array list
//				heap = new Heap(inputList);
//				sortedList = heap.HeapSort();

				System.out.println("Sending the sorted list");

				receivedData(inputList);
				
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

	private void receivedData(ArrayList<String> inputList2) {

		for (String str: inputList2) {
			System.out.print(str + " ");
		}
	}

	public static void main(String[] args) throws ClassNotFoundException {
		new MainSlave("localhost", 6000).start();
	}
	
}