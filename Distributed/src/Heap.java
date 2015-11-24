import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Heap {

	ArrayList<String> alist;
	static Pattern pattern;
	static Matcher match;

	public Heap(ArrayList<String> alist) {
		this.alist = alist;
		pattern = Pattern.compile("[a-zA-Z]");
	}

	/* Get the Small value between the two string */
	public static String getSmallVal(String a , String b ) {
		match = pattern.matcher(a);
		int count =0;
		while(match.find()) {
			count++;
		}
		String sa_1 = a.substring(0, count);
		String sa_2 = a.substring(count);

		match = pattern.matcher(b);
		count = 0;
		while(match.find()) {
			count++;
		}
		String sb_1 = b.substring(0, count);
		String sb_2 = b.substring(count);

		int sa_int = Integer.parseInt(sa_2);
		int sb_int = Integer.parseInt(sb_2);

		if(sa_1.compareTo(sb_1) < 0) {
			return a;
		} else if (sa_1.compareTo(sb_1) > 0) {
			return b;
		} else {
			if(sa_int < sb_int) {
				return a;
			} else {
				return b;
			}
		}
	}
	
	
	/* Get the Small value between the two string */
	public static int myComparator(String a , String b) {
		match = pattern.matcher(a);
		int count =0;
		while(match.find()) {
			count++;
		}
		String sa_1 = a.substring(0, count);
		String sa_2 = a.substring(count);

		match = pattern.matcher(b);
		count = 0;
		while(match.find()) {
			count++;
		}
		String sb_1 = b.substring(0, count);
		String sb_2 = b.substring(count);

		int sa_int = Integer.parseInt(sa_2);
		int sb_int = Integer.parseInt(sb_2);

		if (sa_1.compareTo(sb_1) < 0) {
			return -1;
		} else if (sa_1.compareTo(sb_1) > 0) {
			return 1;
		} else {
			if(sa_int < sb_int) {
				return -1;
			} else if(sa_int > sb_int){
				return 1;
			} else {
				return 0;
			}
		}
	}

	/*Check if parent value is greater than the child node.*/
	public boolean checkParentChild(String a, String b) {

		match = pattern.matcher(a);
		int count = 0;

		while(match.find()) {
			count++;
		}
		String sa_1 = a.substring(0,count);
		String sa_2 = a.substring(count);

		match = pattern.matcher(b);
		count = 0;
		while(match.find()) {
			count++;
		}
		String sb_1 = b.substring(0,count);
		String sb_2 = b.substring(count);

		int sa_int = Integer.parseInt(sa_2);
		int sb_int = Integer.parseInt(sb_2);


		if (sa_1.compareTo(sb_1) < 0) {
			return true;
		} else if (sa_1.compareTo(sb_1) > 0) {
			return false;
		} else {
			if(sa_int<sb_int) {
				return true;
			} else {
				return false;
			}
		}
	}

	/* This procedure adjusts the heap and gets the minimum element at the top*/
	public void Heapify(int i , int length) {

		if (2*i > length && (2*i+1) > length ) {
			return ;
		} else {
			String smallVal=null;
			int leftindex = 2*i ;
			int rightindex = 2*i + 1;
			String leftVal;
			String rightVal;
			String middleVal;
			String temp;

			if(leftindex<=length && rightindex<=length) {	
				middleVal = alist.get(i);
				leftVal = alist.get(leftindex);
				rightVal = alist.get(rightindex); 
				smallVal = getSmallVal(leftVal,rightVal);

				if(!(checkParentChild(middleVal,smallVal))) {

					if(smallVal.equals(leftVal)) {
						temp = alist.get(i);
						alist.set(i, leftVal);
						alist.set(leftindex,temp);
						Heapify(leftindex,length);
					}
					else if(smallVal.equals(rightVal)) {
						temp = alist.get(i);
						alist.set(i,rightVal);
						alist.set(rightindex,temp);
						Heapify(rightindex,length);
					}
				}
			} else if(leftindex<=length) {
				middleVal = alist.get(i);
				leftVal = alist.get(leftindex);
				
				if(!(checkParentChild(middleVal,leftVal))) {
					temp = alist.get(i);
					alist.set(i, leftVal);
					alist.set(leftindex,temp);
					Heapify(leftindex,length);
				}
			}

			else if (rightindex <= length) {
				middleVal = alist.get(i);
				rightVal = alist.get(rightindex);
				
				if(!(checkParentChild(middleVal,rightVal))) {
					temp = alist.get(i);
					alist.set(i,rightVal);
					alist.set(rightindex,temp);
					Heapify(rightindex,length);
				}
			}
		}
	}

	/*To sort the heap*/
	public ArrayList<String> HeapSort() {

		String temp = null;
		String temp2 = null;
		int counter = 1;
		int length = alist.size();

		for (int i = length/2; i>=1; i--) {
			Heapify(i,length-1);
		}
		
		int index = 1;
		counter = 1;
		
		for(int i = 1; i < length-1 ; i++) {
			index = length-counter;
			temp = alist.get(1);
			temp2  = alist.get(index);
			//alist.set(length-counter, temp);
			alist.set(1,temp2);
			alist.set(index,temp);
			Heapify(1,length-(i+1)); // Deleting the end nodes.
			counter++;
		}

		//Reverse the list now
		String tempval;
		counter = 1;
		
		/* Reverse the array to get the sorted output*/
		for (int j = alist.size()-1 ; j > length/2 ; j--) {
			tempval = alist.get(j);
			alist.set(j,alist.get(counter));
			alist.set(counter,tempval);
			counter++;
		}
		return alist;
	}

}
