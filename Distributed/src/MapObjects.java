import java.io.Serializable;
import java.util.HashMap;

public class MapObjects implements Serializable {

	// Sum Map
	HashMap<String, Integer> sum_map = new HashMap<String, Integer>();

	// prefix occurences map
	HashMap<String, Integer> count_map = new HashMap<String, Integer>();

	public MapObjects() {
	
	}
	
	public MapObjects(HashMap<String, Integer> sum_map, HashMap<String, Integer> count_map) {
		this.sum_map = sum_map;
		this.count_map = count_map;
	}
	
}
