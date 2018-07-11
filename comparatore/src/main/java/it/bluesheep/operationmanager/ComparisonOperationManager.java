package it.bluesheep.operationmanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.IProcessDataManager;
import it.bluesheep.io.datacompare.impl.ProcessDataManagerFactory;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.io.datacompare.util.ICompareInformationEvents;
import it.bluesheep.io.datainput.operationmanager.service.impl.InputDataManagerFactory;
import it.bluesheep.io.datainput.operationmanager.service.util.InputDataHelper;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.DirectoryFileUtilManager;
import it.bluesheep.util.json.AbstractBluesheepJsonConverter;

public class ComparisonOperationManager {
	
	private static Logger logger;
	private static ComparisonOperationManager instance;
	private static Map<String, List<Sport>> serviceSportMap;
	private static Map<String, Map<Sport,List<AbstractInputRecord>>> allServiceApiMapResult;
	private static String[] splittedServiceName;
	private static long startTime;

	private ExecutorService executor;
	private ChiaveEventoScommessaInputRecordsMap eventoScommessaRecordMap;

	private ComparisonOperationManager() {
		logger = (new BlueSheepLogger(ComparisonOperationManager.class)).getLogger();
	}
	
	public static synchronized ComparisonOperationManager getComparisonOperationManagerFactory() {
		if(instance == null) {
			instance = new ComparisonOperationManager();
			logger.info("ComparisonOperationManager initialized");
			logger.info(BlueSheepComparatoreMain.getProperties().entrySet().toString());
			allServiceApiMapResult = new ConcurrentHashMap<String, Map<Sport,List<AbstractInputRecord>>>();
			splittedServiceName = BlueSheepComparatoreMain.getProperties().getProperty("SERVICE_NAME").split(",");
		}
		return instance;
	}
	
	private void populateServiceSportMap() {
		
		if(splittedServiceName != null) {
			for(String serviceName : splittedServiceName) {
				List<Sport> sportList = new ArrayList<Sport>();
				if(serviceName != null) {
					switch(serviceName) {
						case "TX_ODDS":
						case "BETFAIR":
						case "BET365":
							sportList = Arrays.asList(Sport.CALCIO, Sport.TENNIS);
							serviceSportMap.put(serviceName, sportList);
							break;
						case "CSV":
							//Generico, per dare omogeneità al processo
							sportList = Arrays.asList(Sport.CALCIO);
							serviceSportMap.put(serviceName, sportList);
							break;
						default:
							break;
					}
				}
			}
		}
		
	}

	/**
	 * GD - 11/07/18
	 * Avvia l'acquisizione dati, il mapping dei campi dei record, applica le trasformazioni dei dati secondo i criteri, 
	 * avvia il processing per il formato di output.
	 */
	public void startProcess() {
		
		if(serviceSportMap == null) {
			serviceSportMap = new HashMap<String, List<Sport>>();
			populateServiceSportMap();
		}
	
		startTime = System.currentTimeMillis();

		startDataRetrivial();
		
		startProcessingDataTransformation();
		
		startComparisonOddsAndSaveOnFileJSON();
		
		long endTime = System.currentTimeMillis();
		
		logger.info("Total execution time = " + (endTime - startTime)/1000 + " seconds");
		
	}

	private void startComparisonOddsAndSaveOnFileJSON() {
		
		IProcessDataManager processDataManager;
		List<RecordOutput> tabellaOutputList = new ArrayList<RecordOutput>();
		
		processDataManager = ProcessDataManagerFactory.getProcessDataManagerByString("TX_ODDS");
		for(Sport sport : Sport.values()) {
			try {
				tabellaOutputList.addAll(processDataManager.compareOdds(eventoScommessaRecordMap, sport));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.severe(e.getMessage());
			}
		}
		
		saveOutputOnFile("TX_ODDS", tabellaOutputList);
		
		tabellaOutputList.clear();
		
		processDataManager = ProcessDataManagerFactory.getProcessDataManagerByString("BETFAIR");

		for(Sport sport : Sport.values()) {
			try {
				tabellaOutputList.addAll(processDataManager.compareOdds(eventoScommessaRecordMap, sport));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.severe(e.getMessage());
			}
		}
		
		saveOutputOnFile("BETFAIR", tabellaOutputList);
		
		tabellaOutputList.clear();
	}

	private void saveOutputOnFile(String string, List<RecordOutput> tabellaOutputList) {
		
		String pathTable = null;
		if("BETFAIR".equalsIgnoreCase(string)) {
			pathTable = "PATH_OUTPUT_TABLE1";
		}else if("TX_ODDS".equalsIgnoreCase(string)) {
			pathTable = "PATH_OUTPUT_TABLE2";
		}
		
		if(tabellaOutputList != null && !tabellaOutputList.isEmpty() && pathTable != null) {
			logger.info("Process calculation completed. Exporting data in JSON");
	    	String jsonString1 = AbstractBluesheepJsonConverter.convertToJSON(tabellaOutputList);
	    	PrintWriter writer1 = null;
	    	String outputFilenameTabella2 = BlueSheepComparatoreMain.getProperties().getProperty(pathTable) + new Timestamp(startTime).toString().replaceAll(" ", "_").replaceAll(":", "-").replaceAll("\\.", "-")  + ".json";
	    	// Indico il path di destinazione dei miei dati
	    	try {
				DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary(BlueSheepComparatoreMain.getProperties().getProperty(pathTable));
	    		
				writer1 = new PrintWriter(outputFilenameTabella2, "UTF-8");    	
		    	// Scrivo
		    	writer1.println(jsonString1);
			} catch (IOException e) {
				logger.severe("Error with file during saving : error is " + e.getMessage());
			}finally {
				if(writer1 != null) {
					writer1.close();
				}
				jsonString1 = null;
			}
		}
		
	}

	private void startProcessingDataTransformation() {
		
		if(splittedServiceName != null) {
			for(String serviceName : splittedServiceName) {

				if("BETFAIR".equals(serviceName) || "BET365".equals(serviceName) || "CSV".equals(serviceName)) {
					ICompareInformationEvents processDataManager = ProcessDataManagerFactory.getICompareInformationEventsByString(serviceName);
					Map<Sport,List<AbstractInputRecord>> serviceNameInputData = allServiceApiMapResult.get(serviceName);
					List<AbstractInputRecord> allRecords = new ArrayList<AbstractInputRecord>();
					for(Sport sport : serviceNameInputData.keySet()) {
						List<AbstractInputRecord> transformedRecords;
						try {
							logger.info("Starting data transformation for " + serviceName + " on sport " + sport);
							transformedRecords = processDataManager.compareAndCollectSameEventsFromBookmakerAndTxOdds(serviceNameInputData.get(sport), eventoScommessaRecordMap);
							allRecords.addAll(transformedRecords);
							logger.info("Data transformation for " + serviceName + " on sport " + sport + " completed");

							addToChiaveEventoScommessaMap(allRecords);
						} catch (Exception e) {
							logger.severe("Error in processingDataTransformation. Error message is : " + e.getMessage());
						}

					}
				}
			}
		}
		
	}

	private void addToChiaveEventoScommessaMap(List<AbstractInputRecord> allRecords) {
		for(AbstractInputRecord record : allRecords) {
			eventoScommessaRecordMap.addToMapEventoScommessaRecord(record);
		}
	}

	private void startDataRetrivial() {

		executor = Executors.newFixedThreadPool(splittedServiceName.length);
		
		//inizializzo le variabili necessarie per effettuare tutte le chiamate
		eventoScommessaRecordMap = new ChiaveEventoScommessaInputRecordsMap();
				
		populateMapWithInputRecord();
		
		executor.shutdown();
		
	}

	private void populateMapWithInputRecord() {
		InputDataHelper inputDataHelper = new InputDataHelper();

		for(String apiServiceName : serviceSportMap.keySet()) {
			if(!inputDataHelper.isBlockedBookmaker(apiServiceName)) {
				for(Sport sport : serviceSportMap.get(apiServiceName)) {
					executor.submit(InputDataManagerFactory.getInputDataManagerByString(sport, apiServiceName, allServiceApiMapResult));
				}
			}
		}
		
		long startWaitTime = System.currentTimeMillis();
		int maxMinWait = 3;
		
		List<String> serviceNameList = new ArrayList<String>();
		
		for(String string : splittedServiceName) {
			serviceNameList.add(string);
		}
		
		boolean txOddsFinished = !serviceNameList.contains("TX_ODDS");
		boolean bet365Finished = !serviceNameList.contains("BET365");
		boolean csvFinished = !serviceNameList.contains("CSV");
		boolean betfairFinished = !serviceNameList.contains("BETFAIR");
		
		do {
			if(!txOddsFinished) {
				String name = "TX_ODDS";
				txOddsFinished = allServiceApiMapResult.get(name) != null && 
						allServiceApiMapResult.get(name).keySet().size() == serviceSportMap.get(name).size();
			}
			
			if(!bet365Finished) {
				String name = "BET365";
				bet365Finished = allServiceApiMapResult.get(name) != null && 
						allServiceApiMapResult.get(name).keySet().size() == serviceSportMap.get(name).size();
			}
			
			if(!csvFinished) {
				String name = "CSV";
				csvFinished = allServiceApiMapResult.get(name) != null && 
						allServiceApiMapResult.get(name).keySet().size() == serviceSportMap.get(name).size();
			}
			
			if(!betfairFinished) {
				String name = "BETFAIR";
				betfairFinished = allServiceApiMapResult.get(name) != null && 
						allServiceApiMapResult.get(name).keySet().size() == serviceSportMap.get(name).size();
			}
			
			boolean atLeastOneNotFinished =  !(betfairFinished && csvFinished && bet365Finished && txOddsFinished);
			
			if(atLeastOneNotFinished) {
				logger.info("Status input data: TxOdds " + txOddsFinished
						+ " Bet365 " + bet365Finished
						+ " CSV " + csvFinished
						+ " Betfair " + betfairFinished);
				logger.info("WAITING FOR INPUT RETRIVIAL PHASE: remaining time to close pool thread " + ((
						maxMinWait * 60 * 1000L + startWaitTime) - System.currentTimeMillis())/1000 + " seconds");
				try {
					Thread.sleep(1000);
				}catch(Exception e) {
					logger.severe(e.getMessage());				
				}
			}
			
		}while((!txOddsFinished || !bet365Finished || !csvFinished || !betfairFinished) 
				&& System.currentTimeMillis() - startWaitTime < maxMinWait * 60 * 1000L);
		
		logger.info("Data retrieval terminated. Adding to chiaveEventoScommessaMap TxOdds events");
		
		for(Sport sport : Sport.values()) {
			addToChiaveEventoScommessaMap(allServiceApiMapResult.get("TX_ODDS").get(sport));
		}
		
		logger.info("Add to chiaveEventoScommessaMap TxOdds events finished");

		
	}
	
}
