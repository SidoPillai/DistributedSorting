import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
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
	String fileName;

	public FileHandler(String file, int buff_size) throws FileNotFoundException {
		fileName = file;
		ios = new FileInputStream(file);
		read = 0;
		this.buff_size = buff_size;
	}

	/*
	 * Data buffer of size is read and passed
	 */
	ArrayList<String> read(int startIndex, int buff_size) throws IOException, InterruptedException {
		FileInputStream ios = new FileInputStream(fileName);
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(ios));
		String s = null;

		for(int i = 0; i < startIndex; i++) {
			br.readLine();
		}

		int currentPos = 0;

		while(currentPos < buff_size ) {
			s = br.readLine();
//			System.out.println("--------------------------------" + s);
			if(s == null || s.equals("")) {

			} else {
				list.add(s);
			}
			currentPos++;
		}

		br.close();
		return list;
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


