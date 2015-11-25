import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;

public class Task_2 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		File my_file = new File("new_dataset_tiny.txt"); // file to read values
															// from

		FileInputStream fs = new FileInputStream(my_file);

		Scanner sc = new Scanner(fs);

		HashMap<String, Integer> sum_map = new HashMap<String, Integer>(); // map
																			// containing
																			// the
																			// sum
																			// of
																			// unique
																			// alphabets

		HashMap<String, Integer> count_map = new HashMap<String, Integer>();// map
																			// containing
																			// count
																			// of
																			// unique
																			// alphabet
																			// values

		TreeSet<String> contains_set = new TreeSet<String>(); // set containing
																// already seen
																// elements

		for (int i = 65; i < 91; i++) {

			sum_map.put(String.valueOf((char) i), 0);
			count_map.put(String.valueOf((char) i), 0);

		}

		while (sc.hasNext()) {

			// System.out.println(sc.next());

			String s = sc.next();

			if (contains_set.contains(s)) {

				// skip duplicates

			} else {
				String letter = s.substring(0, 1).toUpperCase();

				Integer num = Integer.parseInt(s.substring(1));

				if (sum_map.containsKey(letter)) {
					Integer val = sum_map.get(letter);
					sum_map.put(letter, val + num);
				} else {
					sum_map.put(letter, num);
				}

				if (count_map.containsKey(letter)) {
					Integer count = count_map.get(letter);
					count_map.put(letter, ++count);
				} else {
					count_map.put(letter, 1);
				}

			}

			contains_set.add(s);

		}

		File task2 = new File("task2.txt"); // file to write values into

		BufferedWriter file_out = new BufferedWriter(new FileWriter(task2));

		for (int i = 65; i < 91; i++) {

			int val = count_map.get(String.valueOf((char) i));

			if (val > 0) {
				file_out.write((char) i + " " + val + " "
						+ sum_map.get(String.valueOf((char) i)) + "\n");
			}

		}
		file_out.close();
		sc.close();
	}

}
