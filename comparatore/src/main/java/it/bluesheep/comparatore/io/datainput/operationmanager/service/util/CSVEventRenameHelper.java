package it.bluesheep.comparatore.io.datainput.operationmanager.service.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

public class CSVEventRenameHelper {
	private static CSVEventRenameHelper instance;
	private static Map<String, String> oldPlayerNameNewPlayerNameMap = new HashMap<String, String>();
	private static Logger logger = Logger.getLogger(CSVEventRenameHelper.class);
	
	private CSVEventRenameHelper() {}
	
	public static synchronized CSVEventRenameHelper getCSVEventRenameHelperInstance() {
		if(instance == null) {
			instance = new CSVEventRenameHelper();
		}
		return instance;
	}
	
	public void initializeMap() {
		
		FileReader in = null;
		
		try {
			in = new FileReader(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.CSV_PLAYER_RENAME_FILE));
			BufferedReader br = new BufferedReader(in);
			String inLineString;
			while((inLineString = br.readLine()) != null) {
				if(!inLineString.isEmpty() && !inLineString.startsWith("#")) {
					String[] splittedString = inLineString.split(BlueSheepConstants.REGEX_CSV);
					if(splittedString != null && splittedString.length == 2) {
						oldPlayerNameNewPlayerNameMap.put(splittedString[0], splittedString[1]);
					}else {
						logger.warn(inLineString + " cannot be stored as translation for players");
					}
				}
			}
			br.close();
			in.close();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static String getTranslationPlayerNameIfAvailable(String playerToBeRenamed) {
		String returnString = playerToBeRenamed;
		String translation = oldPlayerNameNewPlayerNameMap.get(playerToBeRenamed);
		if(translation != null) {
			returnString = translation;
		}
		return returnString;
	}
}
