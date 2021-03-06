/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author nikhillo
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer {
	
	public String delimiter = " ";
	public static final Logger logger = Logger.getLogger(Tokenizer.class.getName());
	
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */
	public Tokenizer() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		
	}
	
	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		delimiter = delim;
	}
	
	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream.
	 * No other processing must be performed. Also the number of tokens
	 * would be determined by the string and the delimiter.
	 * So if the string were "hello world" with a whitespace delimited
	 * tokenizer, you would get two tokens in the stream. But for the same
	 * text used with lets say "~" as a delimiter would return just one
	 * token in the stream.
	 * @param str : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException : In case any exception occurs during
	 * tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		TokenStream ts = new TokenStream();
		if (str != null && str != "") {
			String[] strArr = str.split(delimiter);
			for (String s: strArr) {
				s = s.trim();
				if (s != "" && s!= null) {
					Token t = new Token();
					t.setTermText(s);
					ts.addToken(t);;
				}
			}
		} else {
			logger.log(Level.WARNING, "Null or Empty string passed to Tokenizer.consume()");
			throw new TokenizerException();
		}
		
		//logger.log(Level.INFO, "TokenStream created: " + ts.toString());
		
		return ts;
	}
}
