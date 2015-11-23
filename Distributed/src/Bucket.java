import java.net.Socket;

public class Bucket {

	Socket socket;
	int startIndex;
	int endIndex;
	int chunkSize; 
	
	Bucket(Socket socket, int startIndex, int endIndex, int chunkSize) {
		this.socket = socket;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.chunkSize = chunkSize;
	}
	
}
