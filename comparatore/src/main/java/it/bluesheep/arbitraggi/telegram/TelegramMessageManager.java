package it.bluesheep.arbitraggi.telegram;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.arbitraggi.imagegeneration.ImageGenerator;
import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.DirectoryFileUtilManager;

public class TelegramMessageManager {
	
	private static Logger logger;
	
	private long startTimeExecution;
	
	public TelegramMessageManager(long startTime) {
		logger = Logger.getLogger(TelegramMessageManager.class);
		startTimeExecution = startTime;
	}

	public void sendMessageToTelegramGroupByBotAndStore(List<String> outputRecordKeys, Map<String, Map<String, Map<String, Map<String, String>>>> alreadySentArbsOdds) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		outputRecordKeys.add(0, "" + sdf.format(new Date(startTimeExecution)));
		
		DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary("../xhtml/");
		
		ImageGenerator imageGenerator = new ImageGenerator();
		Map<String, List<String>> eventsIdLinkMap = imageGenerator.generate(outputRecordKeys);
		
		// Fase di invio tramite telegram
		
		List<String> chat_ids = new ArrayList<String>();
		chat_ids.add(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.CHAT_ID));
		
		String pictureFormat = ".png";
		
		
	    TelegramHandler telegramHandler = new TelegramHandler();
	    String caption = null;
	    for (String eventoIdLink : eventsIdLinkMap.keySet()) {
	    	String idFile = eventoIdLink.split(BlueSheepConstants.IMAGE_ID)[1];
	    	int i = Integer.parseInt(idFile);
	    	
	    	// Aggiungere la parte della didascalia coi link
	    	try {
				caption = createCaptionDescription(eventoIdLink, eventsIdLinkMap.get(eventoIdLink));
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);			
			}
	    	
	    	for (int j = 0; j < chat_ids.size(); j++) {
				
	    		telegramHandler.sendPicture("../xhtml/" + (i - 1) + pictureFormat, ArbsUtil.getTelegramBoldString("Segnalazione numero:") + " " + i, chat_ids.get(j));
				telegramHandler.sendMessage(caption, chat_ids.get(j));
	    	}
	    	
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
