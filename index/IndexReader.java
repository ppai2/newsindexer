/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.*;

/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {
	
	public final Logger logger = Logger.getLogger(IndexReader.class.getName());
	String indexDir;
	IndexType type;
	HashMap<Integer, ArrayList<Integer>> index = new HashMap<Integer, ArrayList<Integer>>();	// TermId, ArrayList<Integer> DocIds
	HashMap<Integer, Integer> frequency = new HashMap<Integer, Integer>();	// TermId, Frequency
	HashMap<String, HashMap<Integer, Integer>> termDocFrequency = new HashMap<String, HashMap<Integer, Integer>>();	// Term, HashMap<DocId, Frequency>
	HashMap<String, Integer> dictionary = new HashMap<String, Integer>();	// Term, TermId
	HashMap<Integer, String> invertedDictionary = new HashMap<Integer, String>();	// TermId, Term
	HashMap<String, Integer> docDictionary = new HashMap<String, Integer>();	// FileId, DocId
	HashMap<Integer, String> invertedDocDictionary = new HashMap<Integer, String>();	// DocId, FileId
	HashMap<Integer, Integer> docLength = new HashMap<Integer, Integer>();	// DocId, DocLength (in number of words)
	
	/**
	 * Default constructor
	 * @param indexDir : The root directory from which the index is to be read.
	 * This will be exactly the same directory as passed on IndexWriter. In case 
	 * you make subdirectories etc., you will have to handle it accordingly.
	 * @param type The {@link IndexType} to read from
	 */
	public IndexReader(String indexDir, IndexType type) {
		//TODO
		this.indexDir = indexDir;
		this.type = type;
		
		try {
			FileInputStream indexFileInput;
			FileInputStream freqFileInput;
			FileInputStream dictionaryFileInput;
			FileInputStream termDocFreqFileInput;
			FileInputStream docDictionaryFileInput;
			FileInputStream docLengthFileInput;
			switch (type) {
				case TERM:
					indexFileInput = new FileInputStream(indexDir + File.separator + "index_term");
					freqFileInput = new FileInputStream(indexDir + File.separator + "frequency_term");
					dictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_term");
					docDictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_doc");
					termDocFreqFileInput = new FileInputStream(indexDir + File.separator + "frequency_termdoc");
					docLengthFileInput = new FileInputStream(indexDir + File.separator + "length_doc");
				case AUTHOR:
					indexFileInput = new FileInputStream(indexDir + File.separator + "index_author");
					freqFileInput = new FileInputStream(indexDir + File.separator + "frequency_author");
					dictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_author");
					docDictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_doc");
					termDocFreqFileInput = new FileInputStream(indexDir + File.separator + "frequency_authordoc");
					docLengthFileInput = new FileInputStream(indexDir + File.separator + "length_doc");
				case CATEGORY:
					indexFileInput = new FileInputStream(indexDir + File.separator + "index_category");
					freqFileInput = new FileInputStream(indexDir + File.separator + "frequency_category");
					dictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_category");
					docDictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_doc");
					termDocFreqFileInput = new FileInputStream(indexDir + File.separator + "frequency_categorydoc");
					docLengthFileInput = new FileInputStream(indexDir + File.separator + "length_doc");
				case PLACE:
					indexFileInput = new FileInputStream(indexDir + File.separator + "index_place");
					freqFileInput = new FileInputStream(indexDir + File.separator + "frequency_place");
					dictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_place");
					docDictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_doc");
					termDocFreqFileInput = new FileInputStream(indexDir + File.separator + "frequency_placedoc");
					docLengthFileInput = new FileInputStream(indexDir + File.separator + "length_doc");
				default:
					indexFileInput = new FileInputStream(indexDir + File.separator + "index_term");
					freqFileInput = new FileInputStream(indexDir + File.separator + "frequency_term");
					dictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_term");
					docDictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_doc");
					termDocFreqFileInput = new FileInputStream(indexDir + File.separator + "frequency_termdoc");
					docLengthFileInput = new FileInputStream(indexDir + File.separator + "length_doc");
			}
			
			ObjectInputStream indexObj = new ObjectInputStream(indexFileInput);
			ObjectInputStream freqObj = new ObjectInputStream(freqFileInput);
			ObjectInputStream dictionaryObj = new ObjectInputStream(dictionaryFileInput);
			ObjectInputStream docDictionaryObj = new ObjectInputStream(docDictionaryFileInput);
			ObjectInputStream termDocFreqObj = new ObjectInputStream(termDocFreqFileInput);
			ObjectInputStream docLengthObj = new ObjectInputStream(docLengthFileInput);
			
			index = (HashMap<Integer, ArrayList<Integer>>)(indexObj.readObject());
			frequency = (HashMap<Integer, Integer>)(freqObj.readObject());
			dictionary = (HashMap<String, Integer>)(dictionaryObj.readObject());
			docDictionary = (HashMap<String, Integer>)(docDictionaryObj.readObject());
			termDocFrequency = (HashMap<String, HashMap<Integer, Integer>>)(termDocFreqObj.readObject());
			docLength = (HashMap<Integer, Integer>)(docLengthObj.readObject());
			for (String key : dictionary.keySet()) {
				int value = dictionary.get(key);
				invertedDictionary.put(value, key);
			}
			for (String key : docDictionary.keySet()) {
				int value = docDictionary.get(key);
				invertedDocDictionary.put(value, key);
			}
			
			indexObj.close();
			freqObj.close();
			dictionaryObj.close();
			docDictionaryObj.close();
			termDocFreqObj.close();
			docLengthObj.close();
			
		} catch (FileNotFoundException fnfe) {
			logger.log(Level.SEVERE, "File Not Found Exception thrown in IndexReader constructor: ", fnfe);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "IOException thrown in IndexReader constructor: ", ioe);
		} catch (ClassNotFoundException cnfe) {
			logger.log(Level.SEVERE, "Class Not Found Exception thrown in IndexReader constructor: ", cnfe);
		}
	}
	
	public HashMap<Integer, HashMap<String, Integer>> getDocFrequency(String term) {
		HashMap<Integer, HashMap<String, Integer>> docFrequency = new HashMap<Integer, HashMap<String, Integer>>();
		HashMap<Integer, Integer> df = new HashMap<Integer, Integer>();
		HashMap<String, Integer> tf = new HashMap<String, Integer>();
		
		if (termDocFrequency.containsKey(term)) {
			df = termDocFrequency.get(term);
		}
		
		for (int docId : df.keySet()) {
			HashMap<String, Integer> value = new HashMap<String, Integer>();
			value.put(term, df.get(docId));
			docFrequency.put(docId, value);
		}
		
		return docFrequency;
	}
	
	public int getDocListSize(String term) {
		return index.get(term).size();
	}
	
	public int getTotalDocCount() {
		return docDictionary.size();
	}
	
	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		//TODO : YOU MUST IMPLEMENT THIS
		//return -1;
		return index.size();
	}
	
	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		//TODO: YOU MUST IMPLEMENT THIS
		//return -1;
		return docDictionary.size();
	}
	
	/**
	 * Method to get the postings for a given term. You can assume that
	 * the raw string that is used to query would be passed through the same
	 * Analyzer as the original field would have been.
	 * @param term : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the 
	 * number of occurrences as values if the given term was found, null otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		//TODO:YOU MUST IMPLEMENT THIS
		Map<String, Integer> postingsMap = new HashMap<String, Integer>();	// FileId, TermFreq in FileId
		HashMap<Integer, Integer> docFreq = null;	// DocId, Frequency
		
		if (termDocFrequency.containsKey(term)) {
			docFreq = termDocFrequency.get(term);
			for (int d : docFreq.keySet()) {
				String fileName = invertedDocDictionary.get(d);
				int f = docFreq.get(d);
				postingsMap.put(fileName, f);
			}
		}
		
		if (docFreq != null)
			return postingsMap;
		else
			return null;
	}
	
	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * @param k : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values
	 * null for invalid k values
	 */
	public List<String> getTopK(int k) {
		//TODO YOU MUST IMPLEMENT THIS
		FrequencyComparator fc = new FrequencyComparator(frequency);
		TreeMap<Integer, Integer> freqSortedMap = new TreeMap<Integer, Integer>(fc);
		List<Integer> topKIds = new ArrayList<Integer>();
		List<String> topKTerms = new ArrayList<String>();
		
		freqSortedMap.putAll(frequency);
		
		for (int key : freqSortedMap.keySet()) {
			topKIds.add(key);
		}
		
		if (topKIds.size() > 0 && topKIds.size() < k)
			topKIds = topKIds.subList(0, topKIds.size()-1);
		else if (k > 0)
			topKIds = topKIds.subList(0, k);
		else
			topKIds = null;
		
		if (topKIds != null) {
			for (int id : topKIds) {
				if (invertedDictionary.containsKey(id)) {
					topKTerms.add(invertedDictionary.get(id));
				}
			}
			while (topKTerms.size() < k) {
				topKTerms.add(null);
			}
		} else {
			topKTerms = null;
		}
		
		return topKTerms;
	}
	
	/**
	 * Method to implement a simple boolean AND query on the given index
	 * @param terms The ordered set of terms to AND, similar to getPostings()
	 * the terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key 
	 * and number of occurrences as the value, the number of occurrences 
	 * would be the sum of occurrences for each participating term. return null
	 * if the given term list returns no results
	 * BONUS ONLY
	 */
	public Map<String, Integer> query(String...terms) {
		//TODO : BONUS ONLY
		Map<String, Integer> termMap = new HashMap<String, Integer>();
		ArrayList<String> commonFileIds = new ArrayList<String>();
		ArrayList<String> removeFileIds = new ArrayList<String>();
		
		for (String t : terms) {
			Map<String, Integer> postingsMap = getPostings(t);
			
			if (commonFileIds.isEmpty()) {
				for (String f : postingsMap.keySet()) {
					commonFileIds.add(f);
					int freq = postingsMap.get(f);
					termMap.put(f, freq);
				}
				for (String r : removeFileIds) {
					termMap.remove(r);
					commonFileIds.remove(r);
				}
				
			} else {
				for (String f : commonFileIds) {
					if (postingsMap.keySet().contains(f)) {
						if (termMap.containsKey(f)) {
							int freq = termMap.get(f);
							freq = freq + postingsMap.get(f);
							termMap.put(f, freq);
						} else {
							int freq = postingsMap.get(f);
							termMap.put(f, freq);
						}
					} else {
						termMap.remove(f);
						removeFileIds.add(f);
					}
				}
				for (String r : removeFileIds) {
					commonFileIds.remove(r);
				}
			}
		}
		
		if (termMap.size() == 0)
			termMap = null;
		
		return termMap;
	}
}
