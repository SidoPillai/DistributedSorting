import java.util.ArrayList;
import java.util.regex.Pattern;

public class QuickSort {

	static Pattern pattern;
	
	private final static boolean isDigit(char ch) {
        return ch >= 48 && ch <= 57;
    }

    /** Length of string is passed in for improved efficiency (only need to calculate it once) **/
    private final static String getChunk(String s, int slength, int marker) {
        StringBuilder chunk = new StringBuilder();
        char c = s.charAt(marker);
        chunk.append(c);
        marker++;
        if (isDigit(c)) {
            while (marker < slength) {
                c = s.charAt(marker);
                if (!isDigit(c))
                    break;
                chunk.append(c);
                marker++;
            }
        } else {
            while (marker < slength) {
                c = s.charAt(marker);
                if (isDigit(c))
                    break;
                chunk.append(c);
                marker++;
            }
        }
        return chunk.toString();
    }

    public static int compare(String s1, String s2) {
        int thisMarker = 0;
        int thatMarker = 0;
        int s1Length = s1.length();
        int s2Length = s2.length();

        while (thisMarker < s1Length && thatMarker < s2Length) {
            String thisChunk = getChunk(s1, s1Length, thisMarker);
            thisMarker += thisChunk.length();

            String thatChunk = getChunk(s2, s2Length, thatMarker);
            thatMarker += thatChunk.length();

            // If both chunks contain numeric characters, sort them numerically
            int result = 0;
            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
                // Simple chunk comparison by length.
                int thisChunkLength = thisChunk.length();
                result = thisChunkLength - thatChunk.length();
                // If equal, the first different number counts
                if (result == 0) {
                    for (int i = 0; i < thisChunkLength; i++) {
                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
                        if (result != 0) {
                            return result;
                        }
                    }
                }
            } else {
                result = thisChunk.compareTo(thatChunk);
            }

            if (result != 0)
                return result;
        }

        return s1Length - s2Length;
    }

	public static void quickSort(ArrayList<String> alist,int p, int r) {
		int q;

		if (p < r) {
			q = partition(alist,p,r);
			quickSort(alist,p,q-1);
			//if(q>1)
			quickSort(alist,q,r);
		}
	}
	
	public static int partition(ArrayList<String>alist, int p, int r) {
		String pivot = alist.get(r);
		int j=r;
		int i =p;
		String tmp;
		int a=compare(alist.get(j),pivot);
		while(true) {

			while(compare(alist.get(j),pivot)>=1) {
				j=j-1;
			}
			while(compare(alist.get(i),pivot)==-1) {
				i=i+1;
			}

			if(i<j) {
				tmp = alist.get(i);
				alist.set(i, alist.get(j));
				alist.set(j,tmp);
			}
			else {
				return j;
			}
		}	
	}
}
