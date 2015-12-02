import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Master {

	// Comparator
	Comparator<String> c = new AlphanumComparator();

	// Server socket
	ServerSocket serverSocket;

	// Port to listen from client
	final int port = 6000;

	// list of available of host - stores IP
	List<String> listOfAvailableHost;

	// Keeping a track of slaves which are online
	List<Socket> listOfSlaves;

	// List of sorted files
	ArrayList<File> files = new ArrayList<File>();

	// Sum Map
	HashMap<String, Integer> sum_map = new HashMap<String, Integer>();

	// prefix occurrences map
	HashMap<String, Integer> count_map = new HashMap<String, Integer>();

	// List of ConnectionHandlers
	List<HandleTask1ConnectionRequest> listOfConnections = new ArrayList<HandleTask1ConnectionRequest>();

	// List of ConnectionHandlers for Task 2
	List<HandleTask2ConnectionRequest> listOfConnectionsTask2 = new ArrayList<HandleTask2ConnectionRequest>();

	// Used for lock in task 2
	static Object o = 0;

	// Data to sort 
	List<String> dataToSort;
	List<List<String>> chunks = new ArrayList<List<String>>();
	List<List<String>> sortedChunks = new ArrayList<List<String>>();

	// Constructor
	public Master() {
		listOfSlaves = new ArrayList<Socket>();
		listOfAvailableHost = new ArrayList<>();
	}

	// Task 1
	public void startTask1() throws IOException, ClassNotFoundException, InterruptedException {

		try {
			long start = System.currentTimeMillis();
			serverSocket = new ServerSocket(port);
			System.out.println("Master listening on port " + port);

			// Gets the IP from the file
			readIP();

			// Establish connection with the clients
			connectTask1();

			// start pinging once all the nodes are connected
			//			new Ping().start();

			long noOfLines = countLines("new_dataset_10000.txt");
			int noOfChunks = 100;
			int chunksize = (int) noOfLines/noOfChunks;

			System.out.println("Number of lines in the file " + noOfLines);
			System.out.println("Number of chunks " + noOfChunks);
			System.out.println("Size of each chunk " + chunksize);

			// handler to read line by line
			FileHandler handler = new FileHandler("new_dataset_10000.txt", chunksize);

			// Read data one by one
			int i = 0;
			int counter = 0;  // assign to slave
			int limitToRead = 0;  // only read based on no. of chunks

			while (true) {

				if(counter < listOfSlaves.size() && limitToRead < noOfChunks) {
					if (listOfConnections.get(counter).queue.size() == 0) {
						listOfConnections.get(counter).queue.add(handler.read(i*chunksize, chunksize));
						counter++;
						i++;
						limitToRead++;
					}
					Thread.sleep(1);
				} else if (limitToRead < noOfChunks) {
					counter = 0;
				} else {
					System.out.println("Data read in complete");
					break;
				}
			}

			System.out.println("Before Merging " + (System.currentTimeMillis()-start)/1000 + " seconds");

			System.out.println("------ MERGING SORTED FILES ------");
			Thread.sleep(500);
			// File Merging
			mergeSortedFiles(files, new File("lalala.txt"));
//			merger(files, new File("lalalulu.txt"));
			System.out.println("Total Computing time " + (System.currentTimeMillis()-start)/1000 + " seconds");
			// Done
			System.out.println("-------------- DONE --------------");
			// Quiting the program
			System.exit(0);

		} catch(IOException e){
			System.out.println("Something went wrong while connecting to a client");
			return;
		} finally {
			serverSocket.close();
			//			System.exit(0);
		}
	}

	// Task 2
	public void startTask2() throws InterruptedException, IOException {
		try{

			long start = System.currentTimeMillis();
			serverSocket = new ServerSocket(port);

			System.out.println("Master listening on port " + port);

			// Gets the IP from the file
			readIP();

			// Establish connection with the clients
			connectTask2();

			// everytime resetting the value of the map
			for (int i = 65; i < 91; i++) {
				sum_map.put(String.valueOf((char) i), 0);
				count_map.put(String.valueOf((char) i), 0);
			}

			// start pinging once all the nodes are connected
			new Ping().start();

			long noOfLines = countLines("new_dataset_10000.txt");
			int noOfChunks = 1000;
			int chunksize = (int) noOfLines/noOfChunks;

			System.out.println("Number of lines in the file " + noOfLines);
			System.out.println("Number of chunks " + noOfChunks);
			System.out.println("Size of each chunk " + chunksize);

			// handler to read line by line
			FileHandler handler = new FileHandler("new_dataset_10000.txt", chunksize);

			// Read data one by one
			int i = 0;
			int counter = 0;  // assign to slave
			int limitToRead = 0;  // only read based on no. of chunks

			while (true) {

				if(counter < listOfSlaves.size() && limitToRead < noOfChunks) {

					if (listOfConnectionsTask2.get(counter).queue.size() == 0) {
						listOfConnectionsTask2.get(counter).queue.add(handler.read(i*chunksize, chunksize));
						counter++;
						i++;
						limitToRead++;
					}
					Thread.sleep(1);
				} else if (limitToRead < noOfChunks) {
					counter = 0;
				} else {
					System.out.println("Data read in complete");
					break;
				}
			}
			System.out.println("---------- WRITING FILES ---------");
			writeToFile();
			System.out.println("Total Computing time " + (System.currentTimeMillis()-start)/1000 + " seconds");
			// Done
			System.out.println("-------------- DONE --------------");
			System.exit(0);
		} catch(IOException e){
			System.out.println("Something went wrong while connecting to a client");
			return;
		} finally {
			serverSocket.close();
		}
	}

	// Setup connections with the slaves for task 1
	private void connectTask1() throws IOException {

		for (int count = 0; count < listOfAvailableHost.size(); count++) {								
			Socket socket = serverSocket.accept();
			listOfSlaves.add(socket);
			HandleTask1ConnectionRequest conn = new HandleTask1ConnectionRequest(socket, count, this); 
			listOfConnections.add(conn);
			conn.start();
		}
		System.out.println("All nodes are now connected..");
	}

	// Setup connections with the slaves for task 2
	private void connectTask2() throws IOException {

		for (int count = 0; count < listOfAvailableHost.size(); count++) {								
			Socket socket = serverSocket.accept();
			listOfSlaves.add(socket);
			HandleTask2ConnectionRequest conn = new HandleTask2ConnectionRequest(socket, count, this); 
			listOfConnectionsTask2.add(conn);
			conn.start();
		}

		System.out.println("All nodes are now connected..");
	}

	// Reads the IP from the file
	private void readIP() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("ip.txt"));

		for (String line = br.readLine(); line != null; line = br.readLine()) {
			System.out.println("Pinging... " + line);
			listOfAvailableHost.add(line);
		}
		br.close();
	}

	// Ping the slaves to get the status
	private class Ping extends Thread {

		public void run() {

			// iterate over the list and ping
			while (true) {

				try {
					InetAddress inet;
					if(listOfSlaves.size() > 0) {
						for (int i = 0; i < listOfSlaves.size(); i++) {
							inet = listOfSlaves.get(i).getInetAddress();
							System.out.println("Sending Ping Request to " + inet);
							System.out.println(inet.isReachable(5000) ? "Host is reachable" : "Host is NOT reachable");
						}
					}
					sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// dividing the file into small blocks
	public static long estimateBestSizeOfBlocks(File filetobesorted) {
		long sizeoffile = filetobesorted.length();

		// we don't want to open up much more than 1024 temporary files, better run
		// out of memory first. (Even 1024 is stretching it.)
		final int MAXTEMPFILES = 1024;
		long blocksize = sizeoffile / MAXTEMPFILES ;

		// on the other hand, we don't want to create many temporary files
		// for naught. If blocksize is smaller than half the free memory, grow it.
		long freemem = Runtime.getRuntime().freeMemory();
		if( blocksize < freemem/2)
			blocksize = freemem/2;
		else {
			if(blocksize >= freemem) 
				System.err.println("We expect to run out of memory. ");
		}
		return blocksize;
	}

	// Merging the sorted files
	public void mergeSortedFiles(List<File> files, File outputfile) throws IOException {

		List <BinaryFileBuffer> alist = new ArrayList<BinaryFileBuffer>();
		PriorityQueue<String> prq = new PriorityQueue<String>(10,c); // For sorting elements.

		BufferedWriter fbw = new BufferedWriter(new FileWriter(outputfile));
		int countNoFile = 0;
		int countOfLines = 0;
		int fileSize = files.size();
		BinaryFileBuffer temp;
		int flag = 0; // To check if all the files are empty or not

		for (File f : files) {
			BinaryFileBuffer bfb = new BinaryFileBuffer(f);
			alist.add(bfb); // Put all the sorted files in the list.
		}

		try {

			while(true) {

				while(countNoFile<fileSize) {

					temp = alist.get(countNoFile);

					if(!temp.empty()) {

						while(countOfLines<1000) {

							if(!temp.empty()) {
								prq.add(temp.pop());
								countOfLines++;
							} else {
								break;
							}
						}
						countOfLines = 0;
						countNoFile++;
					}
					else {
						countNoFile++;
						flag++;  // If this is qual to number of files in the list that means all are empty and this will
						// the last iteration.
					}
				}
				countNoFile = 0;
				String r;

				while(!prq.isEmpty()) {
					r = prq.poll();
					fbw.write(r);
					fbw.newLine();
				}
				if(flag == fileSize) {
					break;
				}
			}
		} finally { 
			fbw.close();
//			comp = null;
			alist = null;
			prq = null;
		}
	}

	// count the number of lines in the file
	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

	// creates a file when an array is sorted
	public File manageSortedArrays(ArrayList<String> sortedList) throws IOException {

		File newtmpfile = File.createTempFile("sortInBatch", "flatfile");
		newtmpfile.deleteOnExit();

		BufferedWriter fbw = new BufferedWriter(new FileWriter(newtmpfile));

		try {
			for(String r : sortedList) {
				fbw.write(r);
				fbw.newLine();
			}
		} finally {
			fbw.close();
		}
		return newtmpfile;
	}

	// Reduction function to keep a track of the provenance
	public void manageMap(MapObjects obj) {

		// update the map objects
		synchronized (o) {
			HashMap<String, Integer> map1 =	obj.count_map;
			HashMap<String, Integer> map2 = obj.sum_map;
			String[] a = new String[map1.size()]; 
			Object[] keys = map1.keySet().toArray();

			for (int row = 0; row < a.length; row++) {
				a[row] = (String) keys[row];
			}

			for (int i = 0; i < map1.size(); i++) {
				Integer l = map1.get(a[i]) + count_map.get(a[i]);
				count_map.put(a[i], l);

				Integer ll = map2.get(a[i]) + sum_map.get(a[i]);
				sum_map.put(a[i], ll);
			}
		}
	}

	// Writing the output of task2 
	private void writeToFile() throws IOException {
		File task2 = new File("task2.txt"); // file to write values into

		BufferedWriter file_out = new BufferedWriter(new FileWriter(task2));

		for (int i = 65; i < 91; i++) {

			int val = count_map.get(String.valueOf((char) i));

			if (val > 0) {
				file_out.write((char) i + " " + val + " "
						+ sum_map.get(String.valueOf((char) i)) + "\n");
			}
		}
		file_out.close();
	}

	public static long estimateAvailableMemory() {
		System.gc();
		return Runtime.getRuntime().freeMemory();
	}

	public static long estimateBestSizeOfBlocks(final long sizeoffile,
			final int maxtmpfiles, final long maxMemory) {
		// we don't want to open up much more than maxtmpfiles temporary
		// files, better run
		// out of memory first.
		long blocksize = sizeoffile / maxtmpfiles
				+ (sizeoffile % maxtmpfiles == 0 ? 0 : 1);

		// on the other hand, we don't want to create many temporary
		// files
		// for naught. If blocksize is smaller than half the free
		// memory, grow it.
		if (blocksize < maxMemory / 2) {
			blocksize = maxMemory / 2;
		}
		return blocksize;
	}

	// file delimiter
	private String empty = "";

	private ArrayList<Scanner> getScanners(ArrayList<File> allFiles) throws FileNotFoundException{
		ArrayList<Scanner> scanners = new ArrayList<Scanner>();
		for(File f : allFiles){
			if(f!=null) {
				Scanner sc = new Scanner(f);
				scanners.add(sc);
			}
		}
		return scanners;
	}

	private void closeScanners(ArrayList<Scanner> scanners){
		for(Scanner sc : scanners){
			sc.close();
		}
	}

	private int getMin(String[] values){
		String min = empty;
		int minindex = -1;
		int index = 0;

		for(String value : values){
			if(!value.equals(empty) && value!= null){
				if(c.compare(value,min) < 0 || min.equals(empty)){
					min = value;
					minindex = index;
				}
			}
			index++;
		}
		return minindex;
	}

	private String[] getValues(ArrayList<Scanner> scanners){
		String[] values = new String[scanners.size()];

		for(int i=0;i<scanners.size();i++){
			if(scanners.get(i).hasNextLine()){
				values[i] = scanners.get(i).nextLine();
			}else{
				values[i] = empty;
			}
		}

		return values;
	}

	private void updateValues(String[] values, int index, ArrayList<Scanner> scanners){
		if(scanners.get(index).hasNextLine()){
			values[index] = scanners.get(index).nextLine();
		} else{
			values[index] = empty;
		}
	}

	private void merger(ArrayList<File> allFiles, File outputfile) throws IOException {

		ArrayList<Scanner> scanners = getScanners(allFiles);
		String[] values = getValues(scanners);

		PrintWriter out = new PrintWriter(outputfile);
		int index = getMin(values);
		while(index!=-1){
			out.println(values[index]);			
			updateValues(values, index, scanners);
			index = getMin(values);
		}

		closeScanners(scanners);
		out.close();

	}


	/**
	 * Default maximal number of temporary files allowed.
	 */
	public static final int DEFAULTMAXTEMPFILES = 1024;

	// Main method
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		System.out.println("Tasks");
		System.out.println("1. - Distributed Sorting");
		System.out.println("2. - Calculate Average Unique");
		System.out.println("Enter your choice");
		Scanner sc = new Scanner(System.in);
		String choice = sc.next();
		if (choice.equals("1")) {
			new Master().startTask1();
		} else {
			new Master().startTask2();
		}
		sc.close();
	}

}
