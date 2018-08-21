package it.bluesheep.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.comparatore.io.datainput.operationmanager.service.util.InputDataHelper;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;

/**
 * 
 * @author GD
 * La classe rappresenta un insieme di risorse condivisa tra tutti gli oggetti del programma.
 * Il loro accesso è garantito in maniera statica. L'inizializzazione delle variabili è a carico
 * della classe: quando un oggetto viene utilizzato dunque non è necessario preoccuparsi della sua
 * inizializzazione
 *
 */
public class BlueSheepSharedResources {
	
	private static ChiaveEventoScommessaInputRecordsMap eventoScommessaRecordMap = new ChiaveEventoScommessaInputRecordsMap();
	private static Long txOddsUpdateTimestamp = new Long(-1);
	private static Long txOddsNowMinimumUpdateTimestamp = new Long(-1);
	private static Boolean csvToBeProcessed = Boolean.TRUE;
	private static long updateFrequencyDiff;
	private static Map<Service, Long> activeServices;
	private static Map<Service, Map<Sport,List<AbstractInputRecord>>> allServiceApiMapResult;
	private static Map<Service, List<Sport>> serviceSportMap;
	private static int updateCallCount = 0;
	
	private BlueSheepSharedResources() {}

	public static ChiaveEventoScommessaInputRecordsMap getEventoScommessaRecordMap() {
		return eventoScommessaRecordMap;
	}

	public static Long getTxOddsUpdateTimestamp() {
		return txOddsUpdateTimestamp;
	}

	public static void setTxOddsUpdateTimestamp(Long txOddsNowMinimumUpdateTimestamp) {
		BlueSheepSharedResources.txOddsUpdateTimestamp = txOddsNowMinimumUpdateTimestamp;
	}

	public static Long getTxOddsNowMinimumUpdateTimestamp() {
		return txOddsNowMinimumUpdateTimestamp;
	}

	public static void setTxOddsNowMinimumUpdateTimestamp(Long txOddsNowMinimumUpdateTimestamp) {
		BlueSheepSharedResources.txOddsNowMinimumUpdateTimestamp = txOddsNowMinimumUpdateTimestamp;
	}

	public static Boolean getCsvToBeProcessed() {
		return csvToBeProcessed;
	}

	public static void setCsvToBeProcessed(Boolean csvToBeProcessed) {
		BlueSheepSharedResources.csvToBeProcessed = csvToBeProcessed;
	}

	public static long getUpdateFrequencyDiff() {
		return updateFrequencyDiff;
	}
	
	/**
	 * GD - 12/07/18
	 * Prepara la mappa dei servizi con i relativi sport da calcolare, ignorando quelli esclusi dalle proprietà
	 */
	private static void populateServiceSportMap() {
		
		InputDataHelper inputDataHelper = new InputDataHelper();
		serviceSportMap = new ConcurrentHashMap<Service, List<Sport>>();
		if(activeServices != null) {
			for(Service serviceName : activeServices.keySet()) {
				List<Sport> sportList = new ArrayList<Sport>();
				if(serviceName.equals(Service.BET365_SERVICENAME)) {
					if(inputDataHelper.isBlockedBookmaker("BET365")) {
						continue;
					}
				}
				if(serviceName != null) {
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
	 * GD - 04/08/18
	 * Controlla la mappa dei record di input e elimina gli eventi già passati 
	 */
	public static void checkAndDeleteOldRecords(long startTime) {
		ChiaveEventoScommessaInputRecordsMap inputRecordMap = BlueSheepSharedResources.getEventoScommessaRecordMap();
		
		for(Sport sport : Sport.values()) {
			Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> sportMap = inputRecordMap.get(sport);
			if(sportMap != null) {
				List<Date> dateList = new ArrayList<Date>(sportMap.keySet());
				for(Date date : dateList) {
					if(date.getTime() - startTime < updateFrequencyDiff) {
						sportMap.remove(date);
					}
				}
			}
		}
	}
	
	/**
	 * GD - 19/07/18	
	 * Inizializza le strutture dati necessarie al processo
	 */
	public static void initializeDataStructures() {
		allServiceApiMapResult = new ConcurrentHashMap<Service, Map<Sport,List<AbstractInputRecord>>>();
		activeServices = getActiveServiceApiFromProperties();		
		//imposta il timestamp minimo tra gli aggiornamenti precedenti di TxOdds per effettuare chiamate più veloci
		BlueSheepSharedResources.setTxOddsUpdateTimestamp(BlueSheepSharedResources.getTxOddsNowMinimumUpdateTimestamp());
		updateFrequencyDiff = Long.valueOf(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.UPDATE_FREQUENCY)) * 1000L * 60L;
		populateServiceSportMap();
	}

	private static Map<Service, Long> getActiveServiceApiFromProperties() {
		Map<Service, Long> activeServicesList = new HashMap<Service, Long>();
 		String[] activeServiceSplitted = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.SERVICE_NAME_LIST).split(",");
		if(activeServiceSplitted != null && activeServiceSplitted.length > 0) {
			for(String serviceString : activeServiceSplitted) {
				switch(serviceString.trim()) {
				case "BET365":
					activeServicesList.put(Service.BET365_SERVICENAME, new Long(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.FREQ_BET365_SEC)));
					break;
				case "CSV":
					activeServicesList.put(Service.CSV_SERVICENAME, new Long(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.FREQ_CSV_SEC)));
					break;
				case "BETFAIR":
					activeServicesList.put(Service.BETFAIR_SERVICENAME, new Long(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.FREQ_BETFAIR_SEC)));
					break;
				case "TXODDS":
					activeServicesList.put(Service.TXODDS_SERVICENAME, new Long(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.FREQ_TXODDS_SEC)));
					break;
				default:
						break;
				}
			}
		}
		return activeServicesList;
	}

	public static Map<Service, Long> getActiveServices() {
		return activeServices;
	}

	public static Map<Service, List<Sport>> getServiceSportMap() {
		return serviceSportMap;
	}

	public static Map<Service, Map<Sport, List<AbstractInputRecord>>> getAllServiceApiMapResult() {
		return allServiceApiMapResult;
	}

	public static int getUpdateCallCount() {
		return updateCallCount;
	}

	public static void setUpdateCallCount(int updateCallCount) {
		BlueSheepSharedResources.updateCallCount = updateCallCount;
	}

}