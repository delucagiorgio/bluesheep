package it.bluesheep.servicehandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.arbitraggi.entities.ArbsType;
import it.bluesheep.arbitraggi.entities.BetReference;
import it.bluesheep.arbitraggi.entities.TwoOptionsArbsRecord;
import it.bluesheep.arbitraggi.telegram.TelegramMessageManager;
import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.util.TranslatorUtil;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.CompareProcessFactory;
import it.bluesheep.comparatore.io.datacompare.IProcessDataManager;
import it.bluesheep.comparatore.io.datacompare.impl.ProcessDataManagerFactory;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepSharedResources;
import it.bluesheep.util.DirectoryFileUtilManager;

/**
 * @author GD Classe che fornisce il servizio di messaggistica degli arbitraggi.
 */
public final class ArbitraggiServiceHandler extends AbstractBlueSheepService {

	private static Logger logger;
	private static ArbitraggiServiceHandler instance;
	private static Map<String, Map<String, List<ArbsRecord>>> twoWayArbsRecordHistoryStatus;
	private static Map<String, Map<String, List<ArbsRecord>>> threeWayArbsRecordHistoryStatus;
	private static List<ArbsRecord> twoWayMessageToBeSentKeysList;
	private static List<ArbsRecord> threeWayMessageToBeSentKeysList;
	private static long startTime;

	private ArbitraggiServiceHandler() {
		super();
		logger = Logger.getLogger(ArbitraggiServiceHandler.class);
	}

	public static synchronized ArbitraggiServiceHandler getArbitraggiServiceHandlerInstance() {
		if (instance == null) {
			instance = new ArbitraggiServiceHandler();
		}
		return instance;
	}

	@Override
	public void run() {
		try {
			startTime = System.currentTimeMillis();

			collectDataAndCheckHistoryAndSend();
			
			saveOutputOnFile(ArbsType.TWO_WAY);
			saveOutputOnFile(ArbsType.THREE_WAY);

			long endTime = System.currentTimeMillis();

			TranslatorUtil.saveTranslationOnFile();

			logger.info("Arbitraggi execution terminated in " + (endTime - startTime) / 1000 + " seconds.");
		} catch (Exception e) {
			logger.error("ERRORE THREAD :: " + e.getMessage(), e);
		}
	}

	/**
	 * GD - 05/08/18 Avvia il processo che calcola gli arbitraggi. 1. comparazione
	 * delle quote 2. creazione dei record univoci di arbitraggio 3. filtraggio
	 * degli arbitraggi già inviati 4. salvataggio degli arbitraggi da inviare su
	 * file 5. invio tramite TelegramBot dei messaggi
	 */
	private void collectDataAndCheckHistoryAndSend() {

		initializeHistoryMaps();

		Map<Service, List<RecordOutput>> outputRecordMap = CompareProcessFactory.startComparisonOdds(this);
		List<RecordOutput> tabellaOutputList = new ArrayList<RecordOutput>();
		for (Service tabellaType : outputRecordMap.keySet()) {
			tabellaOutputList.addAll(outputRecordMap.get(tabellaType));
		}

		outputRecordMap.clear();

		List<ArbsRecord> recordCompared = new ArrayList<ArbsRecord>(tabellaOutputList.size());
		for(RecordOutput record : tabellaOutputList) {
			BetReference[] referencesArray = ArbsUtil.findReferenceInMapFromOutputRecord(record);
			ArbsRecord arbRecord = new TwoOptionsArbsRecord(BlueSheepConstants.STATUS0_ARBS_RECORD, record, referencesArray[0], referencesArray[1]);
			recordCompared.add(arbRecord);
		}

		IProcessDataManager processorTxOdds = ProcessDataManagerFactory.getProcessDataManagerByString(Service.TXODDS_SERVICENAME);
		List<ArbsRecord> resultList = null;
		try {
			resultList = processorTxOdds.compareThreeWayOdds(BlueSheepSharedResources.getEventoScommessaRecordMap(), Sport.CALCIO, this);
			if(resultList != null && !resultList.isEmpty()) {
				recordCompared.addAll(resultList);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);		
		}

		if (recordCompared != null && !recordCompared.isEmpty()) {

			int alreadySentCount = 0;
			for (ArbsRecord record : recordCompared) {
				record = isToBeSentRecord(record);
					
				if (record != null) {
					// Aggiungo solo i record nuovi nella mappa
					Map<String, Map<String, List<ArbsRecord>>> mapToUse = getCorrectMapByArbsType(record.getArbType());
					List<ArbsRecord> listToUse = getCorrectListByArbsType(record.getArbType());
					Map<String, List<ArbsRecord>> runMap = null;
					
					if(mapToUse != null && listToUse != null) {
						listToUse.add(record);
						runMap = mapToUse.get(BlueSheepConstants.STATUS0_ARBS_RECORD);
						if (runMap == null) {
							runMap = new HashMap<String, List<ArbsRecord>>();
							mapToUse.put(BlueSheepConstants.STATUS0_ARBS_RECORD, runMap);
						}
						runMap.put("" + startTime, new ArrayList<ArbsRecord>(listToUse));

					}else {
						logger.warn("Type of arb not supported " + record.getArbType().getCode());
					}
				} else {
					alreadySentCount++;
				}
			}

			logger.info("Message(s) already sent " + alreadySentCount + "/" + recordCompared.size());

			// Se ci sono aggiornamenti o nuovi arbitraggi, invia i risultati e li salva


			TelegramMessageManager tmm = new TelegramMessageManager(startTime);
			for(ArbsType arbsType : ArbsType.values()) {
				logger.info("Sending info via Telegram for ArbsType " + arbsType);
				if (!getCorrectListByArbsType(arbsType).isEmpty()) {
					Map<String, Map<String, List<ArbsRecord>>> correctMap = getCorrectMapByArbsType(arbsType);
					Map<String, List<ArbsRecord>> toBeSentForNewRecord = correctMap
						.get(BlueSheepConstants.STATUS0_ARBS_RECORD);
					List<ArbsRecord> arbsRecordToBeSent = getAllArbsRecordRequired(arbsType, toBeSentForNewRecord, correctMap);
					if (arbsRecordToBeSent != null && !arbsRecordToBeSent.isEmpty()) {
						tmm.sendMessageToTelegramGroupByBotAndStore(arbsRecordToBeSent);
					}
				}
				logger.info("Sending info via Telegram for ArbsType " + arbsType + " completed");

			}
		}
		tabellaOutputList.clear();
	}

	private List<ArbsRecord> getAllArbsRecordRequired(ArbsType type, Map<String, List<ArbsRecord>> toBeSentForNewRecord, Map<String, Map<String, List<ArbsRecord>>> arbsRecordHistoryStatus) {
		Set<ArbsRecord> returnList = new HashSet<ArbsRecord>();
		if(toBeSentForNewRecord != null && !toBeSentForNewRecord.isEmpty()) {
			for (String runId : toBeSentForNewRecord.keySet()) {
				List<ArbsRecord> list = toBeSentForNewRecord.get(runId);
				for (ArbsRecord record : list) {
					if(record.getArbType().equals(type)) {
						Map<String, List<ArbsRecord>> repeatedMessageMap = arbsRecordHistoryStatus
								.get(BlueSheepConstants.STATUS1_ARBS_RECORD);
						if (repeatedMessageMap != null && !repeatedMessageMap.isEmpty()) {
							for (String runIdRepeated : repeatedMessageMap.keySet()) {
								List<ArbsRecord> messageList = repeatedMessageMap.get(runIdRepeated);
								if (messageList != null && !messageList.isEmpty()) {
									for (ArbsRecord recordRepeated : messageList) {
										if (ArbsRecord.isSameEventBookmakerBet(recordRepeated, record)) {
											returnList.add(recordRepeated);
										}
									}
								}
							}
						}
					}
					returnList.add(record);
				}
			}
		}

		return new ArrayList<ArbsRecord>(returnList);
	}

	/**
	 * GD - 19/07/18 Inizializza la mappa relative alle run con i record processati
	 * in quella run secondo la gerarchia di chiavi: -RunID -Chiave Arbitraggio
	 * -Ratings
	 * 
	 * @param filename
	 *            il nome del file da cui leggere
	 * @throws IOException
	 *             nel caso succeda un problema con la lettura del file
	 */
	private void initializeHistoryMaps() {

		String filenamePath2Way = BlueSheepServiceHandlerManager.getProperties()
				.getProperty(BlueSheepConstants.PREVIOUS_RUN_PATH) + BlueSheepConstants.FILENAME_PREVIOUS_RUNS_2WAY;
		String filenamePath3Way = BlueSheepServiceHandlerManager.getProperties()
				.getProperty(BlueSheepConstants.PREVIOUS_RUN_PATH) + BlueSheepConstants.FILENAME_PREVIOUS_RUNS_3WAY;
		
		List<String> twoWayInputStringList = getInputDataFromFile(filenamePath2Way);
		twoWayArbsRecordHistoryStatus = ArbsUtil.initializePreviousRunRecordsMap(twoWayInputStringList);
		
		List<String> threeWayInputStringList = getInputDataFromFile(filenamePath3Way);
		threeWayArbsRecordHistoryStatus = ArbsUtil.initializePreviousRunRecordsMap(threeWayInputStringList);
		
		twoWayMessageToBeSentKeysList = new ArrayList<ArbsRecord>();
		threeWayMessageToBeSentKeysList = new ArrayList<ArbsRecord>();
		
		removeOldNetProfitHistory();
	}

	private void removeOldNetProfitHistory() {
		Map<ArbsType, Map<String, Double>> netProfitHistoryMap = BlueSheepSharedResources.getArbsNetProfitHistoryMap();
		if(netProfitHistoryMap != null && !netProfitHistoryMap.isEmpty()) {
			Set<ArbsType> arbsTypeSet = new HashSet<ArbsType>(netProfitHistoryMap.keySet());
			for(ArbsType arbsType : arbsTypeSet) {
				Map<String, Double> arbsTypeNetProfitMap = netProfitHistoryMap.get(arbsType);
				if(arbsTypeNetProfitMap != null && !arbsTypeNetProfitMap.isEmpty()) {
					Set<String> eventoKeySet = new HashSet<String>(arbsTypeNetProfitMap.keySet());
					for(String eventoKey : eventoKeySet) {
						String dateString = eventoKey.split(BlueSheepConstants.KEY_SEPARATOR)[0].split(BlueSheepConstants.REGEX_CSV)[1];
						if(dateString != null) {
							Date date = null;
							SimpleDateFormat sdfInput = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
							try {
								date = sdfInput.parse(dateString);
							} catch (ParseException e) {
								logger.error(e.getMessage(), e);
								continue;
							}
							
							if(date != null && date.getTime() < System.currentTimeMillis()) {
								arbsTypeNetProfitMap.remove(eventoKey);
							}
						}
					}
				}
			}
		}
	}

	private List<String> getInputDataFromFile(String filenamePath) {
		List<String> inputFileList = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filenamePath));

			String line = br.readLine();
			while (line != null) {
				inputFileList.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return inputFileList;
	}

	/**
	 * GD - 05/08/18 Controlla che, dato un record univoco di arbitraggio, non sia
	 * già stato inviato nelle precedenti run del servizio, al fine di evitare lo
	 * spam. La logica del metodo può restituire un valore FALSE se il record non è
	 * stato inviato o è stato inviato con una condizione di vantaggio minore,
	 * restituisce TRUE in tutti gli altri casi.
	 * 
	 * @param nowComparedRecord
	 *            il record univoco da verificare
	 * @return il recordKey se non inviato o inviato con rating minore, null
	 *         altrimenti
	 */
	private ArbsRecord isToBeSentRecord(ArbsRecord nowComparedRecord) {
		boolean found = false;
		
		Map<String, Map<String, List<ArbsRecord>>> mapToBeUsed = getCorrectMapByArbsType(nowComparedRecord.getArbType());

		if (mapToBeUsed != null && !mapToBeUsed.isEmpty()) {
			// Se non ho trovato una run con lo stesso record ma con rating inferiore o
			// quella che ho trovato è precedente a runId
			Map<String, List<ArbsRecord>> statusRunList = mapToBeUsed.get(BlueSheepConstants.STATUSINVALID_ARBS_RECORD);
			if (statusRunList != null) {
				Set<String> runSet = new HashSet<String>(statusRunList.keySet());
				for (String run : runSet) {
					List<ArbsRecord> arbsList = new ArrayList<ArbsRecord>(statusRunList.get(run));
					for (ArbsRecord arbStoredKey : arbsList) {
						if (ArbsRecord.isSameEventBookmakerBet(nowComparedRecord, arbStoredKey)) {
							
							logger.debug("Already sent record: updating information and re-sent");
							logger.debug("Already sent record is " + nowComparedRecord.getKeyEventoBookmakerBet());
							
							found = true;
							
							Map<String, List<ArbsRecord>> alreadySentArbsRecord = mapToBeUsed
									.get(BlueSheepConstants.STATUS1_ARBS_RECORD);
							if (alreadySentArbsRecord == null) {
								alreadySentArbsRecord = new HashMap<String, List<ArbsRecord>>();
							}
							List<ArbsRecord> arbsRecordList = alreadySentArbsRecord.get(run);
							if (arbsRecordList == null) {
								arbsRecordList = new ArrayList<ArbsRecord>();
							}
							
							arbsRecordList.add(nowComparedRecord);
							alreadySentArbsRecord.put(run, arbsRecordList);
							mapToBeUsed.put(BlueSheepConstants.STATUS1_ARBS_RECORD, alreadySentArbsRecord);
							break;
						}
					}
					if(found) {
						break;
					}
				}
			}
		}

		return found ? null : nowComparedRecord;
	}

	/**
	 * GD - 11/07/18 Salva le comparazioni di quote nei rispettivi file JSON, in
	 * base ai parametri passati
	 * 
	 * @param serviceName
	 *            il servizio
	 * @param processedRecord
	 *            la lista di record
	 */
	private void saveOutputOnFile(ArbsType typeOfDataToBeSaved) {

		logger.info("Storing data for no repeated messages");
		PrintWriter writer1 = null;
		String filenameOutput = null;
		
		Map<String, Map<String, List<ArbsRecord>>> mapToBeSaved = getCorrectMapByArbsType(typeOfDataToBeSaved);
		
		if(ArbsType.THREE_WAY.equals(typeOfDataToBeSaved)) {
			filenameOutput = BlueSheepConstants.FILENAME_PREVIOUS_RUNS_3WAY;
		}else if(ArbsType.TWO_WAY.equals(typeOfDataToBeSaved)) {
			filenameOutput = BlueSheepConstants.FILENAME_PREVIOUS_RUNS_2WAY;
		}
		String filename = BlueSheepServiceHandlerManager.getProperties()
				.getProperty(BlueSheepConstants.PREVIOUS_RUN_PATH) + filenameOutput;
		DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary(
				BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.PREVIOUS_RUN_PATH));
		// Verifico che il file esista, dalla mappa dei record già processati popolata
		// in fase di inizializzazione
		removeOldOrTooMuchStoredRecord(typeOfDataToBeSaved);
		
		if (mapToBeSaved == null) {
			mapToBeSaved = new TreeMap<String, Map<String, List<ArbsRecord>>>();
		}

		// Indico il path di destinazione dei miei dati
		try {
			String line = new String();
			File outputFile = new File(filename);
			if (outputFile.exists() && !outputFile.isDirectory()) {
				outputFile.delete();
			}

			writer1 = new PrintWriter(filename, BlueSheepConstants.ENCODING_UTF_8);
			// Scrivo
			for (String status : mapToBeSaved.keySet()) {
				Map<String, List<ArbsRecord>> toBeWrittenMap = mapToBeSaved.get(status);
				for (String runId : toBeWrittenMap.keySet()) {
					List<ArbsRecord> arbsInfo = toBeWrittenMap.get(runId);
					for (ArbsRecord arbsStoredRecord : arbsInfo) {
						line = arbsStoredRecord.getArbType().getCode() + BlueSheepConstants.KEY_SEPARATOR;
						line += runId + BlueSheepConstants.KEY_SEPARATOR;
						line += arbsStoredRecord.getStoredDataFormat();

						line += System.lineSeparator();
						writer1.write(line);
					}
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (writer1 != null) {
				writer1.close();
			}
		}
		mapToBeSaved.clear();
	}

	private void removeOldOrTooMuchStoredRecord(ArbsType arbsType) {
		
		Map<String, Map<String, List<ArbsRecord>>> mapToBeChecked = getCorrectMapByArbsType(arbsType);
		
		if (mapToBeChecked != null && mapToBeChecked.keySet().size() > 0) {
			long checkBoundTime = System.currentTimeMillis();
				
			// Se la run è entro la mezz'ora, allora ok, altrimenti scartala a prescindere
			Map<String, List<ArbsRecord>> runIdMap = mapToBeChecked.get(BlueSheepConstants.STATUSINVALID_ARBS_RECORD);
			if(runIdMap != null) {
				Set<String> runIdSet = new HashSet<String>(runIdMap.keySet());
				for(String runId : runIdSet) {
					if (checkBoundTime - new Long(runId) >= 24 * 60 * 60 * 1000L) {
						runIdMap.remove(runId);
					}
				}
				// Se dopo la rimozione delle run non più valide, sono ancora presenti più di un
				// tot di run
				if (!(mapToBeChecked.get(BlueSheepConstants.STATUSINVALID_ARBS_RECORD).keySet()
						.size() < BlueSheepConstants.STORED_RUNS_MAX)) {
					// set delle runId aggiornato, senza le run non valide
					runIdSet = new HashSet<String>(
						mapToBeChecked.get(BlueSheepConstants.STATUSINVALID_ARBS_RECORD).keySet());
					// Cerco la run più vecchia, che sia per ordine oltre la k-esima esecuzione
					String oldestRun = null;
					for (String runId : runIdSet) {
						if (oldestRun == null || runId.compareTo(oldestRun) < 0) {
							oldestRun = runId;
						}
					}
					// Rimuovo la più vecchia
					if (oldestRun != null) {
						mapToBeChecked.get(BlueSheepConstants.STATUSINVALID_ARBS_RECORD).remove(oldestRun);
					}
				}
			}
		}
	}

	public Map<String, Map<String, List<ArbsRecord>>> getThreeWayArbsRecordHistoryStatus() {
		return threeWayArbsRecordHistoryStatus;
	}

	public void setThreeWayArbsRecordHistoryStatus(
			Map<String, Map<String, List<ArbsRecord>>> threeWayArbsRecordHistoryStatus) {
		ArbitraggiServiceHandler.threeWayArbsRecordHistoryStatus = threeWayArbsRecordHistoryStatus;
	}
	
	private Map<String, Map<String, List<ArbsRecord>>> getCorrectMapByArbsType(ArbsType type){
		if(ArbsType.THREE_WAY.equals(type)) {
			return threeWayArbsRecordHistoryStatus;
		}else if(ArbsType.TWO_WAY.equals(type)) {
			return twoWayArbsRecordHistoryStatus;
		}else {
			return null;
		}
	}
	
	private List<ArbsRecord> getCorrectListByArbsType(ArbsType type){
		if(ArbsType.THREE_WAY.equals(type)) {
			return threeWayMessageToBeSentKeysList;
		}else if(ArbsType.TWO_WAY.equals(type)) {
			return twoWayMessageToBeSentKeysList;
		}else {
			return null;
		}
	}
}
