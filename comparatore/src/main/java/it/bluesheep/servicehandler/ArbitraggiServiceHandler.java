package it.bluesheep.servicehandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import it.bluesheep.arbitraggi.telegram.TelegramMessageManager;
import it.bluesheep.arbitraggi.util.AntiSpamMessageUtil;
import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.output.subtype.RecordBookmakerVsExchangeOdds;
import it.bluesheep.comparatore.io.datacompare.CompareProcessFactory;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.DirectoryFileUtilManager;

/** 
 * @author GD
 * Classe che fornisce il servizio di messaggistica degli arbitraggi.
 */
public final class ArbitraggiServiceHandler extends AbstractBlueSheepService{

	private static double BETTER_ODD_PERCENTAGE;
	private static Logger logger;
	private static ArbitraggiServiceHandler instance;
	private static Map<String, Map<String, Map<String, String>>> alreadySentArbsOdds;
	private static long startTime;

	
	private ArbitraggiServiceHandler() {
		super();
		logger = Logger.getLogger(ArbitraggiServiceHandler.class);
		BETTER_ODD_PERCENTAGE = new Double(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETTER_ODD_PERCENTAGE));
	}
	
	public static synchronized ArbitraggiServiceHandler getArbitraggiServiceHandlerInstance() {
		if(instance == null) {
			instance = new ArbitraggiServiceHandler();
		}
		return instance;
	}
	
	@Override
	public void run() {
		try {
			startTime = System.currentTimeMillis();
			
			startArbitraggiProcess();
			
			long endTime = System.currentTimeMillis();
			
			logger.info("Arbitraggi execution terminated in " + (endTime - startTime)/1000 + " seconds.");
		}catch(Exception e) {
			logger.error("ERRORE THREAD :: " + e.getMessage(), e);
		}
	}

	/**
	 * GD - 05/08/18
	 * Avvia il processo che calcola gli arbitraggi.
	 * 1. comparazione delle quote
	 * 2. creazione dei record univoci di arbitraggio
	 * 3. filtraggio degli arbitraggi già inviati
	 * 4. salvataggio degli arbitraggi da inviare su file
	 * 5. invio tramite TelegramBot dei messaggi
	 */
	private void startArbitraggiProcess() {
		
		getAlreadySentArbsOdds();
		
		Map<Service, List<RecordOutput>> outputRecordMap = CompareProcessFactory.startComparisonOdds(this);
		List<RecordOutput> tabellaOutputList = new ArrayList<RecordOutput>();
		for(Service tabellaType : outputRecordMap.keySet()) {
			tabellaOutputList.addAll(outputRecordMap.get(tabellaType));
		}
		
		outputRecordMap.clear();
		
		if(tabellaOutputList != null && !tabellaOutputList.isEmpty()) {
			List<String> messageToBeSentKeysList = new ArrayList<String>();
			int alreadySentCount = 0;
			for(RecordOutput record : tabellaOutputList) {
				String recordKey = ArbsUtil.getKeyArbsFromOutputRecord(record);
				boolean isValidExchangeRecord = record instanceof RecordBookmakerVsExchangeOdds && ((RecordBookmakerVsExchangeOdds)record).getLiquidita() >= 50D;
				if((!(record instanceof RecordBookmakerVsExchangeOdds) || isValidExchangeRecord)) {
					//controllo che non l'abbia già mandata, se si non faccio nulla
					if(!alreadySent(recordKey)) {
						messageToBeSentKeysList.add(recordKey + BlueSheepConstants.KEY_SEPARATOR + record.getLinkBook1() + BlueSheepConstants.REGEX_CSV + record.getLinkBook2());
					}else {
						alreadySentCount++;
					}
				}else {
					logger.info("Insufficient size (liquidità) by requirements : " + 50D);
				}
			}
			
			logger.info("" + messageToBeSentKeysList.size() + " message(s) to be sent. Message(s) already sent " + alreadySentCount + "/" + tabellaOutputList.size() );
	
			//Se ci sono aggiornamenti o nuovi arbitraggi, invia i risultati e li salva
			if(!messageToBeSentKeysList.isEmpty()) {
				AntiSpamMessageUtil.initialize();
				
				saveOutputOnFile(messageToBeSentKeysList);
	
				messageToBeSentKeysList = AntiSpamMessageUtil.filterRecordOutputListFromSpam(messageToBeSentKeysList);
				
				TelegramMessageManager tmm = new TelegramMessageManager(startTime);
				tmm.sendMessageToTelegramGroupByBotAndStore(messageToBeSentKeysList);
			}
		}
		tabellaOutputList.clear();
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
	private void getAlreadySentArbsOdds(){
		if(alreadySentArbsOdds == null) {
			alreadySentArbsOdds = new TreeMap<String, Map<String, Map<String, String>>>();
		}
		String filenamePath = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.PREVIOUS_RUN_PATH) + BlueSheepConstants.FILENAME_PREVIOUS_RUNS;
		List<String> inputFileList = new ArrayList<String>();
		try{	
			BufferedReader br = new BufferedReader(new FileReader(filenamePath));

			String line = br.readLine();
			while (line != null) {
				inputFileList.add(line);
				line = br.readLine();
			}
			br.close();
		}catch(IOException e) {
			logger.error(e.getMessage(), e);
		}
		if(!inputFileList.isEmpty()) {
			alreadySentArbsOdds = ArbsUtil.initializePreviousRunRecordsMap(inputFileList);
			logger.info("There are already " + alreadySentArbsOdds.size() + " run collection of message sent.");
		}
		

	}
	
	/**
	 * GD - 05/08/18
	 * Controlla che, dato un record univoco di arbitraggio, non sia già stato inviato nelle precedenti run del servizio,
	 * al fine di evitare lo spam. La logica del metodo può restituire un valore FALSE se il record non è stato inviato o 
	 * è stato inviato con una condizione di vantaggio minore, restituisce TRUE in tutti gli altri casi.
	 * @param recordKey il record univoco da verificare
	 * @return false se non inviato o inviato con rating minore, vero altrimenti
	 */
	private boolean alreadySent(String recordKey) {
		boolean found = false;
		boolean betterRatingFound = false;
		String runIdFoundWithLowerRatings = null;
		String tmpRating1 = null;
		String tmpRating2 = null;
		String tmpRating1Stored = null;
		String tmpRating2Stored = null;
		
		if(alreadySentArbsOdds != null && !alreadySentArbsOdds.isEmpty()) {
			String[] splittedRecord = recordKey.split(BlueSheepConstants.KEY_SEPARATOR);
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
				//Se non ho trovato una run con lo stesso record ma con rating inferiore o quella che ho trovato è precedente a runId
				if(runIdFoundWithLowerRatings == null || runIdFoundWithLowerRatings.compareTo(runId) < 0) {
					Map<String, Map<String, String>> arbsRunMap = alreadySentArbsOdds.get(runId);
					for(String arbs : arbsRunMap.keySet()) {
						if(key.equalsIgnoreCase(arbs)) {
							found = true;
							rating1Stored = arbsRunMap.get(arbs).get(BlueSheepConstants.RATING1);
							rating2Stored = arbsRunMap.get(arbs).get(BlueSheepConstants.RATING2);
							
							String[] ratings = splittedRecord[1].split(BlueSheepConstants.REGEX_CSV);
							rating1 = ratings[0];
							rating2 = null;
							if(ratings.length == 2) {
								rating2 = ratings[1];
							}
							//Se il record è già stato inviato in precedenza ma con dei rating più bassi, lo reinvio
							if(rating1Stored.compareTo(rating1) < 0 && ((rating2Stored == null && rating2 == null) ||
									(rating2Stored != null && rating2 != null && rating2Stored.compareTo(rating2) < 0)) && 
									(new Double(rating1) - new Double(rating1Stored)) > BETTER_ODD_PERCENTAGE) {
								betterRatingFound = true;
								tmpRating1 = rating1;
								tmpRating1Stored = rating1Stored;
								tmpRating2 = rating2;
								tmpRating2Stored = rating2Stored;
								runIdFoundWithLowerRatings = runId;
								logger.info("Key arbitraggio " + key + 
										" has been already sent, but with lower ratings. now_R1 = " +  rating1 + 
										"; stored_R1 = " + rating1Stored + 
										"; new_R2 = " + rating2 + 
										"; stored_R2 = " + rating2Stored + 
										"; increased rating value = " + (new Double(rating1) - new Double(rating1Stored)) +
										"; RunID = " + runId);
							}else {
								logger.debug("Key arbitraggio " + key + 
										" has been already sent, but with higher or equal on the ratings. now_R1 = " +  rating1 + 
										"; stored_R1 = " + rating1Stored + 
										"; new_R2 = " + rating2 + 
										"; stored_R2 = " + rating2Stored + 
										"; RunID = " + runId);
								betterRatingFound = false;
								break;
							}
						}
					}
				}
			}
			if(runIdFoundWithLowerRatings != null && betterRatingFound) {
				logger.info("Key arbitraggio " + key + " has been already sent, but with lower ratings. now_R1 = " +  tmpRating1 + "; stored_R1 = " + tmpRating1Stored + "; new_R2 = " + tmpRating2 + "; stored_R2 = " + tmpRating2Stored);
				logger.info("Message is resent");
				recordKey = "BETTER_ODD" + recordKey;
				found = false;
			}
		}
		return found;
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
	    	
	    	String filename = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.PREVIOUS_RUN_PATH) + BlueSheepConstants.FILENAME_PREVIOUS_RUNS;
			DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.PREVIOUS_RUN_PATH));
			//Verifico che il file esista, dalla mappa dei record già processati popolata in fase di inizializzazione
			if(alreadySentArbsOdds != null && alreadySentArbsOdds.keySet().size() > 0) {
				long checkBoundTime = System.currentTimeMillis();
				if(alreadySentArbsOdds != null && !alreadySentArbsOdds.isEmpty() && !(alreadySentArbsOdds.keySet().size() < BlueSheepConstants.STORED_RUNS_MAX)) {
					List<String> runIdSet = new ArrayList<String>(alreadySentArbsOdds.keySet());
					//Scarta le run non più nell'intervallo di tempo scelto
					for(String runId : runIdSet) {
						//Se la run è entro la mezz'ora, allora ok, altrimenti scartala a prescindere
						if(checkBoundTime - new Long(runId) >= 60 * 60 * 1000L) {
							alreadySentArbsOdds.remove(runId);
						}
					}
					//Se dopo la rimozione delle run non più valide, sono ancora presenti più di un tot di run
					if(!(alreadySentArbsOdds.keySet().size() < BlueSheepConstants.STORED_RUNS_MAX)) {
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
				String temp = new String(record);
				if(temp.startsWith("BETTER_ODD")) {
					temp = temp.substring(10);
				}
				String[] splittedRecord = temp.split(BlueSheepConstants.KEY_SEPARATOR);
				String key = ArbsUtil.createArbsKeyFromRecordKey(splittedRecord[0]);
				Map<String, String> ratingMap = new HashMap<String, String>();
				String[] ratings = splittedRecord[1].split(BlueSheepConstants.REGEX_CSV);
				ratingMap.put(BlueSheepConstants.RATING1, ratings[0]);
				if(ratings.length == 2 && !ratings[1].isEmpty()) {
					ratingMap.put(BlueSheepConstants.RATING2, ratings[1]);
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
	    		
				writer1 = new PrintWriter(filename, BlueSheepConstants.ENCODING_UTF_8);    	
		    	// Scrivo
				
		    	for(String runId : alreadySentArbsOdds.keySet()) {
		    		Map<String, Map<String, String>> arbsRunMap = alreadySentArbsOdds.get(runId);
		    		for(String arbsRecord : arbsRunMap.keySet()) {
			    		line = runId + BlueSheepConstants.KEY_SEPARATOR;
		    			line += arbsRecord + BlueSheepConstants.KEY_SEPARATOR;
		    			Map<String, String> arbsRatingMap = arbsRunMap.get(arbsRecord);
		    			line += arbsRatingMap.get(BlueSheepConstants.RATING1) ;
		    			if(arbsRatingMap.get(BlueSheepConstants.RATING2) != null) {
		    				line += BlueSheepConstants.REGEX_CSV + arbsRatingMap.get(BlueSheepConstants.RATING2);
		    			}
		    			line += System.lineSeparator();
				    	writer1.write(line);
		    		}
		    	}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}finally {
				if(writer1 != null) {
					writer1.close();
				}
			}
	    	alreadySentArbsOdds.clear();
		}

	}
}
