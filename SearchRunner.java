package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.query.IndexSearcher;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;
import edu.buffalo.cse.irf14.query.QueryParserException;
import edu.buffalo.cse.irf14.query.Scorer;

/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 * @author nikhillo
 *
 */
public class SearchRunner {
	
	public final Logger logger = Logger.getLogger(SearchRunner.class.getName());
	public enum ScoringModel {TFIDF, OKAPI};
	
	String indexDir = "";
	String corpusDir = "";
	
	/**
	 * Default (and only public) constuctor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir, 
			char mode, PrintStream stream) {
		//TODO: IMPLEMENT THIS METHOD
		this.indexDir = indexDir;
		this.corpusDir = corpusDir;
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		//TODO: IMPLEMENT THIS METHOD
		long start = System.currentTimeMillis();
		
		int resultRank = 1;
		
		Query parsedQuery = null;
		try {
			parsedQuery = QueryParser.parse(userQuery, "OR");
		} catch (QueryParserException qpe) {
			logger.log(Level.SEVERE, "QueryParserException thrown while executing query: " + userQuery, qpe);
		}
		HashMap<Integer, HashMap<String, Integer>> queryResult = IndexSearcher.executeQuery(parsedQuery, indexDir);
		Scorer s = new Scorer(model);
		HashMap<Integer, Double> docScore = s.getDocScore(queryResult, indexDir);
		
		long end = System.currentTimeMillis();
		long queryTime = end - start;
		System.out.println("----------------------------------------------------------------------");
		System.out.println("Query - " + userQuery);
		System.out.println("Query Time - " + queryTime + " ms");
		System.out.println("----------------------------------------------------------------------");
		
		for (int docId : docScore.keySet()) {
			Document doc = getDocumentFromDocId(docId);
			String resultTitle = doc.getField(FieldNames.TITLE)[0];
			String content = doc.getField(FieldNames.CONTENT)[0];
			String fileId = doc.getField(FieldNames.FILEID)[0];
			String resultSnippet = "";
			double resultRelevancy = 0.0;
			if (content.length() < 150) {
				resultSnippet = content;
			} else {
				String subContent = content.substring(0, 200);
				int periodIndex = 0;
				if (subContent.contains(".")) {
					periodIndex = subContent.lastIndexOf(".");
					resultSnippet = content.substring(0, periodIndex+1);
				} else {
					resultSnippet = content.substring(0, 150);
				}
			}
			resultRelevancy = docScore.get(docId);
			System.out.println("FileId - " + fileId);
			System.out.println("Result Rank - " + resultRank);
			System.out.println("Result Title - " + resultTitle);
			System.out.println("Result snippet - \n" + resultSnippet);
			System.out.println("Result relevancy - " + resultRelevancy);
			System.out.println("----------------------------------------------------------------------");
			resultRank++;
		}
		
	}
	
	public Document getDocumentFromDocId(int docId) {
		Document d = new Document();
		String fileId = "";
		
		HashMap<Integer, String> docDictionary = new HashMap<Integer, String>();	// DocId, FileId
		try {
			FileInputStream docDictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_doc");
			ObjectInputStream docDictionaryObj = new ObjectInputStream(docDictionaryFileInput);
			HashMap<String, Integer> tempDocDictionary = (HashMap<String, Integer>)(docDictionaryObj.readObject());
			for (String key : tempDocDictionary.keySet()) {
				int value = tempDocDictionary.get(key);
				docDictionary.put(value, key);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception thrown in getting docDictionary in SearchRunner.query (in Q mode): ", e);
		}
		
		fileId = docDictionary.get(docId);
		
		try {
			d = Parser.parse(corpusDir + File.separator + fileId);
		} catch (ParserException pe) {
			logger.log(Level.SEVERE, "ParserException thrown in SearchRunner.getDocumentFromDocId: ", pe);
		}
		
		return d;
	}
	
	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		//TODO: IMPLEMENT THIS METHOD
		InputStream in = null;
		BufferedReader fileReader = null;
		String line = "";
		int lineCount = 0;
		
		int numQueries = 0;
		String queryId = "";
		String query = "";
		HashMap<String, String> queryMap = new HashMap<String, String>();
		
		String outputStr = "";
		File output = new File(indexDir);
		FileOutputStream w;
		ObjectOutputStream out = null;
		try {
			output.createNewFile();
			output.setWritable(true);
			w = new FileOutputStream("output");
			out = new ObjectOutputStream(w);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "IO Exception thrown while creating output file in SearchRunner.query (E mode): ", ioe);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception thrown while creating output file in SearchRunner.query (E mode): ", e);
		}
		
		
		try {
			in = new FileInputStream(queryFile);
		} catch (FileNotFoundException fnfe) {
			logger.log(Level.SEVERE, "File Not Found Exception thrown for queryFile", fnfe);
		}
		
		try {
			fileReader = new BufferedReader(new InputStreamReader(in));
			while((line = fileReader.readLine()) != null) {
				if (lineCount == 0) {
					if (line.contains("=")) {
						String numQ = line.split("=")[1];
						numQueries = Integer.parseInt(numQ);
					}
				} else if (lineCount > 0) {
					if (line.contains(":")) {
						queryId = line.split(":")[0];
						query = line.split(":")[1];
						if (query.startsWith("{") && query.endsWith("}"))
							query = query.substring(1, query.length()-1);
						queryMap.put(queryId, query);
					}
				}
				lineCount++;
			}
			fileReader.close();
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "IO Exception thrown while reading queryFile", ioe);
		}
		
		int resultCount = 0;
		for (String qId : queryMap.keySet()) {
			logger.log(Level.INFO, "Running query: " + qId);
			String userQuery = queryMap.get(qId);
			Query parsedQuery = null;
			try {
				parsedQuery = QueryParser.parse(userQuery, "OR");
			} catch (QueryParserException qpe) {
				logger.log(Level.SEVERE, "QueryParserException thrown while executing query: " + qId, qpe);
			}
			if (parsedQuery != null) {
				resultCount++;
				HashMap<Integer, HashMap<String, Integer>> queryResult = IndexSearcher.executeQuery(parsedQuery, indexDir);
				Scorer s = new Scorer();
				HashMap<Integer, Double> docScore = s.getDocScore(queryResult, indexDir);
				String result = "";
				for (Integer dId : docScore.keySet()) {
					result += dId + "#" + docScore.get(dId) + ", ";
				}
				if (result.length() > 1 && result.endsWith(", ")) {
					result = result.substring(0, result.length()-2);
				}
				result = result.trim();
				result = "{" + result + "}";
				result = qId + ":" + result + "\n";
				outputStr += result;
			}
		}
		if (resultCount <= numQueries) {
			outputStr = "numResults=" + resultCount + "\n" + outputStr;
		} else {
			logger.log(Level.WARNING, "Number of Results is greater than the number of queries in querying in E mode");
		}
		
		if (out != null) {
			try {
				out.writeChars(outputStr);
				out.close();
			} catch (IOException ioe) {
				logger.log(Level.SEVERE, "IO Exception thrown in writing query output to file: " + ioe);
			}
		}
	}
	
	/**
	 * General cleanup method
	 */
	public void close() {
		//TODO : IMPLEMENT THIS METHOD
	}
	
	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		return null;
		
	}
	
	/**
	 * Method to indicate if speel correct queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get ordered "full query" substitutions for a given misspelt query
	 * @return : Ordered list of full corrections (null if none present) for the given query
	 */
	public List<String> getCorrections() {
		//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}
}
