package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
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
	private static String USERNAME = "userName";
	private static String CHATID = "chatId";
	private static String REGISTRATIONDATE = "registrationDate";
	private static String ACTIVE = "active";
	private static String LASTMESSAGEID = "lastMessageId";
	private static String BLUESHEEP_USERNAME = "bluesheepUsername";

	private TelegramUserDAO() {
		super(tableName);
	}
	
	public static synchronized TelegramUserDAO getBlueSheepTelegramUserDAOInstance() {
		if(instance == null) {
			instance = new TelegramUserDAO();
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
	protected List<TelegramUser> mapDataIntoObject(ResultSet returnSelect, Connection connection) throws SQLException {
		
		List<TelegramUser> dataMapped = new ArrayList<TelegramUser>();
		
		while(returnSelect.next()) {
			String userName = returnSelect.getString(USERNAME);
			String bsUsername = returnSelect.getString(BLUESHEEP_USERNAME);
			Long chatId = returnSelect.getLong(CHATID) == 0 ? null : returnSelect.getLong(CHATID);
			Timestamp registrationDate = getTimestampFromResultSet(returnSelect, REGISTRATIONDATE);
			Boolean active = returnSelect.getBoolean(ACTIVE);
			Long id = returnSelect.getLong(ID);
			Long lastMessageId = returnSelect.getLong(LASTMESSAGEID) == 0 ? null : returnSelect.getLong(LASTMESSAGEID);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			dataMapped.add(TelegramUser.getBlueSheepTelegramUserFromDatabaseInfo(userName, bsUsername, chatId, active, registrationDate, id, lastMessageId, createTime, updateTime));
		}
		
		return dataMapped;
	}
	
	public List<TelegramUser> getActiveUsers(Connection connection) throws SQLException{
		return getMappedObjectBySelect(getActiveUsersQuery(), connection);
	}
	
	public List<TelegramUser> getInactiveUsers(Connection connection) throws SQLException{
		return getMappedObjectBySelect(getInactiveUsersQuery(), connection);
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
	public TelegramUser getUserFromUser(TelegramUser user, Connection connection) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException, MoreThanOneResultException{
		
		TelegramUser returnUser = null;
		
		if(user != null) {
			String query = getBasicSelectQuery() + 
						   WHERE +
						   USERNAME + " = ? " 
						   + AND + 
						   "(" + CHATID + IS + "NULL" + OR + CHATID + "= ?)" 
						   + AND  + 
						   ACTIVE + IS + true;
			PreparedStatement prepStatement = connection.prepareStatement(query);
			prepStatement.setString(1, user.getUserName());
			prepStatement.setLong(2, user.getChatId());
			if(prepStatement != null) {
				returnUser = getSingleResult(getMappedObjectBySelect(prepStatement, connection));
			}
		}else {
			logger.warn("User is null");
		}
		
		return returnUser;
	}
	
	@Override
	protected String getAllColumnValuesFromEntity(TelegramUser user) {
		return "(" +
					"'" + user.getUserName() + "'" +  BlueSheepConstants.REGEX_COMMA + 
					user.getChatId() + BlueSheepConstants.REGEX_COMMA + 
					"'" + user.getRegistrationDate() + "'" +  BlueSheepConstants.REGEX_COMMA + 
					user.isActive() + BlueSheepConstants.REGEX_COMMA + 
					user.getLastMessageId() + BlueSheepConstants.REGEX_COMMA +
					"'" + user.getBluesheepUsername() + "'" +  BlueSheepConstants.REGEX_COMMA +
					"?" + BlueSheepConstants.REGEX_COMMA +
					"?" +")";
	}

	public TelegramUser getUserFromMessage(Message receivedMessage, Connection connection) throws AskToUsException {
		TelegramUser returnUser = null;
		String query = getBasicSelectQuery() 
				+ WHERE + "(" + CHATID + " = ? " + OR + CHATID + IS + " NULL)" 
				+ AND 
				+ USERNAME + " =  ? " 
				+ AND 
				+ ACTIVE + IS + true;
		
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(query);
			ps.setLong(1, receivedMessage.getChatId());
			ps.setString(2, receivedMessage.getFrom().getUserName());
			
			returnUser = getSingleResult(getMappedObjectBySelect(ps, connection));
			
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (MoreThanOneResultException e) {
			throw new AskToUsException(TelegramUser.getTelegramUserFromMessage(receivedMessage));
		}
		
		return returnUser;
	}

	public void updateLastMessageSent(TelegramUser userDB, Connection connection) throws SQLException {
		
		String query = UPDATE + tableName 
						+ SET + LASTMESSAGEID + " = " + userDB.getLastMessageId()
						+ WHERE + ID + " = " + userDB.getId();
		
		PreparedStatement ps = connection.prepareStatement(query);
		BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeUpdate(ps);
	}
	
	public TelegramUser findByBlueSheepUsername(TelegramUser userDB, Connection connection) throws SQLException, MoreThanOneResultException {
		
		String query = getBasicSelectQuery() + WHERE + BLUESHEEP_USERNAME + " = ?"; 
		
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setString(1, userDB.getBluesheepUsername());
		
		return getSingleResult(getMappedObjectBySelect(ps, connection));
	}
	
	private void updateTelegramUserStatusByParameter(List<TelegramUser> toBeUpdatedList, boolean active, Connection connection) throws SQLException {
		
		int countEntity = toBeUpdatedList.size();
		int i = 0;
		int page = 50;
		do {
			logger.debug("Executing query page " + (i + 1) + " of " + (countEntity / page + (countEntity % page == 0 ? 0 : 1)));
			int startIndex = i * page;
			int endIndex =  startIndex + page;
			List<TelegramUser> subList = toBeUpdatedList.subList(startIndex, Math.min(countEntity, endIndex));
			String query = UPDATE + tableName + SET + ACTIVE + " = ? " + WHERE + ID + " = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			
			for(TelegramUser entity : subList) {
				ps.setBoolean(1, active);
				ps.setLong(2, entity.getId());

				ps.addBatch();
			}
		
			ps.executeBatch();
			i++;
		}while(i * page < countEntity);
	}
	
	public void disableTelegramUserFromList(List<TelegramUser> toBeDisabledList, Connection connection) throws SQLException {
		updateTelegramUserStatusByParameter(toBeDisabledList, false, connection);
	}

	public void enableTelegramUserFromList(List<TelegramUser> toBeDisabledList, Connection connection) throws SQLException {
		updateTelegramUserStatusByParameter(toBeDisabledList, true, connection);
	}

	public void insertMultipleRows(List<TelegramUser> toBeAddList, boolean active, Connection connection) throws SQLException {
		int countEntity = toBeAddList.size();
		
		int i = 0;
		int page = 50;
		do {
			logger.debug("Executing query page " + (i + 1) + " of " + (countEntity / page + (countEntity % page == 0 ? 0 : 1)));
			int startIndex = i * page;
			int endIndex =  startIndex + page;
			List<TelegramUser> subList = toBeAddList.subList(startIndex, Math.min(countEntity, endIndex));
			String query = getinsertBaseTableNameQuery() + getAllColumnValuesFromEntityMultipleInsert();
			PreparedStatement ps = connection.prepareStatement(query);
			
			for(TelegramUser entity : subList) {
				ps.setString(1, entity.getUserName());
				ps.setNull(2, Types.BIGINT);
				ps.setTimestamp(3, entity.getRegistrationDate());
				ps.setBoolean(4, active);
				ps.setNull(5, Types.BIGINT);
				ps.setString(6, entity.getBluesheepUsername());
				ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
				ps.setTimestamp(8, null);
				
				ps.addBatch();
			}
		
			ps.executeBatch();
			i++;
		}while(i * page < countEntity);
		
	}
	
	protected String getAllColumnValuesFromEntityMultipleInsert() {
		return 	"("
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?)";
	}

	public TelegramUser updateChatIdFirstLogin(TelegramUser userMessage, Message receivedMessage, Connection connection) throws SQLException, MoreThanOneResultException, AlreadyRegisteredUserChatBotException {
		String query = UPDATE + tableName + SET + CHATID + " = ?" + WHERE + USERNAME + "= ? " + AND + CHATID + IS + "NULL";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setLong(1, receivedMessage.getChatId());
		ps.setString(2, userMessage.getUserName());
		
		int updated = ps.executeUpdate();
		
		if(updated != 1) {
			throw new AlreadyRegisteredUserChatBotException(userMessage);
		}
		
		return userMessage;
		
	}
}
