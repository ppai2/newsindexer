
package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

public class IndexSearcher {
	
	public final Logger logger = Logger.getLogger(IndexSearcher.class.getName());
	
	public static List<String> formulateQuery(Query query) {
		
		// REFERENCE: http://rosettacode.org/wiki/Parsing/Shunting-yard_algorithm#Java
		
		Stack<Integer> stack = new Stack<Integer>();
		List<String> queryResult = new ArrayList<String>();
		List<String> opIndex = new ArrayList<String>();
		opIndex.add("OR");
		opIndex.add("AND");
		
		for (int i = 0; i < query.size(); i++) {
			String q = query.get(i);
			int index = opIndex.indexOf(q);
			if (index != -1) {
				if (stack.isEmpty()) {
					stack.push(index);
				} else {
					while (!stack.isEmpty()) {
						int op1 = stack.peek();
						if (op1 > index)
							queryResult.add(opIndex.get(stack.pop()));
						else
							break;
					}
					stack.push(index);
				}
			} else if (q.equals("(")) {
				stack.push(-2);
			} else if (q.equals(")")) {
				while (stack.peek() != -2) {
					queryResult.add(opIndex.get(stack.pop()));
				}
				stack.pop();
			} else {
				queryResult.add(q);
			}
		}
		while (!stack.isEmpty()) {
			queryResult.add(opIndex.get(stack.pop()));
		}
		
		return queryResult;
	}
	
	public static HashMap<Integer, HashMap<String, Integer>> executeQuery(Query query, String indexDir) {
		
		if (query.size() == 0) {
			return null;
		}
		List<String> postQuery = formulateQuery(query);
		HashMap<Integer, HashMap<String, Integer>> queryResult = new HashMap<Integer, HashMap<String, Integer>>();
		HashMap<String, HashMap<Integer, HashMap<String, Integer>>> subQueryMap = new HashMap<String, HashMap<Integer, HashMap<String, Integer>>>();
		Stack<String> stack = new Stack<String>();
		int counter = 0;
		
		if (postQuery.size() == 1) {
			String queryTerm = postQuery.get(0);
			String indexType = queryTerm.split(":")[0];
			String term = queryTerm.split(":")[1];
			IndexReader ir = null;
			
			if (indexType.equals("Term")) {
				ir = new IndexReader(indexDir, IndexType.TERM);
			} else if (indexType.equals("Category")) {
				ir = new IndexReader(indexDir, IndexType.CATEGORY);
			} else if (indexType.equals("Author")) {
				ir = new IndexReader(indexDir, IndexType.AUTHOR);
			} else if (indexType.equals("Place")) {
				ir = new IndexReader(indexDir, IndexType.PLACE);
			}
			
			if (ir != null)
				queryResult = ir.getDocFrequency(term);
		} else {
			for (int k = 0; k < postQuery.size(); k++) {
				String pq = postQuery.get(k);
				if (isOperator(pq) && k != postQuery.size()-1) {
					String term1 = "";
					String term2 = "";
					if (stack.size() > 1) {
						term2 = stack.pop();
						term1 = stack.pop();
					}
					String indexType1 = "";
					String indexType2 = "";
					IndexReader ir1 = null;
					IndexReader ir2 = null;
					HashMap<Integer, HashMap<String, Integer>> data1 = new HashMap<Integer, HashMap<String, Integer>>();
					HashMap<Integer, HashMap<String, Integer>> data2 = new HashMap<Integer, HashMap<String, Integer>>();
					boolean isNeg = false;
					
					indexType1 = term1.split(":")[0];
					term1 = term1.split(":")[1];
					
					if (term2.startsWith("<") && term2.endsWith(">")) {
						term2 = term2.substring(1, term2.length()-1);
						isNeg = true;
					}
					indexType2 = term2.split(":")[0];
					term2 = term2.split(":")[1];
					
					// Get term1 dataMap
					if (indexType1.equals("Term")) {
						ir1 = new IndexReader(indexDir, IndexType.TERM);
					} else if (indexType1.equals("Category")) {
						ir1 = new IndexReader(indexDir, IndexType.CATEGORY);
					} else if (indexType1.equals("Author")) {
						ir1 = new IndexReader(indexDir, IndexType.AUTHOR);
					} else if (indexType1.equals("Place")) {
						ir1 = new IndexReader(indexDir, IndexType.PLACE);
					} else if (indexType1.equals("subQuery")) {
						data1 = subQueryMap.get(term1);
					}
					if (ir1 != null && data1.size() == 0) {
						data1 = ir1.getDocFrequency(term1);
					}
					
					// Get term2 dataMap
					if (indexType2.equals("Term")) {
						ir2 = new IndexReader(indexDir, IndexType.TERM);
					} else if (indexType2.equals("Category")) {
						ir2 = new IndexReader(indexDir, IndexType.CATEGORY);
					} else if (indexType2.equals("Author")) {
						ir2 = new IndexReader(indexDir, IndexType.AUTHOR);
					} else if (indexType2.equals("Place")) {
						ir2 = new IndexReader(indexDir, IndexType.PLACE);
					} else if (indexType1.equals("subQuery")) {
						data2 = subQueryMap.get(term2);
					}
					if (ir2 != null && data2.size() == 0) {
						data2 = ir2.getDocFrequency(term2);
					}
					
					// Get query result
					HashMap<Integer, HashMap<String, Integer>> result = new HashMap<Integer, HashMap<String, Integer>>();
					if (pq.equals("OR")) {
						result = orQuery(data1, data2);
					} else if (pq.equals("AND") && isNeg) {
						result = andNotQuery(data1, data2);
					} else if (pq.equals("AND") && !isNeg) {
						result = andQuery(data1, data2);
					}
					
					// Put subquery result in subQueryMap
					subQueryMap.put("subQuery:" + counter, result);
					stack.push("subQuery:" + counter);
					counter++;
				} else {
					stack.push(pq);
				}
			}
			
			String key = stack.pop();
			queryResult = subQueryMap.get(key);
		}
		
		return queryResult;
	}
	
	public static HashMap<Integer, HashMap<String, Integer>> orQuery(HashMap<Integer, HashMap<String, Integer>> d1, HashMap<Integer, HashMap<String, Integer>> d2) {
		HashMap<Integer, HashMap<String, Integer>> result = new HashMap<Integer, HashMap<String, Integer>>();
		
		for (int docId : d1.keySet()) {;
			result.put(docId, d1.get(docId));
		}
		
		for (int docId : d2.keySet()) {
			if (result.containsKey(docId)) {
				HashMap<String, Integer> resultMap = result.get(docId);
				HashMap<String, Integer> d2Map = d2.get(docId);
				for (String s : d2Map.keySet()) {
					resultMap.put(s, d2Map.get(s));
				}
				result.put(docId, resultMap);
			} else {
				result.put(docId, d2.get(docId));
			}
		}
		
		return result;
	}
	
	public static HashMap<Integer, HashMap<String, Integer>> andNotQuery(HashMap<Integer, HashMap<String, Integer>> d1, HashMap<Integer, HashMap<String, Integer>> d2) {
		HashMap<Integer, HashMap<String, Integer>> result = new HashMap<Integer, HashMap<String, Integer>>();
		
		for (int docId : d1.keySet()) {
			result.put(docId, d1.get(docId));
		}
		
		for (int docId : d2.keySet()) {
			if (result.containsKey(docId)) {
				result.remove(docId);
			}
		}
		
		return result;
	}

	public static HashMap<Integer, HashMap<String, Integer>> andQuery(HashMap<Integer, HashMap<String, Integer>> d1, HashMap<Integer, HashMap<String, Integer>> d2) {
		HashMap<Integer, HashMap<String, Integer>> result = new HashMap<Integer, HashMap<String, Integer>>();
		List<Integer> removeDocIds = new ArrayList<Integer>();
		
		for (int docId : d1.keySet()) {
			result.put(docId, d1.get(docId));
		}
		
		for (int docId : result.keySet()) {
			if (!d2.containsKey(docId)) {
				removeDocIds.add(docId);
			} else {
				HashMap<String, Integer> resultMap = result.get(docId);
				HashMap<String, Integer> valueMap = d2.get(docId);
				for (String t : valueMap.keySet()) {
					resultMap.put(t, valueMap.get(t));
				}
				result.put(docId, resultMap);
			}
		}
		
		for (Integer r : removeDocIds) {
			result.remove(r);
		}
		
		return result;
	}
	
	public static boolean isOperator(String s) {
		boolean isOperator = false;
		
		if (s.equals("AND") || s.equals("OR") || s.equals("NOT"))
			isOperator = true;
		
		return isOperator;
	}
	
	public static boolean isIndexType(String s) {
		boolean isIndexType = false;
		
		if (s.equalsIgnoreCase("Author:") || s.equalsIgnoreCase("Category:") || s.equalsIgnoreCase("Place:") || s.equalsIgnoreCase("Term:"))
			isIndexType = true;
		
		return isIndexType;
	}
	
}