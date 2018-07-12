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
import it.bluesheep.serviceapi.Service;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.DirectoryFileUtilManager;
import it.bluesheep.util.json.AbstractBluesheepJsonConverter;

public class ComparisonOperationManager {
	
	private static Logger logger;
	private static ComparisonOperationManager instance;
	private static Map<Service, List<Sport>> serviceSportMap;
	private static Map<Service, Map<Sport,List<AbstractInputRecord>>> allServiceApiMapResult;
	private static String[] activeServiceAPI;
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
			allServiceApiMapResult = new ConcurrentHashMap<Service, Map<Sport,List<AbstractInputRecord>>>();
			activeServiceAPI = BlueSheepComparatoreMain.getProperties().getProperty("SERVICE_NAME").split(",");
		}
		return instance;
	}
	
	/**
	 * GD - 12/07/18
	 * Prepara la mappa dei servizi con i relativi sport da calcolare, ignorando quelli esclusi dalle proprietà
	 */
	private void populateServiceSportMap() {
		
		InputDataHelper inputDataHelper = new InputDataHelper();
		
		if(activeServiceAPI != null) {
			for(String serviceNameString : activeServiceAPI) {
				List<Sport> sportList = new ArrayList<Sport>();
				Service serviceName = Service.getServiceFromString(serviceNameString);
				if(serviceName != null && !inputDataHelper.isBlockedBookmaker(serviceNameString)) {
					switch(serviceName) {
						case TXODDS_SERVICENAME:
						case BETFAIR_SERVICENAME:
						case BET365_SERVICENAME:
							sportList = Arrays.asList(Sport.CALCIO, Sport.TENNIS);
							break;
						case CSV_SERVICENAME:
							//Generico, per dare omogeneità al processo
							sportList = Arrays.asList(Sport.CALCIO);
							break;
						default:
							//La mappa non viene inizializzata, nessun processo prende luogo.
							return;
					}
					serviceSportMap.put(serviceName, sportList);
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
			serviceSportMap = new HashMap<Service, List<Sport>>();
			populateServiceSportMap();
		}
	
		startTime = System.currentTimeMillis();

		startDataRetrivial();
		
		startProcessingDataTransformation();
		
		startComparisonOddsAndSaveOnFileJSON();
		
		long endTime = System.currentTimeMillis();
		
		logger.info("Total execution time = " + (endTime - startTime)/1000 + " seconds");
		
	}

	/**
	 * GD - 11/07/18
	 * Avvia la comparazione delle quote del punta-punta e del punta banca per gli sport attivi. Salva i risultati in nel file JSON
	 * con filename uguale al timestamp dello start
	 */
	private void startComparisonOddsAndSaveOnFileJSON() {
		
		IProcessDataManager processDataManager;
		List<RecordOutput> tabellaOutputList = new ArrayList<RecordOutput>();
		
		processDataManager = ProcessDataManagerFactory.getProcessDataManagerByString(Service.TXODDS_SERVICENAME);
		for(Sport sport : Sport.values()) {
			try {
				List<RecordOutput> outputRecord = processDataManager.compareOdds(eventoScommessaRecordMap, sport);
				tabellaOutputList.addAll(outputRecord);
				logger.info("Punta-Punta :: Odds comparison result size for sport " + sport + " is " + outputRecord.size());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.severe(e.getMessage());
			}
		}
		
		logger.info("Punta-Punta odds comparison size for all sport is " + tabellaOutputList.size());
		
		saveOutputOnFile(Service.TXODDS_SERVICENAME, tabellaOutputList);
		
		tabellaOutputList.clear();
		
		processDataManager = ProcessDataManagerFactory.getProcessDataManagerByString(Service.BETFAIR_SERVICENAME);

		for(Sport sport : Sport.values()) {
			try {
				List<RecordOutput> outputRecord = processDataManager.compareOdds(eventoScommessaRecordMap, sport);
				tabellaOutputList.addAll(outputRecord);
				logger.info("Punta-Banca :: Odds comparison result size for sport " + sport + " is " + outputRecord.size());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.severe(e.getMessage());
			}
		}
		
		logger.info("Punta-Banca odds comparison size for all sport is " + tabellaOutputList.size());

		
		saveOutputOnFile(Service.BETFAIR_SERVICENAME, tabellaOutputList);
		
		tabellaOutputList.clear();
	}

	private void saveOutputOnFile(Service serviceName, List<RecordOutput> tabellaOutputList) {
		
		String pathTable = null;
		if(Service.BETFAIR_SERVICENAME.equals(serviceName)) {
			pathTable = "PATH_OUTPUT_TABLE1";
		}else if(Service.TXODDS_SERVICENAME.equals(serviceName)) {
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
		
		if(activeServiceAPI != null) {
			for(Service serviceName : serviceSportMap.keySet()) {

				if(Service.BETFAIR_SERVICENAME.equals(serviceName) 
						|| Service.BET365_SERVICENAME.equals(serviceName)
						|| Service.CSV_SERVICENAME.equals(serviceName)) {
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
			
			//TODO: da modificare
			ICompareInformationEvents processDataManager = ProcessDataManagerFactory.getICompareInformationEventsByString(Service.CSV_SERVICENAME);
			List<AbstractInputRecord> transformedRecords = new ArrayList<AbstractInputRecord>();
			try {
				transformedRecords = processDataManager.compareAndCollectSameEventsFromBookmakerAndTxOdds(allServiceApiMapResult.get(Service.CSV_SERVICENAME).get(Sport.TENNIS), eventoScommessaRecordMap);
				addToChiaveEventoScommessaMap(transformedRecords);
			} catch (Exception e) {
				logger.severe("Error in processingDataTransformation. Error message is : " + e.getMessage());
			}
		}
		
	}

	private void addToChiaveEventoScommessaMap(List<AbstractInputRecord> allRecords) {
		for(AbstractInputRecord record : allRecords) {
			eventoScommessaRecordMap.addToMapEventoScommessaRecord(record);
		}
	}

	private void startDataRetrivial() {

		executor = Executors.newFixedThreadPool(activeServiceAPI.length);
		
		//inizializzo le variabili necessarie per effettuare tutte le chiamate
		eventoScommessaRecordMap = new ChiaveEventoScommessaInputRecordsMap();
				
		populateMapWithInputRecord();
		
		executor.shutdown();
		
	}

	private void populateMapWithInputRecord() {

		InputDataHelper inputDataHelper = new InputDataHelper();		
		for(Service apiServiceName : serviceSportMap.keySet()) {
			if(!inputDataHelper.getExcludedBookmakers().contains(apiServiceName.getCode().toLowerCase())) {
				for(Sport sport : serviceSportMap.get(apiServiceName)) {
					executor.submit(InputDataManagerFactory.getInputDataManagerByString(sport, apiServiceName, allServiceApiMapResult));
				}
			}
		}
		
		long startWaitTime = System.currentTimeMillis();
		int maxMinWait = 3;
		
		Map<Service, Boolean> serviceNameStatusMap = new HashMap<Service, Boolean>();
		
		for(Service serviceName : serviceSportMap.keySet()) {
			serviceNameStatusMap.put(serviceName, Boolean.FALSE);
		}
		Boolean atLeastOneNotFinished;
		do {
			verifyStatusServiceQuery(serviceNameStatusMap);
			
			atLeastOneNotFinished = true;
			
			for(Service statusService : serviceNameStatusMap.keySet()) {
				atLeastOneNotFinished = atLeastOneNotFinished && serviceNameStatusMap.get(statusService);
			}
			
			if(!atLeastOneNotFinished) {
				logger.info("Status input data: " +  serviceNameStatusMap);
				logger.info("WAITING FOR INPUT RETRIVIAL PHASE: remaining time to close pool thread " + ((
						maxMinWait * 60 * 1000L + startWaitTime) - System.currentTimeMillis())/1000 + " seconds");
				try {
					Thread.sleep(1000);
				}catch(Exception e) {
					logger.severe(e.getMessage());				
				}
			}
			
		}while(!atLeastOneNotFinished 
				&& System.currentTimeMillis() - startWaitTime < maxMinWait * 60 * 1000L);
		
		logger.info("Data retrieval terminated. Adding to chiaveEventoScommessaMap TxOdds events");
		
		for(Sport sport : Sport.values()) {
			addToChiaveEventoScommessaMap(allServiceApiMapResult.get(Service.TXODDS_SERVICENAME).get(sport));
		}
		
		logger.info("Add to chiaveEventoScommessaMap TxOdds events finished");

		
	}

	private void verifyStatusServiceQuery(Map<Service, Boolean> serviceNameStatusMap) {
		
		for(Service service : serviceNameStatusMap.keySet()) {
			if(!serviceNameStatusMap.get(service)) {
				Boolean finished = allServiceApiMapResult.get(service) != null && 
						allServiceApiMapResult.get(service).keySet().size() == serviceSportMap.get(service).size();
				
				if(finished) {
					logger.info("Service " + service + " has terminated");
				}
				
				serviceNameStatusMap.put(service, finished);
			}
		}
	}
	
}
