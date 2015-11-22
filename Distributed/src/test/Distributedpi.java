package test;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Distributedpi {
	

	public static void main(String args[]) throws IOException
	{

		File file = new File("/home/suhaspillai/javaprograms/TopicsDistributed/src/new_dataset_10000.txt");
		FileInputStream f= new FileInputStream(file);
		int SIZE = (int)file.length();
		byte[] barray = new byte[SIZE];
		FileChannel ch = f.getChannel();
		MappedByteBuffer mb = ch.map(FileChannel.MapMode.READ_ONLY,0L, ch.size());
		int i = 0;
		while(mb.hasRemaining())
		{
			barray[i]=mb.get();
			i++;	
		}
		ArrayList<String> valToSort = new ArrayList<String>(); 
		String	str = new String (barray);
		Scanner s = new Scanner(str);
		while (s.hasNext())
		{
			valToSort.add(s.next());
		}


		Heap heap = new Heap(valToSort);
		heap.HeapSort();
		for(int i1 = 1 ; i1 < valToSort.size(); i1++)
		{
			System.out.println(valToSort.get(i1));
		}

	}

}
