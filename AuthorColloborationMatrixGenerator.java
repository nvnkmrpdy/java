import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public class AuthorColloborationMatrixGenerator {
	
	private static final String ARTICLE_TAG_NAME = "Article";
	private static final String ARTICLE_TITLE_TAG_NAME = "ArticleTitle";
	private static final String AUTHOR_TAG_NAME = "Author";
	private static final String COMMA = ",";
	private static final String TAB = "\t";
	
	/**
	 * Generate a map with author as key and a set of his articles as value.
	 * @param xmlFile
	 * @return Map<String, Set<String>>
	 */
	private static Map<String, Set<String>> parseXml(File xmlFile) {
		
		XMLStreamReader xmlStreamReader = null;
		
		// Author is key and set of his articles is value
		Map<String, Set<String>> authorMap = new LinkedHashMap<String, Set<String>>();

		try {
			
			if(!xmlFile.exists()) {
				throw new FileNotFoundException("The input XML file is not found.");
			}
			
			if(!xmlFile.isFile()) {
				throw new IOException("The input is not a valid XML file.");
			}
			
			// Create an XMLStreamReader to parse the XML file.
			xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(xmlFile));
			
			int event = 0;
			
			String articleTitle = null;
			
			while(xmlStreamReader.hasNext()) {
				
				// Move to the next entity in the XML
            	event = xmlStreamReader.next();
				
				switch (event) {
					
					case XMLStreamConstants.START_ELEMENT:
						// Current tag name
						String currentTag = xmlStreamReader.getLocalName();
						
						// Article tag
						if (ARTICLE_TAG_NAME.equals(currentTag)) {
							// Move to Article title
							xmlStreamReader.nextTag();
							String articleTitleTag = xmlStreamReader.getLocalName();
							
							if(!ARTICLE_TITLE_TAG_NAME.equals(articleTitleTag)) {
								throw new XMLSignatureException("Ill formed XML.");
							}
							
							// Store the articleTitle in a tmp. Needed when storing article title in value set of the map.
							articleTitle = xmlStreamReader.getElementText();
							
						} else if (AUTHOR_TAG_NAME.equals(currentTag)) {
							// Author tag
							
							// Move to LastName tag
							xmlStreamReader.nextTag();
							String lastName = xmlStreamReader.getElementText();
							// Move to FirstName tag
							xmlStreamReader.nextTag();
							String firstName = xmlStreamReader.getElementText();
							
							String name = lastName + COMMA + firstName;
							
							// If this author is not already present as key in the map.
							if(!authorMap.containsKey(name)) {
								authorMap.put(name, new HashSet<String>());
							}
							
							// Add the article title to the value of the map.
							authorMap.get(name).add(articleTitle);	
						}
						break;
				}
            	
			}
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(XMLStreamException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(xmlStreamReader != null) {
				try {
					xmlStreamReader.close();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				}
			}
		}
		
		return authorMap;
	}
	
	/**
	 * Generates the matrix from the input author map data.
	 * @param xmlFile
	 */
	public static void generateMatrix(File xmlFile) {
		
		// Author is key and set of his articles is value
		Map<String, Set<String>> authorMap = parseXml(xmlFile);
		
		int authorsCount = authorMap.size();
		
		List<String> authorList = new ArrayList<String>(authorMap.keySet());
		Collections.sort(authorList);
		
		// To store the matrix.
		int sharedArticles[][] = new int[authorsCount][authorsCount];
		
		int i = 0;
		
		for(String authorX: authorList) {
			
			Set<String> authorXArticles = authorMap.get(authorX);
			
			for(int j=0; j<authorsCount; j++) {
				
				if(i == j) {
					// One cannot co-author an article with himself.
					sharedArticles[i][j] = 0;
					continue;
				}
				
				String authorY = authorList.get(j);
				
				if (i > j) {
					// Optimization - When j,i is available, use that value for i,j instead of processing from map again.
					sharedArticles[i][j] = sharedArticles[j][i];
				} else {
					// Find the common articles between the articles list of authorX and authorY
					Set<String> authorYArticles = new HashSet<String>(authorMap.get(authorY));
					authorYArticles.retainAll(authorXArticles);
					
					sharedArticles[i][j] = authorYArticles.size();
				}
			}
			
			i++;
		}
		
		printMatrix(authorList, sharedArticles);
		
	}
	
	/**
	 * Print the formatted matrix.
	 * @param authors
	 * @param sharedArticles
	 */
	private static void printMatrix(List<String> authors, int sharedArticles[][]) {

		System.out.print(TAB);
		for(String author: authors) {
			System.out.print(TAB);
			System.out.print(author);
		}
		
		int authorsCount = authors.size();
		
		for(int i=0; i<authorsCount; i++) {
			System.out.println();
			System.out.print(authors.get(i));
			for(int j=0; j<authorsCount; j++) {
				System.out.print(TAB);
				System.out.print(TAB);
				System.out.print(sharedArticles[i][j]);
			}
		}
		
	}
	
	public static void main(String[] args) {
		generateMatrix(new File("input.xml"));;
	}
	
}
