package it.bluesheep.telegrambot.message.util;

public enum ChatBotCommand {
	
	ADD_PREFERENCE_BONUS_ABUSING("3"),
	DELETE_PREFERENCE_BONUS_ABUSING("4"),
	SHOW_PREFERENCES_BONUS_ABUSING("1"),
	ENABLE_DISABLE_PREFERENCES_BONUS_ABUSING("2"),
	MOD_PREFERENCES_BONUS_ABUSING("5"), 
	BACK_TO_MENU_BONUS_ABUSING("6"),
	CONFIRM_CHANGE_BONUS_ABUSING("7"),
	NEXT_PAGE("8"),
	PREVIOUS_PAGE("9"),
	BACK_TO_KEYBOARD("10");
	
	
	private String code;
	
	ChatBotCommand(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

}
