package it.bluesheep.arbitraggi.imagegeneration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

	public Map<String, Map<String, Set<String>>> generate(List<ArbsRecord> inputRecords) {

		Map<String, Map<String, Set<String>>> eventXHTMLStringMap = new HashMap<String, Map<String, Set<String>>>();
		
		// Converte le stringhe in oggetti rappresentanti gli eventi
		InputReader inputReader = new InputReader();
	    List<Event> events = inputReader.convert(inputRecords, dateStartExecution);
	    
	    // Genero l'xhtml relativo ad ogni evento 
	    for (int i = 0; i < events.size(); i++) {
	    	Map<String, Set<String>> recordKeyLinksMap = new HashMap<String, Set<String>>(); 
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
		int maxThreadPoolSize = 10;
		ExecutorService executorService = Executors.newFixedThreadPool(maxThreadPoolSize);

	    for (int i = 0; i < xhtmlEvents.size(); i++) {
	    	HTMLSender htmlSender = new HTMLSender(i, xhtmlFilePath, xhtmlEvents.get(i));
			executorService.execute(htmlSender);
			
			boolean isLastQueueRequest = (i + 1) == xhtmlEvents.size();
			if((i + 1) % maxThreadPoolSize == 0 || isLastQueueRequest) {
				boolean timeoutReached = true;
				try {
					executorService.shutdown();

					timeoutReached = !executorService.awaitTermination(60, TimeUnit.SECONDS);
					if(timeoutReached) {
						executorService.shutdownNow();
					}
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
					if(!executorService.isShutdown()) {
						executorService.shutdownNow();
					}
				}
				if(!isLastQueueRequest) {
					executorService = Executors.newFixedThreadPool(maxThreadPoolSize);
				}
				
				if(timeoutReached) {
					logger.warn("" + this.getClass().getSimpleName() + " timeout reached = " + timeoutReached);
				}				
			}
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