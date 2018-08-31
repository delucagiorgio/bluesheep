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

import it.bluesheep.arbitraggi.entities.ArbsRecord;
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
			List<String> recordSpamList = new ArrayList<String>(recordOutputOccurrencesMap.keySet());
			for(String recordSpam : recordSpamList) {
				SimpleDateFormat sdfInput = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
				Date date = null;
				try {
					date = sdfInput.parse(recordSpam.split(BlueSheepConstants.REGEX_CSV)[2]);
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
					continue;
				}
				
				if(date != null && date.getTime() - startTime < 0) {
					logger.info("Removing " + recordSpam + " from anti-spam control");
					recordOutputOccurrencesMap.remove(recordSpam);
				}
			}
		}
		
	}
	
	public static List<ArbsRecord> filterRecordOutputListFromSpam(List<ArbsRecord> recordOutputList) {
		
		logger.info("Starting anti-spam control on record list size = " + recordOutputList.size());
		
		List<ArbsRecord> filteredListBySpam = new ArrayList<ArbsRecord>(recordOutputList);
		Set<ArbsRecord> toBeUpdateList = new HashSet<ArbsRecord>();
		
		if(!recordOutputOccurrencesMap.isEmpty()) {
			List<String> recordSpamList = new ArrayList<String>(recordOutputOccurrencesMap.keySet());
			for(ArbsRecord arbsRecord : recordOutputList) {
				Integer occurrencesRecord = null;
				String arbsRecordkey = arbsRecord.getKeyEventoBookmakerBet();
				for(String recordOutputAlreadySentKey : recordSpamList) {
					//Se l'ho già mappato nello spam
					if(arbsRecordkey.equals(recordOutputAlreadySentKey)) {
						occurrencesRecord = recordOutputOccurrencesMap.get(arbsRecordkey);

						logger.info("Same event found : " + arbsRecord.getKeyEvento() + " ::: Bookmakers = " + arbsRecord.getBookmakerList() + " ::: Occurrence = " + occurrencesRecord);
						//Le occorrenze non superano la soglia 
						if(occurrencesRecord != null && occurrencesRecord >= THRESHOLD_SPAM_COUNT) {
							logger.info("Record key has been detected as spam");
							filteredListBySpam.remove(arbsRecord);
							toBeUpdateList.add(arbsRecord);
						}
						break;
					}else {
						logger.debug("Same event not found : " + arbsRecord.getKeyEvento() + " ::: Bookmakers = " + arbsRecord.getBookmakerList());
						occurrencesRecord = new Integer(0);
					}
				}
				
				recordOutputOccurrencesMap.put(arbsRecordkey, occurrencesRecord);
				toBeUpdateList.add(arbsRecord);
				logger.info("Anti-spam control on record " + recordOutputList.indexOf(arbsRecord) + " completed");
			}
		}else {
			
			logger.info("Initializing spam map");
			
			for(ArbsRecord arbsRecord : recordOutputList) {
				String arbsRecordkey = arbsRecord.getKeyEventoBookmakerBet();
				recordOutputOccurrencesMap.put(arbsRecordkey, new Integer(0));
				toBeUpdateList.add(arbsRecord);
			}
		}
		
		for(ArbsRecord spamRecord : toBeUpdateList) {
			String arbsRecordkey = spamRecord.getKeyEventoBookmakerBet();
			recordOutputOccurrencesMap.put(arbsRecordkey, new Integer(recordOutputOccurrencesMap.get(arbsRecordkey).intValue() + 1));
		}
		
		logger.info("Anti-spam control on record list completed. Valid records are " + filteredListBySpam.size());

		
		return filteredListBySpam;
	}
}
