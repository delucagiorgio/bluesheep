package it.bluesheep.servicehandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.util.TranslatorUtil;
import it.bluesheep.comparatore.io.datacompare.CompareProcessFactory;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.database.ConnectionPool;
import it.bluesheep.database.dao.impl.SaveOddProcessHistoryDAO;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.DirectoryFileUtilManager;
import it.bluesheep.util.json.AbstractBluesheepJsonConverter;
import it.bluesheep.util.zip.ZipUtil;

/**
 * 
 * @author GD
 * Classe del servizio di genereazione dei file per il bonus abusing di BlueSheep
 *
 */
public final class JsonGeneratorServiceHandler extends AbstractBlueSheepService{

	private static Logger logger;
	private static JsonGeneratorServiceHandler instance;
	private static long startTime;
	
	private JsonGeneratorServiceHandler() {
		super();
		logger = Logger.getLogger(JsonGeneratorServiceHandler.class);
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
			
			
			
			logger.info("Export data completed in " + (endTime - startTime) / 1000 + " seconds");
		}catch(Exception e) {
			logger.error("ERRORE THREAD :: " + e.getMessage(), e);
		}
		
		TranslatorUtil.saveTranslationOnFile();

	}

	/**
	 * Avvia le comparazione delle quote e salva ne salva i risultati su file
	 * @throws SQLException 
	 */
	private void startComparisonForBonusAbusingAndExportOnFiles() {
		
		Map<Service, List<RecordOutput>> comparisonResultMap = CompareProcessFactory.startComparisonOdds(this);
		
		for(Service service : comparisonResultMap.keySet()) {
			List<RecordOutput> tabellaOutput = comparisonResultMap.get(service);
			saveRecordsOnFile(service, tabellaOutput);
			if(comparisonResultMap.get(service) != null && !comparisonResultMap.get(service).isEmpty()) {
				try {
					Connection connection = ConnectionPool.getConnection();
					SaveOddProcessHistoryDAO processHistoryDao = SaveOddProcessHistoryDAO.getSaveOddProcessHistoryDAOInstance(connection);
					if(!processHistoryDao.stillRunningProcess(service)) {
						BlueSheepServiceHandlerManager.executor.submit(new OddsDatabaseSaveServiceHandler(comparisonResultMap.get(service), service));
						logger.info("Saving odd process for service " + service + " started");
					}else {
						logger.warn("Not executing " + service + " for save odds because still running");
					}
					ConnectionPool.releaseConnection(connection);
				}catch(SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * GD - 05/08/18
	 * Avvia la procedura di salvataggio delle comparazioni in base al tipo richiesto
	 * @param serviceName il servizio di cui si vogliono salvare le quote
	 * @param tabellaOutputList le comparazioni delle quote da salvare
	 */
	private void saveRecordsOnFile(Service serviceName, List<RecordOutput> tabellaOutputList) {
		if(tabellaOutputList != null && !tabellaOutputList.isEmpty()) {
			String pathOutputTable = "";
			if(Service.BETFAIR_SERVICENAME.equals(serviceName)) {
				pathOutputTable = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.JSON_PB_RESULT_PATH);
			}else if(Service.TXODDS_SERVICENAME.equals(serviceName)) {
				pathOutputTable = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.JSON_PP_RESULT_PATH);

			}
			logger.info("Exporting records in JSON for service " + serviceName);
	
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
