package arbs.imagegeneration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arbs.util.ArbsConstants;

/**
 * ImageGenerator gestisce la creazione dell'immagine da inviare a partire dalla lista di stringhe. 
 * Utilizzare il suo metodo generate in maniera trasparente :)
 * @author Fabio
 *
 */
public class ImageGenerator {

	private final static String PICTURE_FORMAT = ".png";
	
	public void delete(String filename) {
		File file = new File(filename);
		file.delete();
	}

	public Map<String, List<String>> generate(List<String> inputRecords) {

		Map<String, List<String>> eventXHTMLStringMap = new HashMap<String, List<String>>();
		
		// Converte le stringhe in oggetti rappresentanti gli eventi
		InputReader inputReader = new InputReader();
	    List<Event> events = inputReader.convert(inputRecords);
	
	    // Genero l'xhtml relativo ad ogni evento 
	    EventToXHTML eventToXhtml = new EventToXHTML();
		List<String> xhtmlEvents = new ArrayList<String>();
	    for (int i = 0; i < events.size(); i++) {
    		xhtmlEvents.add(eventToXhtml.convert(events.get(i), i + 1, events.size()));
    		eventXHTMLStringMap.put(events.get(i).getUnifiedKeyAndLinks() + ArbsConstants.IMAGE_ID + (i + 1), events.get(i).getLinkBook());
	    }
	    
	    // Converto l'xhtml in immagine .png
	    XHTMLFileHandler xhtmlFileHandler = new XHTMLFileHandler();
		XHTML2PngConverter converter = new XHTML2PngConverter();
		String xhtmlFileName = "../xhtml/xhtml.xhtml";
		String xhtmlSourceCode;
	    for (int i = 0; i < xhtmlEvents.size(); i++) {
			
	    	xhtmlSourceCode = xhtmlEvents.get(i);
	    	
			// Create a temp file
			xhtmlFileHandler.generateFile(xhtmlFileName, xhtmlSourceCode);
				
			// Converting the xhtml file to png
			//converter.convert(xhtmlFileName, pictureFileName);
			converter.convert(xhtmlFileName, "../xhtml/" + i + PICTURE_FORMAT);		
			
			// Deleting the temp file
			xhtmlFileHandler.delete(xhtmlFileName);
	    }

		return eventXHTMLStringMap;
	}
}
