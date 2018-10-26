package it.bluesheep.comparatore.io.datainput.operationmanager.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

public class InputDataHelper {
	
	private static Map<String, List<String>> excludedBookmakersByService;
	private static InputDataHelper instance;
	
	private InputDataHelper() {
		excludedBookmakersByService = getListBlockedBookmakers();
	}
	
	public static synchronized InputDataHelper getInputDataHelperInstance() {
		if(instance == null) {
			instance = new InputDataHelper();
		}
		return instance;
	}
	
	/**
	 * GD - 23/06/2018
	 * Colleziona i nomi dei bookmaker da escludere dalla comparazione delle quote
	 * @return la lista di valori dei bookmaker da escludere
	 */
	private Map<String, List<String>> getListBlockedBookmakers() {
		Map<String, List<String>> returnMap = new HashMap<String, List<String>>();
		List<String> blockedBookmakerListBA = new ArrayList<String>();
		List<String> blockedBookmakerListSB = new ArrayList<String>();
		
		String propertyString1 = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BLOCKED_BOOKMAKER_BONUS_ABUSING);
		String propertyString2 = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BLOCKED_BOOKMAKER_SUREBET);
		
		if(propertyString1 != null && !propertyString1.isEmpty()) {
			String[] splittedInputList = propertyString1.split(BlueSheepConstants.REGEX_CSV);
			for(int i = 0; i < splittedInputList.length; i++) {
				String blockedBookmaker = splittedInputList[i];
				if(blockedBookmaker != null && !blockedBookmaker.isEmpty()) {
					blockedBookmakerListBA.add(blockedBookmaker.toLowerCase());
				}
			}
		}
		
		returnMap.put(BlueSheepConstants.BLOCKED_BOOKMAKER_BONUS_ABUSING, blockedBookmakerListBA);
		
		if(propertyString2 != null && !propertyString2.isEmpty()) {
			String[] splittedInputList = propertyString2.split(BlueSheepConstants.REGEX_CSV);
			for(int i = 0; i < splittedInputList.length; i++) {
				String blockedBookmaker = splittedInputList[i];
				if(blockedBookmaker != null && !blockedBookmaker.isEmpty()) {
					blockedBookmakerListSB.add(blockedBookmaker.toLowerCase());
				}
			}
		}
		
		returnMap.put(BlueSheepConstants.BLOCKED_BOOKMAKER_SUREBET, blockedBookmakerListSB);
		
		return returnMap;
	}

	public boolean isBlockedBookmaker(String bookmakerRecordInput, String serviceType) {
		List<String> blockedBookmakerListService = excludedBookmakersByService.get(serviceType);
		boolean excluded = false;
		if(blockedBookmakerListService != null) {
			Iterator<String> itr = blockedBookmakerListService.iterator();
			
			while(itr.hasNext() && !excluded){
				excluded = itr.next().equalsIgnoreCase(bookmakerRecordInput.toLowerCase());
			}
		}
		return excluded;
	}

	public Map<String, List<String>> getExcludedBookmakers() {
		return excludedBookmakersByService;
	}
	
	public void forceUpdateMapBlockedBookmakers() {
		excludedBookmakersByService = getListBlockedBookmakers();
	}
	
}
