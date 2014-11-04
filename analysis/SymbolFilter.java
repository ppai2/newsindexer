
package edu.buffalo.cse.irf14.analysis;

import java.util.ListIterator;
import java.util.logging.Logger;

public class SymbolFilter extends TokenFilter {
	
	public final Logger logger = Logger.getLogger(NumericFilter.class.getName());
	TokenStream stream = new TokenStream();
	
	public SymbolFilter(TokenStream stream) {
		super(stream);
		this.stream = stream;
	}

	@Override
	public boolean increment() throws TokenizerException {
		if (stream.hasNext())
			return true;
		else
			return false;
	}

	@Override
	public TokenStream getStream() {
		ListIterator<Token> streamIt = stream.getTsIterator();
		
		while (streamIt.hasNext()) {
			Token t = streamIt.next();
			String termText = t.getTermText();
			
			// Punctuation Test
			while (termText.endsWith(".") || termText.endsWith("!") || termText.endsWith("?")) {
				termText = termText.substring(0, termText.length()-1);
			}
			
			// Hyphen Test
			if (termText.contains("-")) {
				if (!termText.matches(".*\\d+.*")) {
					if ((!termText.startsWith("-")) && (!termText.endsWith("-")))
						termText = termText.replaceAll("-", " ");
					else
						termText = termText.replaceAll("-", "");
				} else {
					if (termText.startsWith("-") || termText.endsWith("-")) {
						termText = termText.replaceAll("-", "");
					}
				}
			}
			
			// Apostrophe Test
			if (termText.endsWith("'em")) {
				termText = termText.replace("'em", "them");
			}
			if (termText.startsWith("'")) {
				termText = termText.substring(1);
			}
			if (termText.endsWith("'")) {
				termText = termText.substring(0, termText.length()-1);
			}
			if (termText.endsWith("'re")) {
				termText = termText.replace("'re", " are");
			}
			if (termText.endsWith("'ve")) {
				termText = termText.replace("'ve", " have");
			}
			if (termText.endsWith("'m")) {
				termText = termText.replace("'m", " am");
			}
			if (termText.endsWith("'ll")) {
				termText = termText.replace("'ll", " will");
			}
			if (termText.endsWith("'s")) {
				termText = termText.substring(0, termText.length()-2);
			}
			if (termText.endsWith("'d")) {
				termText = termText.replace("'d", " would");
			}
			/*if (termText.contains("isn't")) {
				termText = termText.replace("isn't", "is not");
			}
			if (termText.contains("don't")) {
				termText = termText.replace("don't", "do not");
			}*/
			if (termText.contains("won't")) {
				termText = termText.replace("won't", "will not");
			}
			if (termText.contains("shan't")) {
				termText = termText.replace("shan't", "shall not");
			}
			if (termText.contains("n't")) {
				termText = termText.replace("n't", " not");
			}
			String[] beforeStr = {"'(", "')", "'[", "']", "'{", "'}", "'/", "'\\"};
			String[] afterStr = {"('", ")'", "['", "]'", "{'", "}'", "/'", "\\'"};
			for (String s: beforeStr) {
				while (termText.contains(s))
					termText = termText.replace(s, s.substring(1));
			}
			for (String s: afterStr) {
				while (termText.contains(s))
					termText = termText.replace(s, s.substring(0, 1));
			}
			
			if (!termText.equals("")) {
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