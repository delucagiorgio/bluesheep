package it.bluesheep.io.datainput.operationmanager.service.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.entities.util.ComparatoreConstants;
import it.bluesheep.util.BlueSheepLogger;

public class CSVEventRenameHelper {
	
	private static CSVEventRenameHelper instance;
	private static Map<String, String> oldPlayerNameNewPlayerNameMap = new HashMap<String, String>();
	private static Logger logger = (new BlueSheepLogger(CSVEventRenameHelper.class)).getLogger();
	
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
			
			in = new FileReader(BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.CSV_PLAYER_RENAME_FILE));
			BufferedReader br = new BufferedReader(in);
			String inLineString;
			while((inLineString = br.readLine()) != null) {
				if(!inLineString.isEmpty() && !inLineString.startsWith("#")) {
					String[] splittedString = inLineString.split(ComparatoreConstants.REGEX_CSV);
					if(splittedString != null && splittedString.length == 2) {
						oldPlayerNameNewPlayerNameMap.put(splittedString[0], splittedString[1]);
					}else {
						logger.log(Level.WARNING, inLineString + " cannot be stored as translation for players");
					}
				}
			}
			br.close();
			in.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
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
