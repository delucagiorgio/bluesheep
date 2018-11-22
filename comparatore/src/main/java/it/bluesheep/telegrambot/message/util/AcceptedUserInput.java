package it.bluesheep.telegrambot.message.util;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author giorgio
 *
 * I comandi accettati dal sistema
 */
public enum AcceptedUserInput {

	MENU("/menu"),
	START("/start");
	
	private String inputUser;
	
	AcceptedUserInput(String text) {
		this.inputUser = text;
	}

	public String getInputUser() {
		return inputUser;
	}
	
	public static Set<String> getAvailableUserInputs(){
		Set<String> returnList = new HashSet<String>();
		for(AcceptedUserInput input : AcceptedUserInput.values()) {
			returnList.add(input.getInputUser());
		}
		return returnList;
		
	}
}
