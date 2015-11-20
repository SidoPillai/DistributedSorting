import java.util.ArrayDeque;
import java.util.Queue;

public class BucketSortExamples {

	public static final int numBuckets = 10;

	static int[] sort(int[] sequence, int maxValue) {
		// Bucket Sort
		int[] Bucket = new int[maxValue + 1];
		int[] sorted_sequence = new int[sequence.length];

		for (int i = 0; i < sequence.length; i++)
			Bucket[sequence[i]]++;

		int outPos = 0;
		for (int i = 0; i < Bucket.length; i++)
			for (int j = 0; j < Bucket[i]; j++)
				sorted_sequence[outPos++] = i;

		return sorted_sequence;
	}

	static void printSequence(int[] sorted_sequence) {
		for (int i = 0; i < sorted_sequence.length; i++)
			System.out.print(sorted_sequence[i] + " ");
	}

	static int maxValue(int[] sequence) {
		int maxValue = 0;
		for (int i = 0; i < sequence.length; i++)
			if (sequence[i] > maxValue)
				maxValue = sequence[i];
		return maxValue;
	}

	// recursive helper
	@SuppressWarnings("unchecked")
	static Queue<Integer> bucketSort(Queue<Integer> a, int min, int max, int[] array) {
		// initialize buckets
		Queue<Integer>[] buckets = new Queue[numBuckets];
		for (int i = 0; i < numBuckets; i++)
			buckets[i] = new ArrayDeque<Integer>();

		// fill buckets (scattering)
		int range = max - min + 1;
		for (Integer i : a) {
			buckets[(i - min) * numBuckets / range].add(i);
		}

		for (int i = 0; i < numBuckets; i++) {
			if (buckets[i].size() > 1 && range > 1)
				buckets[i] = bucketSort(buckets[i], i * range / numBuckets + min, (i + 1) * range / numBuckets + min, array);
		}

		// gather into a new queue
		Queue<Integer> result = new ArrayDeque<Integer>();
		for (Queue <Integer> q : buckets)
			for (Integer i : q)
				result.add(i);

		return result;
	}
	
	static void sort(int[] a) {
        // find the maximum and the minimum of the array
        int min = a[0], max = min;
        Queue<Integer> result = new ArrayDeque<Integer>();
        for (int i = 0; i < a.length; i++) {
            if (a[i] < min)
                min = a[i];
            if (a[i] > max) {
                max = a[i];
            }
            result.add(a[i]);
        }
        result = bucketSort(result, min, max, a);

        // put the queue into the original array
        for (int i = 0; i < a.length; i++)
            a[i] = result.remove();
    }


	public static void main(String args[]) {
//		System.out
//		.println("Sorting of randomly generated numbers using BUCKET SORT");
//		Random random = new Random();
//		int N = 20;
//		int[] sequence = new int[N];
//
//		for (int i = 0; i < N; i++)
//			sequence[i] = Math.abs(random.nextInt(100));
//
//		int maxValue = maxValue(sequence);
//
//		System.out.println("\nOriginal Sequence: ");
//		printSequence(sequence);
//
//		System.out.println("\nSorted Sequence: ");
//		printSequence(sort(sequence, maxValue));
		
		System.out.println();
		
		int[] arr = new int[1000];
        for (int i = 0; i < arr.length; i++)
        arr[i] = (int) (Math.random() * arr.length);

        for (int i = 0; i < arr.length; i++) {
            if (i != arr.length - 1)
                System.out.print(arr[i] + ", ");
            else
                System.out.print(arr[i]);
        }

        System.out.println();

        sort(arr);

        for (int i = 0; i < arr.length; i++) {
            if (i != arr.length - 1)
                System.out.print(arr[i] + ", ");
            else
                System.out.print(arr[i]);
        }
	}
}
