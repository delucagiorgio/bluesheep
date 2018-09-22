package it.bluesheep.servicehandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.arbitraggi.entities.ArbsType;
import it.bluesheep.arbitraggi.entities.BetReference;
import it.bluesheep.arbitraggi.entities.ThreeOptionsArbsRecord;
import it.bluesheep.arbitraggi.entities.TwoOptionsArbsRecord;
import it.bluesheep.arbitraggi.telegram.TelegramMessageManager;
import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.output.subtype.RecordBookmakerVsExchangeOdds;
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

			startTwoWayOptionProcess();
			saveOutputOnFile(ArbsType.TWO_WAY);
			
			startThreeWayOptionProcess();
			saveOutputOnFile(ArbsType.THREE_WAY);

			long endTime = System.currentTimeMillis();

			TranslatorUtil.saveTranslationOnFile();

			logger.info("Arbitraggi execution terminated in " + (endTime - startTime) / 1000 + " seconds.");
		} catch (Exception e) {
			logger.error("ERRORE THREAD :: " + e.getMessage(), e);
		}
	}

	/**
	 * GD - 19/09/18 Avvia il processo che calcola gli arbitraggi a tre vie. 1.
	 * comparazione delle quote 2. creazione dei record univoci di arbitraggio 3.
	 * filtraggio degli arbitraggi già inviati 4. salvataggio degli arbitraggi da
	 * inviare su file 5. invio tramite TelegramBot dei messaggi
	 */
	private void startThreeWayOptionProcess() {

		getAlreadySentArbsOddsThreeWay();

		IProcessDataManager processDataManager = ProcessDataManagerFactory
				.getProcessDataManagerByString(Service.TXODDS_SERVICENAME);

		List<ArbsRecord> returnList = new ArrayList<ArbsRecord>();
		for (Sport sport : Sport.values()) {
			try {
				returnList.addAll(processDataManager.compareThreeWayOdds(BlueSheepSharedResources.getEventoScommessaRecordMap(), sport, this));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		logger.info("3-way arbs size = " + returnList.size());
		List<ArbsRecord> toBeSentRecordList = new ArrayList<ArbsRecord>();
		
		int alreadySentCount = 0;
		
		for (ArbsRecord record : returnList) {

			record = hasBeenAlreadySentThreeWay(record);

			if (record != null) {
				Map<String, List<ArbsRecord>> runMap = threeWayArbsRecordHistoryStatus.get(BlueSheepConstants.STATUS0_ARBS_RECORD);
				if (runMap == null) {
					runMap = new HashMap<String, List<ArbsRecord>>();
					threeWayArbsRecordHistoryStatus.put(BlueSheepConstants.STATUS0_ARBS_RECORD, runMap);
					List<ArbsRecord> arbsRecordList = new ArrayList<ArbsRecord>();
					arbsRecordList.add(record);
					runMap.put("" + startTime, arbsRecordList);
				}else {
					List<ArbsRecord> arbsRecordList = runMap.get("" + startTime);
					if(arbsRecordList == null) {
						arbsRecordList = new ArrayList<ArbsRecord>();
					}
					arbsRecordList.add(record);
					runMap.put("" + startTime, arbsRecordList);
				}
				toBeSentRecordList.add(record);
			}else {
				alreadySentCount++;
			}
		}
		
		logger.info("" + toBeSentRecordList.size() + " message(s) to be sent. Message(s) already sent "
				+ alreadySentCount + "/" + returnList.size());		
		if(toBeSentRecordList != null && !toBeSentRecordList.isEmpty()) {
			
//		uta
			
			Map<String, List<ArbsRecord>> toBeSentForUpdate = threeWayArbsRecordHistoryStatus.get(BlueSheepConstants.STATUS0_ARBS_RECORD);
			List<ArbsRecord> arbsRecordToBeSent = getAllArbsRecordRequired(toBeSentForUpdate, threeWayArbsRecordHistoryStatus);
			if(arbsRecordToBeSent != null && !arbsRecordToBeSent.isEmpty()) {
//				System.out.println("**********************************************************************");
//
//				for(ArbsRecord record : arbsRecordToBeSent) {
//					System.out.println(record.getKeyEventoBookmakerBet());
//				}
//				System.out.println("**********************************************************************");

				TelegramMessageManager tmm = new TelegramMessageManager(startTime);
				tmm.sendMessageToTelegramGroupByBotAndStore(arbsRecordToBeSent);
			}
		}
	}

	private ArbsRecord hasBeenAlreadySentThreeWay(ArbsRecord record) {
		boolean found = false;

		ThreeOptionsArbsRecord threeWayRecord = (ThreeOptionsArbsRecord) record;
		
		if (threeWayArbsRecordHistoryStatus != null && !threeWayArbsRecordHistoryStatus.isEmpty()) {
			// Se non ho trovato una run con lo stesso record ma con rating inferiore o
			// quella che ho trovato è precedente a runId
			Map<String, List<ArbsRecord>> statusRunList = threeWayArbsRecordHistoryStatus.get(BlueSheepConstants.STATUSINVALID_ARBS_RECORD);
			if (statusRunList != null) {
				Set<String> runSet = new HashSet<String>(statusRunList.keySet());
				for (String run : runSet) {
					List<ArbsRecord> arbsList = new ArrayList<ArbsRecord>(statusRunList.get(run));
					for (ArbsRecord arbStored : arbsList) {
						if (arbStored.isSameEventBookmakerBet(threeWayRecord)) {
							found = true;
							arbStored.changeStatus();
							statusRunList.get(run).remove(arbStored);
							Map<String, List<ArbsRecord>> alreadySentArbsRecord = threeWayArbsRecordHistoryStatus
									.get(BlueSheepConstants.STATUS1_ARBS_RECORD);
							if (alreadySentArbsRecord == null) {
								alreadySentArbsRecord = new HashMap<String, List<ArbsRecord>>();
							}
							List<ArbsRecord> arbsRecordList = alreadySentArbsRecord.get(run);
							if (arbsRecordList == null) {
								arbsRecordList = new ArrayList<ArbsRecord>();
							}
							
							BetReference[] betReferenceArray = ArbsUtil.findReferenceInMapFromOutputRecord(threeWayRecord);
							BetReference betRef = betReferenceArray[0];
							BetReference betAverage = betReferenceArray[1];
							
							ArbsRecord arbsRecord = new ThreeOptionsArbsRecord(BlueSheepConstants.STATUS0_ARBS_RECORD,
									threeWayRecord.getBookmaker1(), threeWayRecord.getBookmaker2(), threeWayRecord.getBookmaker3(),
									threeWayRecord.getOdd1(), threeWayRecord.getOdd2(), threeWayRecord.getOdd3(), 
									threeWayRecord.getBet1(), threeWayRecord.getBet2(), threeWayRecord.getBet3(),
									threeWayRecord.getDate(), threeWayRecord.getKeyEvento(), threeWayRecord.getChampionship(), 
									threeWayRecord.getSport(), threeWayRecord.getLink1(), threeWayRecord.getLink2(), threeWayRecord.getLink3(),
									threeWayRecord.getCountry(), threeWayRecord.getLiquidita1(), threeWayRecord.getLiquidita2(), threeWayRecord.getLiquidita3(),
									false, false, false, false, false, false, betRef, betAverage);
							
							arbsRecordList.add(arbsRecord);
							alreadySentArbsRecord.put(run, arbsRecordList);
							threeWayArbsRecordHistoryStatus.put(BlueSheepConstants.STATUS1_ARBS_RECORD, alreadySentArbsRecord);
							logger.debug("Updated status for key " + arbStored.getKeyEventoBookmakerBet());
							break;
						}
					}
					if(found) {
						break;
					}
				}
			}
		}

		return found ? null : record;
	}

	private void getAlreadySentArbsOddsThreeWay() {
		if (threeWayArbsRecordHistoryStatus == null) {
			threeWayArbsRecordHistoryStatus = new TreeMap<String, Map<String, List<ArbsRecord>>>();
		}
		String filenamePath = BlueSheepServiceHandlerManager.getProperties()
				.getProperty(BlueSheepConstants.PREVIOUS_RUN_PATH) + BlueSheepConstants.FILENAME_PREVIOUS_RUNS_3WAY;
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

		threeWayArbsRecordHistoryStatus = ArbsUtil.initializePreviousRunRecordsMap(inputFileList);
		logger.info("There are already " + threeWayArbsRecordHistoryStatus.size() + " run collection of message sent.");
	}

	/**
	 * GD - 05/08/18 Avvia il processo che calcola gli arbitraggi. 1. comparazione
	 * delle quote 2. creazione dei record univoci di arbitraggio 3. filtraggio
	 * degli arbitraggi già inviati 4. salvataggio degli arbitraggi da inviare su
	 * file 5. invio tramite TelegramBot dei messaggi
	 */
	private void startTwoWayOptionProcess() {

		getAlreadySentArbsOddsTwoWay();

		Map<Service, List<RecordOutput>> outputRecordMap = CompareProcessFactory.startComparisonOdds(this);
		List<RecordOutput> tabellaOutputList = new ArrayList<RecordOutput>();
		for (Service tabellaType : outputRecordMap.keySet()) {
			tabellaOutputList.addAll(outputRecordMap.get(tabellaType));
		}

		outputRecordMap.clear();

		if (tabellaOutputList != null && !tabellaOutputList.isEmpty()) {
			List<ArbsRecord> messageToBeSentKeysList = new ArrayList<ArbsRecord>();
			List<String> recordKeysList = new ArrayList<String>();
			int alreadySentCount = 0;
			for (RecordOutput record : tabellaOutputList) {
				String recordKey = ArbsUtil.getKeyArbsTwoWayFromOutputRecord(record);
				boolean isExchangeRecord = record instanceof RecordBookmakerVsExchangeOdds;
				boolean isValidExchangeRecord = isExchangeRecord
						&& ((RecordBookmakerVsExchangeOdds) record).getLiquidita2() >= 50D;
				// Creo mapping tra le chiavi evento (data-sport-partecipanti) "recordKeyList" e
				// i record di output
				if ((!(record instanceof RecordBookmakerVsExchangeOdds) || isValidExchangeRecord)) {
					// controllo che non l'abbia già mandata, se si non faccio nulla

					// Il metodo prenderà in input tutta la mappa calcolata sopra, restituisce una
					// mappa la cui chiave è la chiave evento,
					// i valori sono tutti i record di output trovati ed eventualmente da rinviare,
					// differenziondoli a loro volta per tipo
					// ("nuovo arbitraggio - mai inviato prima" o "arbitraggio già inviato - inviato
					// per completezza di informazione")
					//
					recordKey = hasBeenAlreadySentTwoWay(recordKey);

					// Creo tutti i record di arbitraggi con i dati necessari e li colleziono in due
					// liste differenti: in una lista quelli
					// la tipologia di record ("nuovo arbitraggio - mai inviato prima"); nella
					// seconda per la tipologia di record ("arbitraggio
					// già inviato - inviato per completezza di informazione")
					if (recordKey != null) {

						BetReference[] betReferenceArray = ArbsUtil.findReferenceInMapFromOutputRecord(record);
						BetReference betRef = betReferenceArray[0];
						BetReference betAverage = betReferenceArray[1];

						ArbsRecord arbsRecord = new TwoOptionsArbsRecord(BlueSheepConstants.STATUS0_ARBS_RECORD,
								record.getBookmakerName1(), record.getBookmakerName2(),
								record.getQuotaScommessaBookmaker1(), record.getQuotaScommessaBookmaker2(),
								record.getScommessaBookmaker1(), record.getScommessaBookmaker2(),
								record.getDataOraEvento().toString(), record.getEvento(), record.getCampionato(),
								record.getSport(), record.getLinkBook1(), record.getLinkBook2(), record.getNazione(),
								record.getLiquidita1(), record.getLiquidita2(), false, false, false, false, betRef,
								betAverage);
						// Aggiungo solo i record nuovi nella mappa
						messageToBeSentKeysList.add(arbsRecord);
						Map<String, List<ArbsRecord>> runMap = twoWayArbsRecordHistoryStatus
								.get(BlueSheepConstants.STATUS0_ARBS_RECORD);
						if (runMap == null) {
							runMap = new HashMap<String, List<ArbsRecord>>();
							twoWayArbsRecordHistoryStatus.put(BlueSheepConstants.STATUS0_ARBS_RECORD, runMap);
							runMap.put("" + startTime, messageToBeSentKeysList);
						}

						// Tutti i record
						recordKeysList.add(recordKey);
					} else {
						alreadySentCount++;
					}
				} else {
					logger.info("Insufficient size (liquidità) by requirements : " + 50D);
				}
			}

			// Inviare tutta la lista degli arbitraggi secondo gli eventi che si ricevono,
			// pescando dalla history tutti gli arbitraggi necessari.

			logger.info("" + messageToBeSentKeysList.size() + " message(s) to be sent. Message(s) already sent "
					+ alreadySentCount + "/" + tabellaOutputList.size());

			// Se ci sono aggiornamenti o nuovi arbitraggi, invia i risultati e li salva
			if (!messageToBeSentKeysList.isEmpty()) {

				TelegramMessageManager tmm = new TelegramMessageManager(startTime);
				Map<String, List<ArbsRecord>> toBeSentForUpdate = twoWayArbsRecordHistoryStatus
						.get(BlueSheepConstants.STATUS0_ARBS_RECORD);
				List<ArbsRecord> arbsRecordToBeSent = getAllArbsRecordRequired(toBeSentForUpdate, twoWayArbsRecordHistoryStatus);
				if (arbsRecordToBeSent != null && !arbsRecordToBeSent.isEmpty()) {
					tmm.sendMessageToTelegramGroupByBotAndStore(arbsRecordToBeSent);
				}
			}
		}
		tabellaOutputList.clear();
	}

	private List<ArbsRecord> getAllArbsRecordRequired(Map<String, List<ArbsRecord>> toBeSentForUpdate, Map<String, Map<String, List<ArbsRecord>>> arbsRecordHistoryStatus) {
		Set<ArbsRecord> returnList = new HashSet<ArbsRecord>();

		for (String runId : toBeSentForUpdate.keySet()) {
			List<ArbsRecord> list = toBeSentForUpdate.get(runId);
			for (ArbsRecord record : list) {
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
				returnList.add(record);
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
	private void getAlreadySentArbsOddsTwoWay() {
		if (twoWayArbsRecordHistoryStatus == null) {
			twoWayArbsRecordHistoryStatus = new TreeMap<String, Map<String, List<ArbsRecord>>>();
		}
		String filenamePath = BlueSheepServiceHandlerManager.getProperties()
				.getProperty(BlueSheepConstants.PREVIOUS_RUN_PATH) + BlueSheepConstants.FILENAME_PREVIOUS_RUNS_2WAY;
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

		twoWayArbsRecordHistoryStatus = ArbsUtil.initializePreviousRunRecordsMap(inputFileList);
		logger.info("There are already " + twoWayArbsRecordHistoryStatus.size() + " run collection of message sent.");
	}

	/**
	 * GD - 05/08/18 Controlla che, dato un record univoco di arbitraggio, non sia
	 * già stato inviato nelle precedenti run del servizio, al fine di evitare lo
	 * spam. La logica del metodo può restituire un valore FALSE se il record non è
	 * stato inviato o è stato inviato con una condizione di vantaggio minore,
	 * restituisce TRUE in tutti gli altri casi.
	 * 
	 * @param recordKey
	 *            il record univoco da verificare
	 * @return il recordKey se non inviato o inviato con rating minore, null
	 *         altrimenti
	 */
	private String hasBeenAlreadySentTwoWay(String recordKey) {
		String returnString = recordKey;
		boolean found = false;

		if (twoWayArbsRecordHistoryStatus != null && !twoWayArbsRecordHistoryStatus.isEmpty()) {
			// Se non ho trovato una run con lo stesso record ma con rating inferiore o
			// quella che ho trovato è precedente a runId
			Map<String, List<ArbsRecord>> statusRunList = twoWayArbsRecordHistoryStatus.get(BlueSheepConstants.STATUSINVALID_ARBS_RECORD);
			if (statusRunList != null) {
				Set<String> runSet = new HashSet<String>(statusRunList.keySet());
				for (String run : runSet) {
					List<ArbsRecord> arbsList = new ArrayList<ArbsRecord>(statusRunList.get(run));
					for (ArbsRecord arbStoredKey : arbsList) {
						if (arbStoredKey.isSameEventBookmakerBet(recordKey.split(BlueSheepConstants.KEY_SEPARATOR)[0])) {
							found = true;
							
							arbStoredKey.changeStatus();
							statusRunList.get(run).remove(arbStoredKey);
			
							Map<String, List<ArbsRecord>> alreadySentArbsRecord = twoWayArbsRecordHistoryStatus
									.get(BlueSheepConstants.STATUS1_ARBS_RECORD);
							if (alreadySentArbsRecord == null) {
								alreadySentArbsRecord = new HashMap<String, List<ArbsRecord>>();
							}
							List<ArbsRecord> arbsRecordList = alreadySentArbsRecord.get(run);
							if (arbsRecordList == null) {
								arbsRecordList = new ArrayList<ArbsRecord>();
							}
							
							BetReference[] betReferenceArray = ArbsUtil.findReferenceInMapFromString(recordKey);
							BetReference betRef = betReferenceArray[0];
							BetReference betAverage = betReferenceArray[1];

							String[] splittedKey = recordKey.split(BlueSheepConstants.KEY_SEPARATOR);
							String[] splittedEventoInfo = splittedKey[0].split(BlueSheepConstants.REGEX_CSV);
							String[] splittedLink = splittedKey[1].split(BlueSheepConstants.REGEX_CSV);
							String[] splittedSize = splittedKey[2].split(BlueSheepConstants.REGEX_CSV);
							
							ArbsRecord arbsRecord = new TwoOptionsArbsRecord(BlueSheepConstants.STATUS0_ARBS_RECORD,
									splittedEventoInfo[5], splittedEventoInfo[7],
									Double.parseDouble(splittedEventoInfo[9]), Double.parseDouble(splittedEventoInfo[10]),
									splittedEventoInfo[6], splittedEventoInfo[8],
									splittedEventoInfo[1], splittedEventoInfo[0], splittedEventoInfo[4],
									splittedEventoInfo[2], splittedLink[0], splittedLink[1], splittedEventoInfo[3],
									Double.parseDouble(splittedSize[0]), Double.parseDouble(splittedSize[1]), false, false, false, false, betRef,
									betAverage);
							
							
							arbsRecordList.add(arbsRecord);
							alreadySentArbsRecord.put(run, arbsRecordList);
							twoWayArbsRecordHistoryStatus.put(BlueSheepConstants.STATUS1_ARBS_RECORD, alreadySentArbsRecord);
							logger.info("Updated status for key " + arbStoredKey.getKeyEventoBookmakerBet());
							break;
						}
					}
					if(found) {
						break;
					}
				}
			}
		}

		return found ? null : returnString;
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
		
		Map<String, Map<String, List<ArbsRecord>>> mapToBeSaved = null;
		
		if(ArbsType.THREE_WAY.equals(typeOfDataToBeSaved)) {
			filenameOutput = BlueSheepConstants.FILENAME_PREVIOUS_RUNS_3WAY;
			mapToBeSaved = threeWayArbsRecordHistoryStatus;
		}else if(ArbsType.TWO_WAY.equals(typeOfDataToBeSaved)) {
			filenameOutput = BlueSheepConstants.FILENAME_PREVIOUS_RUNS_2WAY;
			mapToBeSaved = twoWayArbsRecordHistoryStatus;
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
		
		Map<String, Map<String, List<ArbsRecord>>> mapToBeChecked = null;
		if(ArbsType.THREE_WAY.equals(arbsType)) {
			mapToBeChecked = threeWayArbsRecordHistoryStatus;
		}else if(ArbsType.TWO_WAY.equals(arbsType)) {
			mapToBeChecked = twoWayArbsRecordHistoryStatus;
		}
		
		if (mapToBeChecked != null && mapToBeChecked.keySet().size() > 0) {
			long checkBoundTime = System.currentTimeMillis();
			if (mapToBeChecked != null && !mapToBeChecked.isEmpty()
					&& !(mapToBeChecked.values().size() < BlueSheepConstants.STORED_RUNS_MAX)) {
				// Se la run è entro la mezz'ora, allora ok, altrimenti scartala a prescindere
				Map<String, List<ArbsRecord>> runIdMap = mapToBeChecked.get(BlueSheepConstants.STATUSINVALID_ARBS_RECORD);
				if(runIdMap != null) {
					Set<String> runIdSet = new HashSet<String>(runIdMap.keySet());
					for(String runId : runIdSet) {
						if (checkBoundTime - new Long(runId) >= 60 * 60 * 1000L) {
							runIdMap.remove(runId);
						}
					}
				}
				// Se dopo la rimozione delle run non più valide, sono ancora presenti più di un
				// tot di run
				if (!(mapToBeChecked.get(BlueSheepConstants.STATUSINVALID_ARBS_RECORD).keySet()
						.size() < BlueSheepConstants.STORED_RUNS_MAX)) {
					// set delle runId aggiornato, senza le run non valide
					Set<String> runIdSet = new HashSet<String>(
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

	public static Map<String, Map<String, List<ArbsRecord>>> getThreeWayArbsRecordHistoryStatus() {
		return threeWayArbsRecordHistoryStatus;
	}

	public static void setThreeWayArbsRecordHistoryStatus(
			Map<String, Map<String, List<ArbsRecord>>> threeWayArbsRecordHistoryStatus) {
		ArbitraggiServiceHandler.threeWayArbsRecordHistoryStatus = threeWayArbsRecordHistoryStatus;
	}
}
