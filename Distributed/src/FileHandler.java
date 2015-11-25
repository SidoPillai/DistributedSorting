import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 
 * FileHandler is a utility class which reads and writes on a file
 * based on chunks. 
 * 
 * @author Siddesh Pillai
 *
 */
public class FileHandler {

	FileInputStream ios = null;
	int read;
	byte[] buffer;
	int buff_size;// Segment Size

	public FileHandler(String file, int buff_size) throws FileNotFoundException {
		ios = new FileInputStream(file);
		read = 0;
		this.buff_size = buff_size;
	}

	/**
	 * Extract a chunk from a file
	 * 
	 * @param fileName          name of the file to read from
	 * @param startPosition     start position for reading
	 * @param size              number of bytes to read
	 * @return                  byte[] containing bytes read. 
	 */
	public static byte[] getChunk(String fileName, int startPosition, int size) {
		byte[] bytes = null;
		try {
			RandomAccessFile raFile = new RandomAccessFile(fileName, "r");
			FileChannel fc = raFile.getChannel();
			fc.position(startPosition);
			ByteBuffer buf = ByteBuffer.allocate(size);
			int bytesRead = fc.read(buf);
			if(bytesRead > 0) {
				bytes = Arrays.copyOf(buf.array(), bytesRead);
			} else if (bytesRead == -1) {
				System.out.println("end of file");
			} else {
				System.out.println(fileName + " " + startPosition + " " + size);
				System.out.println("No bytes read!");
			}
			raFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	/*
	 * Data buffer of size is read and passed
	 */
	ArrayList<String> read(int startIndex, int buff_size) throws IOException, InterruptedException {

		ArrayList<String> list = new ArrayList<String>();
		String str;
		BufferedReader br = new BufferedReader(new InputStreamReader(ios));
		String s = null;

		for(int i = 0; i < startIndex; ++i) {
			br.readLine();
		}

		int currentPos = 0;

		while(currentPos < buff_size) {
			s = br.readLine();
			System.out.println("--------------------------------" + s);
			list.add(s);
			currentPos++;
		}
		return list;


		//		ArrayList<String> list = new ArrayList<String>();
		//		LineNumberReader br = new LineNumberReader(new InputStreamReader(ios));
		//		String s = null;
		//		br.setLineNumber(startIndex);
		//		int currentPos = 0;
		//
		//		while(currentPos < buff_size) {
		//			s = br.readLine();
		//			if(s == null || s.equals("")) {
		//				currentPos++;
		//			} else { 
		//				System.out.println("--------------------------------" + s);
		//				list.add(s);
		//				currentPos++;
		//			}
		//		}
		//		return list;
	}

	/**
	 * Write on the file for the filename provided
	 * @param fileName
	 * @param startPosition
	 * @param bytes
	 */
	public static void writeToFile(String fileName, int startPosition, byte[] bytes) {
		try {
			RandomAccessFile raFile = new RandomAccessFile(fileName, "rw");
			FileChannel fc = raFile.getChannel();
			ByteBuffer buf = ByteBuffer.wrap(bytes);
			fc.write(buf, startPosition);
			raFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getData() throws IOException {
		File file = new File("new_dataset_10000.txt");
		FileInputStream f = new FileInputStream(file);
		int SIZE = (int)file.length();
		byte[] barray = new byte[SIZE];
		FileChannel ch = f.getChannel();
		MappedByteBuffer mb = ch.map(FileChannel.MapMode.READ_ONLY,0L, ch.size());
		int i = 0;

		while(mb.hasRemaining()) {
			barray[i] = mb.get();
			i++;	
		}

		ArrayList<String> valToSort = new ArrayList<String>(); 
		String	str = new String (barray);
		Scanner s = new Scanner(str);

		while (s.hasNext()) {
			valToSort.add(s.next());
		}

		s.close();
		f.close();
		return valToSort;
	}

	// write contents on the file
	public static void writeFile(ArrayList<String> list) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter("out.txt"));

		for (int i = 0; i < list.size(); i++) {
			pw.write(list.get(i) + "\n");
		}
		pw.close();
	}

}


