package it.bluesheep.arbitraggi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

public class AntiSpamMessageUtil {

	private AntiSpamMessageUtil() {}
	
	private static Map<String, Integer> recordOutputOccurrencesMap = new TreeMap<String,Integer>();
	private static int THRESHOLD_SPAM_COUNT;
	private static Logger logger = Logger.getLogger(AntiSpamMessageUtil.class);
	
	
	public static void initialize() {
		long startTime = System.currentTimeMillis();
		THRESHOLD_SPAM_COUNT = Integer.parseInt(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.SPAM_THRESHOLD_COUNT));

		if(!recordOutputOccurrencesMap.isEmpty()) {
			SimpleDateFormat sdfInput = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
			List<String> keyList = new ArrayList<String>(recordOutputOccurrencesMap.keySet());
			for(String keyBookmakerSpam : keyList) {
				Date date = null;
				try {
					date = sdfInput.parse(keyBookmakerSpam.split(BlueSheepConstants.REGEX_CSV)[1]);
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
					continue;
				}
				
				if(date != null) {
					if(date.getTime() - startTime < 0) {
						recordOutputOccurrencesMap.remove(keyBookmakerSpam);
					}
				}
			}
		}
		
	}
	
	public static List<String> filterRecordOutputListFromSpam(List<String> recordOutputList) {
		
		logger.info("Starting anti-spam control on record list size = " + recordOutputList.size());
		
		List<String> filteredListBySpam = new ArrayList<String>(recordOutputList);
		Set<String> toBeUpdateList = new HashSet<String>();
		
		if(!recordOutputOccurrencesMap.isEmpty()) {
			List<String> keyArbsSpam = new ArrayList<String>(recordOutputOccurrencesMap.keySet());
			for(String recordOuput : recordOutputList) {
				logger.info("Starting anti-spam control on record " + recordOutputList.indexOf(recordOuput));
				String spamBookmakersEventoScommessaKey = ArbsUtil.getTransformedKeyToString(recordOuput);
				Integer occurrencesRecord = null;
				for(String recordOutputAlreadySent : keyArbsSpam) {
					
					logger.debug("Key arbs numero " + recordOutputList.indexOf(recordOuput) + " : " + spamBookmakersEventoScommessaKey);
					logger.debug("Stringa di controllo già salvata " + recordOutputAlreadySent + " ::: " + recordOutputOccurrencesMap.get(recordOutputAlreadySent));
					
					//Se l'ho già mappato nello spam
					if(ArbsUtil.isSameBetBookmakerEventRecordOutputKey(recordOuput, recordOutputAlreadySent)) {
						occurrencesRecord = recordOutputOccurrencesMap.get(spamBookmakersEventoScommessaKey);

						logger.info("Same event found : " + spamBookmakersEventoScommessaKey + " ::: Occurrence = " + occurrencesRecord);
						//Le occorrenze non superano la soglia 
						if(occurrencesRecord != null && occurrencesRecord >= THRESHOLD_SPAM_COUNT - 1) {
							logger.info("Record key has been detected as spam : Key = " + spamBookmakersEventoScommessaKey + " :::: Occurrences = " + occurrencesRecord);
							filteredListBySpam.remove(recordOuput);
							toBeUpdateList.add(spamBookmakersEventoScommessaKey);
							break;
						}
					}else {
						logger.debug("Same event not found : " + spamBookmakersEventoScommessaKey);
						occurrencesRecord = new Integer(0);
					}
				}
				
				recordOutputOccurrencesMap.put(spamBookmakersEventoScommessaKey, occurrencesRecord);
				toBeUpdateList.add(spamBookmakersEventoScommessaKey);
				logger.info("Anti-spam control on record " + recordOutputList.indexOf(recordOuput) + " completed");
			}
		}else {
			
			logger.info("Initializing spam map");
			
			for(String recordOuput : recordOutputList) {
				String spamBookmakerEventoScommessaKey = ArbsUtil.getTransformedKeyToString(recordOuput);
				recordOutputOccurrencesMap.put(spamBookmakerEventoScommessaKey, new Integer(0));
				toBeUpdateList.add(spamBookmakerEventoScommessaKey);
			}
		}
		
		for(String spamKey : toBeUpdateList) {
			recordOutputOccurrencesMap.put(spamKey, new Integer(recordOutputOccurrencesMap.get(spamKey).intValue() + 1));
		}
		
		logger.info("Anti-spam control on record list completed. Valid records are " + filteredListBySpam.size());

		
		return filteredListBySpam;
	}
	
	

	
}
