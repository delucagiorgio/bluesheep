package it.bluesheep.servicehandler.servicemanager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

public abstract class ScrapedOddsServiceHandler extends AbstractBlueSheepServiceHandler {

	private String filename;
	
	protected ScrapedOddsServiceHandler(String filename) {
		super();
		this.filename = filename;
	}
	
	@Override
	protected List<AbstractInputRecord> populateMapWithInputRecord(){
		List<AbstractInputRecord> returnList = null;
		String pathToJson = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.SCRAPED_JSON_PATH);
		
		try {
			
		    BufferedReader reader = new BufferedReader(new FileReader(pathToJson + "/" + filename + ".json"));
			
		    String line = null;
		    String outputLine = "";
		    
		    while((line = reader.readLine()) != null) {
		    	outputLine += line;
		    }
		    
		    reader.close();
		    
		    returnList = mapInformationFromFileJSON(outputLine);
		    
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return returnList;
	}

	protected abstract List<AbstractInputRecord> mapInformationFromFileJSON(String outputLine);	

}
