package it.bluesheep.telegrambot.message.util;

import java.util.Arrays;
import java.util.List;
/**
 * 
 * @author giorgio
 * I comandi utilizzati nelle callback
 */
public enum CallbackDataType {

    SHOW_ACTIVE_PREF("1"),
    ADD_PREF("3"),
    DEL_PREF("4"),
    MOD_PREF("5"),
    BACK_TO_MENU("6"),
    CONFIRM("7"),
    BOOKMAKER("bk"),
    RATING("rt"),
    RF("rf"),
    EVENT("ev"),
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
		return Arrays.asList(SHOW_ACTIVE_PREF, ADD_PREF, BACK_TO_MENU, DEL_PREF, MOD_PREF, BACK_TO_KEYBOARD, BACK_TO_MENU);
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
	
	/**
	 * GD - 18/11/2018
	 * Ritorna i comandi "utente"
	 * @return i comandi
	 */
	private List<CallbackDataType> getCommandCallbackType(){
		return Arrays.asList(SHOW_ACTIVE_PREF, ADD_PREF, MOD_PREF, DEL_PREF, BACK_TO_MENU, CONFIRM, MENU);
	}
	
	public boolean isFilter() {
		return getFilterCallbackType().contains(this);
	}
	
	/**
	 * GD - 18/11/2018
	 * Ritorna i filtri "utente"
	 * @return i filtri
	 */
	private List<CallbackDataType> getFilterCallbackType(){
		return Arrays.asList(BOOKMAKER, RF, RATING, EVENT, SIZE, MINODDVALUE);
	}

}
