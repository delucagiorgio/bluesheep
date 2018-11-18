package it.bluesheep.database.dao;

import java.sql.Connection;

import it.bluesheep.database.dao.impl.EventDAO;
import it.bluesheep.database.dao.impl.MinOddValueDAO;
import it.bluesheep.database.dao.impl.RFTypeDAO;
import it.bluesheep.database.dao.impl.RFValueDAO;
import it.bluesheep.database.dao.impl.RatingDAO;
import it.bluesheep.database.dao.impl.SizeDAO;
import it.bluesheep.database.entities.AbstractBlueSheepEntity;
import it.bluesheep.telegrambot.message.util.ChatBotFilterCommand;

public class IFilterDAOFactory {

	private IFilterDAOFactory() {}
	
	public static IFilterDAO<? extends AbstractBlueSheepEntity> getCorrectIFilterDAOFromChatBotFilterCommand(ChatBotFilterCommand filterCommand, Connection connection){
		switch(filterCommand) {
		case EVENT_BONUS_ABUSING:
			return EventDAO.getEventDAOInstance(connection);
			
		case MINVALUEODD_BONUS_ABUSING:
			return MinOddValueDAO.getMinOddValueDAOInstance(connection);

		case RATING_BONUS_ABUSING:
			return RatingDAO.getRatingDAOInstance(connection);
			
		case RF_BONUS_ABUSING:
			return RFValueDAO.getRFDAOInstance(connection);
			
		case RF_TYPE_BONUS_ABUSING:
			return RFTypeDAO.getRFTypeDAOInstance(connection);
			
		case SIZE_BONUS_ABUSING:
			return SizeDAO.getSizeDAOInstance(connection);
			
			default:
				return null;
		}
	}
	
}
