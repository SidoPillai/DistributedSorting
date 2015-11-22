import java.io.IOException;
import java.util.Scanner;

public class DistributedSort {

	public static void main(String[] args) throws IOException {
		
		System.out.println("Enter the Subnet");
		Scanner sc = new Scanner(System.in);
		String subnet = sc.next();
		
		new MainMaster(subnet).start();
		
	}
}
