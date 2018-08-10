package it.bluesheep.arbitraggi.imagegeneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import it.bluesheep.util.BlueSheepConstants;

/**
 * Genera e cancella un file xhtml creato in maniera temporanea
 * @author Fabio
 *
 */
public class XHTMLFileHandler {
	
	private static Logger logger;
	
	public XHTMLFileHandler() {
		logger = Logger.getLogger(XHTMLFileHandler.class);
	}
	
	public void delete(String filename) {
		File file = new File(filename);
		file.delete();
        
        return;
	}
	
	public void generateFile(String filename, String content) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename, BlueSheepConstants.ENCODING_UTF_8);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);		
		}
		writer.println(content);
		writer.close();
	}
}
