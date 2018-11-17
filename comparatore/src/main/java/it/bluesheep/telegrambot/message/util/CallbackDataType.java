package it.bluesheep.telegrambot.message.util;

import java.util.Arrays;
import java.util.List;

public enum CallbackDataType {

    SHOW_ACTIVE_PREF("1"),
	ENABLE_DISABLE_PREF("2"),
    ADD_PREF("3"),
    DEL_PREF("4"),
    MOD_PREF("5"),
    BACK_TO_MENU("6"),
    CONFIRM("7"),
    BOOKMAKER("bk"),
    RATING("rt"),
    RF("rf"),
    EVENT("ev"),
//    CHAMPIONSHIP("cs"),
    RF_TYPE("rft"),
    SIZE("sz"),
    MINODDVALUE("mo"),
    NEXT_PAGE("8"),
    PREVIOUS_PAGE("9"),
    BACK_TO_KEYBOARD("10"),
    MENU("11");
	
	private String code;
	
	private CallbackDataType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	//Lista delle operazioni eseguibili dagli utenti
	public static List<CallbackDataType> getCommandCallbackDataType(){
		return Arrays.asList(SHOW_ACTIVE_PREF, ENABLE_DISABLE_PREF, ADD_PREF, BACK_TO_MENU, DEL_PREF, MOD_PREF, BACK_TO_KEYBOARD, BACK_TO_MENU);
	}
	
	public static CallbackDataType getCallbackDataType(String callbackData) {
		for(CallbackDataType cdt : values()) {
			if(cdt.getCode().equals(callbackData)) {
				return cdt;
			}
		}
		return null;
	}
	
	public boolean isCommand() {
		return getCommandCallbackType().contains(this);
	}
	
	private List<CallbackDataType> getCommandCallbackType(){
		return Arrays.asList(SHOW_ACTIVE_PREF, ADD_PREF, ENABLE_DISABLE_PREF, MOD_PREF, DEL_PREF, BACK_TO_MENU, CONFIRM, MENU);
	}
	
	public boolean isFilter() {
		return getFilterCallbackType().contains(this);
	}
	
	private List<CallbackDataType> getFilterCallbackType(){
		return Arrays.asList(BOOKMAKER, RF, RATING, EVENT, SIZE, MINODDVALUE);
	}

}
