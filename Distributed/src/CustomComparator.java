import java.util.Comparator;

public class CustomComparator implements Comparator<String> {
	public int compare(String s1, String s2) {
		if(s1.length() == s2.length())
		{
			return s1.compareTo(s2);
		}
		else
		{
			if(s1.charAt(0) == s2.charAt(0))
			{
				return s1.length()-s2.length();
			}
			else
			{
				return s1.compareTo(s2);
			}
		}
	}
}