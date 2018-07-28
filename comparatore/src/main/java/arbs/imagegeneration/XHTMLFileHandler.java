package arbs.imagegeneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.bluesheep.util.BlueSheepLogger;

/**
 * Genera e cancella un file xhtml creato in maniera temporanea
 * @author Fabio
 *
 */
public class XHTMLFileHandler {
	
	private static Logger logger;
	
	public XHTMLFileHandler() {
		logger = (new BlueSheepLogger(XHTMLFileHandler.class)).getLogger();
	}
	
	public void delete(String filename) {
		File file = new File(filename);
		file.delete();
        
        return;
	}
	
	public void generateFile(String filename, String content) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);		
		}
		writer.println(content);
		writer.close();
	}
}
