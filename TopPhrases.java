import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Finds the top 100000 most frequent phrases in a file. The file has 50 phrases per line separated by a pipe (|). Assumes that the phrases do not contain pipe.
 * @author Naveen Kumar
 *
 */
public class TopPhrases {
	
	private static final String DELIMITER = "|";
	private static final String TAB = "\t";
	private static final int LIMIT = 100000;

	/**
	 * Build a map with occurrences of a phrase in the file.
	 * @param inputFilePath
	 * @return Map
	 */
	private static Map<String, Integer> buildMap(String inputFilePath) {
		Map<String, Integer> phrases = new LinkedHashMap<String, Integer>();
		
		try (BufferedReader  bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath)))) {
			
			String line = null;
			
			while ((line = bufferedReader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, DELIMITER);
				
				while (tokenizer.hasMoreTokens()) {
					String phrase = tokenizer.nextToken();
					
					if(phrases.containsKey(phrase)) {
						phrases.put(phrase, phrases.get(phrase) + 1);
					} else {
						phrases.put(phrase, 1);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return phrases;
	}
	
	/**
	 * Builds a min heap based on the occurrences of a phrase.
	 * @param phrases
	 * @param limit
	 * @return
	 */
	private static PriorityQueue<Entry<String, Integer>> buildMinHeap(Map<String, Integer> phrases, int limit) {
		
		// Head of the heap should have the lowest occurrence value
		Comparator<Entry<String, Integer>> comparator = (e1, e2) -> e1.getValue().compareTo(e2.getValue());
		
		PriorityQueue<Entry<String, Integer>> minHeap = new PriorityQueue<Map.Entry<String,Integer>>(limit, comparator);
		
		Set<Entry<String, Integer>> entrySet = phrases.entrySet();
		
		for(Entry<String, Integer> entry: entrySet) {
			addToMinHeap(minHeap, entry, limit);
		}
		
		return minHeap;
	}
	
	/**
	 * Adds an element to min heap
	 * @param minHeap
	 * @param entry
	 * @param limit
	 */
	private static void addToMinHeap(PriorityQueue<Entry<String, Integer>> minHeap, Entry<String, Integer> entry, int limit) {

			// If limit is reached and the head of the min heap has an entry with lower occurrence then current entry, replace the head with current entry.
			if (minHeap.size() == limit) {
				Entry<String, Integer> headEntry = minHeap.peek();
				if (minHeap.comparator().compare(headEntry, entry) < 0) {
					minHeap.poll();
					minHeap.add(entry);
				}
			} else {
				minHeap.add(entry);
			}
	}
	
	/**
	 * Writes the output to the file
	 * @param minHeap
	 * @param outputFilePath
	 */
	private static void writeOutputToFile(PriorityQueue<Entry<String, Integer>> minHeap, String outputFilePath) {
		
		// Linked list is used here to maintain order on inserting from the min heap.
		LinkedList<Entry<String, Integer>> entries = new LinkedList<Map.Entry<String,Integer>>();
		
		while(!minHeap.isEmpty()) {
			Entry<String, Integer> entry = minHeap.poll();
			entries.addFirst(entry);
		}
		
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath)))) {
			
			for(Entry<String, Integer> entry: entries) {
				writer.write(entry.getKey() + TAB + entry.getValue());
				writer.newLine();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Finds the most frequent phrases in a file.
	 * @param inputFilePath
	 * @param outputFilePath
	 * @param count
	 */
	public static void findTopPhrases(String inputFilePath, String outputFilePath, int limit) {
		
		// Runs in O(n) time
		// Takes O(n) time to construct the map
		Map<String, Integer> phrases = buildMap(inputFilePath);
		
		// Takes less than O(n) to construct a min heap
		PriorityQueue<Entry<String, Integer>> minHeap = buildMinHeap(phrases, limit);
		
		writeOutputToFile(minHeap, outputFilePath);
		
	}
	
	public static void main(String[] args) {
		findTopPhrases("D:\\input.txt", "D:\\output.txt", LIMIT);
	}
}
