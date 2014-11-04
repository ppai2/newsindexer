/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.*;
import java.util.logging.*;

/**
 * @author nikhillo
 * Class that parses a given file into a Document
 */
public class Parser {
	/**
	 * Static method to parse the given file into the Document object
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	
	public static final Logger logger = Logger.getLogger(Parser.class.getName());
	
	public static Document parse(String filename) throws ParserException {
		// TODO YOU MUST IMPLEMENT THIS
		
		if (filename == null || filename == "") {
			logger.log(Level.SEVERE, "Blank or null filename passed to Parser.parse(): " + filename);
			throw new ParserException();
		}
		
		if (filename.contains(".") || filename.contains("$") || filename.contains("%") || filename.contains("^")) {
			logger.log(Level.SEVERE, "Invalid filename passed to Parser.parse(): " + filename);
			throw new ParserException();
		}
		
		Document doc = new Document();
		InputStream in = null;
		BufferedReader fileReader = null;
		int lineCount = 0;
		String line = "";
		Boolean hasAuthor = false;
		Boolean hasAuthorOrg = false;
		
		String[] category = new String[1];
		String[] fileId = new String[1];
		String[] title = new String[1];
		String[] author = new String[1];
		String[] authorOrg = new String[1];
		String[] place = new String[1];
		String[] newsDate = new String[1];
		String[] content = new String[1];
		String fLine = "";
		String authorLine = "";
		
		if (filename.contains("\\")) {
			category[0] = filename.substring(0, filename.lastIndexOf("\\"));
			category[0] = category[0].substring(category[0].lastIndexOf("\\")+1);
			fileId[0] = filename.substring(filename.lastIndexOf("\\")+1);
		} else {
			logger.log(Level.SEVERE, "\\ not present in filename passed to Parser.parse(): " + filename);
		}
		
		
		try {
			in = new FileInputStream(new File(filename));
		} catch (FileNotFoundException fnfe) {
			logger.log(Level.SEVERE, "File Not Found Exception thrown for filename: " + filename, fnfe);
			throw new ParserException();
		}
		
		try {
			fileReader = new BufferedReader(new InputStreamReader(in));
			while((line = fileReader.readLine()) != null) {
				
				if (lineCount == 2) {
					title[0] = line;
				} else if (lineCount == 4) {
					if (line.contains("<AUTHOR>")) {
						hasAuthor = true;
						authorLine = line.substring(line.indexOf(">")+1, line.lastIndexOf("<")).trim();
						if (authorLine.contains(","))
							hasAuthorOrg = true;
					} else {
						hasAuthor = false;
						hasAuthorOrg = false;
						author = null;
						authorOrg = null;
						fLine = line;
					}
				} else if (lineCount == 5) {
					if (hasAuthor) {
						fLine = line;
					} else {
						content[0] += line;
					}
				} else if (lineCount > 5) {
					content[0] += line;
				}
				
				lineCount++;
				
			}
			fileReader.close();
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "IO Exception thrown while reading file for filename: " + filename, ioe);
		}
		
		if (hasAuthor) {
			author = getAuthor(authorLine);
		}
		if (hasAuthorOrg) {
			authorOrg[0] = getAuthorOrg(authorLine);
		}
		
		if (fLine.indexOf(" - ")+3 < fLine.length())
			content[0] = fLine.substring((fLine.indexOf(" - ")+3)) + content[0];
		if (fLine.contains(" - ")) {
			String[] placeDateArr = getPlaceDate(fLine);
			place[0] = placeDateArr[0];
			newsDate[0] = placeDateArr[1];
		} else {
			logger.log(Level.WARNING, "No Place and NewsDate found in first line of article!");
			place[0] = "";
			newsDate[0] = "";
		}
		
		if (category[0] != "" && category[0] != null) {
			doc.setField(FieldNames.CATEGORY, category);
		}
		
		if (fileId[0] != "" && fileId[0] != null) {
			doc.setField(FieldNames.FILEID, fileId);
		}
		
		if (title[0] != "" || title != null) {
			doc.setField(FieldNames.TITLE, title);
		}
		
		if (newsDate[0] != "" || newsDate != null) {
			doc.setField(FieldNames.NEWSDATE, newsDate);
		}
		
		if (place[0] != "" || place != null) {
			doc.setField(FieldNames.PLACE, place);
		}
		
		if (content[0] != "" || content != null) {
			doc.setField(FieldNames.CONTENT, content);
		}
		
		if (hasAuthor) {
			if (author[0] != "" || author[0] != null) {
				doc.setField(FieldNames.AUTHOR, author);
			}
		}
		
		if (hasAuthorOrg) {
			if (authorOrg[0] != "" || authorOrg[0] != null) {
				doc.setField(FieldNames.AUTHORORG, authorOrg);
			}
		}
		
		return doc;
		//return null;
	}
	
	public static String[] getAuthor(String authorLine) {
		String[] author;
		int byIndex = 0;
		
		if (authorLine.contains("By")) {
			byIndex = authorLine.indexOf("By");
		} else if (authorLine.contains("BY")) {
			byIndex = authorLine.indexOf("BY");
		} else if (authorLine.contains("by")) {
			byIndex = authorLine.indexOf("by");
		} else {
			logger.log(Level.WARNING, "In Parser.getAuthor(), byIndex = 0");
		}
		
		if (authorLine.contains(","))
			authorLine = authorLine.substring(byIndex+3, authorLine.lastIndexOf(",")).trim();
		else
			authorLine = authorLine.substring(byIndex+3).trim();
		
		if (authorLine.contains("and")) {
			authorLine = authorLine.replaceAll("and", ",");
		} else if (authorLine.contains("And")) {
			authorLine = authorLine.replaceAll("And", ",");
		} else if (authorLine.contains("AND")) {
			authorLine = authorLine.replaceAll("AND", ",");
		}
		
		author = authorLine.split(",");
		
		return author;
	}
	
	public static String getAuthorOrg(String authorLine) {
		String authorOrg = "";
		
		authorLine = authorLine.substring(authorLine.lastIndexOf(",")+1);
		authorOrg = authorLine.trim();
		
		return authorOrg;
	}
	
	public static String[] getPlaceDate(String fLine) {
		
		String[] placeDate = new String[2];
		placeDate[0] = "";
		placeDate[1] = "";
		String place = "";
		String newsDate = "";
		String placeDateLine = "";
		
		try {
			placeDateLine = fLine.substring(0, fLine.indexOf(" - ")).trim();
			if (placeDateLine.contains(","))
				place = placeDateLine.substring(0, placeDateLine.lastIndexOf(",")).trim();
			if (placeDateLine.contains(",") && (placeDateLine.lastIndexOf(",")+1) < placeDateLine.length())
				newsDate = placeDateLine.substring(placeDateLine.lastIndexOf(",")+1).trim();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception thrown in Parser.getPlaceDate(): ", e);
		}
		
		if (place != null && place != "" && newsDate != null && newsDate != "") {
			placeDate[0] = place;
			placeDate[1] = newsDate;
		} else {
			logger.log(Level.WARNING, "In Parser.getPlaceDate(), Place or NewsDate is null/empty");
		}
		
		return placeDate;
	}

}
