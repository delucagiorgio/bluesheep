package it.bluesheep.telegrambot.message.util;
/**
 * I testi relativi ai comandi
 * @author giorgio
 *
 */
public enum TextButtonCommand {
    
	SHOW_ACTIVE_PREF("Visualizza preferenze di notifica attive"),
    ADD_PREF("Aggiungi preferenze di notifica"),
    DEL_PREF("Rimuovi preferenze di notifica"),
    MOD_PREF("Modifica preferenze di notifica"),
    NEXT_PAGE(">>"), PREVIOUS_PAGE("<<"),
    CONFIRM("Conferma e attiva preferenza"), 
    BACK_TO_MENU("<< Al menù iniziale"),
    BACK_TO_KEYBOARD("<< Torna allo step precedente");
	
	private String code;
	
	private TextButtonCommand(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	public static TextButtonCommand getTextButtonCommandByChatBotCommand(ChatBotCommand chatBotCommand) {
		switch(chatBotCommand) {
		case ADD_PREFERENCE_BONUS_ABUSING:
			return ADD_PREF;
		
		case BACK_TO_MENU_BONUS_ABUSING:
			return BACK_TO_MENU;
			
		case CONFIRM_CHANGE_BONUS_ABUSING:
			return CONFIRM;
			
//		case ENABLE_DISABLE_PREFERENCES_BONUS_ABUSING:
//			return ENABLE_DISABLE_PREF;
			
		case DELETE_PREFERENCE_BONUS_ABUSING:
			return DEL_PREF;
			
		case MOD_PREFERENCES_BONUS_ABUSING:
			return MOD_PREF;
			
		case NEXT_PAGE:
			return NEXT_PAGE;
		
		case PREVIOUS_PAGE:
			return PREVIOUS_PAGE;
			
		case SHOW_PREFERENCES_BONUS_ABUSING:
			return SHOW_ACTIVE_PREF;
			
		case BACK_TO_KEYBOARD:
			return BACK_TO_KEYBOARD;
		
		default:
			return null;
		}
	}
}
