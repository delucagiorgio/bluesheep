package it.bluesheep.database.dao.manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import it.bluesheep.database.dao.impl.TelegramUserDAO;
import it.bluesheep.database.dao.impl.UserPreferenceDAO;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.telegrambot.ChatBotCommand;
import it.bluesheep.telegrambot.ChatBotCommandUtilManager;
import it.bluesheep.telegrambot.exception.AlreadyRegisteredUserChatBotException;
import it.bluesheep.telegrambot.exception.AskToUsException;
import it.bluesheep.telegrambot.exception.BluesheepChatBotException;
import it.bluesheep.telegrambot.exception.InsertBlueSheepEntityException;

public class TelegramUserDatabaseManager {
	
	private TelegramUserDatabaseManager() {}
	
	/**
	 * GD - 04/11/18
	 * @param message the message received containing info related to user
	 * @return null if 
	 * @throws BluesheepChatBotException
	 * @throws MoreThanOneResultException 
	 */
	public static synchronized TelegramUser addUserArbsMap(TelegramUser userToSave, Connection connection) throws BluesheepChatBotException, SQLException, MoreThanOneResultException {
		userToSave.setActive(Boolean.TRUE);
		if(!TelegramUserDAO.getBlueSheepTelegramUserDAOInstance(connection).insertRow(userToSave)) {
			throw new InsertBlueSheepEntityException(userToSave);
		}
		
		return TelegramUserDAO.getBlueSheepTelegramUserDAOInstance(connection).getUserFromUser(userToSave);
	}
	
	public static Set<ChatBotCommand> getAvailableActionForUser(TelegramUser userMessage, Connection connection) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		Set<ChatBotCommand> commandAvailable = null;
		
		List<UserPreference> userPreferencesList = UserPreferenceDAO.getUserPreferenceDAOInstance(connection).getRelatedUserPreferenceFromUser(userMessage);
		if(userPreferencesList != null) {
			commandAvailable = ChatBotCommandUtilManager.getCommandAvailableFromUserPreferenceHistory(userPreferencesList);
		}
		
		return commandAvailable;
	}
}
