import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This class is used in the merge file operation when we 
// add elements in the priority queue
public class BinaryFileBuffer implements Comparator {

	// Member Variables
	public static final int BUFFERSIZE = 2048;
	public BufferedReader fbr;
	public File originalfile;
	private String cache;
	private boolean empty;
	static Pattern pattern;

	// Constructor
	public BinaryFileBuffer() {

	}

	// Parameterized Constructor
	public BinaryFileBuffer(File f) throws IOException {
		originalfile = f;
		fbr = new BufferedReader(new FileReader(f), BUFFERSIZE);
		reload();
	}

	// Check the boolean
	public boolean empty() {
		return empty;
	}

	// used after the buffer is popped 
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

	// Close the connection
	public void close() throws IOException {
		fbr.close();
	}

	// Checks the elements
	public String peek() {
		if(empty()) return null;
		return cache.toString();
	}

	// Removes the elements
	public String pop() throws IOException {
		String answer = peek();
		reload();
		return answer;
	}

	// Comparing the 2 strings
	public int compare(Object o1, Object o2) {
		BinaryFileBuffer b1 = (BinaryFileBuffer)o1;
		BinaryFileBuffer b2 = (BinaryFileBuffer)o2;
		String a = (String)b1.peek();
		String b = (String)b2.peek();
		pattern = Pattern.compile("[a-zA-Z]");
		Matcher match;
		int a_first = (int)a.charAt(0);
		int b_first = (int)b.charAt(0);
		String sa_1;
		String sa_2;
		String sb_1;
		String sb_2;
		int count;
		//	System.out.println(a_first);

		if(48 <= a_first && a_first <= 57) {
			sa_1 = "";
			sa_2 = a;
		} else {
			match = pattern.matcher(a);
			count = 0;

			while(match.find()) {
				count++;
			}
			sa_1 = a.substring(0, count);
			sa_2 = a.substring(count);
		}

		if(48 <= b_first && b_first <= 57) {
			sb_1 = "";
			sb_2 = b;
		} else {
			match = pattern.matcher(b);
			count = 0;

			while(match.find()) {
				count++;
			}
			sb_1 = b.substring(0, count);
			sb_2 = b.substring(count);	
		}

		int val = 0 ;
		if(a.compareTo(b) == 0) {
			val = 0;
		} else {
			if(sa_1 != null && sb_1 != null) {
				if(sa_1.compareTo(sb_1) < 0) {
					val = -1;
				} else if (sa_1.compareTo(sb_1) > 0) {
					val = 1;
				} else {
					if(!sa_2.equals("") && !sb_2.equals("")) {
						int sa_int = Integer.parseInt(sa_2);
						int sb_int = Integer.parseInt(sb_2);

						if(sa_int < sb_int) {
							val = -1;	
						} else {
							val = 1;
						}
					} else {
						if(sa_2.equals("")) {
							val = -1;
						} else {
							val = 1;
						}
					}	
				}		
			}
		}
		return val;
	}
}
