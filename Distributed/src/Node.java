
public class Node {

	Address address;
	Master master;
	Data data;
	
	Node(Address address, Master master) {
		this.address = address;
		this.master = master;
	}
	
	public void setData(Data data) {
		this.data = data;
	}
	
}
