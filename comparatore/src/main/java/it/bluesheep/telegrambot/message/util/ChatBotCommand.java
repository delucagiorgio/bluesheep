package it.bluesheep.telegrambot.message.util;

/**
 * I comandi utente utilizzabili dall'utente
 * @author giorgio
 *
 */
public enum ChatBotCommand {
	
	ADD_PREFERENCE_BONUS_ABUSING("3"),
	DELETE_PREFERENCE_BONUS_ABUSING("4"),
	SHOW_PREFERENCES_BONUS_ABUSING("1"),
	MOD_PREFERENCES_BONUS_ABUSING("5"), 
	BACK_TO_MENU_BONUS_ABUSING("6"),
	CONFIRM_CHANGE_BONUS_ABUSING("7"),
	NEXT_PAGE("8"),
	PREVIOUS_PAGE("9"),
	BACK_TO_KEYBOARD("10"),
	MENU("11");
	
	
	private String code;
	
	ChatBotCommand(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

}
