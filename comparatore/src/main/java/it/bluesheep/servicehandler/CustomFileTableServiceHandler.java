package it.bluesheep.servicehandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.io.datacompare.CompareProcessFactory;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.DirectoryFileUtilManager;
import it.bluesheep.util.json.AbstractBluesheepJsonConverter;
import it.bluesheep.util.zip.ZipUtil;

public class CustomFileTableServiceHandler extends AbstractBlueSheepService {

	private static Logger logger;
	private static CustomFileTableServiceHandler instance;
	private static long startTime;
	
	private CustomFileTableServiceHandler() {
		super();
		logger = Logger.getLogger(CustomFileTableServiceHandler.class);
	}
	
	public static synchronized CustomFileTableServiceHandler getCustomFileTableServiceHandlerInstance() {
		if(instance == null) {
			instance = new CustomFileTableServiceHandler();
		}
		
		return instance;
	}
	
	@Override
	public void run() {
		
		try {
			startTime = System.currentTimeMillis();
			logger.info("Starting custom file table creation");
			
			startComparisonForBonusAbusingAndExportOnFiles();
			
			long endTime = System.currentTimeMillis();
			logger.info("Custom file table creation finished in " + ((endTime - startTime) / 1000) + " seconds");
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}
	
	/**
	 * Avvia le comparazione delle quote e salva ne salva i risultati su file
	 * @throws SQLException 
	 */
	private void startComparisonForBonusAbusingAndExportOnFiles() {
		
		Map<Service, List<RecordOutput>> comparisonResultMap = CompareProcessFactory.startComparisonOdds(this);
		
		List<RecordOutput> tabellaOutput = comparisonResultMap.get(Service.TXODDS_SERVICENAME);
		if(tabellaOutput != null) {
			saveRecordsOnFile(tabellaOutput);
		}else {
			logger.info("No records to be saved on the file");
		}
	}
	
	/**
	 * GD - 05/08/18
	 * Avvia la procedura di salvataggio delle comparazioni in base al tipo richiesto
	 * @param serviceName il servizio di cui si vogliono salvare le quote
	 * @param tabellaOutputList le comparazioni delle quote da salvare
	 */
	private void saveRecordsOnFile(List<RecordOutput> tabellaOutputList) {
		if(tabellaOutputList != null && !tabellaOutputList.isEmpty()) {
			String pathOutputTable = "";
			pathOutputTable = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.JSON_PP_CUSTOM_RESULT_PATH);
			logger.info("Exporting records in JSON for service " + Service.CUSTOM_FILETABLE_CREATOR_SERVICENAME);
	
	    	String jsonString = AbstractBluesheepJsonConverter.convertToJSON(tabellaOutputList);
	    	String outputFilenameTabella = pathOutputTable + new Timestamp(startTime).toString().replaceAll(" ", "_").replaceAll(":", "-").replaceAll("\\.", "-")  + ".json";
	
	    	// Indico il path di destinazione dei miei dati
	    	PrintWriter writer = null;
			try {
				
				DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary(pathOutputTable);
				
				writer = new PrintWriter(outputFilenameTabella, BlueSheepConstants.ENCODING_UTF_8);
		    	// Scrivo
		    	writer.println(jsonString);
			} catch (IOException e) {
				logger.error("Error with file during saving : error is " + e.getMessage(), e);
			}finally {
				if(writer != null) {
					writer.close();
				}
				jsonString = null;
			}
	    	
	    	logger.info("Export in JSON completed. File is " + outputFilenameTabella);
	    	

	    	try {
	    		logger.debug("Starting zipping old JSON file");
	    		ZipUtil zipUtil = new ZipUtil();
	    		zipUtil.zipOldJsonFiles(pathOutputTable);
	    		logger.debug("Zipping old JSON file completed");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
