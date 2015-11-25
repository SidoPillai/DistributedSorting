import java.io.IOException;

public class DistributedSort {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		
		new MainMaster().startTask1();
	}
}
