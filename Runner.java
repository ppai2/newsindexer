/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.File;
import java.util.logging.*;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;

/**
 * @author nikhillo
 *
 */
public class Runner {

	/**
	 * 
	 */
	
	public static final Logger logger = Logger.getLogger(Runner.class.getName());
	
	public Runner() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String ipDir = args[0];
		String indexDir = args[1];
		//more? idk!
		System.out.println("ipDir:" + ipDir);
		System.out.println("indexDir:" + indexDir);
		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();
		
		String[] files;
		File dir;
		
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		
		try {
			for (String cat : catDirectories) {
				dir = new File(ipDir+ File.separator+ cat);
				dir = new File(ipDir);
				files = dir.list();
				
				if (files == null) {
					continue;
				}
				
				for (String f : files) {
					try {
						d = Parser.parse(dir.getAbsolutePath() + File.separator +f);
						writer.addDocument(d);
						logger.log(Level.INFO, "Document written to index: " + d.getField(FieldNames.FILEID)[0]);
					} catch (ParserException e) {
						// TODO Auto-generated catch block
						logger.log(Level.SEVERE, "Parsing Exception thrown for document: " + d.getField(FieldNames.CATEGORY)[0] + File.separator + d.getField(FieldNames.FILEID)[0]);
						e.printStackTrace();
					} 
					
				}
				
			}
			
			writer.close();
			logger.log(Level.INFO, "Finished writing Documents!");
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
