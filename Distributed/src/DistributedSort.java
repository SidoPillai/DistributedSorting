import java.io.IOException;

public class DistributedSort {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		// Start the program
		new Master().startTask1();
	}
}
