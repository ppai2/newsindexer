/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.*;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * Class responsible for writing indexes to disk
 */
public class IndexWriter {
	
	public final Logger logger = Logger.getLogger(IndexWriter.class.getName());
	String indexDir = "";
	
	public HashMap<Integer, ArrayList<Integer>> termIndex = new HashMap<Integer, ArrayList<Integer>>();	// TermId, ArrayList<DocId>
	public HashMap<Integer, ArrayList<Integer>> authorIndex = new HashMap<Integer, ArrayList<Integer>>();	// AuthorId, ArrayList<DocId>
	public HashMap<Integer, ArrayList<Integer>> categoryIndex = new HashMap<Integer, ArrayList<Integer>>();	// Category, ArrayList<DocId>
	public HashMap<Integer, ArrayList<Integer>> placeIndex = new HashMap<Integer, ArrayList<Integer>>();	// Place, ArrayList<DocId>
	
	public HashMap<String, Integer> docDictionary = new HashMap<String, Integer>();	// Category + File.separator + FileId, DocId
	public HashMap<String, Integer> termDictionary = new HashMap<String, Integer>();	// Term, TermId
	public HashMap<String, Integer> authorDictionary = new HashMap<String, Integer>();	// Author, AuthorId
	public HashMap<String, Integer> placeDictionary = new HashMap<String, Integer>();	// Place, PlaceId
	public HashMap<String, Integer> categoryDictionary = new HashMap<String, Integer>();	// Category, CategoryId
	
	public HashMap<Integer, Integer> authorFrequency = new HashMap<Integer, Integer>();	// AuthorId, AuthorFreq
	public HashMap<Integer, Integer> termFrequency = new HashMap<Integer, Integer>();	// AuthorId, AuthorFreq
	public HashMap<Integer, Integer> placeFrequency = new HashMap<Integer, Integer>();	// AuthorId, AuthorFreq
	public HashMap<Integer, Integer> categoryFrequency = new HashMap<Integer, Integer>();	// AuthorId, AuthorFreq
	
	public HashMap<String, HashMap<Integer, Integer>> termDocFrequency = new HashMap<String, HashMap<Integer, Integer>>();	// Term, HashMap<DocId, Frequency>
	public HashMap<String, HashMap<Integer, Integer>> authorDocFrequency = new HashMap<String, HashMap<Integer, Integer>>();	// Term, HashMap<DocId, Frequency>
	public HashMap<String, HashMap<Integer, Integer>> categoryDocFrequency = new HashMap<String, HashMap<Integer, Integer>>();	// Term, HashMap<DocId, Frequency>
	public HashMap<String, HashMap<Integer, Integer>> placeDocFrequency = new HashMap<String, HashMap<Integer, Integer>>();	// Term, HashMap<DocId, Frequency>
	
	public HashMap<Integer, Integer> docLength = new HashMap<Integer, Integer>();	// DocId, DocLength (in number of words)
	
	int termId = 1;
	int authorId = 1;
	int placeId = 1;
	int categoryId = 1;
	int docId = 1;
	
	int currDocId = 0;
	
	HashMap<String, File> fileList = new HashMap<String, File>();
	
	File index_term;
	File index_author;
	File index_category;
	File index_place;
	
	File dictionary_doc;
	File dictionary_term;
	File dictionary_author;
	File dictionary_place;
	File dictionary_category;
	
	File frequency_author;
	File frequency_term;
	File frequency_place;
	File frequency_category;
	
	File frequency_termdoc;
	File frequency_authordoc;
	File frequency_categorydoc;
	File frequency_placedoc;
	
	File length_doc;
	
	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		//TODO : YOU MUST IMPLEMENT THIS
		this.indexDir = indexDir;
		
		File index_term = new File(indexDir);
		File index_author = new File(indexDir);
		File index_category = new File(indexDir);
		File index_place = new File(indexDir);
		
		File dictionary_doc = new File(indexDir);
		File dictionary_term = new File(indexDir);
		File dictionary_author = new File(indexDir);
		File dictionary_place = new File(indexDir);
		File dictionary_category = new File(indexDir);
		
		File frequency_author = new File(indexDir);
		File frequency_term = new File(indexDir);
		File frequency_place = new File(indexDir);
		File frequency_category = new File(indexDir);
		
		File frequency_termdoc = new File(indexDir);
		File frequency_authordoc = new File(indexDir);
		File frequency_categorydoc = new File(indexDir);
		File frequency_placedoc = new File(indexDir);
		
		File length_doc = new File(indexDir);
		
		try {
			index_term.createNewFile();
			index_author.createNewFile();
			index_category.createNewFile();
			index_place.createNewFile();
			
			dictionary_doc.createNewFile();
			dictionary_term.createNewFile();
			dictionary_author.createNewFile();
			dictionary_place.createNewFile();
			dictionary_category.createNewFile();
			
			frequency_author.createNewFile();
			frequency_term.createNewFile();
			frequency_place.createNewFile();
			frequency_category.createNewFile();
			
			frequency_termdoc.createNewFile();
			frequency_authordoc.createNewFile();
			frequency_categorydoc.createNewFile();
			frequency_placedoc.createNewFile();
			
			length_doc.createNewFile();
			
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "IO Exception occurred while creating indexFile: ", ioe);
		}
		
		index_term.setWritable(true);
		index_author.setWritable(true);
		index_category.setWritable(true);
		index_place.setWritable(true);
		
		dictionary_doc.setWritable(true);
		dictionary_term.setWritable(true);
		dictionary_author.setWritable(true);
		dictionary_place.setWritable(true);
		dictionary_category.setWritable(true);
		
		frequency_author.setWritable(true);
		frequency_term.setWritable(true);
		frequency_place.setWritable(true);
		frequency_category.setWritable(true);
		
		frequency_termdoc.setWritable(true);
		frequency_authordoc.setWritable(true);
		frequency_categorydoc.setWritable(true);
		frequency_placedoc.setWritable(true);
		
		length_doc.setWritable(true);
		
		fileList.put("index_term", index_term);
		fileList.put("index_author", index_author);
		fileList.put("index_category", index_category);
		fileList.put("index_place", index_place);
		
		fileList.put("dictionary_doc", dictionary_doc);
		fileList.put("dictionary_term", dictionary_term);
		fileList.put("dictionary_author", dictionary_author);
		fileList.put("dictionary_place", dictionary_place);
		fileList.put("dictionary_category", dictionary_category);
		
		fileList.put("frequency_author", frequency_author);
		fileList.put("frequency_term", frequency_term);
		fileList.put("frequency_place", frequency_place);
		fileList.put("frequency_category", frequency_category);
		
		fileList.put("frequency_termdoc", frequency_termdoc);
		fileList.put("frequency_authordoc", frequency_authordoc);
		fileList.put("frequency_categorydoc", frequency_categorydoc);
		fileList.put("frequency_placedoc", frequency_placedoc);
		fileList.put("length_doc", length_doc);
		
		
	}
	
	/**
	 * Method to add the given Document to the index
	 * This method should take care of reading the filed values, passing
	 * them through corresponding analyzers and then indexing the results
	 * for each indexable field within the document. 
	 * @param d : The Document to be added
	 * @throws IndexerException : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		//TODO : YOU MUST IMPLEMENT THIS
		String title = "";
		String authorOrg = "";
		String[] author = null;
		String category = "";
		String fileId = "";
		String fileName = "";
		String place = "";
		String newsDate = "";
		String content = "";
		
		if (d.getField(FieldNames.TITLE) != null)
			title = d.getField(FieldNames.TITLE)[0];
		if (d.getField(FieldNames.AUTHORORG) != null)
			authorOrg = d.getField(FieldNames.AUTHORORG)[0];
		if (d.getField(FieldNames.AUTHOR) != null)
			author = d.getField(FieldNames.AUTHOR);
		if (d.getField(FieldNames.CATEGORY) != null)
			category = d.getField(FieldNames.CATEGORY)[0];
		if (d.getField(FieldNames.FILEID) != null) {
			fileId = d.getField(FieldNames.FILEID)[0];
			fileName = fileId;
		}
		if (d.getField(FieldNames.PLACE) != null)
			place = d.getField(FieldNames.PLACE)[0];
		if (d.getField(FieldNames.NEWSDATE) != null)
			newsDate = d.getField(FieldNames.NEWSDATE)[0];
		if (d.getField(FieldNames.CONTENT) != null)
			content = d.getField(FieldNames.CONTENT)[0];
		
		//Get Doc Length
		String doc = title + " " + "author" + " " + authorOrg + " " + place + " " + newsDate + " " + content;
		String[] docWords = doc.split("\\s+|\\t+");
		
		ArrayList<TokenStream> authorTsArr = new ArrayList<TokenStream>();
		if (author != null) {
			for (int i = 0; i < author.length; i++) {
				String auth = author[i];
				Tokenizer authorTokenizer = new Tokenizer("  ");
				TokenStream authorTs = new TokenStream();
				if (auth != null && auth != "") {
					try {
						authorTs = authorTokenizer.consume(auth);
						AnalyzerFactory authorFactory = AnalyzerFactory.getInstance();
						Analyzer authorAnalyzer = authorFactory.getAnalyzerForField(FieldNames.AUTHOR, authorTs);
						//authorTs = authorAnalyzer.getStream();
						authorTs.reset();
						authorTsArr.add(authorTs);
					} catch (TokenizerException te) {
						logger.log(Level.SEVERE, "Tokenizer Exception caught in consuming author: " + auth + " - ", te);
					}
				}
			}
		}
		
		Tokenizer authorOrgTokenizer = new Tokenizer("  ");
		TokenStream authorOrgTs = new TokenStream();
		if (authorOrg != null && authorOrg != "") {
			try {
				authorOrgTs = authorOrgTokenizer.consume(authorOrg);
				AnalyzerFactory authorOrgFactory = AnalyzerFactory.getInstance();
				Analyzer authorOrgAnalyzer = authorOrgFactory.getAnalyzerForField(FieldNames.AUTHORORG, authorOrgTs);
				//authorOrgTs = authorOrgAnalyzer.getStream();
				authorOrgTs.reset();
			} catch (TokenizerException te) {
				logger.log(Level.SEVERE, "Tokenizer Exception caught in consuming authorOrg: " + authorOrg + " - ", te);
			}
		}
		
		Tokenizer fileIdTokenizer = new Tokenizer();
		TokenStream fileIdTs = new TokenStream();
		if (fileId != null && fileId != "") {
			try {
				fileIdTs = fileIdTokenizer.consume(fileId);
				AnalyzerFactory fileIdFactory = AnalyzerFactory.getInstance();
				Analyzer fileIdAnalyzer = fileIdFactory.getAnalyzerForField(FieldNames.FILEID, fileIdTs);
				//fileIdTs = fileIdAnalyzer.getStream();
				fileIdTs.reset();
			} catch (TokenizerException te) {
				logger.log(Level.SEVERE, "Tokenizer Exception caught in consuming fileId: " + fileId + " - ", te);
			}
		}
		
		Tokenizer categoryTokenizer = new Tokenizer();
		TokenStream categoryTs = new TokenStream();
		if (category != null && category != "") {
			try {
				categoryTs = categoryTokenizer.consume(category);
				AnalyzerFactory categoryFactory = AnalyzerFactory.getInstance();
				Analyzer categoryAnalyzer = categoryFactory.getAnalyzerForField(FieldNames.CATEGORY, categoryTs);
				//categoryTs = categoryAnalyzer.getStream();
				categoryTs.reset();
			} catch (TokenizerException te) {
				logger.log(Level.SEVERE, "Tokenizer Exception caught in consuming category: " + category + " - ", te);
			}
		}
		
		Tokenizer titleTokenizer = new Tokenizer();
		TokenStream titleTs = new TokenStream();
		if (title != null && title != "") {
			try {
				titleTs = titleTokenizer.consume(title);
				AnalyzerFactory titleFactory = AnalyzerFactory.getInstance();
				Analyzer titleAnalyzer = titleFactory.getAnalyzerForField(FieldNames.TITLE, titleTs);
				//titleTs = titleAnalyzer.getStream();
				titleTs.reset();
			} catch (TokenizerException te) {
				logger.log(Level.SEVERE, "Tokenizer Exception caught in consuming title: " + title + " - ", te);
			}
		}
		
		Tokenizer placeTokenizer = new Tokenizer("  ");
		TokenStream placeTs = new TokenStream();
		if (place != null && place != "") {
			try {
				placeTs = placeTokenizer.consume(place);
				AnalyzerFactory placeFactory = AnalyzerFactory.getInstance();
				Analyzer placeAnalyzer = placeFactory.getAnalyzerForField(FieldNames.PLACE, placeTs);
				//placeTs = placeAnalyzer.getStream();
				placeTs.reset();
			} catch (TokenizerException te) {
				logger.log(Level.SEVERE, "Tokenizer Exception caught in consuming place: " + place + " - ", te);
			}
		}
		
		Tokenizer newsDateTokenizer = new Tokenizer("  ");
		TokenStream newsDateTs = new TokenStream();
		if (newsDate != null && newsDate != "") {
			try {
				newsDateTs = newsDateTokenizer.consume(newsDate);
				AnalyzerFactory newsDateFactory = AnalyzerFactory.getInstance();
				Analyzer newsDateAnalyzer = newsDateFactory.getAnalyzerForField(FieldNames.NEWSDATE, newsDateTs);
				//newsDateTs = newsDateAnalyzer.getStream();
				newsDateTs.reset();
			} catch (TokenizerException te) {
				logger.log(Level.SEVERE, "Tokenizer Exception caught in consuming newsDate: " + newsDate + " - ", te);
			}
		}
		
		Tokenizer contentTokenizer = new Tokenizer();
		TokenStream contentTs = new TokenStream();
		if (content != null && content != "") {
			try {
				contentTs = contentTokenizer.consume(content);
				AnalyzerFactory contentFactory = AnalyzerFactory.getInstance();
				Analyzer contentAnalyzer = contentFactory.getAnalyzerForField(FieldNames.CONTENT, contentTs);
				//contentTs = contentAnalyzer.getStream();
				contentTs.reset();
			} catch (TokenizerException te) {
				logger.log(Level.SEVERE, "Tokenizer Exception caught in consuming content: " + content + " - ", te);
			}
		}
		
		// Document Dictionary
		if (!docDictionary.containsKey(fileName)) {
			// Document does not exist in Dictionary
			docDictionary.put(fileName, docId);
			currDocId = docId;
			docId++;
		} else {
			currDocId = docDictionary.get(fileName);
		}
		
		// Document Length
		docLength.put(currDocId, docWords.length);
		
		// Author Dictionary
		for (TokenStream authorTs : authorTsArr) {
			while (authorTs.hasNext()) {
				Token t = authorTs.next();
				String auth = t.toString();
				
				if (!authorDictionary.containsKey(auth)) {
					// Author does not exist in Dictionary
					authorDictionary.put(auth, authorId);
					authorFrequency.put(authorId, 1);
					addAuthorIndex(authorId, currDocId);
					authorId++;
				} else {
					// Author already exists in Dictionary
					int authId = authorDictionary.get(auth);
					int authFreq = authorFrequency.get(authId);
					authFreq += 1;
					authorFrequency.put(authId, authFreq);
					addAuthorIndex(authId, currDocId);
				}
				
				if (!authorDocFrequency.containsKey(auth)) {
					// Author does not exist in DocFrequency
					HashMap<Integer, Integer> value = new HashMap<Integer, Integer>();
					value.put(currDocId, 1);
					authorDocFrequency.put(auth, value);
				} else {
					// Author already exists in DocFrequency
					HashMap<Integer, Integer> value = authorDocFrequency.get(auth);
					int f;
					if (value.containsKey(currDocId)) {
						f = value.get(currDocId);
						f += 1;
					} else {
						f = 1;
					}
					
					value.put(currDocId, f);
					authorDocFrequency.put(auth, value);
				}
			}
		}
		
		// AuthorOrg to Author Dictionary
		while (authorOrgTs.hasNext()) {
			Token t = authorOrgTs.next();
			String authOrg = t.toString();
			
			if (!authorDictionary.containsKey(authOrg)) {
				// AuthorOrg does not exist in Dictionary
				authorDictionary.put(authOrg, authorId);
				authorFrequency.put(authorId, 1);
				addAuthorIndex(authorId, currDocId);
				authorId++;
			} else {
				// AuthorOrg already exists in Dictionary
				int authId = authorDictionary.get(authOrg);
				int authFreq = authorFrequency.get(authId);
				authFreq += 1;
				authorFrequency.put(authId, authFreq);
				addAuthorIndex(authId, currDocId);
			}
			
			if (!authorDocFrequency.containsKey(authOrg)) {
				// AuthorOrg does not exist in DocFrequency
				HashMap<Integer, Integer> value = new HashMap<Integer, Integer>();
				value.put(currDocId, 1);
				authorDocFrequency.put(authOrg, value);
			} else {
				// AuthorOrg already exists in DocFrequency
				HashMap<Integer, Integer> value = authorDocFrequency.get(authOrg);
				int f;
				if (value.containsKey(currDocId)) {
					f = value.get(currDocId);
					f += 1;
				} else {
					f = 1;
				}
				
				value.put(currDocId, f);
				authorDocFrequency.put(authOrg, value);
			}
		}
		
		// Place Dictionary
		while (placeTs.hasNext()) {
			Token t = placeTs.next();
			String p = t.toString();
			
			if (!placeDictionary.containsKey(p)) {
				// Place does not exist in Dictionary
				placeDictionary.put(p, placeId);
				placeFrequency.put(placeId, 1);
				addPlaceIndex(placeId, currDocId);
				placeId++;
			} else {
				// Place already exists in Dictionary
				int pId = placeDictionary.get(p);
				int placeFreq = placeFrequency.get(pId);
				placeFreq += 1;
				placeFrequency.put(pId, placeFreq);
				addPlaceIndex(pId, currDocId);
			}
			
			if (!placeDocFrequency.containsKey(p)) {
				// Place does not exist in DocFrequency
				HashMap<Integer, Integer> value = new HashMap<Integer, Integer>();
				value.put(currDocId, 1);
				placeDocFrequency.put(p, value);
			} else {
				// Place already exists in DocFrequency
				HashMap<Integer, Integer> value = placeDocFrequency.get(p);
				int f;
				if (value.containsKey(currDocId)) {
					f = value.get(currDocId);
					f += 1;
				} else {
					f = 1;
				}
				
				value.put(currDocId, f);
				placeDocFrequency.put(p, value);
			}
		}
		
		// Category Dictionary
		while (categoryTs.hasNext()) {
			Token t = categoryTs.next();
			String cat = t.toString();
			
			if (!categoryDictionary.containsKey(cat)) {
				// Category does not exist in Dictionary
				categoryDictionary.put(cat, categoryId);
				categoryFrequency.put(categoryId, 1);
				addCategoryIndex(categoryId, currDocId);
				categoryId++;
			} else {
				// Place already exists in Dictionary
				int catId = categoryDictionary.get(cat);
				int catFreq = placeFrequency.get(catId);
				catFreq += 1;
				categoryFrequency.put(catId, catFreq);
				addCategoryIndex(catId, currDocId);
			}
			
			if (!categoryDocFrequency.containsKey(cat)) {
				// Place does not exist in DocFrequency
				HashMap<Integer, Integer> value = new HashMap<Integer, Integer>();
				value.put(currDocId, 1);
				categoryDocFrequency.put(cat, value);
			} else {
				// Place already exists in DocFrequency
				HashMap<Integer, Integer> value = categoryDocFrequency.get(cat);
				int f;
				if (value.containsKey(currDocId)) {
					f = value.get(currDocId);
					f += 1;
				} else {
					f = 1;
				}
				
				value.put(currDocId, f);
				categoryDocFrequency.put(cat, value);
			}
		}
		
		// Term Dictionary
		while (titleTs.hasNext()) {
			Token t = titleTs.next();
			String titleStr = t.toString();
			
			if (!termDictionary.containsKey(titleStr)) {
				// Title term does not exist in Dictionary
				termDictionary.put(titleStr, termId);
				termFrequency.put(termId, 1);
				addTermIndex(termId, currDocId);
				termId++;
			} else {
				// Title term already exists in Dictionary
				int tId = termDictionary.get(titleStr);
				int tFreq = termFrequency.get(tId);
				tFreq += 1;
				termFrequency.put(tId, tFreq);
				addTermIndex(tId, currDocId);
			}
			
			if (!termDocFrequency.containsKey(titleStr)) {
				// Place does not exist in DocFrequency
				HashMap<Integer, Integer> value = new HashMap<Integer, Integer>();
				value.put(currDocId, 1);
				termDocFrequency.put(titleStr, value);
			} else {
				// Place already exists in DocFrequency
				HashMap<Integer, Integer> value = termDocFrequency.get(titleStr);
				int f;
				if (value.containsKey(currDocId)) {
					f = value.get(currDocId);
					f += 1;
				} else {
					f = 1;
				}
				
				value.put(currDocId, f);
				termDocFrequency.put(titleStr, value);
			}
		}
		while (newsDateTs.hasNext()) {
			Token t = newsDateTs.next();
			String nDate = t.toString();
			
			if (!termDictionary.containsKey(nDate)) {
				// Title term does not exist in Dictionary
				termDictionary.put(nDate, termId);
				termFrequency.put(termId, 1);
				addTermIndex(termId, currDocId);
				termId++;
			} else {
				// Title term already exists in Dictionary
				int tId = termDictionary.get(nDate);
				int tFreq = termFrequency.get(tId);
				tFreq += 1;
				termFrequency.put(tId, tFreq);
				addTermIndex(tId, currDocId);
			}
			
			if (!termDocFrequency.containsKey(nDate)) {
				// Place does not exist in DocFrequency
				HashMap<Integer, Integer> value = new HashMap<Integer, Integer>();
				value.put(currDocId, 1);
				termDocFrequency.put(nDate, value);
			} else {
				// Place already exists in DocFrequency
				HashMap<Integer, Integer> value = termDocFrequency.get(nDate);
				int f;
				if (value.containsKey(currDocId)) {
					f = value.get(currDocId);
					f += 1;
				} else {
					f = 1;
				}
				
				value.put(currDocId, f);
				termDocFrequency.put(nDate, value);
			}
		}
		while (contentTs.hasNext()) {
			Token t = contentTs.next();
			String contentTerm = t.toString();
			if (!termDictionary.containsKey(contentTerm)) {
				// Title term does not exist in Dictionary
				termDictionary.put(contentTerm, termId);
				termFrequency.put(termId, 1);
				addTermIndex(termId, currDocId);
				termId++;
			} else {
				// Title term already exists in Dictionary
				int tId = termDictionary.get(contentTerm);
				int tFreq = termFrequency.get(tId);
				tFreq += 1;
				termFrequency.put(tId, tFreq);
				addTermIndex(tId, currDocId);
			}
			
			if (!termDocFrequency.containsKey(contentTerm)) {
				// Place does not exist in DocFrequency
				HashMap<Integer, Integer> value = new HashMap<Integer, Integer>();
				value.put(currDocId, 1);
				termDocFrequency.put(contentTerm, value);
			} else {
				// Place already exists in DocFrequency
				HashMap<Integer, Integer> value = termDocFrequency.get(contentTerm);
				int f;
				if (value.containsKey(currDocId)) {
					f = value.get(currDocId);
					f += 1;
				} else {
					f = 1;
				}
				value.put(currDocId, f);
				termDocFrequency.put(contentTerm, value);
			}
		}
		
	}

	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 */
	public void close() throws IndexerException {
		//TODO
		boolean success = false;
		try {
			for (String fName : fileList.keySet()) {
				FileOutputStream w;
				ObjectOutputStream out;
				w = new FileOutputStream(fName);
				out = new ObjectOutputStream(w);
				
				if (fName == "index_term") {
					out.writeObject(termIndex);
					out.close();
				} else if (fName == "index_category") {
					out.writeObject(categoryIndex);
					out.close();
				} else if (fName == "index_place") {
					out.writeObject(placeIndex);
					out.close();
				} else if (fName == "index_author") {
					out.writeObject(authorIndex);
					out.close();
				} else if (fName == "dictionary_doc") {
					out.writeObject(docDictionary);
					out.close();
				} else if (fName == "dictionary_term") {
					out.writeObject(termDictionary);
					out.close();
				} else if (fName == "dictionary_author") {
					out.writeObject(authorDictionary);
					out.close();
				} else if (fName == "dictionary_place") {
					out.writeObject(placeDictionary);
					out.close();
				} else if (fName == "dictionary_category") {
					out.writeObject(categoryDictionary);
					out.close();
				} else if (fName == "frequency_author") {
					out.writeObject(authorFrequency);
					out.close();
				} else if (fName == "frequency_term") {
					out.writeObject(termFrequency);
					out.close();
				} else if (fName == "frequency_place") {
					out.writeObject(placeFrequency);
					out.close();
				} else if (fName == "frequency_category") {
					out.writeObject(categoryFrequency);
					out.close();
				} else if (fName == "frequency_termdoc") {
					out.writeObject(termDocFrequency);
					out.close();
				} else if (fName == "frequency_authordoc") {
					out.writeObject(authorDocFrequency);
					out.close();
				} else if (fName == "frequency_categorydoc") {
					out.writeObject(categoryDocFrequency);
					out.close();
				} else if (fName == "frequency_placedoc") {
					out.writeObject(placeDocFrequency);
					out.close();
				} else if (fName == "length_doc") {
					out.writeObject(docLength);
					out.close();
				}
				success = true;
			}
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "IOException caught in IndexWriter.close(): ", ioe);
			success = false;
		}
		
		if (success) {
			termIndex.clear();
			authorIndex.clear();
			categoryIndex.clear();
			placeIndex.clear();
			
			docDictionary.clear();
			termDictionary.clear();
			authorDictionary.clear();
			placeDictionary.clear();
			categoryDictionary.clear();
			
			authorFrequency.clear();
			termFrequency.clear();
			placeFrequency.clear();
			categoryFrequency.clear();
			
			termDocFrequency.clear();
			authorDocFrequency.clear();
			categoryDocFrequency.clear();
			placeDocFrequency.clear();
			
			docLength.clear();
		}
		 
	}
	
	public void addAuthorIndex(int aId, int dId) {
		if (authorIndex.containsKey(aId)) {
			ArrayList<Integer> docIds = authorIndex.get(aId);
			docIds.add(dId);
			Collections.sort(docIds);
			authorIndex.put(aId, docIds);
		} else {
			ArrayList<Integer> docIds = new ArrayList<Integer>();
			docIds.add(dId);
			authorIndex.put(aId, docIds);
		}
	}
	
	public void addPlaceIndex(int pId, int dId) {
		if (placeIndex.containsKey(pId)) {
			ArrayList<Integer> docIds = placeIndex.get(pId);
			docIds.add(dId);
			Collections.sort(docIds);
			placeIndex.put(pId, docIds);
		} else {
			ArrayList<Integer> docIds = new ArrayList<Integer>();
			docIds.add(dId);
			placeIndex.put(pId, docIds);
		}
	}
	
	public void addCategoryIndex(int cId, int dId) {
		if (categoryIndex.containsKey(cId)) {
			ArrayList<Integer> docIds = categoryIndex.get(cId);
			docIds.add(dId);
			Collections.sort(docIds);
			categoryIndex.put(cId, docIds);
		} else {
			ArrayList<Integer> docIds = new ArrayList<Integer>();
			docIds.add(dId);
			categoryIndex.put(cId, docIds);
		}
	}
	
	public void addTermIndex(int tId, int dId) {
		if (termIndex.containsKey(tId)) {
			ArrayList<Integer> docIds = termIndex.get(tId);
			docIds.add(dId);
			Collections.sort(docIds);
			termIndex.put(tId, docIds);
		} else {
			ArrayList<Integer> docIds = new ArrayList<Integer>();
			docIds.add(dId);
			termIndex.put(tId, docIds);
		}
	}
	
}
