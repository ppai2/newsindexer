/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 * @throws QueryParserException 
	 */
	public static Query parse(String userQuery, String defaultOperator) throws QueryParserException {
		//TODO: YOU MUST IMPLEMENT THIS METHOD
		Query query = new Query();
		boolean valid = true;
		
		if (userQuery.length() == 0) {
			valid = false;
			throw new QueryParserException();
		}
		
		if (defaultOperator == null || !isOperator(defaultOperator))
			defaultOperator = "OR";
		
		if (userQuery.contains("(") && !userQuery.contains(")"))
			valid = false;
		else if (userQuery.contains(")") && !userQuery.contains("("))
			valid = false;
		
		
		if (userQuery.contains("(") && userQuery.contains(")")) {
			userQuery = userQuery.replaceAll("\\(", " \\( ");
			userQuery = userQuery.replaceAll("\\)", " \\) ");
		}
		userQuery = userQuery.trim();
		String[] userQueryArray = userQuery.split("\\s+|\\t+");
		String phrase = "";
		boolean inPhrase = false;
		
		for (int k = 0; k < userQueryArray.length; k++) {
			
			// Build query while handling " "
			if (userQueryArray[k].startsWith("\"")) {
				inPhrase = true;
			}
			
			if (inPhrase) {
				phrase += userQueryArray[k] + " ";
			}
			
			if (userQueryArray[k].endsWith("\"")) {
				inPhrase = false;
				phrase = phrase.trim();
				query.add(phrase);
			} else if (!inPhrase) {
				query.add(userQueryArray[k]);
			}
			
		}
		
		boolean openBraces = false;
		boolean closeBraces = true;
		for (int i = 0; i < query.size(); i++) {
			
			// Take IndexType in front of ( inside
			if (query.get(i).equals("(") && i != 0) {
				String prev = query.get(i-1);
				if (prev.equalsIgnoreCase("Author:") || prev.equalsIgnoreCase("Category:") || prev.equalsIgnoreCase("Place:") 
						|| prev.equalsIgnoreCase("Term:")) {
					int j = i+1;
					while (!query.get(j).endsWith(")")) {
						if (!isOperator(query.get(j))) {
							query.replace(j, prev+query.get(j));
						}
						j++;
					}
					query.remove(i-1);
				}
			}
			
			// Add IndexType.TERM if no IndexType present
			if (!query.get(i).equals("(") && !query.get(i).equals(")") && !query.get(i).contains(":") && !isOperator(query.get(i))) {
				query.replace(i, "Term:" + query.get(i));
				
			}
			
			// Convert NOT to AND NOT
			if (i!= 0 && query.get(i-1).equals("NOT")) {
				query.replace(i-1, "AND");
				query.replace(i, "<" + query.get(i) + ">");
			}
			
		}
		
		// Filter query terms through the relevant Analyzers
		for (int n = 0; n < query.size(); n++) {
			String t = query.get(n);
			boolean isNeg = false;
			
			if (!isOperator(t)) {
				if (t.startsWith("<") && t.endsWith(">")) {
					t = t.substring(1, t.length()-1);
					isNeg = true;
				}
				if (t.contains(":")) {
					String indexType = t.split(":")[0];
					String term = t.split(":")[1];
					AnalyzerFactory factory = AnalyzerFactory.getInstance();
					Analyzer analyzer = null;
					Token token = new Token(term);
					TokenStream ts = new TokenStream(token);
					
					if (indexType.equalsIgnoreCase("Author")) {
						analyzer = factory.getAnalyzerForField(FieldNames.AUTHOR, ts);
					} else if (indexType.equalsIgnoreCase("Category")) {
						analyzer = factory.getAnalyzerForField(FieldNames.CATEGORY, ts);
					} else if (indexType.equalsIgnoreCase("Place")) {
						analyzer = factory.getAnalyzerForField(FieldNames.PLACE, ts);
					} else if (indexType.equalsIgnoreCase("Term")) {
						analyzer = factory.getAnalyzerForField(FieldNames.TERM, ts);
					}
					
					ts = analyzer.getStream();
					ts.reset();
					token = ts.next();
					term = token.toString();
					t = indexType + ":" + term;
					if (isNeg)
						t = "<" + t + ">";
					query.replace(n, t);
				}
			}
		}
		
		// Insert defaultOperator between terms with no operators between them
		for (int m = 0; m < query.size(); m++) {
			outerloop:
			if (!query.get(m).equals("(") && !query.get(m).equals(")") && !isOperator(query.get(m)) && query.size() > 1) {
				
				if (m == 0 && !isOperator(query.get(m+1))) {
					
					if (query.get(m+1).equals("("))
						query.add(m+1, defaultOperator);
					else {
						if (m+2 != query.size()) {
							query.add(m, "(");
							query.add(m+1, defaultOperator);
							openBraces = true;
							closeBraces = false;
						} else {
							query.add(m+1, defaultOperator);
						}
					}
					
					break outerloop;
				} else if (m != 0 && m != query.size()-1) {
					if (!isOperator(query.get(m-1)) || !isOperator(query.get(m+1))) {
						if (query.get(m-1).equals("(") || (query.get(m+1).equals("("))) {
							if (!isOperator(query.get(m+1))) {
								query.add(m+1, defaultOperator);
							}
						} else if (query.get(m-1).equals(")") || query.get(m+1).equals(")")) {
							if (!isOperator(query.get(m-1))) {
								query.add(m, defaultOperator);
							}
						} else {
							if (isOperator(query.get(m-1))) {
								if (!openBraces) {
									query.add(m, "(");
									query.add(m+2, defaultOperator);
								} else {
									query.add(m+1, defaultOperator);
								}
								openBraces = true;
								closeBraces = false;
							} else if (isOperator(query.get(m+1))) {
								query.add(m, defaultOperator);
								query.add(m+1, ")");
							} else {
								query.add(m, defaultOperator);
								query.add(m+2, defaultOperator);
							}
						}
										
						if (m+3 < query.size()) {
							if (isOperator(query.get(m+3)) && !closeBraces) {
								query.add(m+3, ")");
								closeBraces = true;
							}
						} else {
							if (!closeBraces) {
								query.add(")");
								closeBraces = true;
							}
						}
						break outerloop;
					}
				} else if (m == query.size()-1 && !isOperator(query.get(m-1))) {
					if (query.get(m-1).equals(")")) {
						query.add(m, defaultOperator);
					}
					break outerloop;
				}
			}
		}
		
		if (valid)
			return query;
		else
			return null;
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
