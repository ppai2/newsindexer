
package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.logging.*;

public class AccentFilter extends TokenFilter {
	
	public final Logger logger = Logger.getLogger(AccentFilter.class.getName());
	TokenStream stream = new TokenStream();

	public AccentFilter(TokenStream stream) {
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
			HashMap<String, String> acc = new HashMap<String, String>();
			acc.put("â", "a");
			acc.put("ô", "o");
			acc.put("é", "e");
			acc.put("à", "a");
			acc.put("è", "e");
			acc.put("é", "e");
			acc.put("è", "e");
			acc.put("û", "u");
			acc.put("ü", "u");
			acc.put("ë", "e");
			
			acc.put("À", "A");
			acc.put("Á", "A");
			acc.put("Â", "A");
			acc.put("Ã", "A");
			acc.put("Ä", "A");
			acc.put("Å", "A");
			acc.put("Æ", "AE");
			acc.put("Ç", "C");
			acc.put("È", "E");
			acc.put("É", "E");
			acc.put("Ê", "E");
			acc.put("Ë", "E");
			acc.put("Ì", "I");
			acc.put("Í", "I");
			acc.put("Î", "I");
			acc.put("Ï", "I");
			acc.put("Ĳ", "IJ");
			acc.put("Ð", "D");
			acc.put("Ñ", "N");
			acc.put("Ò", "O");
			acc.put("Ó", "O");
			acc.put("Ô", "O");
			acc.put("Õ", "O");
			acc.put("Ö", "O");
			acc.put("Ø", "O");
			acc.put("Œ", "OE");
			acc.put("Þ", "TH");
			acc.put("Ù", "U");
			acc.put("Ú", "U");
			acc.put("Û", "U");
			acc.put("Ü", "U");
			acc.put("Ý", "Y");
			acc.put("Ÿ", "Y");
			acc.put("à", "a");
			acc.put("á", "a");
			acc.put("â", "a");
			acc.put("ã", "a");
			acc.put("ä", "a");
			acc.put("å", "a");
			acc.put("æ", "ae");
			acc.put("ç", "c");
			acc.put("è", "e");
			acc.put("é", "e");
			acc.put("ê", "e");
			acc.put("ë", "e");
			acc.put("ì", "i");
			acc.put("í", "i");
			acc.put("î", "i");
			acc.put("ï", "i");
			acc.put("ĳ", "ij");
			acc.put("ð", "d");
			acc.put("ñ", "n");
			acc.put("ò", "o");
			acc.put("ó", "o");
			acc.put("ô", "o");
			acc.put("õ", "o");
			acc.put("ö", "o");
			acc.put("ø", "o");
			acc.put("œ", "oe");
			acc.put("ß", "ss");
			acc.put("þ", "th");
			acc.put("ù", "u");
			acc.put("ú", "u");
			acc.put("û", "u");
			acc.put("ü", "u");
			acc.put("ý", "y");
			acc.put("ÿ", "y");
			acc.put("ﬀ", "ff");
			acc.put("ﬁ", "fi");
			acc.put("ﬂ", "fl");
			acc.put("ﬃ", "ffi");
			acc.put("ﬄ", "ffl");
			acc.put("ﬅ", "ft");
			acc.put("ﬆ", "st");
			
			for (String s: acc.keySet()) {
				if (termText.contains(s)) {
					termText = termText.replace(s, acc.get(s));
				}
			}
			
			Token newToken = new Token();
			newToken.setTermText(termText);
			streamIt.set(newToken);
		}
		
		return stream;
	}
	
}