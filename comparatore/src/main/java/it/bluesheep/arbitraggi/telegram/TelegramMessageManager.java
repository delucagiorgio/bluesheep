package it.bluesheep.arbitraggi.telegram;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.arbitraggi.imagegeneration.ImageGenerator;
import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.DirectoryFileUtilManager;

public class TelegramMessageManager {
	
	private static Logger logger = Logger.getLogger(TelegramMessageManager.class);
	
	private long startTimeExecution;
	
	public TelegramMessageManager(long startTime) {
		startTimeExecution = startTime;
	}

	public void sendMessageToTelegramGroupByBotAndStore(List<ArbsRecord> outputRecordKeys) {
		if(outputRecordKeys == null || outputRecordKeys.isEmpty()) {
			return;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary("../xhtml/");
		
		ImageGenerator imageGenerator = new ImageGenerator(sdf.format(new Date(startTimeExecution)));
		
		Map<String, Map<String, List<String>>> eventsIdLinkMap = imageGenerator.generate(outputRecordKeys);
			
		// Fase di invio tramite telegram
	    TelegramHandler telegramHandler = new TelegramHandler();
		List<String> chat_ids = telegramHandler.getTelegramUsersIds();	
		String pictureFormat = ".png";
		
		
	    String caption = null;
	    List<String> idFileOrderedList = new ArrayList<String>(eventsIdLinkMap.keySet());
	    
	    Collections.sort(idFileOrderedList, new Comparator<String>() {
	        public int compare(String o1, String o2) {
	            Integer i1 = Integer.parseInt(o1);
	            Integer i2 = Integer.parseInt(o2);
	            return (i1 < i2 ? -1 : (i1 == i2 ? 0 : 1));
	        }
	    });
	    
		String text = "🐑🐑 " + ArbsUtil.getTelegramBoldString("LE SHEEPPATE") + " 🐑🐑";
		telegramHandler.sendMessage(text, chat_ids);
	    
	    for (String idFile : idFileOrderedList) {
	    	Map<String, List<String>> recordKeyLinksMap = eventsIdLinkMap.get(idFile);
	    	int i = Integer.parseInt(idFile);
	    	
	    	//Dovrebbe essere sempre unico
	    	String recordKey = new ArrayList<String>(recordKeyLinksMap.keySet()).get(0);
	    	
	    	// Aggiungere la parte della didascalia coi link
	    	try {
				caption = createCaptionDescription(recordKey, recordKeyLinksMap.get(recordKey));
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);			
			}
	    					
	    	telegramHandler.sendPicture("../xhtml/" + (i - 1) + pictureFormat, ArbsUtil.getTelegramBoldString("Segnalazione numero:") + " " + i, chat_ids);
			telegramHandler.sendMessage(caption, chat_ids);
	    	
		    imageGenerator.delete("../xhtml/" + (i - 1) + pictureFormat);

	    }
	    
	    if(eventsIdLinkMap.keySet().size() > 0 ) {
		    logger.info("Photos sending completed");
	    }else {
	    	logger.info("No photo to be sent");
	    }
	}

	private String createCaptionDescription(String eventoIdLink, List<String> linkBookmakerList) throws ParseException {
		String[] eventoIdLinkSplitted = eventoIdLink.split(BlueSheepConstants.IMAGE_ID);
		String[] eventoSplittedKey = eventoIdLinkSplitted[0].split(BlueSheepConstants.REGEX_CSV);
		SimpleDateFormat sdfOutput = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat sdfInput = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

		String linkBookmakers = "";
		for(String bookmakerLink : linkBookmakerList) {
			String[] splittedBookmakerLink = bookmakerLink.split(BlueSheepConstants.KEY_SEPARATOR);
			linkBookmakers += ArbsUtil.getTelegramBoldString(splittedBookmakerLink[0] + ":") + " " + splittedBookmakerLink[1] + System.lineSeparator();
		}
		
		
		return ArbsUtil.getTelegramBoldString("Segnalazione numero:") + " " + eventoIdLinkSplitted[1] + System.lineSeparator() + 
				ArbsUtil.getTelegramBoldString("Evento:") + " " + eventoSplittedKey[0] + BlueSheepConstants.REGEX_VERSUS + eventoSplittedKey[1] + System.lineSeparator() + 
				ArbsUtil.getTelegramBoldString("Data e ora:") + " " + sdfOutput.format(sdfInput.parse(eventoSplittedKey[2])) + System.lineSeparator() + 
				ArbsUtil.getTelegramBoldString("Links:") + System.lineSeparator() + 
				linkBookmakers;
	}
}
