package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

/**
 * Class that represents a parsed query
 * @author nikhillo
 *
 */
public class Query {
	
	public final Logger logger = Logger.getLogger(Query.class.getName());
	
	private List<String> query;
	
	/**
	 * Default constructor
	 */
	public Query() {
		query = new ArrayList<String>();
	}
	
	public void add(String t) {
		query.add(t);
	}
	
	public void add(int index, String t) {
		query.add(index, t);
	}
	
	public void replace(int index, String t) {
		query.remove(index);
		query.add(index, t);
	}
	
	public void remove(int index) {
		query.remove(index);
	}
	
	public String get(int index) {
		return query.get(index);
	}
	
	public int size() {
		return query.size();
	}
	
	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() {
		//TODO: YOU MUST IMPLEMENT THIS
		String q = "";
		for (String s : query) {
			if (s.equals(")")) {
				q = q.trim();
				q += "] ";
			} else if (s.equals("("))
				q += "[";
			else
				q += s + " ";
		}
		q = q.trim();
		q = "{" + q + "}";
		
		return q;
	}
}
