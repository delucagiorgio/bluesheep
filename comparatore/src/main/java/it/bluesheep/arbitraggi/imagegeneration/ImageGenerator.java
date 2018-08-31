package it.bluesheep.arbitraggi.imagegeneration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.util.BlueSheepConstants;

/**
 * ImageGenerator gestisce la creazione dell'immagine da inviare a partire dalla lista di stringhe. 
 * Utilizzare il suo metodo generate in maniera trasparente
 * @author Fabio
 *
 */
public class ImageGenerator {

	private String dateStartExecution;
	private static Logger logger = Logger.getLogger(ImageGenerator.class);
	
	public ImageGenerator(String dateStartExecution) {
		this.dateStartExecution = dateStartExecution;
	}

	public void delete(String filename) {
		File file = new File(filename);
		file.delete();        
	}

	public Map<String, Map<String, List<String>>> generate(List<ArbsRecord> inputRecords) {

		Map<String, Map<String, List<String>>> eventXHTMLStringMap = new HashMap<String, Map<String, List<String>>>();
		
		// Converte le stringhe in oggetti rappresentanti gli eventi
		InputReader inputReader = new InputReader();
	    List<Event> events = inputReader.convert(inputRecords, dateStartExecution);
	    
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
	    
	    // Converto l'html in immagine .png
		String xhtmlFilePath = "../xhtml/";
		logger.info("xhtmlEvents size = " + xhtmlEvents.size());
		ExecutorService executorService = Executors.newFixedThreadPool(xhtmlEvents.size());

	    for (int i = 0; i < xhtmlEvents.size(); i++) {
	    	HTMLSender htmlSender = new HTMLSender(i, xhtmlFilePath, xhtmlEvents.get(i));
			executorService.execute(htmlSender);
	    }
				
		executorService.shutdown();
		
		try {
		    if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
		    	// chiudi tutto se dopo 1 min non ha finito con gli screen
		        executorService.shutdownNow();
		    } 
		} catch (InterruptedException e) {
		    executorService.shutdownNow();
		}

		
		return eventXHTMLStringMap;
	}
}

class HTMLSender implements Runnable {

	private int index;
	private String xhtmlFilePath;
	private String xhtmlSourceCode;
	private final static String pictureFormat = ".png";

	public HTMLSender(int i, String xhtmlFilePath, String htmlEvent) {
		super();
		this.index = i;
		this.xhtmlFilePath = xhtmlFilePath;
		this.xhtmlSourceCode = htmlEvent;
	}
	
	public void run(){
				
	    HtmlFileHandler xhtmlFileHandler = new HtmlFileHandler();
		Html2PngConverter converter = new Html2PngConverter();

		String xhtmlFileName = xhtmlFilePath + index + ".html";
  
		// Create a temp file
		xhtmlFileHandler.generateFile(xhtmlFileName, xhtmlSourceCode);
		
		// Converting the xhtml file to png
		//converter.convert(xhtmlFileName, pictureFileName);
		converter.convert(xhtmlFileName, xhtmlFilePath + index + pictureFormat);		
		
		// Deleting the temp file
		xhtmlFileHandler.delete(xhtmlFileName);
	}
}