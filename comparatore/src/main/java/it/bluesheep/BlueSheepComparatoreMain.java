package it.bluesheep;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.ComparatoreConstants;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.IProcessDataManager;
import it.bluesheep.io.datacompare.impl.Bet365ProcessDataManager;
import it.bluesheep.io.datacompare.impl.BetfairExchangeProcessDataManager;
import it.bluesheep.io.datacompare.impl.CSVProcessDataManager;
import it.bluesheep.io.datacompare.impl.TxOddsProcessDataManager;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.io.datainput.IInputDataManager;
import it.bluesheep.io.datainput.operationmanager.csv.CSVInputDataManagerImpl;
import it.bluesheep.io.datainput.operationmanager.service.impl.Bet365InputDataManagerImpl;
import it.bluesheep.io.datainput.operationmanager.service.impl.BetfairExchangeInputDataManagerImpl;
import it.bluesheep.io.datainput.operationmanager.service.impl.TxOddsInputDataManagerImpl;
import it.bluesheep.io.datainput.operationmanager.service.util.InputDataHelper;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.DirectoryFileUtilManager;
import it.bluesheep.util.json.AbstractBluesheepJsonConverter;
import it.bluesheep.util.zip.ZipUtil;

public class BlueSheepComparatoreMain {
	
	private static Properties properties = new Properties(); 
	private static Logger logger;

	public static void main(String[] args) throws Exception{
				
		
        try {
        	InputStream in = new FileInputStream(args[0]);
//        	InputStream in = new FileInputStream("../RISORSE_BLUESHEEP/bluesheepComparatore.properties");
            properties.load(in);
        	// va stabilito un path per il file delle proprieta'    	
            in.close();
        } catch (IOException exception) {
        	System.out.println("Error retrieving properties\n" + exception.getMessage());
            System.exit(-1);
        }
		
		logger = (new BlueSheepLogger(BlueSheepComparatoreMain.class)).getLogger();
		logger.log(Level.INFO, properties.entrySet().toString());
		
		InputDataHelper inputDataHelper = new InputDataHelper();
		
		long startTime = System.currentTimeMillis();
		/**
		 * 										TXODDS 
		 * 										
		 * 										START
		 */
		//inizializzo le variabili necessarie per effettuare tutte le chiamate
		IInputDataManager inputDataManager;
		IProcessDataManager processDataManager;
		ChiaveEventoScommessaInputRecordsMap eventoScommessaRecordMap = new ChiaveEventoScommessaInputRecordsMap();
		
		logger.log(Level.INFO, "Initializing TxOdds API query");
			
		//interrogazione di TxOdds
		inputDataManager = new TxOddsInputDataManagerImpl();
		List<AbstractInputRecord> txOddsMappedRecordsFromJsonBySport = null;
		//Per ogni sport
		for(Sport sport : Sport.values()) {
			logger.log(Level.INFO, "Delegate TxOdds request and mapping odds process of sport " + sport + " to " + inputDataManager.getClass().getName());
			txOddsMappedRecordsFromJsonBySport = inputDataManager.processAllData(sport);	
			for(AbstractInputRecord record : txOddsMappedRecordsFromJsonBySport) {
				eventoScommessaRecordMap.addToMapEventoScommessaRecord(record, sport);
			}
		}
		
		txOddsMappedRecordsFromJsonBySport = null;
		
		/**
		 * 										BET365 
		 * 										
		 * 										START
		 */
		//inizializzo le variabili necessarie per effettuare tutte le chiamate
		inputDataManager = new Bet365InputDataManagerImpl();
		processDataManager = new Bet365ProcessDataManager();
		
		logger.log(Level.INFO, "Initializing Bet365 API query");
		if(!inputDataHelper.isBlockedBookmaker("Bet365")) {
			//Per ogni sport
			for(Sport sport : Sport.values()) {
				logger.log(Level.INFO, "Delegate Bet365 request and mapping odds process of sport " + sport + " to " + inputDataManager.getClass().getName());
				List<AbstractInputRecord> bet365MappedRecordsFromJsonBySport = inputDataManager.processAllData(sport);	
				List<AbstractInputRecord> bet365MatchedEventsTxOdds = ((Bet365ProcessDataManager) processDataManager).
						compareAndCollectSameEventsFromBookmakerAndTxOdds(bet365MappedRecordsFromJsonBySport, eventoScommessaRecordMap);
				
				if(bet365MatchedEventsTxOdds != null && !bet365MatchedEventsTxOdds.isEmpty()) {
					bet365MappedRecordsFromJsonBySport = bet365MatchedEventsTxOdds;
				}
				
				for(AbstractInputRecord record : bet365MappedRecordsFromJsonBySport) {
					eventoScommessaRecordMap.addToMapEventoScommessaRecord(record, sport);
				}
				bet365MappedRecordsFromJsonBySport = null;
				bet365MatchedEventsTxOdds = null;
			}
		}else {
			logger.log(Level.WARNING, "Bet365 retrivial data process excluded.");
		}
		
		/**
		 * 										BET365 
		 * 										
		 * 										 END
		 */
		
		/**
		 * 								MANUAL INPUT ODDS BY CSV 
		 * 										
		 * 										  START
		 */
		
		CSVInputDataManagerImpl csvInputDataManager = new CSVInputDataManagerImpl();
		CSVProcessDataManager csvProcessDataManager = new CSVProcessDataManager();
		List<AbstractInputRecord> csvRecordList = csvInputDataManager.processManualOddsByCsv();
		
		List<AbstractInputRecord> csvRecordListUpdatedInfo = csvProcessDataManager.compareAndCollectSameEventsFromBookmakerAndTxOdds(csvRecordList, eventoScommessaRecordMap);
		List<AbstractInputRecord> toBeIteratedList = csvRecordListUpdatedInfo != null && !csvRecordListUpdatedInfo.isEmpty() ? csvRecordListUpdatedInfo : csvRecordList;
		
		for(AbstractInputRecord csvRecord : toBeIteratedList) {
			eventoScommessaRecordMap.addToMapEventoScommessaRecord(csvRecord);
		}
		
		csvRecordList = null;
		csvRecordListUpdatedInfo = null;
		toBeIteratedList = null;
		
		/**
		 * 								MANUAL INPUT ODDS BY CSV 
		 * 										
		 * 										  END
		 */
		
		//Avvio comparazione quote tabella 2
		List<RecordOutput> tabella2OutputList = new ArrayList<RecordOutput>();
		processDataManager = new TxOddsProcessDataManager();
		for(Sport sport : Sport.values()) {
			List<RecordOutput> oddsComparisonList = new ArrayList<RecordOutput>();
			try{
				logger.log(Level.INFO, "Starting odds comparison for PUNTA-PUNTA, sport " + sport);
				oddsComparisonList = processDataManager.compareOdds(eventoScommessaRecordMap, sport);
				tabella2OutputList.addAll(oddsComparisonList);
			}catch(Exception e) {
				logger.log(Level.SEVERE, "Error with odds comparison: error is " + e.getMessage(), e);
			}
			logger.log(Level.INFO, "Odds comparison for PUNTA-PUNTA completed. Rows mapped for sport " + sport + " = " + oddsComparisonList.size());
		}
    	
		if(tabella2OutputList != null && !tabella2OutputList.isEmpty()) {
			logger.log(Level.INFO, "PUNTA-PUNTA process calculation completed. Exporting data in JSON");
	    	String jsonString1 = AbstractBluesheepJsonConverter.convertToJSON(tabella2OutputList);
	    	PrintWriter writer1 = null;
	    	String outputFilenameTabella2 = BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.JSON_PP_RESULT_PATH) + new Timestamp(startTime).toString().replaceAll(ComparatoreConstants.REGEX_SPACE, "_").replaceAll(":", "-").replaceAll("\\.", "-")  + ".json";
	    	// Indico il path di destinazione dei miei dati
	    	try {
				DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary(BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.JSON_PP_RESULT_PATH));
	    		
				writer1 = new PrintWriter(outputFilenameTabella2, ComparatoreConstants.ENCODING_UTF_8);    	
		    	// Scrivo
		    	writer1.println(jsonString1);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error with file during saving : error is " + e.getMessage(), e);
			}finally {
				if(writer1 != null) {
					writer1.close();
				}
				jsonString1 = null;
			}

	    	logger.log(Level.INFO, "Export in JSON completed. File is " + outputFilenameTabella2);
		}
		
		tabella2OutputList = null;
		
		/**
		 * 										 TXODDS 
		 * 										
		 * 										  END
		 */
		
		/**
		 * 										 BETFAIR 
		 * 										
		 * 										  START
		 */
		
		logger.log(Level.INFO, "Initializing Betfair API query");
    	
		//Interrogazione Betfair
		inputDataManager = new BetfairExchangeInputDataManagerImpl();
		processDataManager = new BetfairExchangeProcessDataManager();
		
		List<AbstractInputRecord> betfairMappedRecordsFromJson = new ArrayList<AbstractInputRecord>();
		
		//Per ogni sport
		for(Sport sport : Sport.values()) {
			logger.log(Level.INFO, "Delegate Betfair request and mapping odds process of sport " + sport + " to " + inputDataManager.getClass().getName());
			betfairMappedRecordsFromJson = inputDataManager.processAllData(sport);	
			betfairMappedRecordsFromJson = ((BetfairExchangeProcessDataManager) processDataManager).
					compareAndCollectSameEventsFromBookmakerAndTxOdds(betfairMappedRecordsFromJson, eventoScommessaRecordMap);
			for(AbstractInputRecord record : betfairMappedRecordsFromJson) {
				eventoScommessaRecordMap.addToMapEventoScommessaRecord(record);
			}
		}
		betfairMappedRecordsFromJson = null;
		
		
		//Avvio comparazione quote tabella 1
		List<RecordOutput> tabella1OutputList = new ArrayList<RecordOutput>();
		for(Sport sport : Sport.values()) {
			List<RecordOutput> oddsComparisonList = new ArrayList<RecordOutput>();
			try{
				logger.log(Level.INFO, "Starting odds comparison for PUNTA-BANCA, sport " + sport);
				oddsComparisonList = processDataManager.compareOdds(eventoScommessaRecordMap, sport);
				tabella1OutputList.addAll(oddsComparisonList);
			}catch(Exception e) {
				logger.log(Level.SEVERE, "Error with odds comparison: error is " + e.getMessage(), e);
			}
			logger.log(Level.INFO, "Odds comparison for PUNTA-BANCA completed. Rows mapped for sport " + sport + " = " + oddsComparisonList.size());

		}
		
		long endTime = System.currentTimeMillis();
		
		if(tabella1OutputList != null && !tabella1OutputList.isEmpty()) {
			logger.log(Level.INFO, "PUNTA-BANCA process calculation completed. Exporting data in JSON");
	
	    	String jsonString2 = AbstractBluesheepJsonConverter.convertToJSON(tabella1OutputList);
	    	String outputFilenameTabella1 = BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.JSON_PB_RESULT_PATH) + new Timestamp(startTime).toString().replaceAll(ComparatoreConstants.REGEX_SPACE, "_").replaceAll(":", "-").replaceAll("\\.", "-")  + ".json";
	
	    	// Indico il path di destinazione dei miei dati
	    	PrintWriter writer2 = null;
			try {
				
				DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary(BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.JSON_PB_RESULT_PATH));
				
				writer2 = new PrintWriter(outputFilenameTabella1, ComparatoreConstants.ENCODING_UTF_8);
		    	// Scrivo
		    	writer2.println(jsonString2);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error with file during saving : error is " + e.getMessage(), e);
			}finally {
				if(writer2 != null) {
					writer2.close();
				}
				jsonString2 = null;
			}
	    	
	    	logger.log(Level.INFO, "Export in JSON completed. File is " + outputFilenameTabella1);
		}
		
		tabella1OutputList = null;
		
		/**
		 * 										BETFAIR 
		 * 										
		 * 										  END
		 */
    	
		logger.log(Level.INFO, "Total execution time = " + (endTime - startTime)/1000 + " seconds");
		
		ZipUtil zipUtil = new ZipUtil();
		zipUtil.zipLastRunLogFiles();
	}
	
	public static Properties getProperties() {
		return properties;
	}
}