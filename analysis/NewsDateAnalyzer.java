
package edu.buffalo.cse.irf14.analysis;

import java.util.logging.*;

public class NewsDateAnalyzer implements Analyzer {

	public final Logger logger = Logger.getLogger(NewsDateAnalyzer.class.getName());
	TokenStream stream = new TokenStream();
	
	public NewsDateAnalyzer(TokenStream stream) {
		this.stream = stream;
		TokenFilterFactory factory = TokenFilterFactory.getInstance();
		TokenFilter accentFilter = factory.getFilterByType(TokenFilterType.ACCENT, stream);
		TokenFilter dateFilter = factory.getFilterByType(TokenFilterType.DATE, stream);
		stream = accentFilter.getStream();
		stream = dateFilter.getStream();
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