package it.bluesheep.servicehandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.io.datacompare.CompareProcessFactory;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.DirectoryFileUtilManager;
import it.bluesheep.util.json.AbstractBluesheepJsonConverter;

public final class JsonGeneratorServiceHandler extends AbstractBlueSheepService{

	private static Logger logger;
	private static JsonGeneratorServiceHandler instance;
	private static long startTime;
	
	private JsonGeneratorServiceHandler() {
		super();
		logger = (new BlueSheepLogger(JsonGeneratorServiceHandler.class)).getLogger();
	}
	
	public static synchronized JsonGeneratorServiceHandler getJsonGeneratorServiceHandlerInstance() {
		if(instance == null) {
			instance = new JsonGeneratorServiceHandler();
		}
		return instance;
	}
	
	@Override
	public void run() {
		try {
			startTime = System.currentTimeMillis();
			
			startComparisonForBonusAbusingAndExportOnFiles();
			
			long endTime = System.currentTimeMillis();
			
			logger.log(Level.INFO, "Export data completed in " + (endTime - startTime) / 1000 + " seconds");
		}catch(Exception e) {
			logger.log(Level.SEVERE, "ERRORE THREAD :: " + e.getMessage(), e);
		}
	}

	private void startComparisonForBonusAbusingAndExportOnFiles() {
		
		Map<Service, List<RecordOutput>> comparisonResultMap = CompareProcessFactory.startComparisonOdds(this);
		
		for(Service service : comparisonResultMap.keySet()) {
			List<RecordOutput> tabellaOutput = comparisonResultMap.get(service);
			saveRecordsOnFile(service, tabellaOutput);
		}
	}
	
	private void saveRecordsOnFile(Service serviceName, List<RecordOutput> tabellaOutputList) {
		if(tabellaOutputList != null && !tabellaOutputList.isEmpty()) {
			String pathOutputTable = "";
			if(Service.BETFAIR_SERVICENAME.equals(serviceName)) {
				pathOutputTable = BlueSheepConstants.JSON_PB_RESULT_PATH;
			}else if(Service.TXODDS_SERVICENAME.equals(serviceName)) {
				pathOutputTable = BlueSheepConstants.JSON_PP_RESULT_PATH;
			}
			logger.log(Level.INFO, "Exporting records in JSON for service " + serviceName);
	
	    	String jsonString = AbstractBluesheepJsonConverter.convertToJSON(tabellaOutputList);
	    	String outputFilenameTabella = BlueSheepComparatoreMain.getProperties().getProperty(pathOutputTable) + new Timestamp(startTime).toString().replaceAll(" ", "_").replaceAll(":", "-").replaceAll("\\.", "-")  + ".json";
	
	    	// Indico il path di destinazione dei miei dati
	    	PrintWriter writer = null;
			try {
				
				DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary(BlueSheepComparatoreMain.getProperties().getProperty(pathOutputTable));
				
				writer = new PrintWriter(outputFilenameTabella, BlueSheepConstants.ENCODING_UTF_8);
		    	// Scrivo
		    	writer.println(jsonString);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error with file during saving : error is " + e.getMessage(), e);
			}finally {
				if(writer != null) {
					writer.close();
				}
				jsonString = null;
			}
	    	
	    	logger.log(Level.INFO, "Export in JSON completed. File is " + outputFilenameTabella);
		}
	}

}
