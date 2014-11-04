
package edu.buffalo.cse.irf14.analysis;

import java.util.logging.*;

public class PlaceAnalyzer implements Analyzer {

	public final Logger logger = Logger.getLogger(PlaceAnalyzer.class.getName());
	TokenStream stream = new TokenStream();
	
	public PlaceAnalyzer(TokenStream stream) {
		this.stream = stream;
		TokenFilterFactory factory = TokenFilterFactory.getInstance();
		TokenFilter accentFilter = factory.getFilterByType(TokenFilterType.ACCENT, stream);
		
		stream = accentFilter.getStream();
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