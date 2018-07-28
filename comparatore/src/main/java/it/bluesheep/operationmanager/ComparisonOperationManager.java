package it.bluesheep.operationmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import it.bluesheep.util.DirectoryFileUtilManager;

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
			List<String> messageToBeSentKeysList = new ArrayList<String>();
			int alreadySentCount = 0;
			for(RecordOutput record : tabellaOutputList) {
				String recordKey = ArbsUtil.getKeyArbsFromOutputRecord(record);
				
				//controllo che non l'abbia già mandata, se si non faccio nulla
				if(!alreadySent(recordKey)) {
					messageToBeSentKeysList.add(recordKey + ArbsConstants.KEY_SEPARATOR + record.getLinkBook1() + ArbsConstants.VALUE_SEPARATOR + record.getLinkBook2());
				}else {
					alreadySentCount++;
				}
			}
			
			logger.log(Level.INFO, "" + messageToBeSentKeysList.size() + " message(s) to be sent. Message(s) already sent " + alreadySentCount + "/" + tabellaOutputList.size() );

			//Se ci sono aggiornamenti o nuovi arbitraggi, invia i risultati e li salva
			if(!messageToBeSentKeysList.isEmpty()) {
				
				saveOutputOnFile(messageToBeSentKeysList);

				TelegramMessageManager tmm = new TelegramMessageManager(startTime);
				tmm.sendMessageToTelegramGroupByBotAndStore(messageToBeSentKeysList, alreadySentArbsOdds);
			}
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
	
	private boolean alreadySent(String recordKey) {
		boolean found = false;
		boolean betterRatingFound = false;
		String runIdFoundWithLowerRatings = null;
		String tmpRating1 = null;
		String tmpRating2 = null;
		String tmpRating1Stored = null;
		String tmpRating2Stored = null;
		
		if(alreadySentArbsOdds != null && !alreadySentArbsOdds.isEmpty()) {
			String[] splittedRecord = recordKey.split(ArbsConstants.KEY_SEPARATOR);
			String key = ArbsUtil.createArbsKeyFromRecordKey(splittedRecord[0]);

			for(String runId : alreadySentArbsOdds.keySet()) {
				String rating1 = null;
				String rating2 = null;
				String rating1Stored = null;
				String rating2Stored = null;
				tmpRating1 = null;
				tmpRating2 = null;
				tmpRating1Stored = null;
				tmpRating2Stored = null;
				betterRatingFound = false;
				//Se non ho trovato una run con lo stesso record ma con rating inferiore o quella che ho trovato è precedente a runId
				if(runIdFoundWithLowerRatings == null || runIdFoundWithLowerRatings.compareTo(runId) < 0) {
					Map<String, Map<String, String>> arbsRunMap = alreadySentArbsOdds.get(runId);
					for(String arbs : arbsRunMap.keySet()) {
						if(key.equalsIgnoreCase(arbs)) {
							found = true;
							rating1Stored = arbsRunMap.get(arbs).get(ArbsConstants.RATING1);
							rating2Stored = arbsRunMap.get(arbs).get(ArbsConstants.RATING2);
							
							String[] ratings = splittedRecord[1].split(ArbsConstants.VALUE_SEPARATOR);
							rating1 = ratings[0];
							rating2 = null;
							if(ratings.length == 2) {
								rating2 = ratings[1];
							}
							//Se il record è già stato inviato in precedenza ma con dei rating più bassi, lo reinvio
							if(rating1Stored.compareTo(rating1) < 0 && ((rating2Stored == null && rating2 == null) ||
									(rating2Stored != null && rating2 != null && rating2Stored.compareTo(rating2) < 0))) {
								betterRatingFound = true;
								tmpRating1 = rating1;
								tmpRating1Stored = rating1Stored;
								tmpRating2 = rating2;
								tmpRating2Stored = rating2Stored;
								runIdFoundWithLowerRatings = runId;
							}else {
								betterRatingFound = false;
								break;
							}
						}
					}
				}
			}
			if(runIdFoundWithLowerRatings != null && betterRatingFound) {
				logger.info("Key arbs " + key + " has been already sent, but with lower ratings. now_R1 = " +  tmpRating1 + "; stored_R1 = " + tmpRating1Stored + "; new_R2 = " + tmpRating2 + "; stored_R2 = " + tmpRating2Stored);
				logger.info("Message is resent");
			}
		}
		return found || betterRatingFound;
	}
	
	/**
	 * GD - 11/07/18
	 * Salva le comparazioni di quote nei rispettivi file JSON, in base ai parametri passati
	 * @param serviceName il servizio
	 * @param processedRecord la lista di record
	 */
	private void saveOutputOnFile(List<String> processedRecord) {
		
		if(processedRecord != null && !processedRecord.isEmpty()) {
			logger.info("Storing data for no repeated messages");
	    	PrintWriter writer1 = null;
	    	String filename = BlueSheepComparatoreMain.getProperties().getProperty(ArbsConstants.PREVIOUS_RUN_PATH) + ArbsConstants.FILENAME_PREVIOUS_RUNS;
			DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary(BlueSheepComparatoreMain.getProperties().getProperty(ArbsConstants.PREVIOUS_RUN_PATH));

			//Verifico che il file esista, dalla mappa dei record già processati popolata in fase di inizializzazione
			if(alreadySentArbsOdds != null && alreadySentArbsOdds.keySet().size() > 0) {
				long checkBoundTime = System.currentTimeMillis();
				//TODO inserire una variabile di proprietà per questa costante
				if(!(alreadySentArbsOdds.keySet().size() < ArbsConstants.STORED_RUNS_MAX)) {
					List<String> runIdSet = new ArrayList<String>(alreadySentArbsOdds.keySet());
					//Scarta le run non più nell'intervallo di tempo scelto
					for(String runId : runIdSet) {
						//Se la run è entro la mezz'ora, allora ok, altrimenti scartala a prescindere
						//TODO inserire una variabile di proprietà per questa costante
						if(checkBoundTime - new Long(runId) >= 60 * 60 * 1000L) {
							alreadySentArbsOdds.remove(runId);
						}
					}
					//Se dopo la rimozione delle run non più valide, sono ancora presenti più di un tot di run
					if(!(alreadySentArbsOdds.keySet().size() < ArbsConstants.STORED_RUNS_MAX)) {
						//set delle runId aggiornato, senza le run non valide
						runIdSet = new ArrayList<String>(alreadySentArbsOdds.keySet());
						//Cerco la run più vecchia, che sia per ordine oltre la k-esima esecuzione
						String oldestRun = null;
						for(String runId : runIdSet) {
							if(oldestRun == null || runId.compareTo(oldestRun) < 0) {
								oldestRun = runId;
							}
						}
						//Rimuovo la più vecchia
						if(oldestRun != null) {
							alreadySentArbsOdds.remove(oldestRun);
						}
					}
				}
			}
			
			Map<String, Map<String, String>> arbsLastExecutionMap = new HashMap<String, Map<String, String>>();
			if(alreadySentArbsOdds == null) {
				alreadySentArbsOdds = new TreeMap<String, Map<String, Map<String, String>>>();
			}
			alreadySentArbsOdds.put("" + startTime, arbsLastExecutionMap);
			//Inserisco i dati della nuova run
			for(String record : processedRecord) {
				String[] splittedRecord = record.split(ArbsConstants.KEY_SEPARATOR);
				String key = ArbsUtil.createArbsKeyFromRecordKey(splittedRecord[0]);
				Map<String, String> ratingMap = new HashMap<String, String>();
				String[] ratings = splittedRecord[1].split(ArbsConstants.VALUE_SEPARATOR);
				ratingMap.put(ArbsConstants.RATING1, ratings[0]);
				if(ratings.length == 2 && !ratings[1].isEmpty()) {
					ratingMap.put(ArbsConstants.RATING2, ratings[1]);
				}
				arbsLastExecutionMap.put(key,ratingMap);
			}
			
	    	// Indico il path di destinazione dei miei dati
	    	try {
	    		String line = new String();
	    		File outputFile = new File(filename);
	    		if(outputFile.exists() && !outputFile.isDirectory()) {
	    			outputFile.delete();
	    		}
	    		
				writer1 = new PrintWriter(filename, "UTF-8");    	
		    	// Scrivo
		    	for(String runId : alreadySentArbsOdds.keySet()) {
		    		Map<String, Map<String, String>> arbsRunMap = alreadySentArbsOdds.get(runId);
		    		for(String arbsRecord : arbsRunMap.keySet()) {
			    		line = runId + ArbsConstants.KEY_SEPARATOR;
		    			line += arbsRecord + ArbsConstants.KEY_SEPARATOR;
		    			Map<String, String> arbsRatingMap = arbsRunMap.get(arbsRecord);
		    			line += arbsRatingMap.get(ArbsConstants.RATING1) ;
		    			if(arbsRatingMap.get(ArbsConstants.RATING2) != null) {
		    				line += ArbsConstants.VALUE_SEPARATOR + arbsRatingMap.get(ArbsConstants.RATING2);
		    			}
		    			line += System.lineSeparator();
				    	writer1.write(line);
		    		}
		    	}
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}finally {
				if(writer1 != null) {
					writer1.close();
				}
			}
		}
		alreadySentArbsOdds.clear();
	}
	
}
