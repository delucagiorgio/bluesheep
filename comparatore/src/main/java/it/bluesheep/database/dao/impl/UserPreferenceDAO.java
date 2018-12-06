package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.BlueSheepDatabaseManager;
import it.bluesheep.database.entities.Bookmaker;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.telegrambot.exception.AskToUsException;
import it.bluesheep.telegrambot.exception.TooMuchUserPreferenceActive;
import it.bluesheep.util.BlueSheepConstants;

public class UserPreferenceDAO extends AbstractDAO<UserPreference> {

	private static UserPreferenceDAO instance;
	public static final String tableName = "USER_PREFERENCE";
	private static final String BOOKMAKER = "bookmaker";
	private static final String USER = "user";
	private static final String RATING = "ratingValue";
	private static final String RF = "rfValue";
	private static final String LIQUIDITA = "liquidita";
	private static final String EVENT = "event";
	private static final String MINODDVALUE = "minOddValue";
	private static final String ACTIVE = "active";
	private static final String RFTYPE = "rfType";
	
	private UserPreferenceDAO() {
		super(tableName);
	}
	
	public static synchronized UserPreferenceDAO getUserPreferenceDAOInstance() {
		if(instance == null) {
			instance = new UserPreferenceDAO();
		}
		return instance;
	} 
	
	@Override
	public List<UserPreference> getAllActiveRows(Connection connection) throws SQLException{
		String queryStatement = getBasicSelectQuery() + WHERE + ACTIVE + IS + true + ";";
		List<UserPreference> returnList = getMappedObjectBySelect(queryStatement, connection);
		
		return returnList;
	}

	@Override
	protected List<UserPreference> mapDataIntoObject(ResultSet returnSelect, Connection connection) throws SQLException {
		
		List<UserPreference> dataMapped = new ArrayList<UserPreference>();
		
		while(returnSelect.next()) {
			Long bookmaker = returnSelect.getLong(BOOKMAKER);
			Long user = returnSelect.getLong(USER);

			
			TelegramUser userDB = TelegramUserDAO.getBlueSheepTelegramUserDAOInstance().getEntityById(user, connection);
			Bookmaker bookmakerDB = BookmakerDAO.getBlueSheepBookmakerDAOInstance().getEntityById(bookmaker, connection);
			
			if(userDB != null && bookmakerDB != null) {
				Double rating = returnSelect.getDouble(RATING) == 0 ? null : returnSelect.getDouble(RATING);
				Double rf = returnSelect.getDouble(RF) == 0 ? null : returnSelect.getDouble(RF);
				Double liquidita = returnSelect.getDouble(LIQUIDITA) == 0 ? null : returnSelect.getDouble(LIQUIDITA);
				String event = returnSelect.getString(EVENT);
				Double minOddValue = returnSelect.getDouble(MINODDVALUE) == 0 ? null : returnSelect.getDouble(MINODDVALUE);
				Double rfType = returnSelect.getDouble(RFTYPE) == 0 ? null : returnSelect.getDouble(RFTYPE);
				boolean active = returnSelect.getBoolean(ACTIVE);
				long id = returnSelect.getLong(ID);
				Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
				Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
				
				dataMapped.add(UserPreference.getBlueSheepUserPreferenceFromDatabaseInfo(bookmakerDB, userDB, rating, rf, liquidita, event, minOddValue, active, id, createTime, updateTime, rfType));
			}
		}
		
		
		return dataMapped;
	}

	@Override
	protected String getAllColumnValuesFromEntity(UserPreference entity) {	
		return "(" + 
				entity.getBookmaker().getId() + BlueSheepConstants.REGEX_COMMA + 
				entity.getUser().getId() + BlueSheepConstants.REGEX_COMMA + 
				entity.getRating() + BlueSheepConstants.REGEX_COMMA + 
				entity.getRfValue() + BlueSheepConstants.REGEX_COMMA + 
				entity.getLiquidita() + BlueSheepConstants.REGEX_COMMA + 
				entity.getEvent() + BlueSheepConstants.REGEX_COMMA + 
				entity.getMinOddValue() + BlueSheepConstants.REGEX_COMMA + 
				entity.isActive() + BlueSheepConstants.REGEX_COMMA +
				entity.getRfType() + BlueSheepConstants.REGEX_COMMA +
				"?" + BlueSheepConstants.REGEX_COMMA +
				"?" +")";
	}

	public List<UserPreference> getRelatedUserPreferenceFromUser(TelegramUser userMessage, Connection connection) throws SQLException {
		
		String query = getBasicSelectQuery() + WHERE + USER + " = " + userMessage.getId();
	
		return getMappedObjectBySelect(query, connection);
	}
	
	public List<UserPreference> getUserPreferenceFromUser(TelegramUser userMessage, Connection connection) throws AskToUsException, SQLException {
		String query = getBasicSelectQuery() + 
						WHERE + USER + " = " + userMessage.getId();
		
		List<UserPreference> resultList = getMappedObjectBySelect(query, connection);
		
		if(resultList == null || resultList.size() > Integer.parseInt(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.CHAT_BOT_MAX_PREF))) {
			throw new AskToUsException(userMessage);
		}
		
		return resultList;
	}

	public UserPreference getUserPreferenceFromUserAndBookmaker(TelegramUser userMessage, Bookmaker bookmaker, Connection connection) throws MoreThanOneResultException, SQLException {
		String query = getBasicSelectQuery() 
						+ WHERE + USER + " = " + userMessage.getId() 
						+ AND + BOOKMAKER + " = " + bookmaker.getId();
		
		UserPreference userPreference = getSingleResult(getMappedObjectBySelect(query, connection));
		
		return userPreference;
	}

	public void updateUserPreferenceFilters(UserPreference updatedUP, Connection connection) {
		String query = "UPDATE " + 
				tableName + " SET " + EVENT + " = ?, "
				+ LIQUIDITA + " = ?, "
				+ MINODDVALUE + " = ?, "
				+ RATING + " = ?, " 
				+ RF + " = ?,"
				+ RFTYPE + " = ?" 
				+ WHERE + ID + " = ?;";
		
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(query);
			if(updatedUP.getEvent() != null) {
				ps.setString(1, updatedUP.getEvent());
			}else {
				ps.setNull(1, Types.VARCHAR);
			}
			
			if(updatedUP.getLiquidita() != null) {
				ps.setDouble(2, updatedUP.getLiquidita());
			}else {
				ps.setNull(2,Types.DOUBLE);
			}
			
			if(updatedUP.getMinOddValue() != null) {
				ps.setDouble(3, updatedUP.getMinOddValue());	
			}else {
				ps.setNull(3, Types.DOUBLE);
			}
			
			if(updatedUP.getRating() != null) {
				ps.setDouble(4, updatedUP.getRating());	
			}else {
				ps.setNull(4, Types.DOUBLE);
			}
			
			if(updatedUP.getRfValue() != null) {
				ps.setDouble(5, updatedUP.getRfValue());
			}else {
				ps.setNull(5, Types.DOUBLE);
			}
			
			if(updatedUP.getRfType() != null) {
				ps.setDouble(6, updatedUP.getRfType());
			}else {
				ps.setNull(6, Types.DOUBLE);
			}

			ps.setLong(7, updatedUP.getId());	
			
			
			BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeUpdate(ps);
			
			ps.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void activateUserPreference(UserPreference up, Connection connection) throws AskToUsException, TooMuchUserPreferenceActive, SQLException {
		
		if(!up.isActive()) {
			List<UserPreference> userPreferencesForUser = getRelatedUserPreferenceFromUser(up.getUser(), connection);
			
			int activeUP = 1;
			for(UserPreference upDB : userPreferencesForUser) {
				if(upDB.isActive() && !upDB.sameRecord(up)) {
					activeUP++;
				}
			}
			
			if(activeUP > 2) {
				throw new TooMuchUserPreferenceActive(up.getUser(), up);
			}
		}
		
		String query = UPDATE + tableName + SET + ACTIVE + " = ?" + WHERE + ID + " = ?";
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(query);
			ps.setBoolean(1, true);
			ps.setLong(2, up.getId());
			
			BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeUpdate(ps);
			ps.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new AskToUsException(up.getUser());
		}

	}

	public void removeUserPreferenceByBookmakerAndUser(Bookmaker bookmaker, TelegramUser user, Connection connection) throws AskToUsException {
		String query = DELETE + tableName + WHERE + USER + " = ? " + AND + BOOKMAKER + " = ?";
		
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(query);
			ps.setLong(1, user.getId());
			ps.setLong(2, bookmaker.getId());
			BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeDelete(ps);
			ps.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new AskToUsException(user);
		}
		
	}

	public void deactivateUserPreference(UserPreference up, Connection connection) {
		String query = UPDATE + tableName + SET + ACTIVE + " = ?" + WHERE + ID + " = ?";
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(query);
			ps.setBoolean(1, false);
			ps.setLong(2, up.getId());
			
			BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeUpdate(ps);
			ps.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
