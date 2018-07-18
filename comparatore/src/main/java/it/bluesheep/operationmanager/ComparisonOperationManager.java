package it.bluesheep.operationmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.output.subtype.RecordBookmakerVsBookmakerOdds;
import it.bluesheep.entities.output.subtype.RecordBookmakerVsExchangeOdds;
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
	
	private static final String VALUE_SEPARATOR = ";";
	private static final String KEY_SEPARATOR = "BS_KEY";

	private static Logger logger;
	private static ComparisonOperationManager instance;
	private static Map<Service, List<Sport>> serviceSportMap;
	private static Map<Service, Map<Sport,List<AbstractInputRecord>>> allServiceApiMapResult;
	private static String[] activeServiceAPI;
	private static Map<String, Map<String, Map<String, String>>> alreadySentArbsOdds;
	private static long startTime;
	private static String previousRunFilename;

	private ExecutorService executor;
	private ChiaveEventoScommessaInputRecordsMap eventoScommessaRecordMap;

	private ComparisonOperationManager() {
		logger = (new BlueSheepLogger(ComparisonOperationManager.class)).getLogger();
		previousRunFilename = "previousRunFile.txt";
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
			getAlreadySentArbsOdds(previousRunFilename);
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

	private void getAlreadySentArbsOdds(String filename) throws IOException{
		String filenamePath = BlueSheepComparatoreMain.getProperties().getProperty("PREVIOUS_RUN_PATH") + previousRunFilename;
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
			alreadySentArbsOdds = new TreeMap<String, Map<String, Map<String, String>>>();
		   for(String line : inputFileList) {
			   String[] splittedLine = line.split(KEY_SEPARATOR);
			   String runId = splittedLine[0];
			   Map<String, Map<String, String>> keyArbsMap = alreadySentArbsOdds.get(runId);
			   if(keyArbsMap == null) {
				   keyArbsMap = new HashMap<String, Map<String, String>>();
				   alreadySentArbsOdds.put(runId, keyArbsMap);
			   }
			   String[] keyArbsAllValues = splittedLine[1].split(VALUE_SEPARATOR);
			   String key = keyArbsAllValues[0] + VALUE_SEPARATOR +
					   keyArbsAllValues[1] + VALUE_SEPARATOR + 
					   keyArbsAllValues[2] + VALUE_SEPARATOR + 
					   keyArbsAllValues[3] + VALUE_SEPARATOR +
					   keyArbsAllValues[4] + VALUE_SEPARATOR +
					   keyArbsAllValues[5] + VALUE_SEPARATOR +
					   keyArbsAllValues[6];
			   Map<String, String> arbsRatingMap = keyArbsMap.get(key);
			   
			   if(arbsRatingMap == null) {
				   arbsRatingMap = new HashMap<String, String>();
				   String[] ratings = splittedLine[2].split(VALUE_SEPARATOR);
				   arbsRatingMap.put("R1", ratings[0]);
				   if(ratings.length == 2) {
					   arbsRatingMap.put("R2", ratings[1]);
				   }
				   keyArbsMap.put(key, arbsRatingMap);
			   }
		   }
		   logger.info("There are already " + alreadySentArbsOdds.size() + " run collection of message sent.");
		}
	}

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
		
		sendMessageToTelegramGroupByBotAndStore(tabellaOutputList);

		tabellaOutputList.clear();
	}

	private void sendMessageToTelegramGroupByBotAndStore(List<RecordOutput> outputRecord) {
		
		if(outputRecord != null && !outputRecord.isEmpty()) {
			/*https://api.telegram.org/bot618342797:AAEHIeL4dxNgp4_giX8C6VU9bOraCu-n7S8/sendMessage?chat_id=-238763100&text=Ciao*/
			
//			51337759
			List<String> messageList = prepareDataToTextMessageFormat(outputRecord);
//			-238763100
			String chatId = "-238763100";
			
			for(String textToBeSent : messageList) {
				String encodedTextToBeSent = null;
				textToBeSent = textToBeSent.substring(0, textToBeSent.length() - 1);
				
				logger.config("Url parameters = " + textToBeSent);
				
				try {
					encodedTextToBeSent = URLEncoder.encode(textToBeSent, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				if(encodedTextToBeSent != null) {
		            String url = "https://api.telegram.org/bot618342797:AAEHIeL4dxNgp4_giX8C6VU9bOraCu-n7S8/sendMessage?chat_id=" + chatId  + "&parse_mode=Markdown" + "&text=" + encodedTextToBeSent;
		            BufferedReader in = null;
		            try {
						HttpsURLConnection httpsConnection = (HttpsURLConnection) new URL(url).openConnection();
			            in = new BufferedReader(
			            		new InputStreamReader(httpsConnection.getInputStream()));
			            
			            String inputLine;
			            while ((inputLine = in.readLine()) != null) {
			                logger.info(inputLine);
			            }
		
			            in.close();
		
					} catch (Exception e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			}
		}
		
	}

	private List<String> prepareDataToTextMessageFormat(List<RecordOutput> outputRecord) {
		List<String> stringList = new ArrayList<String>();
		StringBuilder returnString;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		List<String> recordKeyList = new ArrayList<String>();
		
		int i = 0;
		for(RecordOutput record : outputRecord) {
			returnString = new StringBuilder();
			String recordKey = createRecordKey(record);
			//controllo che non l'abbia già mandata, se si non faccio nulla
			
			if(!alreadySent(recordKey)) {
				//preparo il layout del messaggio da inviare e lo concateno con quello già processato
				i++;
				String[] splittedRecordKey = recordKey.split(KEY_SEPARATOR);
				String[] arbsValuesSplitted = splittedRecordKey[0].split(VALUE_SEPARATOR);
				returnString.append("*Quota numero:* " + i + System.lineSeparator()); 
				returnString.append("*Evento:* " + arbsValuesSplitted[0] + System.lineSeparator());
				returnString.append("*Data evento:* " + sdf.format(new Date(record.getDataOraEvento().getTime())) + System.lineSeparator());
				returnString.append("*Sport:* " + arbsValuesSplitted[2] + System.lineSeparator());
				
				String nazione = arbsValuesSplitted[3];
				if(nazione != null && !nazione.isEmpty() && !"null".equalsIgnoreCase(nazione)) {
					returnString.append("*Nazione/Competizione:* " + nazione + System.lineSeparator());
				}
				
				returnString.append("*Campionato:* " + arbsValuesSplitted[4] + System.lineSeparator());
				returnString.append("*Book1:* " + arbsValuesSplitted[5] + System.lineSeparator());
				returnString.append("*Scommessa1:* " + getScommessaStringURL(arbsValuesSplitted[6])+ System.lineSeparator());
				returnString.append("*Quota1:* " + arbsValuesSplitted[7] + System.lineSeparator());
				returnString.append("*Book2:* " + arbsValuesSplitted[8] + System.lineSeparator());
				returnString.append("*Scommessa2:* " + getScommessaStringURL(arbsValuesSplitted[9]) + System.lineSeparator());
				returnString.append("*Quota2:* " + arbsValuesSplitted[10] + System.lineSeparator());
				
				String modifier = "";
				String[] ratings = splittedRecordKey[1].split(VALUE_SEPARATOR);
				String rating1 = ratings[0];

				if(new Double(rating1) >= 150.00) {
					modifier = " 🚀🚀🚀";
				}
				
				returnString.append("*Rating1:* " + rating1.substring(0, Math.min(rating1.length(), 6)) + modifier + System.lineSeparator());
				String rating2 = null;
				if(ratings.length == 2) {
					rating2 = ratings[1];
				}
				if(rating2 != null && !rating2.isEmpty()) {
					returnString.append("*Rating2:* " + rating2.substring(0, Math.min(rating2.length(), 6)) + modifier + System.lineSeparator());
				}
				if(splittedRecordKey.length == 3) {
					String liquidità = splittedRecordKey[2];
					if(!liquidità.isEmpty() && new Double(liquidità) >= 50.00) {
						returnString.append("*Liquidità:* " + liquidità.substring(0, Math.min(liquidità.length(), 8)) + System.lineSeparator());
					}else {
						logger.info("Skipped line for insufficent size: " + recordKey);
						returnString = new StringBuilder();
						continue;
					}
				}
				stringList.add(returnString.toString());
				recordKeyList.add(recordKey);
			}else {
				logger.info("Skipping already sent arbs = " + recordKey);
			}
			
		}
		
		if(stringList.size() != 0) {
			returnString = new StringBuilder();
			Date date = new Date(startTime);
			returnString.append("🐑🐑🐑 Le SHEEPATE 🐑🐑🐑" + System.lineSeparator());
			returnString.append("*Ora estrazione:* " + sdf.format(date));

			returnString.append(System.lineSeparator());
			stringList.add(0,returnString.toString());
			
			returnString = new StringBuilder();
			returnString.append("🐑🐑🐑 FINE 🐑🐑🐑" + System.lineSeparator());
			stringList.add(returnString.toString());
			
			saveOutputOnFile(recordKeyList);
		}
		
		
		return stringList;
	}

	private String getScommessaStringURL(String scommessaBookmaker1) {
		String result = scommessaBookmaker1;
		if(result != null && (result.startsWith("U") || result.startsWith("O"))) {
			String tmp = result.substring(0,1);
			switch(tmp) {
			case "U":
				result = "Under " + result.substring(1) + "_";
				break;
			case "O":
				result = "Over " + result.substring(1) + "_";
				break;
			default:
				break;
			}
		}
		
		return result;
	}

	private boolean alreadySent(String recordKey) {
		boolean found = false;
		boolean betterRatingFound = false;
		if(alreadySentArbsOdds != null && !alreadySentArbsOdds.isEmpty()) {
			String[] splittedRecord = recordKey.split(KEY_SEPARATOR);
			String[] arbSplittedValues = splittedRecord[0].split(VALUE_SEPARATOR);
			String key = arbSplittedValues[0] + VALUE_SEPARATOR +
					arbSplittedValues[1] + VALUE_SEPARATOR + 
					arbSplittedValues[2] + VALUE_SEPARATOR + 
					arbSplittedValues[5] + VALUE_SEPARATOR +
					arbSplittedValues[6] + VALUE_SEPARATOR +
					arbSplittedValues[8] + VALUE_SEPARATOR +
					arbSplittedValues[9];
			
			String runIdFoundWithLowerRatings = null;
			String tmpRating1 = null;
			String tmpRating2 = null;
			String tmpRating1Stored = null;
			String tmpRating2Stored = null;

			for(String runId : alreadySentArbsOdds.keySet()) {
				String rating1 = null;
				String rating2 = null;
				String rating1Stored = null;
				String rating2Stored = null;
				betterRatingFound = false;
				//Se non ho trovato una run con lo stesso record ma con rating inferiore o quella che ho trovato è precedente a runId
				if(runIdFoundWithLowerRatings == null || runIdFoundWithLowerRatings.compareTo(runId) < 0) {
					Map<String, Map<String, String>> arbsRunMap = alreadySentArbsOdds.get(runId);
					for(String arbs : arbsRunMap.keySet()) {
						if(key.equalsIgnoreCase(arbs)) {
							found = true;
							rating1 = arbsRunMap.get(arbs).get("R1");
							rating2 = arbsRunMap.get(arbs).get("R2");
							
							String[] ratings = splittedRecord[1].split(VALUE_SEPARATOR);
							rating1Stored = ratings[0];
							rating2Stored = null;
							if(ratings.length == 2) {
								rating2Stored = ratings[1];
							}
							//Se il record è già stato inviato in precedenza ma con dei rating più bassi, lo reinvio
							if(rating1Stored.compareTo(rating1) > 0 && ((rating2Stored == null && rating2 == null) ||
									(rating2Stored != null && rating2 != null && rating2Stored.compareTo(rating2) > 0))) {
								betterRatingFound = true;
								tmpRating1 = rating1;
								tmpRating1Stored = rating1Stored;
								tmpRating2 = rating2;
								tmpRating2Stored = rating2Stored;
								runIdFoundWithLowerRatings = runId;
							}else {
								break;
							}
						}
					}
				}
			}
			if(runIdFoundWithLowerRatings != null) {
				logger.info("Key arbs " + key + " has been already sent, but with lower ratings. now_R1 = " +  tmpRating1 + "; stored_R1 = " + tmpRating1Stored + "; new_R2 = " + tmpRating2 + "; stored_R2 = " + tmpRating2Stored);
				logger.info("Message is resent");
			}
		}
		return found || betterRatingFound;
	}

	private String createRecordKey(RecordOutput record) {
		
		String rating2 = "";
		String liquidita = "";
		
		if(record instanceof RecordBookmakerVsBookmakerOdds) {
			rating2 += ((RecordBookmakerVsBookmakerOdds)record).getRating2();
		}
		
		if(record instanceof RecordBookmakerVsExchangeOdds) {
			liquidita += ((RecordBookmakerVsExchangeOdds) record).getLiquidita();
		}
		
		return record.getEvento() + VALUE_SEPARATOR +
				record.getDataOraEvento() + VALUE_SEPARATOR +
				record.getSport() + VALUE_SEPARATOR + 
				record.getNazione()+ VALUE_SEPARATOR + 
				record.getCampionato() + VALUE_SEPARATOR + 
				record.getBookmakerName1() + VALUE_SEPARATOR + 
				record.getScommessaBookmaker1() + VALUE_SEPARATOR + 
				record.getQuotaScommessaBookmaker1() + VALUE_SEPARATOR + 
				record.getBookmakerName2() + VALUE_SEPARATOR + 
				record.getScommessaBookmaker2() + VALUE_SEPARATOR + 
				record.getQuotaScommessaBookmaker2() + KEY_SEPARATOR + 
				record.getRating() + (rating2.isEmpty() ? "" : VALUE_SEPARATOR + 
				rating2) + (liquidita.isEmpty() ? "" : KEY_SEPARATOR + 
				liquidita);
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
	    	String filename = BlueSheepComparatoreMain.getProperties().getProperty("PREVIOUS_RUN_PATH") + previousRunFilename;
			DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary(BlueSheepComparatoreMain.getProperties().getProperty("PREVIOUS_RUN_PATH"));

			//Verifico che il file esista, dalla mappa dei record già processati popolata in fase di inizializzazione
			if(alreadySentArbsOdds != null && alreadySentArbsOdds.keySet().size() > 0) {
				long checkBoundTime = System.currentTimeMillis();
				//TODO inserire una variabile di proprietà per questa costante
				if(!(alreadySentArbsOdds.keySet().size() < 20)) {
					Set<String> runIdSet = alreadySentArbsOdds.keySet();
					//Scarta le run non più nell'intervallo di tempo scelto
					for(String runId : runIdSet) {
						//Se la run è entro la mezz'ora, allora ok, altrimenti scartala a prescindere
						//TODO inserire una variabile di proprietà per questa costante
						if(checkBoundTime - new Long(runId) >= 60 * 60 * 1000L) {
							alreadySentArbsOdds.remove(runId);
						}
					}
					//Se dopo la rimozione delle run non più valide, sono ancora presenti più di un tot di run
					if(!(alreadySentArbsOdds.keySet().size() < 20)) {
						//set delle runId aggiornato, senza le run non valide
						runIdSet = alreadySentArbsOdds.keySet();
						//Cerco la run più vecchia, che sia per ordine oltre la 20-esima esecuzione
						String oldestRun = null;
						for(String runId : runIdSet) {
							if(oldestRun == null || runId.compareTo(oldestRun) > 0) {
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
				String[] splittedRecord = record.split(KEY_SEPARATOR);
				String[] arbsSplittedValues = splittedRecord[0].split(VALUE_SEPARATOR);
				String key = arbsSplittedValues[0] + VALUE_SEPARATOR +
						arbsSplittedValues[1] + VALUE_SEPARATOR + 
						arbsSplittedValues[2] + VALUE_SEPARATOR + 
						arbsSplittedValues[5] + VALUE_SEPARATOR +
						arbsSplittedValues[6] + VALUE_SEPARATOR +
						arbsSplittedValues[8] + VALUE_SEPARATOR +
						arbsSplittedValues[9];
				Map<String, String> ratingMap = new HashMap<String, String>();
				String[] ratings = splittedRecord[1].split(VALUE_SEPARATOR);
				ratingMap.put("R1", ratings[0]);
				if(ratings.length == 2 && !ratings[1].isEmpty()) {
					ratingMap.put("R2", ratings[1]);
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
			    		line = runId + KEY_SEPARATOR;
		    			line += arbsRecord + KEY_SEPARATOR;
		    			Map<String, String> arbsRatingMap = arbsRunMap.get(arbsRecord);
		    			line += arbsRatingMap.get("R1") ;
		    			if(arbsRatingMap.get("R2") != null) {
		    				line += VALUE_SEPARATOR + arbsRatingMap.get("R2");
		    			}
		    			line += System.lineSeparator();
				    	writer1.write(line);
		    		}
		    	}
		    	//Scrive il file

		    	
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
			
//			//TODO: da modificare
//			ICompareInformationEvents processDataManager = ProcessDataManagerFactory.getICompareInformationEventsByString(Service.CSV_SERVICENAME);
//			List<AbstractInputRecord> transformedRecords = new ArrayList<AbstractInputRecord>();
//			try {
//				transformedRecords = processDataManager.compareAndCollectSameEventsFromBookmakerAndTxOdds(allServiceApiMapResult.get(Service.CSV_SERVICENAME).get(Sport.TENNIS), eventoScommessaRecordMap);
//				addToChiaveEventoScommessaMap(transformedRecords);
//			} catch (Exception e) {
//				logger.log(Level.SEVERE, e.getMessage(), e);
//			}
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
		
		logger.info("Data retrieval terminated. Adding to chiaveEventoScommessaMap TxOdds events");
		
		for(Sport sport : Sport.values()) {
			addToChiaveEventoScommessaMap(allServiceApiMapResult.get(Service.TXODDS_SERVICENAME).get(sport));
		}
		
		logger.info("Add to chiaveEventoScommessaMap TxOdds events finished");

		
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
