package it.bluesheep.servicehandler.servicemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.impl.ProcessDataManagerFactory;
import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.comparatore.io.datacompare.util.ICompareInformationEvents;
import it.bluesheep.comparatore.io.datainput.operationmanager.service.impl.InputDataManagerFactory;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.AbstractBlueSheepService;
import it.bluesheep.util.BlueSheepSharedResources;

/**
 * 
 * @author GD
 * Classe astratta per la definizione del processo di acquisizione e salvataggio dei dati provenienti dai servizi
 *
 */
public abstract class AbstractBlueSheepServiceHandler extends AbstractBlueSheepService{
	
	protected Logger logger;
	protected AbstractBlueSheepServiceHandler instance;
	protected long startTime;
	protected ExecutorService executor;
	protected Service serviceName;

	protected AbstractBlueSheepServiceHandler() {
		super();
	}

	/**
	 * GD - 11/07/18
	 * Avvia l'acquisizione dati, il mapping dei campi dei record, applica le trasformazioni dei dati secondo i criteri, 
	 * avvia il processing per il formato di output.
	 */
	protected void startProcess() {
		
		BlueSheepSharedResources.checkAndDeleteOldRecords(startTime);
	
		List<AbstractInputRecord> inputRecordList = populateMapWithInputRecord();
		
		startProcessingDataTransformation(inputRecordList);
	}

	/**
	 * GD - 11/07/18
	 * Avvia il matching di dati tra gli stessi eventi, trasforma secondo priorità i dati da renderli omogenei e li aggiunge alla mappa generale
	 * @param inputRecordList lista dei record di input appena ricevuto dal servizios
	 */
	protected void startProcessingDataTransformation(List<AbstractInputRecord> inputRecordList) {

		ICompareInformationEvents processDataManager = ProcessDataManagerFactory.getICompareInformationEventsByString(serviceName);
		Map<Sport,List<AbstractInputRecord>> serviceNameInputData = BlueSheepSharedResources.getAllServiceApiMapResult().get(serviceName);
		List<AbstractInputRecord> transformedRecords;
		for(Sport sport : serviceNameInputData.keySet()) {
			try {
				List<AbstractInputRecord> recordDeleted = BlueSheepSharedResources.getEventoScommessaRecordMap().findDeleteAbstractInputRecordInMap(serviceNameInputData.get(sport));
				logger.info("Deleted record for update count = " + recordDeleted.size());
				
				logger.info("Starting data transformation for " + serviceName + " on sport " + sport);
				transformedRecords = processDataManager.compareAndCollectSameEventsFromBookmakerAndTxOdds(serviceNameInputData.get(sport));
				logger.info("Data transformation for " + serviceName + " on sport " + sport + " completed");
				
				addToChiaveEventoScommessaMap(transformedRecords);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Aggiunge ogni evento passato alla mappa generale degli eventi
	 * @param allRecords i record da salvare nella mappa
	 */
	protected void addToChiaveEventoScommessaMap(List<AbstractInputRecord> allRecords) {
		ChiaveEventoScommessaInputRecordsMap inputRecordMap = BlueSheepSharedResources.getEventoScommessaRecordMap();
		for(AbstractInputRecord record : allRecords) {
			inputRecordMap.addToMapEventoScommessaRecord(record);
		}
	}

	/**
	 * GD - 11/07/18
	 * Per ogni servizio richiesto, avvia una linea di acquisizione indipendente rispetto allo sport
	 */
	protected List<AbstractInputRecord> populateMapWithInputRecord(){

		List<AbstractInputRecord> inputRecordList = new ArrayList<AbstractInputRecord>();
		List<Sport> sportToBeRetrieved = BlueSheepSharedResources.getServiceSportMap().get(serviceName);
		
		executor = Executors.newFixedThreadPool(sportToBeRetrieved.size());

		for(Sport sport : sportToBeRetrieved) {
			logger.info("Starting data retrivial for " + serviceName + " on sport " + sport);
			if(InputDataManagerFactory.getInputDataManagerByString(sport, serviceName) != null) {
				executor.submit(InputDataManagerFactory.getInputDataManagerByString(sport, serviceName));
			}else {
				logger.warn("Cannot start InputDataManager for " + serviceName);
			}
		}
		boolean timeoutReached = true;
		
		try {			
			executor.shutdown();
			timeoutReached = !executor.awaitTermination(5, TimeUnit.MINUTES);
			if(timeoutReached) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			logger.warn(e.getMessage(), e);
			if(!executor.isTerminated()) {
				executor.shutdownNow();
			}
		}
		
		logger.info("" + serviceName + " service handler for data retrieval completed for all sports. Timeout reached = " + timeoutReached);
		
		Map<Sport, List<AbstractInputRecord>> sportInputRecordListMap = BlueSheepSharedResources.getAllServiceApiMapResult().get(serviceName);
		
		for(Sport sport : sportInputRecordListMap.keySet()) {
			inputRecordList.addAll(sportInputRecordListMap.get(sport));
		}
		
		return inputRecordList;
	}

	@Override
	public void run() {
		try {
			startTime = System.currentTimeMillis();
			
			startProcess();		
			
			long endTime = System.currentTimeMillis();
			
			//Svuota la mappa dei risultati
			BlueSheepSharedResources.getAllServiceApiMapResult().get(serviceName).clear();
			
			logger.info("Data retrivial  for service " + serviceName + " completed in " + (endTime - startTime)/1000 + " seconds");
		}catch(Exception e) {
			logger.error("ERRORE THREAD :: " + e.getMessage(), e);
		}
	}
	
}
