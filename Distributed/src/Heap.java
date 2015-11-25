import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Heap {

	ArrayList<String> alist;
	static Pattern pattern;
	static Matcher match;

	public Heap(ArrayList<String> alist) {
		this.alist = alist;
		pattern = Pattern.compile("[a-zA-Z]+");
	}

	/* Get the Small value between the two string */
	public static String getSmallVal(String a , String b ) {

		System.out.println("Value of a is :     "+a+"  "+"value of b is      :"+b);
		pattern = Pattern.compile("[a-zA-Z]");
//		String a = "v153";
//		String b = "s6248";
		int a_first = (int)a.charAt(0);
		int b_first = (int)b.charAt(0);
		String sa_1;
		String sa_2;
		String sb_1;
		String sb_2;
		int count;
		System.out.println(a_first);
		if(48<=a_first && a_first<=57)
		{
		//	System.out.println("inside ascii");
			sa_1="";
			sa_2 = a;
		}
		else
		{
			match = pattern.matcher(a);
			count=0;
			while(match.find()) {
				count++;
			}

			sa_1 = a.substring(0, count);
			sa_2 = a.substring(count);

		}

		if(48<=b_first && b_first<=57)
		{
		//	System.out.println("inside ascii");
			sb_1="";
			sb_2 = b;
		}
		else

		{
			match = pattern.matcher(b);
			count = 0;
			while(match.find()) {
				count++;
			}
			sb_1 = b.substring(0, count);
			sb_2 = b.substring(count);	
		}
		
		

		String val = null;
		if(a.compareTo(b)==0)
		{
			//	System.out.println("exactly same");
			val = a;
		}
		else
		{
			if(sa_1!=null && sb_1!=null)
			{
				if(sa_1.compareTo(sb_1) < 0) {
						//		System.out.println("sa_1 less than sb_1");
					val = a;

				} else if (sa_1.compareTo(sb_1) > 0) {
					//	System.out.println("sa_1 greater than sb_1");
					val = b;
				}

				else {
					//	System.out.println("sa_2 value"+sa_2+"    "+"Value of sb_2"+sb_2);
					if(!sa_2.equals("") && !sb_2.equals(""))
					{
						int sa_int = Integer.parseInt(sa_2);
						int sb_int = Integer.parseInt(sb_2);

						if(sa_int < sb_int) {
							//	System.out.println("Comparing integers sa_int less that sb_int");
							val = a;	
						} 
						else {
							//	System.out.println("sa_int is greater that sb_int");
							val = b;
						}

					}
					else
					{
						if(sa_2.equals(""))
						{
							//	int sb_int = Integer.parseInt(sb_2);
							//	System.out.println("b is greater");
							val = a;
						}
						else
						{
							//	System.out.println("a is greater");
							val=b;
						}
					}	
				}		
			}

		}	
		return val;
		/*
		int a_first = (int)a.charAt(0);
		int b_first = (int)b.charAt(0);
		String sa_1;
		String sa_2;
		String sb_1;
		String sb_2;
		int count;
		System.out.println(a_first);
		if(48<=a_first && a_first<=57)
		{
			System.out.println("inside ascii");
			sa_1="";
			sa_2 = a;
		}
		else
		{
			match = pattern.matcher(a);
			count=0;
			while(match.find()) {
				count++;
			}

			sa_1 = a.substring(0, count);
			sa_2 = a.substring(count);

		}

		if(48<=b_first && b_first<=57)
		{
			System.out.println("inside ascii");
			sb_1="";
			sb_2 = b;
		}
		else

		{
			match = pattern.matcher(b);
			count = 0;
			while(match.find()) {
				count++;
			}
			sb_1 = b.substring(0, count);
			sb_2 = b.substring(count);	
		}



		String val = null;
		if(a.compareTo(b)==0)
		{
			System.out.println("exactly same");
			val = a;
		}
		else
		{
			if(sa_1!=null && sb_1!=null)
			{
				if(sa_1.compareTo(sb_1) < 0) {
					System.out.println("sa_1 less than sb_1");
					val = a;

				} else if (sa_1.compareTo(sb_1) > 0) {
					System.out.println("sa_1 greater than sb_1");
					val = b;
				}

				else {
					System.out.println("sa_2 value"+sa_2+"    "+"Value of sb_2"+sb_2);
					if(!sa_2.equals("") && !sb_2.equals(""))
					{
						int sa_int = Integer.parseInt(sa_2);
						int sb_int = Integer.parseInt(sb_2);

						if(sa_int < sb_int) {
							System.out.println("Comparing integers sa_int less that sb_int");
							val = a;	
						} 
						else {
							System.out.println("sa_int is greater that sb_int");
							val = b;
						}

					}
					else
					{
						if(sa_2.equals(""))
						{
							//	int sb_int = Integer.parseInt(sb_2);
							System.out.println("b is greater");
							val = a;
						}
						else
						{
							System.out.println("a is greater");
							val=b;
						}
					}	
				}		
			}

		}
		return val;
		/*
		System.out.println("VALUE OF A----" + a);
		System.out.println("VALUE OF B----" + b);

		match = pattern.matcher(a);
		int count = 0;
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
		 */
	}


	/* Get the Small value between the two string */
	public static int myComparator(String a , String b) {
		pattern = Pattern.compile("[a-zA-Z]");
		System.out.println("Value of a is :     "+a+"  "+"value of b is      :"+b);
		int a_first = (int)a.charAt(0);
		int b_first = (int)b.charAt(0);
		String sa_1;
		String sa_2;
		String sb_1;
		String sb_2;
		int count;
		System.out.println(a_first);
		if(48<=a_first && a_first<=57)
		{
		//	System.out.println("inside ascii");
			sa_1="";
			sa_2 = a;
		}
		else
		{
		//	System.out.println("Value of a:"+ a+"Lenghth"+a.length());
			match = pattern.matcher(a);
			count=0;
			while(match.find()) {
				count++;
			}

			sa_1 = a.substring(0, count);
			sa_2 = a.substring(count);

		}

		if(48<=b_first && b_first<=57)
		{
			System.out.println("inside ascii");
			sb_1="";
			sb_2 = b;
		}
		else

		{
		//	System.out.println("Value of b:"+ b+"Lenghth"+b.length());
			match = pattern.matcher(b);
			count = 0;
			while(match.find()) {
				count++;
			}
			sb_1 = b.substring(0, count);
			sb_2 = b.substring(count);	
		}

		int val = 0 ;
		if(a.compareTo(b)==0)
		{
		//	System.out.println("exactly same");
			val = 0;
		}
		else
		{
			if(sa_1!=null && sb_1!=null)
			{
				if(sa_1.compareTo(sb_1) < 0) {
			//		System.out.println("sa_1 less than sb_1");
					val = -1;

				} else if (sa_1.compareTo(sb_1) > 0) {
			//		System.out.println("sa_1 greater than sb_1");
					val = 1;
				}

				else {
			//		System.out.println("sa_2 value"+sa_2+"    "+"Value of sb_2"+sb_2);
					if(!sa_2.equals("") && !sb_2.equals(""))
					{
						int sa_int = Integer.parseInt(sa_2);
						int sb_int = Integer.parseInt(sb_2);

						if(sa_int < sb_int) {
				//			System.out.println("Comparing integers sa_int less that sb_int");
							val = -1;	
						} 
						else {
				//			System.out.println("sa_int is greater that sb_int");
							val = 1;
						}

					}
					else
					{
						if(sa_2.equals(""))
						{
							//	int sb_int = Integer.parseInt(sb_2);
							System.out.println("b is greater");
							val = -1;
						}
						else
						{
					//		System.out.println("a is greater");
							val=1;
						}
					}	
				}		
			}

		}
		return val;

		/*

		System.out.println("VALUE OF A----" + a);
		System.out.println("VALUE OF B----" + b);
		match = pattern.matcher(a);
		int count = 0;
		int val=0;
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

		if(sa_1!=null && sb_1!=null)
		{
			if (sa_1.compareTo(sb_1) < 0) {
				//	return -1;
				val = -1;
			} else if (sa_1.compareTo(sb_1) > 0) {
				//	return 1;
				val = 1;
			}
		}	
	//		else

			//return val;	
		else {
			if(sa_int < sb_int) {
				//	return -1;
				val=-1;
			} else if(sa_int > sb_int){
				//	return 1;
				val=1;

			} else {
			//	return 0;
				val=0;
			}
	//	return val;	
		}
		return val;

		 */	
	}

	/*Check if parent value is greater than the child node.*/
	public boolean checkParentChild(String a, String b) {
		pattern = Pattern.compile("[a-zA-Z]");
		
		System.out.println("Value of a is :     "+a+"  "+"value of b is      :"+b);
		int a_first = (int)a.charAt(0);
		int b_first = (int)b.charAt(0);
		String sa_1;
		String sa_2;
		String sb_1;
		String sb_2;
		int count;
		System.out.println(a_first);
		if(48<=a_first && a_first<=57)
		{
			System.out.println("inside ascii");
			sa_1="";
			sa_2 = a;
		}
		else
		{
			match = pattern.matcher(a);
			count=0;
			while(match.find()) {
				count++;
			}

			sa_1 = a.substring(0, count);
			sa_2 = a.substring(count);

		}

		if(48<=b_first && b_first<=57)
		{
			System.out.println("inside ascii");
			sb_1="";
			sb_2 = b;
		}
		else

		{
			match = pattern.matcher(b);
			count = 0;
			while(match.find()) {
				count++;
			}
			sb_1 = b.substring(0, count);
			sb_2 = b.substring(count);	
		}
	
		boolean val = true;
		if(a.compareTo(b)==0)
		{
			//	System.out.println("exactly same");
			val = true;
		}
		else
		{
			if(sa_1!=null && sb_1!=null)
			{
				if(sa_1.compareTo(sb_1) < 0) {
					//			System.out.println("sa_1 less than sb_1");
					val = true;

				} else if (sa_1.compareTo(sb_1) > 0) {
					//	System.out.println("sa_1 greater than sb_1");
					val = false;
				}

				else {
					//	System.out.println("sa_2 value"+sa_2+"    "+"Value of sb_2"+sb_2);
					if(!sa_2.equals("") && !sb_2.equals(""))
					{
						int sa_int = Integer.parseInt(sa_2);
						int sb_int = Integer.parseInt(sb_2);

						if(sa_int < sb_int) {
							//	System.out.println("Comparing integers sa_int less that sb_int");
							val = true;	
						} 
						else {
							//	System.out.println("sa_int is greater that sb_int");
							val = false;
						}

					}
					else
					{
						if(sa_2.equals(""))
						{
							//	int sb_int = Integer.parseInt(sb_2);
							//	System.out.println("b is greater");
							val = true;
						}
						else
						{
							//	System.out.println("a is greater");
							val=false;
						}
					}	
				}		
			}

		}
		return val;



		/*
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
		 */
	}

	/* This procedure adjusts the heap and gets the minimum element at the top*/
	public void Heapify(int i , int length) {

		if (2*i > length && (2*i+1) > length ) {
			return ;
		} else {
			String smallVal = null;
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

		for (int i = length/2; i >= 1; i--) {
			Heapify(i,length-1);
		}

		int index = 1;
		counter = 1;

		for(int i = 1; i < length-1 ; i++) {
			index = length-counter;
			temp = alist.get(1);
			temp2  = alist.get(index);
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