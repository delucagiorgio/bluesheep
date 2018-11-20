package it.bluesheep.telegrambot.message.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;
/**
 * Classe di utilità per gestire, mappare e ottenere i corretti ChatBotCommand
 * @author giorgio
 *
 */
public class ChatBotCommandUtilManager {

	private ChatBotCommandUtilManager() {}
	
	/**
	 * GD - 16/11/18
	 * Ritorna i possibili comandi relativi alle preferenze dell'utente, in base alle regole
	 * @param userPreferencesDB preferenze dell'utente
	 * @return la lista di comandi
	 */
	public static Set<ChatBotCommand> getCommandAvailableFromUserPreferenceHistory(List<UserPreference> userPreferencesDB){
		
		Set<ChatBotCommand> commandToShow = new HashSet<ChatBotCommand>();
		
		if(userPreferencesDB.size() < Integer.parseInt(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.CHAT_BOT_MAX_PREF))) {
			commandToShow.add(ChatBotCommand.ADD_PREFERENCE_BONUS_ABUSING);
		}
		
		boolean atLeastOneActive = false;
		boolean atLeastOneInactive = false;
		
		for(UserPreference pref : userPreferencesDB) {
			if(pref.isActive()) {
				atLeastOneActive = true;
			}else {
				atLeastOneInactive = true;
			}
		}
		
		if(atLeastOneActive) {
			commandToShow.add(ChatBotCommand.SHOW_PREFERENCES_BONUS_ABUSING);
			commandToShow.add(ChatBotCommand.DELETE_PREFERENCE_BONUS_ABUSING);
			commandToShow.add(ChatBotCommand.MOD_PREFERENCES_BONUS_ABUSING);
		}
		
		if(atLeastOneInactive) {
			commandToShow.add(ChatBotCommand.DELETE_PREFERENCE_BONUS_ABUSING);
			commandToShow.add(ChatBotCommand.MOD_PREFERENCES_BONUS_ABUSING);
		}
		
		return commandToShow;
	}
	
	/**
	 * GD - 16/11/18
	 * Ritorna la lista di comandi di navigazione tra le funzionalità
	 * @return la lista di comandi
	 */
	public static List<ChatBotCommand> getNavigationChatBotCommand(){
			return Arrays.asList(ChatBotCommand.BACK_TO_KEYBOARD, ChatBotCommand.NEXT_PAGE, ChatBotCommand.PREVIOUS_PAGE, ChatBotCommand.CONFIRM_CHANGE_BONUS_ABUSING);
	}
	
	/**
	 * GD - 16/11/18
	 * Ritorna la lista di comandi di navigazione nelle pagine
	 * @return la lista di comandi
	 */
	public static List<ChatBotCommand> getPageNavChatBotCommand(){
		return Arrays.asList(ChatBotCommand.NEXT_PAGE, ChatBotCommand.PREVIOUS_PAGE);
	}
	
	/**
	 * GD - 16/11/18
	 * Ritorna la lista di comandi che prevede una modifica ad una preferenza dello storico
	 * @return la lista di comandi
	 */
	public static List<ChatBotCommand> getModificationCommand(){
		return Arrays.asList(ChatBotCommand.DELETE_PREFERENCE_BONUS_ABUSING,
							 ChatBotCommand.MOD_PREFERENCES_BONUS_ABUSING);
	}
	
	/**
	 * GD - 16/11/18
	 * Ritorna la lista di comandi che prevede una modifica o la creazione di una preferenza
	 * @return la lista di comandi
	 */
	public static List<ChatBotCommand> getNewOrModifyCommand(){
		return Arrays.asList(ChatBotCommand.ADD_PREFERENCE_BONUS_ABUSING, ChatBotCommand.MOD_PREFERENCES_BONUS_ABUSING);
	}
	
	/**
	 * GD - 16/11/18
	 * Ritorna la lista di comandi che prevede una cancellazione o una attivazione/disattivazione di una preferenza
	 * @return la lista di comandi
	 */
	public static List<ChatBotCommand> getDeleteOrActivactionCommand(){
		return Arrays.asList(ChatBotCommand.DELETE_PREFERENCE_BONUS_ABUSING);
	}
	
	/**
	 * GD - 16/11/18
	 * Ritorna il comando corretto rispetto al dato contenuto nel bottone Telegram
	 * @param callbackDataType callbackType del bottone
	 * @return la lista di comandi
	 */
	public static ChatBotCommand getCorrectChatBotCommandByCallbackDataType(CallbackDataType callbackDataType) {
		if(callbackDataType != null) {
			switch(callbackDataType) {
			case ADD_PREF:
				return ChatBotCommand.ADD_PREFERENCE_BONUS_ABUSING;
			case BACK_TO_MENU:
				return ChatBotCommand.BACK_TO_MENU_BONUS_ABUSING;
			case CONFIRM:
				return ChatBotCommand.CONFIRM_CHANGE_BONUS_ABUSING;
			case DEL_PREF:
				return ChatBotCommand.DELETE_PREFERENCE_BONUS_ABUSING;
			case MOD_PREF:
				return ChatBotCommand.MOD_PREFERENCES_BONUS_ABUSING;
			case SHOW_ACTIVE_PREF:
				return ChatBotCommand.SHOW_PREFERENCES_BONUS_ABUSING;
			case BACK_TO_KEYBOARD:
				return ChatBotCommand.BACK_TO_KEYBOARD;
			case NEXT_PAGE:
				return ChatBotCommand.NEXT_PAGE;
			case PREVIOUS_PAGE:
				return ChatBotCommand.PREVIOUS_PAGE;
			case MENU:
				return ChatBotCommand.MENU;
				default:
					break;
			}
		}
		return null;
	}
	
	/**
	 * GD - 16/11/18
	 * Ritorna il testo corretto da mostrare rispetto al comando di root
	 * @param filter callbackType del bottone
	 * @return il testo da mostrare
	 */
	public static String getCorrectTextFromChatBotCommand(ChatBotCommand filter) {
		switch(filter) {
		case ADD_PREFERENCE_BONUS_ABUSING:
			return TextOptionBookmakerCommand.ADD_PREF.toString();
			
		case DELETE_PREFERENCE_BONUS_ABUSING:
			return TextOptionBookmakerCommand.DEL_PREF.toString();
			
		case SHOW_PREFERENCES_BONUS_ABUSING:
			return TextOptionBookmakerCommand.SHOW_ACTIVE_PREF.toString();
			
		case MOD_PREFERENCES_BONUS_ABUSING:
			return TextOptionBookmakerCommand.MOD_PREF.toString();
			
		default:
			return null;
		}
	}
}
