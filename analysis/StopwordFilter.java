
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.*;

public class StopwordFilter extends TokenFilter {
	
	Logger logger = Logger.getLogger(StopwordFilter.class.getName());
	TokenStream stream = new TokenStream();

	public StopwordFilter(TokenStream stream) {
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
		ArrayList<String> stopwords = new ArrayList<String>();
		String[] stopwordsArr = {"a","able","about","across","after","all","almost","also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","however","i","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their","them","then","there","these","they","this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your"};
		for (String s: stopwordsArr) {
			stopwords.add(s);
		}
		
		while (streamIt.hasNext()) {
			Token t = streamIt.next();
			String termText = t.getTermText();
			if (stopwords.contains(termText)) {
				streamIt.remove();
			}
		}
		
		return stream;
	}
	
}