import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class MainMaster {

	// Server socket
	ServerSocket serverSocket;

	// Port to listen from client
	final int port = 6000;

	// list of available of host - stores IP
	List<String> listOfAvailableHost;

	// Subnet for discovering the machines connected to the network
	String subnet;

	// flag to indicate when to start sending to client
	static volatile boolean flag = false;

	static Boolean f = false;

	// List of list of string which are set out to sort
	List<ArrayList<String>> filestoSort = new ArrayList<ArrayList<String>>();

	// Keeping a track of slaves which are online
	List<Socket> listOfSlaves;

	// List of sorted files
	List<File> files = new ArrayList<File>();

	// List of ConnectionHandlers
	List<HandleConnectionRequest> listOfConnections = new ArrayList<HandleConnectionRequest>();

	CustomComparator comp = new CustomComparator();

	// main comparator for in-place sort
	Comparator<String> comparator = new Comparator<String>() {
		public int compare(String a, String b) {
			return a.compareTo(b);
		}
	};

	// Data to sort 
	List<String> dataToSort;
	List<List<String>> chunks = new ArrayList<List<String>>();
	List<List<String>> sortedChunks = new ArrayList<List<String>>();

	public MainMaster() {
		listOfSlaves = new ArrayList<Socket>();
		listOfAvailableHost = new ArrayList<>();
	}

	MainMaster(String subnet) {
		this.subnet = subnet;
	}

	public void start() throws IOException, ClassNotFoundException, InterruptedException {

		try{

			long start = System.currentTimeMillis();
			serverSocket = new ServerSocket(port);

			System.out.println("Master listening on port " + port);

			// Gets the IP from the file
			readIP();

			// Establish connection with the clients
			connect();

			// start pinging once all the nodes are connected
//			new Ping().start();

//			System.out.println("Sending the data...");

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

//			System.out.println("Adding data to chunks");

			int counter = 0;  // assign to slave
			int limitToRead = 0;  // only read based on no. of chunks

			while (true) {

				if(counter < listOfSlaves.size() && limitToRead < noOfChunks) {

					if (listOfConnections.get(counter).queue.size() == 0) {
						listOfConnections.get(counter).queue.add(handler.read(i*chunksize, chunksize));
						counter++;
						i++;
						limitToRead++;
//						System.out.println("Added data to chunk");
					}
					Thread.sleep(1);
				} else if (limitToRead < noOfChunks) {
					counter = 0;
				} else {
					System.out.println("Data read in complete");
					break;
				}
			}

			System.out.println("------ MERGING SORTED FILEs ------");

			Thread.sleep(5000);

			// File Merging
			//			mergeSortedFiles(files, new File("output_file_sorted_10000.txt"), comparator);

			mergeSort(files, new File("output_10000.txt"), comp);

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

	private void connect() throws IOException {

		for (int count = 0; count < listOfAvailableHost.size(); count++) {								
			Socket socket = serverSocket.accept();
//			System.out.println("Accepted one node");
			listOfSlaves.add(socket);
			HandleConnectionRequest conn = new HandleConnectionRequest(socket, count, this); 
			listOfConnections.add(conn);
			conn.start();
			filestoSort.add(null);
			System.out.println("Slave : " + (count+1) + " connected");
		}

		System.out.println("All nodes are now connected..");
	}

	// Reads the IP from the file
	private void readIP() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("check.txt"));

		for (String line = br.readLine(); line != null; line = br.readLine()) {
			System.out.println("Pinging... " + line);
			listOfAvailableHost.add(line);
		}
		br.close();
	}

	// looks for the host in the network and adds inti the list of online devices 
	public static ArrayList<String> checkHosts(String subnet) throws UnknownHostException, IOException {
		ArrayList<String> onlineHost = new ArrayList<String>();
		int timeout = 1000;

		for (int i = 1;i < 254;i++){
			System.out.println(i);
			String host=subnet + "." + i;
			if (InetAddress.getByName(host).isReachable(timeout)){
				//				System.out.println(host + " is reachable");
				onlineHost.add(host);
			}
		}
		return onlineHost;
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

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		new MainMaster().start();
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

	public void mergeSort(List<File> inFiles, File outFile, Comparator<String> comparator) throws IOException  {

		PrintWriter writer = null;

		try {
			BufferedReader[] readers = new BufferedReader[inFiles.size()];
			writer = new PrintWriter(outFile);
			TreeMap<String, Integer> treeMap = new TreeMap<String, Integer>(
					comparator);

			// read first line of each file. We don't check for EOF here, probably should
			for (int i = 0; i < inFiles.size(); i++) {
				readers[i] = new BufferedReader(new FileReader(inFiles.get(i)));
				String line = readers[i].readLine();
				treeMap.put(line, Integer.valueOf(i));
			}

			while (!treeMap.isEmpty()) {
				Map.Entry<String, Integer> nextToGo = treeMap.pollFirstEntry();
				int fileIndex = nextToGo.getValue().intValue();
				writer.println(nextToGo.getKey());

				String line = readers[fileIndex].readLine();
				if (line != null)
					treeMap.put(line, Integer.valueOf(fileIndex));
			}
		}
		finally {
			writer.close();
		}
	}

	// merging the sorted file
	public int mergeSortedFiles(List<File> files, File outputfile, final Comparator<String> cmp) throws IOException {

		PriorityQueue<BinaryFileBuffer> pq = new PriorityQueue<BinaryFileBuffer>(11, new Comparator<BinaryFileBuffer>() {

			public int compare(BinaryFileBuffer i, BinaryFileBuffer j) {
				return cmp.compare(i.peek(), j.peek());
			}
		});

		for (File f : files) {
			BinaryFileBuffer bfb = new BinaryFileBuffer(f);
			pq.add(bfb);
		}

		BufferedWriter fbw = new BufferedWriter(new FileWriter(outputfile));
		int rowcounter = 0;

		try {
			while(pq.size() > 0) {
				BinaryFileBuffer bfb = pq.poll();
				String r = bfb.pop();
				fbw.write(r);
				fbw.newLine();
				++rowcounter;

				if(bfb.empty()) {
					bfb.fbr.close();
					bfb.originalfile.delete(); // we don't need you anymore
				} else {
					pq.add(bfb); // add it back
				}
			}
		} finally { 
			fbw.close();
			for(BinaryFileBuffer bfb : pq ) bfb.close();
		}
		return rowcounter;
	}

	public class BinaryFileBuffer {
		public static final int BUFFERSIZE = 2048;
		public BufferedReader fbr;
		public File originalfile;
		private String cache;
		private boolean empty;

		public BinaryFileBuffer(File f) throws IOException {
			originalfile = f;
			fbr = new BufferedReader(new FileReader(f), BUFFERSIZE);
			reload();
		}

		public boolean empty() {
			return empty;
		}

		private void reload() throws IOException {
			try {
				if((this.cache = fbr.readLine()) == null){
					empty = true;
					cache = null;
				}
				else{
					empty = false;
				}
			} catch(EOFException oef) {
				empty = true;
				cache = null;
			}
		}

		public void close() throws IOException {
			fbr.close();
		}


		public String peek() {
			if(empty()) return null;
			return cache.toString();
		}

		public String pop() throws IOException {
			String answer = peek();
			reload();
			return answer;
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
}
