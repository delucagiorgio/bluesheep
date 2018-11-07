package it.bluesheep.telegrambot;

import java.util.HashSet;
import java.util.Set;

public enum AcceptedUserInput {

	MENU("/menu"),
	REGISTRATION("/registrazione"),
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
