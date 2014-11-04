
package edu.buffalo.cse.irf14.analysis;

import java.util.logging.*;

public class ContentAnalyzer implements Analyzer {

	public final Logger logger = Logger.getLogger(ContentAnalyzer.class.getName());
	TokenStream stream = new TokenStream();
	
	public ContentAnalyzer(TokenStream stream) {
		this.stream = stream;
		TokenFilterFactory factory = TokenFilterFactory.getInstance();
		TokenFilter accentFilter = factory.getFilterByType(TokenFilterType.ACCENT, stream);
		TokenFilter capitalFilter = factory.getFilterByType(TokenFilterType.CAPITALIZATION, stream);
		TokenFilter dateFilter = factory.getFilterByType(TokenFilterType.DATE, stream);
		TokenFilter specialCharFilter = factory.getFilterByType(TokenFilterType.SPECIALCHARS, stream);
		TokenFilter stemmerFilter = factory.getFilterByType(TokenFilterType.STEMMER, stream);
		TokenFilter stopwordFilter = factory.getFilterByType(TokenFilterType.STOPWORD, stream);
		TokenFilter symbolFilter = factory.getFilterByType(TokenFilterType.SYMBOL, stream);
		stream = accentFilter.getStream();
		stream = capitalFilter.getStream();
		stream = dateFilter.getStream();
		stream = specialCharFilter.getStream();
		stream = stopwordFilter.getStream();
		stream = stemmerFilter.getStream();
		stream = symbolFilter.getStream();
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
		return stream;
	}
	
}