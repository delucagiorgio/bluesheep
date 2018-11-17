package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;

import it.bluesheep.database.BlueSheepDatabaseManager;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.telegrambot.exception.AlreadyRegisteredUserChatBotException;
import it.bluesheep.telegrambot.exception.AskToUsException;
import it.bluesheep.util.BlueSheepConstants;

public class TelegramUserDAO extends AbstractDAO<TelegramUser>{
	
	private static TelegramUserDAO instance;
	public static String tableName = "USERS";
	private static String FIRSTNAME = "firstName";
	private static String LASTNAME = "lastName";
	private static String CHATID = "chatId";
	private static String REGISTRATIONDATE = "registrationDate";
	private static String ACTIVE = "active";
	private static String LASTMESSAGEID = "lastMessageId";

	private TelegramUserDAO(Connection connection) {
		super(tableName,connection);
	}
	
	public static synchronized TelegramUserDAO getBlueSheepTelegramUserDAOInstance(Connection connection) {
		if(instance == null) {
			instance = new TelegramUserDAO(connection);
		}
		return instance;
	}
	
	private String getActiveUsersQuery() {
		
		return getBasicSelectQuery() +  
			    WHERE + ACTIVE + " is true";
	}
	
	private String getInactiveUsersQuery() {
		
		return getBasicSelectQuery() + 
			    WHERE + ACTIVE + " is false";
	}

	@Override
	protected List<TelegramUser> mapDataIntoObject(ResultSet returnSelect) throws SQLException {
		
		List<TelegramUser> dataMapped = new ArrayList<TelegramUser>();
		
		while(returnSelect.next()) {
			String firstName = returnSelect.getString(FIRSTNAME);
			String lastName = returnSelect.getString(LASTNAME);
			Long chatId = returnSelect.getLong(CHATID);
			Timestamp registrationDate = getTimestampFromResultSet(returnSelect, REGISTRATIONDATE);
			Boolean active = returnSelect.getBoolean(ACTIVE);
			Long id = returnSelect.getLong(ID);
			Long lastMessageId = returnSelect.getLong(LASTMESSAGEID) == 0 ? null : returnSelect.getLong(LASTMESSAGEID);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			dataMapped.add(TelegramUser.getBlueSheepTelegramUserFromDatabaseInfo(firstName, lastName, chatId, active, registrationDate, id, lastMessageId, createTime, updateTime));
		}
		
		return dataMapped;
	}
	
	public List<TelegramUser> getActiveUsers(){
		return getMappedObjectBySelect(getActiveUsersQuery());
	}
	
	public List<TelegramUser> getInactiveUsers(){
		return getMappedObjectBySelect(getInactiveUsersQuery());
	}

	/**
	 * GD - 04/11/2018
	 * Restituisce l'utente dato in input un utente da cercare sul Database
	 * @param user lo user da cercare
	 * @return null in caso di inputUser == null || "caso non gestito", 
	 * 			   lo stesso utente di input, nel caso in cui non è stato trovato nel db,
	 * 			   l'utente presente nel DB, se trovato.
	 * @throws MoreThanOneResultException 
	 */
	public TelegramUser getUserFromUser(TelegramUser user) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException, MoreThanOneResultException{
		
		TelegramUser returnUser = null;
		
		if(user != null) {
			String query = getBasicSelectQuery() + 
						   WHERE + 
						   CHATID + " =  ? " + AND +
						   FIRSTNAME + " = ? " + AND + 
						   LASTNAME + " = ? ";
			PreparedStatement prepStatement = connection.prepareStatement(query);
			prepStatement.setLong(1, user.getChatId());
			prepStatement.setString(2, user.getFirstName());
			prepStatement.setString(3, user.getLastName());
			if(prepStatement != null) {
				returnUser = getSingleResult(getMappedObjectBySelect(prepStatement));
			}
		}else {
			logger.warn("User is null");
		}
		
		return returnUser;
	}
	
	@Override
	protected String getAllColumnValuesFromEntity(TelegramUser user) {
		return "(" +
					"'" + user.getFirstName() + "'" +  BlueSheepConstants.REGEX_COMMA + 
					"'" + user.getLastName() + "'" +  BlueSheepConstants.REGEX_COMMA + 
					user.getChatId() + BlueSheepConstants.REGEX_COMMA + 
					"'" + user.getRegistrationDate() + "'" +  BlueSheepConstants.REGEX_COMMA + 
					user.isActive() + BlueSheepConstants.REGEX_COMMA + 
					user.getLastMessageId() + BlueSheepConstants.REGEX_COMMA +
					"?" + BlueSheepConstants.REGEX_COMMA +
					"?" +")";
	}

	public TelegramUser getUserFromMessage(Message receivedMessage) throws AskToUsException {
		TelegramUser returnUser = null;
		String query = getBasicSelectQuery() + WHERE + CHATID + " = ? " + AND + FIRSTNAME + " =  ? " + AND + LASTNAME + " = ? ";
		
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(query);
			ps.setLong(1, receivedMessage.getChatId());
			ps.setString(2, receivedMessage.getFrom().getFirstName());
			ps.setString(3, receivedMessage.getFrom().getLastName());
			
			returnUser = getSingleResult(getMappedObjectBySelect(ps));
			
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (MoreThanOneResultException e) {
			throw new AskToUsException(TelegramUser.getTelegramUserFromMessage(receivedMessage));
		}
		
		return returnUser;
	}

	public void updateLastMessageSent(TelegramUser userDB) throws SQLException {
		
		String query = UPDATE + tableName 
						+ SET + LASTMESSAGEID + " = " + userDB.getLastMessageId()
						+ WHERE + ID + " = " + userDB.getId();
		
		PreparedStatement ps = connection.prepareStatement(query);
		BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeUpdate(ps);
	}
}
