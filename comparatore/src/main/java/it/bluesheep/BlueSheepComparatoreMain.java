package it.bluesheep;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.IProcessDataManager;
import it.bluesheep.io.datacompare.impl.ExchangeProcessDataManager;
import it.bluesheep.io.datacompare.impl.TxOddsProcessDataManager;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.io.datainput.IInputDataManager;
import it.bluesheep.io.datainput.operationmanager.impl.BookmakerVsBookmakerInputDataManagerImpl;
import it.bluesheep.io.datainput.operationmanager.impl.ExchangeVsBookmakerInputDataManagerImpl;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.json.AbstractBluesheepJsonConverter;
import it.bluesheep.util.zip.ZipUtil;

public class BlueSheepComparatoreMain {
	
	private static Properties properties = new Properties(); 
	private static Logger logger;
	
	static {
        try {
            InputStream in = BlueSheepComparatoreMain.class.getResourceAsStream("/bluesheepComparatore.properties");
            properties.load(in);
        	// va stabilito un path per il file delle proprieta'    	
            in.close();
        } catch (IOException exception) {
        	System.out.println("Error retrieving properties\n" + exception.getMessage());
            System.exit(-1);
        }
	}
	
	
	public static void main(String[] args) throws IOException{
				
		logger = (new BlueSheepLogger(BlueSheepComparatoreMain.class)).getLogger();
		
		long startTime = System.currentTimeMillis();
		
		//inizializzo le variabili necessarie per effettuare tutte le chiamate
		IInputDataManager inputDataManager;
		IProcessDataManager processDataManager;
		ChiaveEventoScommessaInputRecordsMap eventoScommessaRecordMap = new ChiaveEventoScommessaInputRecordsMap();
		
		logger.info("Initializing TxOdds API query");
			
		//interrogazione di TxOdds
		inputDataManager = new BookmakerVsBookmakerInputDataManagerImpl();
		List<AbstractInputRecord> txOddsMappedRecordsFromJson = new ArrayList<AbstractInputRecord>();

		//Per ogni sport
		for(Sport sport : Sport.values()) {
			logger.info("Delegate TxOdds request and mapping odds process of sport " + sport + " to " + inputDataManager.getClass().getName());
			List<AbstractInputRecord> txOddsMappedRecordsFromJsonBySport = inputDataManager.processAllData(sport);	
			for(AbstractInputRecord record : txOddsMappedRecordsFromJsonBySport) {
				eventoScommessaRecordMap.addToMapEventoScommessaRecord(record);
			}
			txOddsMappedRecordsFromJson.addAll(txOddsMappedRecordsFromJsonBySport);
		}
		
		
		//Avvio comparazione quote tabella 1
		List<RecordOutput> tabella2OutputList = new ArrayList<RecordOutput>();
		processDataManager = new TxOddsProcessDataManager();
		for(Sport sport : Sport.values()) {
			try{
				logger.info("Starting odds comparison for Tabella2 (TxOdds vs TxOdds) for sport " + sport);
				tabella2OutputList.addAll(processDataManager.compareOdds(eventoScommessaRecordMap, sport));
			}catch(Exception e) {
				logger.severe("Error with odds comparison: error is\n" + e.getStackTrace());
			}
		}
    	
		if(tabella2OutputList != null && !tabella2OutputList.isEmpty()) {
			logger.info("Tabella 2 (TxOdds vs TxOdds) process calculation completed. Exporting data in JSON");
	    	String jsonString1 = AbstractBluesheepJsonConverter.convertToJSON(tabella2OutputList);
	    	PrintWriter writer1 = null;
	    	String outputFilenameTabella2 = BlueSheepComparatoreMain.getProperties().getProperty("PATH_OUTPUT_TABLE2") + new Timestamp(System.currentTimeMillis()).toString().replaceAll(" ", "_").replaceAll(":", "-").replaceAll("\\.", "-")  + ".json";
	    	// Indico il path di destinazione dei miei dati
	    	try {
				writer1 = new PrintWriter(outputFilenameTabella2, "UTF-8");    	
		    	// Scrivo
		    	writer1.println(jsonString1);
		    	writer1.close();
			} catch (IOException e) {
				logger.severe("Error with file during saving : error is\n" + e.getStackTrace());
			}

	    	logger.info("Export in JSON completed. File is " + outputFilenameTabella2);
		}
		
		logger.info("Initializing Betfair API query");
    	
		//Interrogazione Betfair
		inputDataManager = new ExchangeVsBookmakerInputDataManagerImpl();
		processDataManager = new ExchangeProcessDataManager();
		
		List<AbstractInputRecord> betfairMappedRecordsFromJson = new ArrayList<AbstractInputRecord>();
		
		//Per ogni sport
		for(Sport sport : Sport.values()) {
			logger.info("Delegate Betfair request and mapping odds process of sport " + sport + " to " + inputDataManager.getClass().getName());
			betfairMappedRecordsFromJson = inputDataManager.processAllData(sport);	
			betfairMappedRecordsFromJson = ((ExchangeProcessDataManager) processDataManager).
					compareAndCollectSameEventsFromExchangeAndBookmakers(betfairMappedRecordsFromJson, eventoScommessaRecordMap);
			for(AbstractInputRecord record : betfairMappedRecordsFromJson) {
				eventoScommessaRecordMap.addToMapEventoScommessaRecord(record);
			}
		}
		
		//Avvio comparazione quote tabella 2
		List<RecordOutput> tabella1OutputList = new ArrayList<RecordOutput>();
		processDataManager = new ExchangeProcessDataManager();
		for(Sport sport : Sport.values()) {
			try{
				logger.info("Starting odds comparison for Tabella1 (TxOdds vs Exchange) for sport " + sport);
				tabella1OutputList.addAll(processDataManager.compareOdds(eventoScommessaRecordMap, sport));
			}catch(Exception e) {
				logger.severe("Error with odds comparison: error is\n" + e.getStackTrace());
			}
		}
		
		long endTime = System.currentTimeMillis();
		
		if(tabella1OutputList != null && !tabella1OutputList.isEmpty()) {
			logger.info("Tabella 1 (TxOdds vs Exchange) process calculation completed. Exporting data in JSON");
	
	    	String jsonString2 = AbstractBluesheepJsonConverter.convertToJSON(tabella1OutputList);
	    	String outputFilenameTabella1 = BlueSheepComparatoreMain.getProperties().getProperty("PATH_OUTPUT_TABLE1") + new Timestamp(System.currentTimeMillis()).toString().replaceAll(" ", "_").replaceAll(":", "-").replaceAll("\\.", "-")  + ".json";
	
	    	// Indico il path di destinazione dei miei dati
	    	PrintWriter writer2 = null;
			try {
				writer2 = new PrintWriter(outputFilenameTabella1, "UTF-8");
		    	// Scrivo
		    	writer2.println(jsonString2);
		    	writer2.close();
			} catch (IOException e) {
				logger.severe("Error with file during saving : error is\n" + e.getStackTrace());
			}
	    	
	    	logger.info("Export in JSON completed. File is " + outputFilenameTabella1);
		}
    	
		logger.info("Total execution time = " + (endTime - startTime)/1000);
		
		ZipUtil zipUtil = new ZipUtil();
		zipUtil.zipLastRunLogFiles();
	}
	
	public static Properties getProperties() {
		return properties;
	}
}