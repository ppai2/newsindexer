
package edu.buffalo.cse.irf14.analysis;

import java.util.ListIterator;
import java.util.logging.*;

public class SpecialCharsFilter extends TokenFilter {

	public final Logger logger = Logger.getLogger(SpecialCharsFilter.class.getName());
	TokenStream stream = new TokenStream();
	
	public SpecialCharsFilter(TokenStream stream) {
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
			if (termText.matches(".*[[\\W]&&[^\\.]&&[^\\-]]+.*")) {
				termText = termText.replaceAll("[[\\W]&&[^\\.]&&[^\\-]]", "");
				if (termText.matches(".*[a-zA-Z][\\-][a-zA-Z]+.*")) {
					termText = termText.replace("-", "");
				}
				if (termText.matches(".*[\\_]+.*")) {
					termText = termText.replace("_", "");
				}
			}
			
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