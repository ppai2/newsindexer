/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;


/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public class TokenFilterFactory {
	
	private static TokenFilterFactory tokenFilterFactory = new TokenFilterFactory();
	
	private TokenFilterFactory() {
		
	}
	/**
	 * Static method to return an instance of the factory class.
	 * Usually factory classes are defined as singletons, i.e. 
	 * only one instance of the class exists at any instance.
	 * This is usually achieved by defining a private static instance
	 * that is initialized by the "private" constructor.
	 * On the method being called, you return the static instance.
	 * This allows you to reuse expensive objects that you may create
	 * during instantiation
	 * @return An instance of the factory
	 */
	public static TokenFilterFactory getInstance() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		//return null;
		return tokenFilterFactory;
	}
	
	/**
	 * Returns a fully constructed {@link TokenFilter} instance
	 * for a given {@link TokenFilterType} type
	 * @param type: The {@link TokenFilterType} for which the {@link TokenFilter}
	 * is requested
	 * @param stream: The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		TokenFilter filter = null;
		
		switch (type) {
			case ACCENT: filter = new AccentFilter(stream); break;
			case CAPITALIZATION: filter = new CapitalizationFilter(stream); break;
			case DATE: filter = new DateFilter(stream); break;
			case NUMERIC: filter = new NumericFilter(stream); break;
			case SPECIALCHARS: filter = new SpecialCharsFilter(stream); break;
			case STEMMER: filter = new StemmerFilter(stream); break;
			case STOPWORD: filter = new StopwordFilter(stream); break;
			case SYMBOL: filter = new SymbolFilter(stream); break;
		}
		
		return filter;
	}
}
