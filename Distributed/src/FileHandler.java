import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * 
 * FileHandler is a utility class which reads and writes on a file
 * based on chunks. 
 * 
 * @author Siddesh Pillai
 *
 */
public class FileHandler {

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
}