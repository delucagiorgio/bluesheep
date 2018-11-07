package it.bluesheep.telegrambot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.telegrambot.button.CallbackDataType;
import it.bluesheep.util.BlueSheepConstants;

public class ChatBotCommandUtilManager {

	private ChatBotCommandUtilManager() {}
	
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
		
		if(atLeastOneActive || atLeastOneInactive) {
			commandToShow.add(ChatBotCommand.SHOW_PREFERENCES_BONUS_ABUSING);
			commandToShow.add(ChatBotCommand.ENABLE_DISABLE_PREFERENCES_BONUS_ABUSING);
			commandToShow.add(ChatBotCommand.DELETE_PREFERENCE_BONUS_ABUSING);
			commandToShow.add(ChatBotCommand.MOD_PREFERENCES_BONUS_ABUSING);
		}
		
		return commandToShow;
	}
	
	public static ChatBotCommand getRootCommand(CallbackDataType callbackType) {

		switch(callbackType) {
		case BOOKMAKER:
		case CHAMPONSHIP:
		case RF:
			return ChatBotCommand.ADD_PREFERENCE_BONUS_ABUSING;
			default:
				return null;
		}
	}
}
