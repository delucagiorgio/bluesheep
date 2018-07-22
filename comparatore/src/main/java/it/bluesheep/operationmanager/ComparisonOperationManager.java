package it.bluesheep.operationmanager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import arbs.telegram.TelegramMessageManager;
import arbs.util.ArbsConstants;
import arbs.util.ArbsUtil;
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

public class ComparisonOperationManager {
	
	private static Logger logger;
	private static ComparisonOperationManager instance;
	private static Map<Service, List<Sport>> serviceSportMap;
	private static Map<Service, Map<Sport,List<AbstractInputRecord>>> allServiceApiMapResult;
	private static String[] activeServiceAPI;
	private static Map<String, Map<String, Map<String, String>>> alreadySentArbsOdds;
	private static long startTime;

	private ExecutorService executor;
	private ChiaveEventoScommessaInputRecordsMap eventoScommessaRecordMap;

	private ComparisonOperationManager() {
		logger = (new BlueSheepLogger(ComparisonOperationManager.class)).getLogger();
	}
	
	public static synchronized ComparisonOperationManager getComparisonOperationManagerFactory() {
		instance = new ComparisonOperationManager();
		logger.info("ComparisonOperationManager initialized");
		logger.info(BlueSheepComparatoreMain.getProperties().entrySet().toString());
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
		
		try {
			getAlreadySentArbsOdds(ArbsConstants.FILENAME_PREVIOUS_RUNS);
		}catch(IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		initializeDataStructures();
		
		if(serviceSportMap == null) {
			serviceSportMap = new HashMap<Service, List<Sport>>();
			populateServiceSportMap();
		}
	
		startTime = System.currentTimeMillis();

		startDataRetrivial();
		
		startProcessingDataTransformation();
		
		startComparisonOddsAndSaveOnFileJSON();
		
		eventoScommessaRecordMap.clear();
		
		long endTime = System.currentTimeMillis();
		
		logger.info("Total execution time = " + (endTime - startTime)/1000 + " seconds");
		
	}

	/**
	 * GD - 19/07/18	
	 * Inizializza la mappa relative alle run con i record processati in quella run secondo la gerarchia di chiavi:
	 * -RunID
	 * -Chiave Arbitraggio
	 * -Ratings
	 * @param filename il nome del file da cui leggere
	 * @throws IOException nel caso succeda un problema con la lettura del file
	 */
	private void getAlreadySentArbsOdds(String filename) throws IOException{
		String filenamePath = BlueSheepComparatoreMain.getProperties().getProperty(ArbsConstants.PREVIOUS_RUN_PATH) + ArbsConstants.FILENAME_PREVIOUS_RUNS;
		BufferedReader br = new BufferedReader(new FileReader(filenamePath));
		List<String> inputFileList = new ArrayList<String>();
		try{
			String line = br.readLine();
			while (line != null) {
				inputFileList.add(line);
				line = br.readLine();
			}
		}finally{
			br.close();
		}
		
		if(!inputFileList.isEmpty()) {
			alreadySentArbsOdds = ArbsUtil.initializePreviousRunRecordsMap(inputFileList);
			logger.info("There are already " + alreadySentArbsOdds.size() + " run collection of message sent.");
		}
	}

	/**
	 * GD - 19/07/18	
	 * Inizializza le strutture dati necessarie al processo
	 */
	private void initializeDataStructures() {
		allServiceApiMapResult = new ConcurrentHashMap<Service, Map<Sport,List<AbstractInputRecord>>>();
		activeServiceAPI = BlueSheepComparatoreMain.getProperties().getProperty("SERVICE_NAME").split(",");		
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
		List<RecordOutput> outputRecord = null;
		
		for(Sport sport : Sport.values()) {
			try {
				outputRecord = processDataManager.compareOdds(eventoScommessaRecordMap, sport);
				tabellaOutputList.addAll(outputRecord);
				logger.info("Punta-Punta :: Odds comparison result size for sport " + sport + " is " + outputRecord.size());
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		outputRecord.clear();
		
		processDataManager = ProcessDataManagerFactory.getProcessDataManagerByString(Service.BETFAIR_SERVICENAME);
		for(Sport sport : Sport.values()) {
			try {
				outputRecord = processDataManager.compareOdds(eventoScommessaRecordMap, sport);
				tabellaOutputList.addAll(outputRecord);
				logger.info("Punta-Banca :: Odds comparison result size for sport " + sport + " is " + outputRecord.size());
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		if(tabellaOutputList != null && !tabellaOutputList.isEmpty()) {
			TelegramMessageManager tmm = new TelegramMessageManager(startTime);
			tmm.sendMessageToTelegramGroupByBotAndStore(tabellaOutputList, alreadySentArbsOdds);
		}
		tabellaOutputList.clear();
	}
	
	/**
	 * GD - 11/07/18
	 * Avvia il matching di dati tra gli stessi eventi, trasforma secondo priorità i dati da renderli omogenei e li aggiunge alla mappa generale
	 */
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
							logger.log(Level.SEVERE, e.getMessage(), e);
						}
					}
				}
			}
		}		
	}

	/**
	 * Aggiunge ogni evento passato alla mappa generale degli eventi
	 * @param allRecords i record da salvare nella mappa
	 */
	private void addToChiaveEventoScommessaMap(List<AbstractInputRecord> allRecords) {
		for(AbstractInputRecord record : allRecords) {
			eventoScommessaRecordMap.addToMapEventoScommessaRecord(record);
		}
	}

	/**
	 * GD - 11/07/18
	 * Avvia l'acquisizione dei dati tramite le API
	 */
	private void startDataRetrivial() {

		executor = Executors.newFixedThreadPool(activeServiceAPI.length);
		
		//inizializzo le variabili necessarie per effettuare tutte le chiamate
		eventoScommessaRecordMap = new ChiaveEventoScommessaInputRecordsMap();
				
		populateMapWithInputRecord();
		
		executor.shutdown();
		
	}

	/**
	 * GD - 11/07/18
	 * Per ogni servizio richiesto, avvia una linea di acquisizione indipendente rispetto allo sport
	 */
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
		
		//Inizializzazione delle variabili di controllo stato
		
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
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
			
		}while(!atLeastOneNotFinished 
				&& System.currentTimeMillis() - startWaitTime < maxMinWait * 60 * 1000L);
		
		logger.config("Data retrieval terminated. Adding to chiaveEventoScommessaMap TxOdds events");
		
		for(Sport sport : Sport.values()) {
			addToChiaveEventoScommessaMap(allServiceApiMapResult.get(Service.TXODDS_SERVICENAME).get(sport));
		}
		
		logger.config("Add to chiaveEventoScommessaMap TxOdds events finished");

		
	}

	/**
	 * GD - 11/07/18
	 * Verifica lo stato del processo di acquisizione dati relativo a tutti i servizi richiesti
	 * @param serviceNameStatusMap la mappa riassuntiva dello stato per servizio
	 */
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
