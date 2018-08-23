package it.bluesheep.arbitraggi.imagegeneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Genera e cancella un file Html creato in maniera temporanea per la creazione dell'immagine di output
 * @author Fabio
 *
 */
public class HtmlFileHandler {
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
			e.printStackTrace();
		}
		writer.println(content);
		writer.close();
	}
}
