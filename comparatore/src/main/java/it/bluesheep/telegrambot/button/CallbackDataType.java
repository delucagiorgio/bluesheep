package it.bluesheep.telegrambot.button;

import java.util.Arrays;
import java.util.List;

public enum CallbackDataType {

    SHOW_ACTIVE_PREF("COMMAND_show_active_prefs"),
	ENABLE_DISABLE_PREF("COMMAND_enable_disable_prefs"),
    ADD_PREF("COMMAND_add_prefs"),
    DEL_PREF("COMMAND_delete_active_prefs"),
    MOD_PREF("COMMAND_modify_active_prefs"),
    BACKTOMENU("COMMAND_back_to_menu"),
    BOOKMAKER("BOOKMAKER"),
    RF("RF_BONUS_ABUSING"),
    CHAMPONSHIP("CHAMPIONSHIP_BONUS_ABUSING"),;
	
	private String code;
	
	private CallbackDataType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	public static List<CallbackDataType> getCommandCallbackDataType(){
		return Arrays.asList(SHOW_ACTIVE_PREF, ENABLE_DISABLE_PREF, ADD_PREF, BACKTOMENU, DEL_PREF, MOD_PREF);
	}

}
