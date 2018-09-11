package it.bluesheep.comparatore.io.datainput.operationmanager.service.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

public class InputDataHelper {
	
	private static List<String> excludedBookmakers;
	
	public InputDataHelper() {
		excludedBookmakers = getListBlockedBookmakers();
	}
	
	/**
	 * GD - 23/06/2018
	 * Colleziona i nomi dei bookmaker da escludere dalla comparazione delle quote
	 * @return la lista di valori dei bookmaker da escludere
	 */
	private List<String> getListBlockedBookmakers() {
		List<String> blockedBookmakerList = new ArrayList<String>();
		String propertyString = BlueSheepServiceHandlerManager.getProperties().getProperty("BLOCKED_BOOKMAKER");
		
		if(propertyString != null && !propertyString.isEmpty()) {
			String[] splittedInputList = propertyString.split(BlueSheepConstants.REGEX_CSV);
			for(int i = 0; i < splittedInputList.length; i++) {
				String blockedBookmaker = splittedInputList[i];
				if(blockedBookmaker != null && !blockedBookmaker.isEmpty()) {
					blockedBookmakerList.add(blockedBookmaker.toLowerCase());
				}
			}
		}
		return blockedBookmakerList;
	}

	public boolean isBlockedBookmaker(String bookmakerRecordInput) {
		
		Iterator<String> itr = excludedBookmakers.iterator();
		boolean excluded = false;
		
		while(itr.hasNext() && !excluded){
			excluded = itr.next().equalsIgnoreCase(bookmakerRecordInput.toLowerCase());
		}
		
		return excluded;
	}

	public List<String> getExcludedBookmakers() {
		return excludedBookmakers;
	}
	
}
