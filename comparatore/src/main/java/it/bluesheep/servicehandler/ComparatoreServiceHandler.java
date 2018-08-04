package it.bluesheep.servicehandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.impl.ProcessDataManagerFactory;
import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.comparatore.io.datacompare.util.ICompareInformationEvents;
import it.bluesheep.comparatore.io.datainput.operationmanager.service.impl.InputDataManagerFactory;
import it.bluesheep.comparatore.io.datainput.operationmanager.service.util.InputDataHelper;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.BlueSheepSharedResources;
import it.bluesheep.util.zip.ZipUtil;

public final class ComparatoreServiceHandler extends AbstractBlueSheepService{
	
	private static Logger logger;
	private static ComparatoreServiceHandler instance;
	private static Map<Service, List<Sport>> serviceSportMap;
	private static Map<Service, Map<Sport,List<AbstractInputRecord>>> allServiceApiMapResult;
	private static String[] activeServiceAPI;
	private static long startTime;

	private ExecutorService executor;

	private ComparatoreServiceHandler() {
		super();
		logger = (new BlueSheepLogger(ComparatoreServiceHandler.class)).getLogger();
	}
	
	public static synchronized ComparatoreServiceHandler getComparisonOperationManagerFactory() {
		instance = new ComparatoreServiceHandler();
		logger.log(Level.INFO, "ComparatoreServiceHandler initialized");
		logger.log(Level.INFO, BlueSheepComparatoreMain.getProperties().entrySet().toString());
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
						case CSV_SERVICENAME:
							sportList = Arrays.asList(Sport.CALCIO, Sport.TENNIS);
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
		
		initializeDataStructures();
		
		if(serviceSportMap == null) {
			serviceSportMap = new HashMap<Service, List<Sport>>();
			populateServiceSportMap();
		}
	
		populateMapWithInputRecord();
		
		startProcessingDataTransformation();
		
		ZipUtil zipUtil = new ZipUtil();
		try {
			zipUtil.zipLastRunLogFiles();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * GD - 19/07/18	
	 * Inizializza le strutture dati necessarie al processo
	 */
	private void initializeDataStructures() {
		allServiceApiMapResult = new ConcurrentHashMap<Service, Map<Sport,List<AbstractInputRecord>>>();
		activeServiceAPI = BlueSheepComparatoreMain.getProperties().getProperty("SERVICE_NAME").split(",");		
		//imposta il timestamp minimo tra gli aggiornamenti precedenti di TxOdds per effettuare chiamate più veloci
		BlueSheepSharedResources.setTxOddsUpdateTimestamp(BlueSheepSharedResources.getTxOddsNowMinimumUpdateTimestamp());
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
					List<AbstractInputRecord> transformedRecords;
					for(Sport sport : serviceNameInputData.keySet()) {
						try {
							logger.log(Level.INFO, "Starting data transformation for " + serviceName + " on sport " + sport);
							transformedRecords = processDataManager.compareAndCollectSameEventsFromBookmakerAndTxOdds(serviceNameInputData.get(sport), BlueSheepSharedResources.getEventoScommessaRecordMap());
							logger.log(Level.INFO, "Data transformation for " + serviceName + " on sport " + sport + " completed");

							addToChiaveEventoScommessaMap(transformedRecords);
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
		ChiaveEventoScommessaInputRecordsMap inputRecordMap = BlueSheepSharedResources.getEventoScommessaRecordMap();
		for(AbstractInputRecord record : allRecords) {
			inputRecordMap.addToMapEventoScommessaRecord(record);
		}
	}

	/**
	 * GD - 11/07/18
	 * Per ogni servizio richiesto, avvia una linea di acquisizione indipendente rispetto allo sport
	 */
	private void populateMapWithInputRecord() {

		executor = Executors.newFixedThreadPool(activeServiceAPI.length);
		
		InputDataHelper inputDataHelper = new InputDataHelper();		
		for(Service apiServiceName : serviceSportMap.keySet()) {
			if(!inputDataHelper.getExcludedBookmakers().contains(apiServiceName.getCode().toLowerCase())) {
				for(Sport sport : serviceSportMap.get(apiServiceName)) {
					logger.log(Level.INFO, "Starting data retrivial for " + apiServiceName + " on sport " + sport);
					executor.submit(InputDataManagerFactory.getInputDataManagerByString(sport, apiServiceName, allServiceApiMapResult));
				}
			}
		}
		
		boolean timeoutReached = true;
		try {			
			executor.shutdown();

			timeoutReached = !executor.awaitTermination(3, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
				
		logger.log(Level.CONFIG, "Data retrieval terminated. Adding to chiaveEventoScommessaMap TxOdds events");
		
		logger.log(Level.INFO, "" + allServiceApiMapResult.keySet() + "\nTimeout reached = " + timeoutReached);

		
		for(Sport sport : Sport.values()) {
			addToChiaveEventoScommessaMap(allServiceApiMapResult.get(Service.TXODDS_SERVICENAME).get(sport));
		}
		
		logger.log(Level.CONFIG, "Add to chiaveEventoScommessaMap TxOdds events finished");
	}

	@Override
	public void run() {
		try {
			startTime = System.currentTimeMillis();
			
			startProcess();		
			
			long endTime = System.currentTimeMillis();
			
			logger.log(Level.INFO, "Data retrivial completed in " + (endTime - startTime)/1000 + " seconds");
		}catch(Exception e) {
			logger.log(Level.SEVERE, "ERRORE THREAD :: " + e.getMessage(), e);
		}
	}
	
}
