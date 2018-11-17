package it.bluesheep.telegrambot.message.util;

import java.sql.Connection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import it.bluesheep.database.dao.impl.AbstractDAO;
import it.bluesheep.database.dao.impl.BookmakerDAO;
import it.bluesheep.database.dao.impl.EventDAO;
import it.bluesheep.database.dao.impl.MinOddValueDAO;
import it.bluesheep.database.dao.impl.RFTypeDAO;
import it.bluesheep.database.dao.impl.RFValueDAO;
import it.bluesheep.database.dao.impl.RatingDAO;
import it.bluesheep.database.entities.AbstractBlueSheepEntity;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackCommand;

public class ChatBotFilterCommandUtilManager {

	private ChatBotFilterCommandUtilManager() {}
	
	/**
	 * GD - 16/11/18
	 * Mappa il filtro con i relativi dati di callback del bottone Telegram
	 * @param filterCallback il filtro della callback
	 * @param callbackData i dati specifici del filtro
	 * @return la tupla 'filtro - dati bottone'
	 */
	public static Entry<ChatBotFilterCommand, String> getChatBotFilterCommandByCallbackDataType(String filterCallback, String callbackData) throws Exception {
		Entry<ChatBotFilterCommand, String> returnEntry = null;
		
		if(filterCallback != null && callbackData != null) {
			CallbackDataType callbackDataType = CallbackDataType.getCallbackDataType(filterCallback);
			switch(callbackDataType) {
			case BOOKMAKER:
				returnEntry = new AbstractMap.SimpleEntry<ChatBotFilterCommand, String>(ChatBotFilterCommand.BOOKMAKER_BONUS_ABUSING, callbackData);
				break;
//			case CHAMPIONSHIP:
//				returnEntry = new AbstractMap.SimpleEntry<ChatBotFilterCommand, String>(ChatBotFilterCommand.CHAMPIONSHIP_BONUS_ABUSING, callbackData);
//				break;
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
			case RF_TYPE:
				returnEntry = new AbstractMap.SimpleEntry<ChatBotFilterCommand, String>(ChatBotFilterCommand.RF_TYPE_BONUS_ABUSING, callbackData);
				break;
			default:
					throw new Exception("Caso non gestito. callback is " + callbackData.toString());
			}
		}else {
			throw new Exception("ERROR mapping filtri da messaggio. callback string is " + callbackData.toString());
		}
		return returnEntry;
	}

	/**
	 * GD - 16/11/18
	 * Ritorna il DAO corretto relativo al filtro dell'ultimo callback command
	 * @param command la callback
	 * @param connection la connessione al DB
	 * @return il DAO relativo all'ultimo filtro
	 */
	public static AbstractDAO<? extends AbstractBlueSheepEntity> getCorrectDAOByChatBotCallackCommand(ChatBotCallbackCommand command, Connection connection) {
		switch(command.getLastChatBotCallbackFilter().getFilter()) {
//		case CHAMPIONSHIP_BONUS_ABUSING:
//			return ChampionshipDAO.getChampionshipDAOInstance(connection);
		case BOOKMAKER_BONUS_ABUSING:
			return BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection);
			
		case EVENT_BONUS_ABUSING:
			return EventDAO.getEventDAOInstance(connection);
		
		case RF_TYPE_BONUS_ABUSING:
			return RFTypeDAO.getRFTypeDAOInstance(connection);
			
		case RF_BONUS_ABUSING:
			return RFValueDAO.getRFDAOInstance(connection);

		case MINVALUEODD_BONUS_ABUSING:
			return MinOddValueDAO.getMinOddValueDAOInstance(connection);
		
		case RATING_BONUS_ABUSING:
			return RatingDAO.getRatingDAOInstance(connection);
		
		case SIZE_BONUS_ABUSING:
			default:
				return null;
		}
	}
	
	/**
	 * GD - 16/11/18
	 * Ritorna la lista di filtri non settati rispetto alla preferenza dell'utente
	 * @param up la preferenza dell'utente
	 * @param toBeModified 
	 * @return la lista di filtri non settati 
	 */
	public static List<ChatBotFilterCommand> getChatBotFilterCommandListFromUserPreference(UserPreference up, boolean toBeModified){
		
		List<ChatBotFilterCommand> returnList = new ArrayList<ChatBotFilterCommand>();
		
		//1.Se la liquidità non è impostata
		if(up.getLiquidita() == null || toBeModified) {
			returnList.add(ChatBotFilterCommand.SIZE_BONUS_ABUSING);
		}
		
		//2.Se il campionato non è impostato e l'evento non è impostato (solo uno dei due)
		if(up.getEvent() == null || toBeModified) {
//			availableFilter.add(ChatBotFilterCommand.CHAMPIONSHIP_BONUS_ABUSING);
			returnList.add(ChatBotFilterCommand.EVENT_BONUS_ABUSING);
		}
		
		//3.Se la quota minima non è impostata
		if(up.getMinOddValue() == null || toBeModified) {
			returnList.add(ChatBotFilterCommand.MINVALUEODD_BONUS_ABUSING);
		}
		
		//4.Se il rating non è impostato e l' RFValue non è impostato (solo uno dei due)
		if((up.getRating() == null && up.getRfValue() == null && up.getRfType() == null) || toBeModified) {
			returnList.add(ChatBotFilterCommand.RF_TYPE_BONUS_ABUSING);
			returnList.add(ChatBotFilterCommand.RATING_BONUS_ABUSING);
		}
		
		return returnList;
	}
	
	/**
	 * GD - 16/11/18
	 * Ritorna il testo corretto rispetto al filtro 
	 * @param filter il filtro da mostrare
	 * @return il test da mostrare nel menu del filtro
	 */
	public static String getCorrectTextFromChatBotFilterCommand(ChatBotFilterCommand filter) {
		switch(filter) {
		case EVENT_BONUS_ABUSING:
			return TextOptionMenuFilterCommand.EVENT.toString();
			
		case MINVALUEODD_BONUS_ABUSING:
			return TextOptionMenuFilterCommand.MINODDVALUE.toString();
			
		case RATING_BONUS_ABUSING:
			return TextOptionMenuFilterCommand.RATING.toString();
			
		case RF_BONUS_ABUSING:
			return TextOptionMenuFilterCommand.RF_VALUE.toString();
			
		case RF_TYPE_BONUS_ABUSING:
			return TextOptionMenuFilterCommand.RF_TYPE.toString();
			
		case SIZE_BONUS_ABUSING:
			return TextOptionMenuFilterCommand.RF_TYPE.toString();
			
		default:
			return null;
		}
	}
	
}
