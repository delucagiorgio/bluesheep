package it.bluesheep.telegrambot.message.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import it.bluesheep.telegrambot.message.util.CallbackDataType;
import it.bluesheep.telegrambot.message.util.ChatBotCommand;
import it.bluesheep.telegrambot.message.util.ChatBotCommandUtilManager;
import it.bluesheep.telegrambot.message.util.ChatBotFilterCommand;
import it.bluesheep.telegrambot.message.util.ChatBotFilterCommandUtilManager;
import it.bluesheep.util.BlueSheepConstants;

public class ChatBotCallbackCommand extends ChatBotCallback {
	
	public ChatBotCallbackCommand(ChatBotCallbackCommand callbackCommand) {
		super(callbackCommand, callbackCommand.getNavigationCommand());
	}
	
	private ChatBotCallbackCommand(ChatBotCommand callback, List<ChatBotCallbackFilter> filterList, ChatBotCommand navigationCommand) {
		super(callback, filterList, navigationCommand);
	}
	
	private ChatBotCallbackCommand(ChatBotCommand callback, String callbackData) {
		super(callback, callbackData, null);
	}
	
	/**
	 * GD - 08/11/2018 
	 * Restituisce l'oggetto ChatBotCallback in cui sono conteute tutte le informazioni relative all'interazione con l'utente
	 * @param callbackDataInfo la stringa di callback
	 * @return l'oggetto ChatBotCallback relativo alla CallbackQuery
	 * @throws Exception per errore di mapping
	 */
	public static ChatBotCallbackCommand getChatBotCallbackCommandFromCallbackData(String callbackDataInfo) throws Exception {
		
		if(callbackDataInfo != null && !StringUtils.isEmpty(callbackDataInfo)) {
			//Splitto sulla virgola per dividere gli step
			String[] splittedFilterCallbackData = callbackDataInfo.split(BlueSheepConstants.REGEX_COMMA);
			if(splittedFilterCallbackData != null) {
				//comando che origina il flusso
				ChatBotCommand rootCommand = ChatBotCommandUtilManager.getCorrectChatBotCommandByCallbackDataType(CallbackDataType.getCallbackDataType(splittedFilterCallbackData[0]));
				ChatBotCommand navigationCommand = null;
				if(splittedFilterCallbackData.length > 1) {
					navigationCommand = ChatBotCommandUtilManager.getCorrectChatBotCommandByCallbackDataType(CallbackDataType.getCallbackDataType(splittedFilterCallbackData[splittedFilterCallbackData.length - 1]));
					if(!ChatBotCommandUtilManager.getNavigationChatBotCommand().contains(navigationCommand)) {
						navigationCommand = null;
					}
				}
				if(rootCommand != null) {
					int supportNavigationIndex = navigationCommand != null ? 1 : 0;
					//Controllo che la callback contenga filtri
					if(splittedFilterCallbackData.length > 1) {
						List<ChatBotCallbackFilter> chatBotFilterCommandsList = new ArrayList<ChatBotCallbackFilter>();
						for(int i = 1; i < splittedFilterCallbackData.length - supportNavigationIndex; i++) {
							String[] splittedCallbackData = splittedFilterCallbackData[i].split(BlueSheepConstants.REGEX_CSV);
							String filterCode = splittedCallbackData[0];
							String callbackData = splittedCallbackData[1];
							boolean pagination = Integer.parseInt(callbackData.split(BlueSheepConstants.REGEX_TWOPOINTS)[0]) == 1;
							boolean isKeyboardMode = Integer.parseInt(callbackData.split(BlueSheepConstants.REGEX_TWOPOINTS)[1]) == 1;
							boolean isIdMode = Integer.parseInt(callbackData.split(BlueSheepConstants.REGEX_TWOPOINTS)[2]) == 1;
							int pageIndex = Integer.parseInt(callbackData.split(BlueSheepConstants.REGEX_TWOPOINTS)[3]);
							String text = callbackData.split(BlueSheepConstants.REGEX_TWOPOINTS)[4];

							Entry<ChatBotFilterCommand, String> filterCommand = ChatBotFilterCommandUtilManager.getChatBotFilterCommandByCallbackDataType(filterCode, callbackData);
							if(filterCommand != null) {
								//Controllo che il valore riportato nel campo del filtro sia un intervallo o un valore specifico
								chatBotFilterCommandsList.add(ChatBotCallbackFilterFactory.getCorrectChatBotCallbackFilterFactory(pagination, filterCommand.getKey(), text, filterCommand.getValue(), isKeyboardMode, isIdMode, pageIndex));
							}else {
								throw new Exception("CALLBACK mapping ERROR : string is ." + callbackDataInfo + ".");
							}
						}
						
						if(!chatBotFilterCommandsList.isEmpty()) {
							return new ChatBotCallbackCommand(rootCommand, chatBotFilterCommandsList, navigationCommand);
						}else {
							throw new Exception("CALLBACK mapping ERROR : string is ." + callbackDataInfo + ".");
						}
					}else {
						//Nuova richiesta
						return new ChatBotCallbackCommand(rootCommand, callbackDataInfo);
					}
				}else {
					throw new Exception("CALLBACK mapping ERROR : string is ." + callbackDataInfo + ".");
				}
			}
		}
		return null;
		
	}
	
}
