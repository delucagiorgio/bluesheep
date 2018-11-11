package it.bluesheep.telegrambot.message.util;

import java.sql.Connection;
import java.util.AbstractMap;
import java.util.Map.Entry;

import it.bluesheep.database.dao.impl.AbstractDAO;
import it.bluesheep.database.dao.impl.ChampionshipDAO;
import it.bluesheep.database.dao.impl.EventDAO;
import it.bluesheep.database.dao.impl.RFDAO;
import it.bluesheep.database.entities.AbstractBlueSheepEntity;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackCommand;

public class ChatBotFilterCommandUtilManager {

	private ChatBotFilterCommandUtilManager() {}
	
	public static Entry<ChatBotFilterCommand, String> getChatBotFilterCommandByCallbackDataType(String filterCallback, String callbackData) throws Exception {
		Entry<ChatBotFilterCommand, String> returnEntry = null;
		
		if(filterCallback != null && callbackData != null) {
			CallbackDataType callbackDataType = CallbackDataType.getCallbackDataType(filterCallback);
			switch(callbackDataType) {
			case BOOKMAKER:
				returnEntry = new AbstractMap.SimpleEntry<ChatBotFilterCommand, String>(ChatBotFilterCommand.BOOKMAKER_BONUS_ABUSING, callbackData);
				break;
			case CHAMPIONSHIP:
				returnEntry = new AbstractMap.SimpleEntry<ChatBotFilterCommand, String>(ChatBotFilterCommand.CHAMPIONSHIP_BONUS_ABUSING, callbackData);
				break;
			case EVENT:
				returnEntry = new AbstractMap.SimpleEntry<ChatBotFilterCommand, String>(ChatBotFilterCommand.EVENT_BONUS_ABUSING, callbackData);
				break;
			case MINODDVALUE:
				returnEntry = new AbstractMap.SimpleEntry<ChatBotFilterCommand, String>(ChatBotFilterCommand.MINVALUEODD_BONUS_ABUSING, callbackData);
				break;
			case RATING:
				returnEntry = new AbstractMap.SimpleEntry<ChatBotFilterCommand, String>(ChatBotFilterCommand.RATING_BONUS_ABUSING, callbackData);
				break;
			case RF:
				returnEntry = new AbstractMap.SimpleEntry<ChatBotFilterCommand, String>(ChatBotFilterCommand.RF_BONUS_ABUSING, callbackData);
				break;
			case SIZE:
				returnEntry = new AbstractMap.SimpleEntry<ChatBotFilterCommand, String>(ChatBotFilterCommand.SIZE_BONUS_ABUSING, callbackData);
				break;
			default:
					throw new Exception("Caso non gestito. callback is " + callbackData.toString());
			}
		}else {
			throw new Exception("ERROR mapping filtri da messaggio. callback string is " + callbackData.toString());
		}
		return returnEntry;
	}

	public static AbstractDAO<? extends AbstractBlueSheepEntity> getCorrectDAOByChatBotCallackCommand(ChatBotCallbackCommand command, Connection connection) {
		switch(command.getLastChatBotCallbackFilter().getFilter()) {
		case CHAMPIONSHIP_BONUS_ABUSING:
			return ChampionshipDAO.getChampionshipDAOInstance(connection);
			
		case EVENT_BONUS_ABUSING:
			return EventDAO.getEventDAOInstance(connection);
		
		case RF_BONUS_ABUSING:
			return RFDAO.getRFDAOInstance(connection);

		case MINVALUEODD_BONUS_ABUSING:
		case RATING_BONUS_ABUSING:
		case SIZE_BONUS_ABUSING:
			default:
				return null;
		}
	}
	
//	public static ChatBotButton getChatBotButton
	
}
