
package edu.buffalo.cse.irf14.analysis;

import java.util.ListIterator;
import java.util.logging.*;


public class NumericFilter extends TokenFilter {
	
	public final Logger logger = Logger.getLogger(NumericFilter.class.getName());
	TokenStream stream = new TokenStream();

	public NumericFilter(TokenStream stream) {
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
		ListIterator<Token> streamIt = stream.getTsIterator();
		
		while (streamIt.hasNext()) {
			Token t = streamIt.next();
			String termText = t.getTermText();
			termText = termText.replaceAll("\\d,\\d", "");
			termText = termText.replaceAll("\\d\\.\\d", "");
			termText = termText.replaceAll("\\d", "");
			if (!termText.equals("") && termText != null) {
				Token newToken = new Token();
				newToken.setTermText(termText);
				streamIt.set(newToken);
			} else {
				streamIt.remove();
			}
		}
		
		return stream;
	}
	
}