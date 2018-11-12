package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.entities.Bookmaker;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.telegrambot.exception.AskToUsException;
import it.bluesheep.util.BlueSheepConstants;

public class UserPreferenceDAO extends AbstractDAO<UserPreference> {

	private static UserPreferenceDAO instance;
	public static final String tableName = "USER_PREFERENCE";
	private static final String BOOKMAKER = "bookmaker";
	private static final String USER = "user";
	private static final String RATING = "rating";
	private static final String RF = "rf";
	private static final String LIQUIDITA = "liquidita";
	private static final String EVENT = "event";
	private static final String CHAMPIONSHIP = "championship";
	private static final String MINODDVALUE = "minOddValue";
	private static final String ACTIVE = "active";
	
	private UserPreferenceDAO(Connection connection) {
		super(tableName, connection);
	}
	
	public static synchronized UserPreferenceDAO getUserPreferenceDAOInstance(Connection connection) {
		if(instance == null) {
			instance = new UserPreferenceDAO(connection);
		}
		return instance;
	} 

	@Override
	protected List<UserPreference> mapDataIntoObject(ResultSet returnSelect) throws SQLException {
		
		List<UserPreference> dataMapped = new ArrayList<UserPreference>();
		
		while(returnSelect.next()) {
			Long bookmaker = returnSelect.getLong(BOOKMAKER);
			Long user = returnSelect.getLong(USER);
			
			TelegramUser userDB = TelegramUserDAO.getBlueSheepTelegramUserDAOInstance(connection).getEntityById(user);
			Bookmaker bookmakerDB = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getEntityById(bookmaker);
			
			if(userDB != null && bookmakerDB != null) {
				Double rating = returnSelect.getDouble(RATING) == 0 ? null : returnSelect.getDouble(RATING);
				Double rf = returnSelect.getDouble(RF) == 0 ? null : returnSelect.getDouble(RF);
				Double liquidita = returnSelect.getDouble(LIQUIDITA) == 0 ? null : returnSelect.getDouble(LIQUIDITA);
				String event = returnSelect.getString(EVENT);
				String championship = returnSelect.getString(CHAMPIONSHIP);
				Double minOddValue = returnSelect.getDouble(MINODDVALUE) == 0 ? null : returnSelect.getDouble(MINODDVALUE);
				boolean active = returnSelect.getBoolean(ACTIVE);
				long id = returnSelect.getLong(ID);
				Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
				Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
				
				dataMapped.add(UserPreference.getBlueSheepUserPreferenceFromDatabaseInfo(bookmakerDB, userDB, rating, rf, liquidita, event, championship, minOddValue, active, id, createTime, updateTime));
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
				entity.getRf() + BlueSheepConstants.REGEX_COMMA + 
				entity.getLiquidita() + BlueSheepConstants.REGEX_COMMA + 
				entity.getEvent() + BlueSheepConstants.REGEX_COMMA + 
				entity.getChampionship() + BlueSheepConstants.REGEX_COMMA + 
				entity.getMinOddValue() + BlueSheepConstants.REGEX_COMMA + 
				entity.isActive() + BlueSheepConstants.REGEX_COMMA +
				"?" + BlueSheepConstants.REGEX_COMMA +
				"?" +")";
	}

	public List<UserPreference> getRelatedUserPreferenceFromUser(TelegramUser userMessage) {
		
		String query = getBasicSelectQuery() + WHERE + USER + " = " + userMessage.getId();
	
		return getMappedObjectBySelect(query);
	}
	
	public List<UserPreference> getUserPreferenceFromUser(TelegramUser userMessage) throws AskToUsException {
		String query = getBasicSelectQuery() + 
						WHERE + USER + " = " + userMessage.getId();
		
		List<UserPreference> resultList = getMappedObjectBySelect(query);
		
		if(resultList == null || resultList.size() > Integer.parseInt(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.CHAT_BOT_MAX_PREF))) {
			throw new AskToUsException(userMessage);
		}
		
		return resultList;
	}

	public UserPreference getUserPreferenceFromUserAndBookmaker(TelegramUser userMessage, Bookmaker bookmaker) throws MoreThanOneResultException {
		String query = getBasicSelectQuery() + WHERE + USER + " = " + userMessage.getId() + AND + BOOKMAKER + " = " + bookmaker.getId();
		
		UserPreference userPreference = getSingleResult(getMappedObjectBySelect(query));
		
		return userPreference;
	}

}
