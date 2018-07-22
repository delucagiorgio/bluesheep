package arbs.telegram;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import arbs.util.ArbsConstants;
import arbs.util.ArbsUtil;
import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.DirectoryFileUtilManager;

public class TelegramMessageManager {
	
	private static Logger logger;
	
	private long startTimeExecution;
	private Map<String, Map<String, Map<String, String>>> alreadySentArbsOdds;
	private Map<String, Map<String, Map<String,String>>> eventoScommessaBookmakersMap;
	
	public TelegramMessageManager(long startTime) {
		logger = (new BlueSheepLogger(TelegramMessageManager.class)).getLogger();
		startTimeExecution = startTime;
	}

	public void sendMessageToTelegramGroupByBotAndStore(List<RecordOutput> outputRecord, Map<String, Map<String, Map<String, String>>> alreadySentArbsOdds) {
		
		if(this.alreadySentArbsOdds == null) {
			this.alreadySentArbsOdds = alreadySentArbsOdds;
		}
		
		if(outputRecord != null && !outputRecord.isEmpty()) {
			List<String> messageList = prepareDataToTextMessageFormat(outputRecord);
			String chatId = BlueSheepComparatoreMain.getProperties().getProperty(ArbsConstants.CHAT_ID);
			if(messageList != null && !messageList.isEmpty()) {
				
				for(String textToBeSent : messageList) {
					String encodedTextToBeSent = null;
					textToBeSent = textToBeSent.substring(0, textToBeSent.length() - 1);
					logger.config("Url parameters = " + textToBeSent);
					try {
						encodedTextToBeSent = URLEncoder.encode(textToBeSent, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
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
				                logger.config(inputLine);
				            }
				            in.close();
						} catch (Exception e) {
							logger.log(Level.SEVERE, e.getMessage(), e);
						}
					}
				}
			}else {
				logger.log(Level.INFO, "No updates");
			}
		}
	}


	private List<String> prepareDataToTextMessageFormat(List<RecordOutput> outputRecord) {
		List<String> stringList = new ArrayList<String>();
		StringBuilder returnString;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		List<String> recordKeyList = new ArrayList<String>();
		eventoScommessaBookmakersMap = new HashMap<String, Map<String, Map<String,String>>>();
		
		collectInformationAboutPreviousRuns();
		
		int i = 0;
		for(RecordOutput record : outputRecord) {
			returnString = new StringBuilder();
			String recordKey = ArbsUtil.getKeyArbsFromOutputRecord(record);
			//controllo che non l'abbia già mandata, se si non faccio nulla
			
			if(!alreadySent(recordKey)) {
				//preparo il layout del messaggio da inviare e lo concateno con quello già processato
				i++;
				String[] splittedRecordKey = recordKey.split(ArbsConstants.KEY_SEPARATOR);
				String[] arbsValuesSplitted = splittedRecordKey[0].split(ArbsConstants.VALUE_SEPARATOR);
				returnString.append(ArbsUtil.getTelegramBoldString("Quota numero:") + " " + i + System.lineSeparator()); 
				returnString.append(ArbsUtil.getTelegramBoldString("Evento:") + " " + arbsValuesSplitted[0] + System.lineSeparator());
				returnString.append(ArbsUtil.getTelegramBoldString("Data evento:") + " " + sdf.format(new Date(record.getDataOraEvento().getTime())) + System.lineSeparator());
				returnString.append(ArbsUtil.getTelegramBoldString("Sport:") + " " + arbsValuesSplitted[2] + System.lineSeparator());
				
				String nazione = arbsValuesSplitted[3];
				if(nazione != null && !nazione.isEmpty() && !"null".equalsIgnoreCase(nazione)) {
					returnString.append(ArbsUtil.getTelegramBoldString("Nazione/Competizione:") + " " + nazione + System.lineSeparator());
				}
				
				returnString.append(ArbsUtil.getTelegramBoldString("Campionato:") + " " + arbsValuesSplitted[4] + System.lineSeparator());
				returnString.append(ArbsUtil.getTelegramBoldString("Book1:") + " " + arbsValuesSplitted[5] + System.lineSeparator());
				returnString.append(ArbsUtil.getTelegramBoldString("Scommessa1:") + " " + ArbsUtil.getScommessaStringURL(arbsValuesSplitted[6])+ System.lineSeparator());
				returnString.append(ArbsUtil.getTelegramBoldString("Quota1:") + " " + arbsValuesSplitted[7] + System.lineSeparator());
				returnString.append(ArbsUtil.getTelegramBoldString("Book2:") + " " + arbsValuesSplitted[8] + System.lineSeparator());
				returnString.append(ArbsUtil.getTelegramBoldString("Scommessa2:") + " " + ArbsUtil.getScommessaStringURL(arbsValuesSplitted[9]) + System.lineSeparator());
				returnString.append(ArbsUtil.getTelegramBoldString("Quota2:") + " "+ arbsValuesSplitted[10] + System.lineSeparator());
				
				String modifier = "";
				String[] ratings = splittedRecordKey[1].split(ArbsConstants.VALUE_SEPARATOR);
				String rating1 = ratings[0];

				if(new Double(rating1) >= 150.00) {
					modifier = " 🚀🚀🚀";
				}
				
				returnString.append(ArbsUtil.getTelegramBoldString("Rating1:") + " " + rating1.substring(0, Math.min(rating1.length(), 6)) + modifier + System.lineSeparator());
				String rating2 = null;
				if(ratings.length == 2) {
					rating2 = ratings[1];
				}
				if(rating2 != null && !rating2.isEmpty()) {
					returnString.append(ArbsUtil.getTelegramBoldString("Rating2:") + " " + rating2.substring(0, Math.min(rating2.length(), 6)) + modifier + System.lineSeparator());
				}
				if(splittedRecordKey.length == 3) {
					String liquidità = splittedRecordKey[2];
					if(!liquidità.isEmpty() && new Double(liquidità) >= 50.00) {
						returnString.append(ArbsUtil.getTelegramBoldString("Liquidità:") + " " + liquidità.substring(0, Math.min(liquidità.length(), 8)) + System.lineSeparator());
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
			Date date = new Date(startTimeExecution);
			returnString.append("🐑🐑🐑 Le SHEEPATE 🐑🐑🐑" + System.lineSeparator());
			returnString.append(ArbsUtil.getTelegramBoldString("Ora estrazione:") + " " + sdf.format(date));

			returnString.append(System.lineSeparator());
			stringList.add(0,returnString.toString());
			
			returnString = new StringBuilder();
			returnString.append("🐑🐑🐑 FINE 🐑🐑🐑" + System.lineSeparator());
			stringList.add(returnString.toString());
			
			saveOutputOnFile(recordKeyList);
		}
		
		
		return stringList;
	}

	private void collectInformationAboutPreviousRuns() {
		if(alreadySentArbsOdds != null && !alreadySentArbsOdds.isEmpty()) {
			for(String runId : alreadySentArbsOdds.keySet()) {
				Map<String, Map<String, String>> arbsMapByRun = alreadySentArbsOdds.get(runId);
				for(String arbsKey : arbsMapByRun.keySet()) {
					
				}
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
			alreadySentArbsOdds.put("" + startTimeExecution, arbsLastExecutionMap);
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
