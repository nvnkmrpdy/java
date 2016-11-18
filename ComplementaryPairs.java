import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains util methods to find K-complementary pairs in an array
 * @author Naveen Kumar
 */
public class ComplementaryPairs {
	
	private static final String OPEN_PARANTHESIS = "(";
	private static final String CLOSE_PARANTHESIS = ")";
	private static final String COMMA = ",";
	private static final String WITH_VALUE = " with value ";
	
	/**
	 * Prints K-complementary phrases in an array for an input k.
	 * @param arr
	 * @param k
	 */
	public static void printComplementaryPairs(int[] arr, int k) {
		Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		
		// Construct the map with the value occurrences
		for (int i=0; i<arr.length; i++) {
			if(!map.containsKey(arr[i])) {
				map.put(arr[i], new ArrayList<Integer>());
			}
			map.get(arr[i]).add(i);
		}
		
		// Based on the occurrences of a value in the array, find its k-complementary pair
		// Runs in avg O(n) time
		// In the worst case scenario where all the elements of the array are of same value, it runs in O(n log n) because of iterating through the occurrence list inside the loop 
		for(int i=0; i<arr.length; i++) {
			int diff = k - arr[i];
			if(map.containsKey(diff)) {
				for(int j: map.get(diff)) {
					if(i != j) {
						printComplementaryPair(i, j, arr[i], arr[j]);
					}
				}
			}
		}
	}
	
	// Print formatted text to the out 
	private static void printComplementaryPair(int i, int j, int iValue, int jValue) {
		StringBuilder sb = new StringBuilder();
		sb.append(OPEN_PARANTHESIS).append(i).append(COMMA).append(j).append(CLOSE_PARANTHESIS);
		sb.append(WITH_VALUE);
		sb.append(OPEN_PARANTHESIS).append(iValue).append(COMMA).append(jValue).append(CLOSE_PARANTHESIS);
		
		System.out.println(sb);
		
	}
	
	public static void main(String[] args) {
		
		int arr1[] = {2, 4, 3, 8, 3};
		System.out.println("Case1 - All positive integers");
		System.out.println("Input array: " + Arrays.toString(arr1));
		System.out.println("K-complementary pairs for k=6");
		printComplementaryPairs(arr1, 6);
		System.out.println();
		
		int arr2[] = {2, 4, 3, 8, 3, -2};
		System.out.println("Case2 - Positive and negative integers");
		System.out.println("Input array: " + Arrays.toString(arr2));
		System.out.println("K-complementary pairs for k=6");
		printComplementaryPairs(arr2, 6);
		System.out.println();
		
		int arr3[]={3, 3, 3};
		System.out.println("Case3 - Array with same value as elements");
		System.out.println("Input array: " + Arrays.toString(arr3));
		System.out.println("K-complementary pairs for k=6");
		printComplementaryPairs(arr3, 6);
		System.out.println();
		
		int arr4[]={3};
		System.out.println("Case4 - Array of length 1");
		System.out.println("Input array: " + Arrays.toString(arr4));
		System.out.println("K-complementary pairs for k=6");
		printComplementaryPairs(arr4, 6);
		System.out.println();
		
		int arr5[]={4, 3, 1};
		System.out.println("Case5 - Array with no K-complementary pair");
		System.out.println("Input array: " + Arrays.toString(arr5));
		System.out.println("K-complementary pairs for k=6");
		printComplementaryPairs(arr5, 6);
		
		int arr6[]={-1, -3, -5, -7};
		System.out.println("Case6 - Array with all negative values");
		System.out.println("Input array: " + Arrays.toString(arr6));
		System.out.println("K-complementary pairs for k=-8");
		printComplementaryPairs(arr6, -8);
	}
	
}
