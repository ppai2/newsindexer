/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.logging.*;

/**
 * @author nikhillo
 * Class that represents a stream of Tokens. All {@link Analyzer} and
 * {@link TokenFilter} instances operate on this to implement their
 * behavior
 */
public class TokenStream implements Iterator<Token>{
	
	public static final Logger logger = Logger.getLogger(TokenStream.class.getName());
	
	private LinkedList<Token> ts = new LinkedList<Token>();
	public ListIterator<Token> tsIterator = ts.listIterator();
	Token token = null;
	
	public TokenStream() {
		ts = new LinkedList<Token>();
	}
	
	public TokenStream(Token t) {
		ts = new LinkedList<Token>();
		ts.add(t);
	}
	
	public int size() {
		return ts.size();
	}
	
	public ListIterator<Token> getTsIterator() {
		return ts.listIterator();
	}
	
	public void addToken(Token t) {
		ts.add(t);
	}
	
	/**
	 * Method that checks if there is any Token left in the stream
	 * with regards to the current pointer.
	 * DOES NOT ADVANCE THE POINTER
	 * @return true if at least one Token exists, false otherwise
	 */
	@Override
	public boolean hasNext() {
		// TODO YOU MUST IMPLEMENT THIS
		if (tsIterator.hasNext())
			return true;
		else
			return false;
	}
	
	public boolean hasPrevious() {
		if (tsIterator.hasPrevious())
			return true;
		else
			return false;
	}

	/**
	 * Method to return the next Token in the stream. If a previous
	 * hasNext() call returned true, this method must return a non-null
	 * Token.
	 * If for any reason, it is called at the end of the stream, when all
	 * tokens have already been iterated, return null
	 */
	@Override
	public Token next() {
		// TODO YOU MUST IMPLEMENT THIS
		if (hasNext()) {
			token = tsIterator.next();
		} else
			token = (Token) null;
		
		return token;
	}
	
	public int nextIndex() {
		return tsIterator.nextIndex();
	}
	
	public Token previous() {
		if (hasPrevious()) {
			token = tsIterator.previous();
		} else
			token = (Token) null;
		
		return token;
	}
	
	public int previousIndex() {
		return tsIterator.previousIndex();
	}
	
	/**
	 * Method to remove the current Token from the stream.
	 * Note that "current" token refers to the Token just returned
	 * by the next method. 
	 * Must thus be NO-OP when at the beginning of the stream or at the end
	 */
	@Override
	public void remove() {
		// TODO YOU MUST IMPLEMENT THIS
		if (token != null)
			tsIterator.remove();
		token = null;
	}
	
	/**
	 * Method to reset the stream to bring the iterator back to the beginning
	 * of the stream. Unless the stream has no tokens, hasNext() after calling
	 * reset() must always return true.
	 */
	public void reset() {
		//TODO : YOU MUST IMPLEMENT THIS
		token = null;
		tsIterator = ts.listIterator();
	}
	
	/**
	 * Method to append the given TokenStream to the end of the current stream
	 * The append must always occur at the end irrespective of where the iterator
	 * currently stands. After appending, the iterator position must be unchanged
	 * Of course this means if the iterator was at the end of the stream and a 
	 * new stream was appended, the iterator hasn't moved but that is no longer
	 * the end of the stream.
	 * @param stream : The stream to be appended
	 */
	public void append(TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS
		
		if (stream != null) {
			stream.reset();
			int prevIndex = -1;
			int nextIndex = -1;
			
			if (tsIterator.hasNext())
				nextIndex = tsIterator.nextIndex();
			else if (tsIterator.hasPrevious())
				prevIndex = tsIterator.previousIndex();
			
			while (stream.hasNext()) {
				Token t = stream.next();
				ts.add(t);
			}
			
			reset();
			if (nextIndex == -1) {
				while (previousIndex() != prevIndex) {
					next();
				}
			} else if (prevIndex == -1) {
				while (nextIndex() != nextIndex) {
					next();
				}
			}
		}
		
	};
	
	/**
	 * Method to get the current Token from the stream without iteration.
	 * The only difference between this method and {@link TokenStream#next()} is that
	 * the latter moves the stream forward, this one does not.
	 * Calling this method multiple times would not alter the return value of {@link TokenStream#hasNext()}
	 * @return The current {@link Token} if one exists, null if end of stream
	 * has been reached or the current Token was removed
	 */
	public Token getCurrent() {
		//TODO: YOU MUST IMPLEMENT THIS
		return token;
	}
	
	@Override
	public String toString() {
		String tsString = "{";
		ListIterator<Token> tsIt = ts.listIterator();
		while (tsIt.hasNext()) {
			tsString += tsIt.next().toString() + ", ";
		}
		if (tsString.contains(",")) {
			tsString = tsString.substring(0, tsString.lastIndexOf(",")).trim();
		}
		tsString += "}";
		
		return tsString;
	}
	
}
