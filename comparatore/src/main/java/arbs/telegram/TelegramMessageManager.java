package arbs.telegram;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import arbs.imagegeneration.ImageGenerator;
import arbs.util.ArbsConstants;
import arbs.util.ArbsUtil;
import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.DirectoryFileUtilManager;

public class TelegramMessageManager {
	
	private static Logger logger;
	
	private long startTimeExecution;
	
	public TelegramMessageManager(long startTime) {
		logger = (new BlueSheepLogger(TelegramMessageManager.class)).getLogger();
		startTimeExecution = startTime;
	}

	public void sendMessageToTelegramGroupByBotAndStore(List<String> outputRecordKeys, Map<String, Map<String, Map<String, String>>> alreadySentArbsOdds) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		outputRecordKeys.add(0, "" + sdf.format(new Date(startTimeExecution)));
		
		DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary("../xhtml/");
		
		ImageGenerator imageGenerator = new ImageGenerator();
		Map<String, List<String>> eventsIdLinkMap = imageGenerator.generate(outputRecordKeys);
		
		// Fase di invio tramite telegram
		
		List<String> chat_ids = new ArrayList<String>();
		chat_ids.add(BlueSheepComparatoreMain.getProperties().getProperty("CHAT_ID"));
		
		String pictureFormat = ".png";
		
		
	    TelegramHandler telegramHandler = new TelegramHandler();
	    String caption;
	    for (String eventoIdLink : eventsIdLinkMap.keySet()) {
	    	String idFile = eventoIdLink.split(ArbsConstants.IMAGE_ID)[1];
	    	int i = Integer.parseInt(idFile);
	    	
	    	caption = createCaptionDescription(eventoIdLink.split(ArbsConstants.IMAGE_ID)[0], eventsIdLinkMap.get(eventoIdLink));  // Aggiungere la parte della didascalia coi link
		    for (int j = 0; j < chat_ids.size(); j++) {
				telegramHandler.sendPicture("../xhtml/" + i + pictureFormat, caption, chat_ids.get(j));
		    }
		    imageGenerator.delete("../xhtml/" + i + pictureFormat);
	    }
	    if(eventsIdLinkMap.keySet().size() > 0 ) {
		    logger.info("Photos sending completed");
	    }else {
	    	logger.info("No photo to be sent");
	    }
	}

	private String createCaptionDescription(String eventoIdLink, List<String> linkBookmakerList) {
		String[] eventoSplittedKey = eventoIdLink.split(ArbsConstants.VALUE_SEPARATOR);
		
		String linkBookmakers = "";
		for(String bookmakerLink : linkBookmakerList) {
			String[] splittedBookmakerLink = bookmakerLink.split(ArbsConstants.KEY_SEPARATOR);
			linkBookmakers += ArbsUtil.getTelegramBoldString(splittedBookmakerLink[0] + ":") + " " + splittedBookmakerLink[1] + System.lineSeparator();
		}
		
		return ArbsUtil.getTelegramBoldString("Evento:") + " " + eventoSplittedKey[0] + " vs " + eventoSplittedKey[1] + System.lineSeparator() + 
				ArbsUtil.getTelegramBoldString("Data e ora:") + " " + eventoSplittedKey[2] + System.lineSeparator() + 
				ArbsUtil.getTelegramBoldString("Links:") + System.lineSeparator() + 
				linkBookmakers;
	}
}
