package it.bluesheep.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.bluesheep.arbitraggi.entities.ArbsType;
import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
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
	private static List<AbstractInputRecord> exchangeRecordsList = new ArrayList<AbstractInputRecord>();
	private static List<String> boidOTBList = new ArrayList<String>();
	private static Map<ArbsType, Map<String, Double>> arbsNetProfitHistoryMap = new HashMap<ArbsType, Map<String, Double>>();
	private static long timeReferenceOfToday = System.currentTimeMillis();
	private static int arbsIdOfToday = 0;
	
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
		
		serviceSportMap = new ConcurrentHashMap<Service, List<Sport>>();
		if(activeServices != null) {
			for(Service serviceName : activeServices.keySet()) {
				List<Sport> sportList = new ArrayList<Sport>();
				if(serviceName != null) {
					switch(serviceName) {
						case TXODDS_SERVICENAME:
						case BETFAIR_SERVICENAME:
						case BET365_SERVICENAME:
						case CSV_SERVICENAME:
						case EVERY_MATRIX:
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
				if(dateList != null) {
					for(Date date : dateList) {
						if(date.getTime() - startTime < updateFrequencyDiff) {
							sportMap.remove(date);
						}
					}
				}
				
				if(sportMap.keySet() != null && sportMap.keySet().isEmpty()) {
					inputRecordMap.remove(sport);
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
				case "EVERYMATRIX":
					activeServicesList.put(Service.EVERY_MATRIX, new Long(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.FREQ_EVERYMATRIX_SEC)));
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

	public static List<AbstractInputRecord> getExchangeRecordsList() {
		return exchangeRecordsList;
	}

	/**
	 * GD - 23/08/2018
	 * Tiene aggiornata la lista di record finora ricevute dalle interrogazioni, tenendo conto della validità (passata o meno) 
	 * della data dell'evento.
	 * @param exchangeRecordsList la nuova lista di record provenienti dall'Exchange
	 * @param startTime la data di start del processo
	 */
	public static void setExchangeRecordsList(List<AbstractInputRecord> exchangeRecordsList, long startTime) {
		BlueSheepSharedResources.exchangeRecordsList = exchangeRecordsList;
	}

	public static AbstractInputRecord findExchangeRecord(AbstractInputRecord record, List<AbstractInputRecord> exchangeRecordsListCopy) {
		AbstractInputRecord exchangeRecordFound = null;
		if(exchangeRecordsList != null && !exchangeRecordsList.isEmpty()) {
			for(AbstractInputRecord exchangeRecord : exchangeRecordsListCopy) {
				if(AbstractInputRecord.compareSport(exchangeRecord.getSport(), record.getSport())  
						&& AbstractInputRecord.compareDate(exchangeRecord.getDataOraEvento(), record.getDataOraEvento())
						&& (AbstractInputRecord.compareParticipants(exchangeRecord.getPartecipante1(), exchangeRecord.getPartecipante2(), record.getPartecipante1(), record.getPartecipante2()) || 
								exchangeRecord.isSameEventSecondaryMatch(record.getDataOraEvento(), record.getSport().getCode(), record.getPartecipante1(), record.getPartecipante2()))){
					exchangeRecordFound = exchangeRecord;
					break;
				}
			}
		}
		return exchangeRecordFound;
	}

	public static List<String> getBoidOTBList() {
		return boidOTBList;
	}

	public static void setBoidOTBList(List<String> boidOTBList) {
		BlueSheepSharedResources.boidOTBList = boidOTBList;
	}

	public static Map<ArbsType, Map<String, Double>> getArbsNetProfitHistoryMap() {
		return arbsNetProfitHistoryMap;
	}
	
	public static int getCorrectArbsIdOfToday(long startTime) {
		Calendar refCalendar = Calendar.getInstance();
		refCalendar.setTimeInMillis(timeReferenceOfToday);
		Calendar arbsNotificationCalendar = Calendar.getInstance();
		arbsNotificationCalendar.setTimeInMillis(startTime);
		
		if(refCalendar.get(Calendar.DAY_OF_YEAR) == arbsNotificationCalendar.get(Calendar.DAY_OF_YEAR) &&
				refCalendar.get(Calendar.YEAR) == arbsNotificationCalendar.get(Calendar.YEAR)) {
			arbsIdOfToday++;
		}else {
			arbsIdOfToday = 1;
			timeReferenceOfToday = System.currentTimeMillis();
		}
		
		return arbsIdOfToday;
	}

}
