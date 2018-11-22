package it.bluesheep.database.dao.manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import it.bluesheep.database.dao.impl.UserPreferenceDAO;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.telegrambot.exception.AlreadyRegisteredUserChatBotException;
import it.bluesheep.telegrambot.exception.AskToUsException;
import it.bluesheep.telegrambot.message.util.ChatBotCommand;
import it.bluesheep.telegrambot.message.util.ChatBotCommandUtilManager;

public class TelegramUserDatabaseManager {
	
	private TelegramUserDatabaseManager() {}
	
	public static Set<ChatBotCommand> getAvailableActionForUser(TelegramUser userMessage, Connection connection) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		Set<ChatBotCommand> commandAvailable = null;
		
		List<UserPreference> userPreferencesList = UserPreferenceDAO.getUserPreferenceDAOInstance().getRelatedUserPreferenceFromUser(userMessage, connection);
		if(userPreferencesList != null) {
			commandAvailable = ChatBotCommandUtilManager.getCommandAvailableFromUserPreferenceHistory(userPreferencesList);
		}
		
		return commandAvailable;
	}
}
