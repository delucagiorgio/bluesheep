package it.bluesheep.telegrambot.message.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
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

//	public static List<Entry<CallbackDataType, String>> getCommandEntryListOrderedByExecution(ChatBotCallbackCommand callbackCommand) {
//		
//		List<Entry<CallbackDataType, String>> returnList = null;
//		
//		if(callbackCommand!= null) {
//			String[] splittedCallbakcDataByResponseLevel = callbackData.split(BlueSheepConstants.KEY_SEPARATOR);
//			if(splittedCallbakcDataByResponseLevel != null && splittedCallbakcDataByResponseLevel.length > 0) {
//				returnList = new ArrayList<Entry<CallbackDataType, String>>();
//				
//				for(String callbackStep : splittedCallbakcDataByResponseLevel) {
//					String callbackTypeString = callbackStep.split(BlueSheepConstants.REGEX_CSV)[0];
//					CallbackDataType callbackType = CallbackDataType.getCallbackDataType(callbackTypeString);
//					if(callbackType != null && callbackType.isCommand()) {
//						Entry<CallbackDataType, String> entry = new AbstractMap.SimpleEntry<CallbackDataType, String>(callbackType, "");
//						returnList.add(entry);
//					}else if(callbackType != null && callbackType.isFilter()){
//						String callbackTypeTextString = callbackStep.split(BlueSheepConstants.REGEX_CSV)[1];
//						if(StringUtils.isEmpty(callbackTypeTextString)) {
//							Entry<CallbackDataType, String> entry = new AbstractMap.SimpleEntry<CallbackDataType, String>(callbackType, callbackTypeTextString);
//							returnList.add(entry);
//						}
//					}
//				}
//			}
//		}
//		
//		return returnList;
//	}
	
	public static List<ChatBotFilterCommand> getFilterCommandByChatBotCommand(ChatBotCommand command){
		switch(command){
		case ADD_PREFERENCE_BONUS_ABUSING:
			return Arrays.asList(ChatBotFilterCommand.CHAMPIONSHIP_BONUS_ABUSING,
								 ChatBotFilterCommand.EVENT_BONUS_ABUSING,
								 ChatBotFilterCommand.MINVALUEODD_BONUS_ABUSING,
								 ChatBotFilterCommand.RATING_BONUS_ABUSING,
								 ChatBotFilterCommand.RF_BONUS_ABUSING,
								 ChatBotFilterCommand.SIZE_BONUS_ABUSING);
		default:
			return null;
		}
	}
	
	public static List<ChatBotCommand> getNavigationChatBotCommand(){
			return Arrays.asList(ChatBotCommand.BACK_TO_KEYBOARD, ChatBotCommand.NEXT_PAGE, ChatBotCommand.PREVIOUS_PAGE, ChatBotCommand.CONFIRM_CHANGE_BONUS_ABUSING);
	}
	
	public static List<ChatBotCommand> getPageNavChatBotCommand(){
		return Arrays.asList(ChatBotCommand.NEXT_PAGE, ChatBotCommand.PREVIOUS_PAGE);
}
	
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
			case ENABLE_DISABLE_PREF:
				return ChatBotCommand.ENABLE_DISABLE_PREFERENCES_BONUS_ABUSING;
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
				default:
					break;
			}
		}
		return null;
	}
}
