package it.bluesheep.arbitraggi.imagegeneration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bluesheep.util.BlueSheepConstants;

/**
 * ImageGenerator gestisce la creazione dell'immagine da inviare a partire dalla lista di stringhe. 
 * Utilizzare il suo metodo generate in maniera trasparente
 * @author Fabio
 *
 */
public class ImageGenerator {

	private final static String pictureFormat = ".png";
	
	public void delete(String filename) {
		File file = new File(filename);
		file.delete();        
        return;
	}

	public Map<String, Map<String, List<String>>> generate(List<String> inputRecords) {

		Map<String, Map<String, List<String>>> eventXHTMLStringMap = new HashMap<String, Map<String, List<String>>>();
		
		// Converte le stringhe in oggetti rappresentanti gli eventi
		InputReader inputReader = new InputReader();
	    List<Event> events = inputReader.convert(inputRecords);
	    
	    // Genero l'xhtml relativo ad ogni evento 
	    for (int i = 0; i < events.size(); i++) {
	    	Map<String, List<String>> recordKeyLinksMap = new HashMap<String, List<String>>(); 
	    	recordKeyLinksMap.put(events.get(i).getUnifiedKeyAndLinks() + BlueSheepConstants.IMAGE_ID + (i + 1), events.get(i).getLinkBook());
    		eventXHTMLStringMap.put("" + (i + 1), recordKeyLinksMap);
	    }
	    
	    
	    // Genero l'xhtml relativo ad ogni evento 
		List<String> xhtmlEvents = new ArrayList<String>();
	    for (int i = 0; i < events.size(); i++) {
    		xhtmlEvents.add(events.get(i).toHtml(i + 1, events.size()));
	    }
	    
	    // Converto l'xhtml in immagine .png
	    HtmlFileHandler xhtmlFileHandler = new HtmlFileHandler();
		Html2PngConverter converter = new Html2PngConverter();
		String xhtmlFileName = "../xhtml/html.html";
		String xhtmlSourceCode;
	    for (int i = 0; i < xhtmlEvents.size(); i++) {
			
	    	xhtmlSourceCode = xhtmlEvents.get(i);
	    	
			// Create a temp file
			xhtmlFileHandler.generateFile(xhtmlFileName, xhtmlSourceCode);
					
			// Converting the xhtml file to png
			//converter.convert(xhtmlFileName, pictureFileName);
			converter.convert(xhtmlFileName, "../xhtml/" + i + pictureFormat);		
			
			// Deleting the temp file
//			xhtmlFileHandler.delete(xhtmlFileName);
	    }
		
		return eventXHTMLStringMap;
	}
}
