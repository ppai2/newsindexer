
package edu.buffalo.cse.irf14.query;

import java.io.File;
import java.io.PrintStream;

import edu.buffalo.cse.irf14.SearchRunner;
import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;

public class QueryTest {
	
	public static void main (String[] args) {
		
		String indexDir = ".";
		String corpusDir = "corpus";
		String modeQStr = "Q";
		char modeQ = modeQStr.toCharArray()[0];
		String modeEStr = "E";
		char modeE = modeEStr.toCharArray()[0];
		PrintStream stream = null;
		File input = new File("input_query.txt");
		
		String userQuery = "NATO";
		ScoringModel model = ScoringModel.TFIDF;
		
		SearchRunner srq = new SearchRunner(indexDir, corpusDir, modeQ, stream);
		srq.query(userQuery, model);
		
		SearchRunner sre = new SearchRunner(indexDir, corpusDir, modeE, stream);
		//sre.query(input);
		
	}
	
}