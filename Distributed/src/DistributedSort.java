import java.io.IOException;
import java.util.Scanner;


public class DistributedSort {

	// start the program by providing the subnet
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		
		System.out.println("Enter the Subnet");
		Scanner sc = new Scanner(System.in);
		String subnet = sc.next();
		new MainMaster(subnet).start();
	}
}
