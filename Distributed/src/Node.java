import java.util.ArrayList;
import java.util.List;

public class Node {

	Address address;
	Master master;
	Heap heap;
	public List<String> data;

	Node(Address address, Master master, ArrayList<String> data) {
		this.address = address;
		this.master = master;
		this.data = data;
		if (this.data != null) heap = new Heap(data);
	}
	
	public void setData(ArrayList<String> data) {
		this.data = data;
	}
	
	public ArrayList<String> sortedList() {
		if (data != null) return heap.HeapSort();
		return null;
	}
	
}
