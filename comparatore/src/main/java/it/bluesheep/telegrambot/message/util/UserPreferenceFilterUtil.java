package it.bluesheep.telegrambot.message.util;

import java.sql.Connection;

import it.bluesheep.database.dao.IFilterDAO;
import it.bluesheep.database.dao.IFilterDAOFactory;
import it.bluesheep.database.entities.AbstractBlueSheepFilterEntity;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.telegrambot.exception.AskToUsException;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackCommand;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackFilter;

public class UserPreferenceFilterUtil {
	
	private UserPreferenceFilterUtil() {}
	
	public static UserPreference updateUserPreferenceWithFilterInformationFromCallback(UserPreference up, ChatBotCallbackCommand command, TelegramUser userMessage, Connection connection) throws AskToUsException, MoreThanOneResultException {
		
		//trovo il filtro contenuto nei comandi
		
		ChatBotCallbackFilter filter = command.getLastChatBotCallbackFilter();
		
		String textFilter = filter.getValue();
		
		//Se non è il comando di scelta
		if(!TextFilterCommand.isTextFilterCommand(textFilter)) {
			
			IFilterDAO<? extends AbstractBlueSheepFilterEntity> filterDao = IFilterDAOFactory.getCorrectIFilterDAOFromChatBotFilterCommand(filter.getFilter(), connection);
			
			AbstractBlueSheepFilterEntity filterEntity = filterDao.getSingleRowFromButtonText(textFilter);
			
			textFilter = filterEntity.getCodeDB();
			
			//trovo la relativa colonna da aggiornare
			if(ChatBotFilterCommand.EVENT_BONUS_ABUSING.equals(filter.getFilter())) {
				up.setEvent(textFilter);
				
			}else if(ChatBotFilterCommand.MINVALUEODD_BONUS_ABUSING.equals(filter.getFilter())) {
				up.setMinOddValue(new Double(textFilter));
				
			}else if(ChatBotFilterCommand.RATING_BONUS_ABUSING.equals(filter.getFilter())) {
				up.setRating(new Double(textFilter));
				if(up.getRfType() != null || up.getRfValue() != null) {
					up.setRfType(null);
					up.setRfValue(null);
				}
			
			}else if(ChatBotFilterCommand.RF_BONUS_ABUSING.equals(filter.getFilter())) {
				up.setRfValue(new Double(textFilter));
				if(up.getRating() != null) {
					up.setRating(null);
				}
			
			}else if(ChatBotFilterCommand.RF_TYPE_BONUS_ABUSING.equals(filter.getFilter())) {
				up.setRfType(new Double(textFilter));
				if(up.getRating() != null) {
					up.setRating(null);
				}
				
			}else if(ChatBotFilterCommand.SIZE_BONUS_ABUSING.equals(filter.getFilter())) {
				up.setLiquidita(new Double(textFilter));
				
			}else {
				throw new AskToUsException(userMessage);
			}
		}
		
		return up;
	}
	

}
