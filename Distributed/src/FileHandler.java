import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileHandler {

	// Member variables
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

	// Data buffer of size is read and passed
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
			if(s == null || s.equals("")) {

			} else {
				list.add(s);
			}
			currentPos++;
		}

		br.close();
		return list;
	}

}


