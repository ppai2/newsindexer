/**
 *
 */

package edu.buffalo.cse.irf14.query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;

public class Scorer {
	
	public final Logger logger = Logger.getLogger(Scorer.class.getName());
	
	ScoringModel model;
	
	public Scorer() {
		model = ScoringModel.TFIDF;
	}
	
	public Scorer(ScoringModel model) {
		this.model = model;
	}
	
	public HashMap<Integer, Double> getDocScore(HashMap<Integer, HashMap<String, Integer>> result, String indexDir) {
		HashMap<Integer, Double> docScore = new HashMap<Integer, Double>();
		HashMap<String, Object> indexes = getIndexes(indexDir);
		HashMap<Integer, ArrayList<Integer>> index = (HashMap<Integer, ArrayList<Integer>>)(indexes.get("index"));
		HashMap<String, Integer> termDictionary = (HashMap<String, Integer>)(indexes.get("termDictionary"));
		HashMap<Integer, Integer> docLength = (HashMap<Integer, Integer>)(indexes.get("docLength"));
		int totalDocCount = (Integer)(indexes.get("docDictionarySize"));
		double max = -1.0;
		double min = -1.0;
		
		if (model == ScoringModel.TFIDF) {
			docScore = getTfidfScore(result, index, totalDocCount, termDictionary);
		} else if (model == ScoringModel.OKAPI) {
			docScore = getOkapiScore(result, index, totalDocCount, docLength, termDictionary);
		};
		
		// Sort docScores in descending order of scores
		docScore = getSortedDocScore(docScore);
		for (double v : docScore.values()) {
			if (max == -1.0)
				max = v;
			if (min == -1.0)
				min = v;
			
			if (max < v)
				max = v;
			if (min > v)
				min = v;
		}
		
		for (Entry<Integer, Double> e : docScore.entrySet()) {
			int key = e.getKey();
			double value = e.getValue();
			value = ((double)(value-min))/((double) (max-min));
			value = formatScore(value);
			docScore.put(key, value);
		}
		
		return docScore;
	}
	
	private HashMap<Integer, Double> getTfidfScore (HashMap<Integer, HashMap<String, Integer>> result, HashMap<Integer, ArrayList<Integer>> index,
			int totalDocCount, HashMap<String, Integer> termDictionary) {
		
		HashMap<Integer, Double> scores = new HashMap<Integer, Double>();
		double idfWeight1 = 0.0;
		double tfidfWeight = 0.0; 
		double idf1 = 0.0;
		double tf1 = 0.0;
		
		for (int docId : result.keySet()) {
			HashMap<String, Integer> termFreq = result.get(docId);
			for (String term : termFreq.keySet()) {
				int termId = termDictionary.get(term);
				int freq = termFreq.get(term);
				int docFreq = index.get(termId).size();
				tf1 = Math.log(1+freq);
				idf1 = ((double) totalDocCount)/((double) docFreq);
				idfWeight1 = Math.log10(idf1);
				tfidfWeight = ((double) tf1)/((double) idfWeight1);
			}
			scores.put(docId, tfidfWeight);
		}
		
		return scores;
	}
	
	private HashMap<Integer, Double> getOkapiScore (HashMap<Integer, HashMap<String, Integer>> result, HashMap<Integer, ArrayList<Integer>> index,
			int totalDocCount, HashMap<Integer, Integer> docLength, HashMap<String, Integer> termDictionary) {
		HashMap<Integer, Double> scores = new HashMap<Integer, Double>();
		double okapiWeight=0.0; 
		double okapi_den = 0.0; 
		double idf1 = 0.0;
		double k  = 1.2, b = 0.75;
		double tf_num = 0.0;
		double sum = 0.0;
		int count = 0;
		
		HashMap<Integer, Integer> hm = docLength;
		for (Integer i : hm.keySet()) {
			sum = sum + hm.get(i);
			count++;
		}
		
		double avgDocLength = ((double) sum)/((double) count);
		
		for (Integer docId : result.keySet()) {
			HashMap<String, Integer> termFreq = result.get(docId);
			for (String term : termFreq.keySet()) {
				int termId = termDictionary.get(term);
				int freq = termFreq.get(term);
				int docFreq = index.get(termId).size();
				tf_num = freq*(k+1);
				idf1 = 	Math.log(((double) totalDocCount-docFreq+0.5)/((double) (docFreq+0.5)));
				double val = 0.0;
				for (Integer i : hm.keySet()) {
					val = ((double) hm.get(i))/((double) avgDocLength);
					okapi_den = (double) (freq+(k*(1-b+(b*val))));
					okapiWeight = ((double) idf1*tf_num)/((double) okapi_den);
				}
			}
			scores.put(docId, okapiWeight);		
		}	
		return scores;
	}
	
	private HashMap<String, Object> getIndexes(String indexDir) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<Integer, ArrayList<Integer>> index = new HashMap<Integer, ArrayList<Integer>>();
		HashMap<String, Integer> docDictionary = new HashMap<String, Integer>();
		HashMap<String, Integer> termDictionary = new HashMap<String, Integer>();
		HashMap<Integer, Integer> docLength = new HashMap<Integer, Integer>();
		
		try {
			FileInputStream docDictionaryFileInput;
			FileInputStream termDictionaryFileInput;
			FileInputStream indexFileInput;
			FileInputStream docLengthFileInput;
			docDictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_doc");
			indexFileInput = new FileInputStream(indexDir + File.separator + "index_term");
			termDictionaryFileInput = new FileInputStream(indexDir + File.separator + "dictionary_term");
			docLengthFileInput = new FileInputStream(indexDir + File.separator + "length_doc");
			ObjectInputStream indexObj = new ObjectInputStream(indexFileInput);
			ObjectInputStream docDictionaryObj = new ObjectInputStream(docDictionaryFileInput);
			ObjectInputStream termDictionaryObj = new ObjectInputStream(termDictionaryFileInput);
			ObjectInputStream docLengthObj = new ObjectInputStream(docLengthFileInput);
			index = (HashMap<Integer, ArrayList<Integer>>)(indexObj.readObject());
			docDictionary = (HashMap<String, Integer>)(docDictionaryObj.readObject());
			termDictionary = (HashMap<String, Integer>)(termDictionaryObj.readObject());
			docLength = (HashMap<Integer, Integer>)(docLengthObj.readObject());
			indexObj.close();
			docDictionaryObj.close();
			termDictionaryObj.close();
			docLengthObj.close();
		} catch (FileNotFoundException fnfe) {
			logger.log(Level.SEVERE, "File Not Found Exception thrown in Scorer.tfidfScorer(): ", fnfe);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "IOException thrown in Scorer.tfidfScorer(): ", ioe);
		} catch (ClassNotFoundException cnfe) {
			logger.log(Level.SEVERE, "Class Not Found Exception thrown in Scorer.tfidfScorer(): ", cnfe);
		}
		
		map.put("index", index);
		map.put("docLength", docLength);
		map.put("docDictionarySize", docDictionary.size());
		map.put("termDictionary", termDictionary);
		
		return map;
	}
	
	private HashMap<Integer, Double> getSortedDocScore(HashMap<Integer, Double> docScore) {
		// REFERENCE: http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
		HashMap<Integer, Double> sortedScore = new LinkedHashMap<Integer, Double>();
		final boolean asc = false;
		int count = 0;
		
		List<Entry<Integer, Double>> list = new LinkedList<Entry<Integer, Double>>(docScore.entrySet());
		
		Collections.sort(list, new Comparator<Entry<Integer, Double>>() {
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				if (asc) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());
				}
			}
		});
		
		for (Entry<Integer, Double> entry : list) {
			if (count < 10) {
				sortedScore.put(entry.getKey(), entry.getValue());
				count++;
			}
		}
		
		return sortedScore;
	}
	
	private double formatScore(double score) {
		// REFERENCE: http://stackoverflow.com/questions/16309189/java-use-decimalformat-to-format-doubles-and-integers-but-keep-integers-without
		double formattedScore = 0.0;
		
		NumberFormat scoreFormat = DecimalFormat.getInstance();
		scoreFormat.setRoundingMode(RoundingMode.FLOOR);
		scoreFormat.setMinimumFractionDigits(1);
		scoreFormat.setMaximumFractionDigits(5);
		
		formattedScore = Double.parseDouble(scoreFormat.format(score));
		
		return formattedScore;
	}

}