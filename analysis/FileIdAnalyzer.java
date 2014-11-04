
package edu.buffalo.cse.irf14.analysis;

import java.util.logging.*;

public class FileIdAnalyzer implements Analyzer {

	public final Logger logger = Logger.getLogger(FileIdAnalyzer.class.getName());
	TokenStream stream = new TokenStream();
	
	public FileIdAnalyzer(TokenStream stream) {
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
		return stream;
	}
	
}