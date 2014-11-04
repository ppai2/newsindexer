package edu.buffalo.cse.irf14.analysis;

import java.util.ListIterator;
import java.util.logging.*;

public class StemmerFilter extends TokenFilter {
	
	/*
	 * * REFERENCE: http://tartarus.org/martin/PorterStemmer/java.txt
	 */
	
	public final Logger logger = Logger.getLogger(StemmerFilter.class.getName());
	TokenStream stream = new TokenStream();
	StemmerFunction s = new StemmerFunction();
	
	public StemmerFilter(TokenStream stream) {
		super(stream);
		this.stream = stream;
	}

	@Override
	public boolean increment() throws TokenizerException {
		if (stream.hasNext()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public TokenStream getStream() {
		
		String strReturnStem = "";
		ListIterator<Token> streamIt = stream.getTsIterator();
			
		while(streamIt.hasNext()) {
			Token t = streamIt.next();
			String termText = t.getTermText();
			if (termText != null && termText.length() > 0) {
				if (termText.substring(0, 1).matches("[^A-Za-z]")) {
					strReturnStem = termText;
					
					Token newToken = new Token();
					newToken.setTermText(strReturnStem);
					streamIt.set(newToken);
				} else {
					char[] ch = termText.toCharArray();
					s.add(ch, termText.length());
					s.stem();
					strReturnStem = s.toString();
					
					Token newToken = new Token();
					newToken.setTermText(strReturnStem);
					streamIt.set(newToken);
				}
			}
		}
		
		return stream;
	}
	
}