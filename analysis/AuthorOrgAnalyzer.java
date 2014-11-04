
package edu.buffalo.cse.irf14.analysis;

import java.util.logging.*;

public class AuthorOrgAnalyzer implements Analyzer {

	public final Logger logger = Logger.getLogger(AuthorOrgAnalyzer.class.getName());
	TokenStream stream = new TokenStream();
	
	public AuthorOrgAnalyzer(TokenStream stream) {
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