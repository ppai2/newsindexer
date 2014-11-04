
package edu.buffalo.cse.irf14.analysis;

import java.util.ListIterator;
import java.util.logging.*;


public class CapitalizationFilter extends TokenFilter {
	
	public final Logger logger = Logger.getLogger(CapitalizationFilter.class.getName());
	TokenStream stream = new TokenStream();

	public CapitalizationFilter(TokenStream stream) {
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
		ListIterator<Token> capsIt = stream.getTsIterator();
		boolean firstWord = true;
		boolean middleCap = false;
		boolean allCaps = true;
		String termText1 = "";
		String termText2 = "";
		
		while (capsIt.hasNext()) {
			Token t = capsIt.next();
			String termText = t.getTermText();
			char[] termChar = termText.toCharArray();
			for (char c : termChar) {
				if (Character.isLetter(c)) {
					if (Character.isLowerCase(c))
						allCaps = false;
				}
			}
			if (!allCaps)
				break;
		}
		
		while (streamIt.hasNext()) {
			Token t = streamIt.next();
			String termText = t.getTermText();
			char[] charText = termText.toCharArray();
			
			if (allCaps) {
				termText = "";
				for (int i = 0; i < charText.length; i++) {
					if (Character.isLetter(charText[i]))
						charText[i] = Character.toLowerCase(charText[i]);
					termText += charText[i];
				}
				
				Token newToken = new Token();
				newToken.setTermText(termText);
				streamIt.set(newToken);
			} else {
				allCaps = true;
				char[] termChar = termText.toCharArray();
				for (char c : termChar) {
					if (Character.isLetter(c)) {
						if (Character.isLowerCase(c))
							allCaps = false;
					}
					if (!allCaps)
						break;
				}
				
				if (!allCaps) {
					
					if (firstWord) {
						if (Character.isUpperCase(charText[0]))
							charText[0] = Character.toLowerCase(charText[0]);
						
						termText = "";
						for (char c : charText) {
							termText += c;
						}
						
						Token newToken = new Token();
						newToken.setTermText(termText);
						streamIt.set(newToken);
						
					} else {
						
						if (Character.isUpperCase(charText[0])) {
							if (!middleCap) {
								termText1 = termText;
								middleCap = true;
								if (streamIt.hasNext())
									streamIt.remove();
							} else {
								termText2 = termText;
								middleCap = false;
								charText = (termText1 + " " + termText2).toCharArray();
								termText = "";
								for (char c : charText) {
									termText += c;
								}
								Token newToken = new Token();
								newToken.setTermText(termText);
								streamIt.set(newToken);
							}
							
						} else if (middleCap) {
							streamIt.previous();
							Token newToken = new Token();
							newToken.setTermText(termText1);
							streamIt.add(newToken);
							streamIt.next();
							if (streamIt.hasNext())
								streamIt.next();
							middleCap = false;
						}
					}
				}
			}
			
			if (termText.endsWith(".") && !(termText.endsWith(".m.") || termText.endsWith(".M."))) {
				firstWord = true;
			} else {
				firstWord = false;
			}
			
		}
		
		return stream;
	}
	
}