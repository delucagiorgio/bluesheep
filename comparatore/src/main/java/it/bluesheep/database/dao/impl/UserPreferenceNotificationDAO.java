package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.BlueSheepDatabaseManager;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.database.entities.UserPreferenceNotification;
import it.bluesheep.util.BlueSheepConstants;

public class UserPreferenceNotificationDAO extends AbstractDAO<UserPreferenceNotification> {

	public static final String tableName = "USER_PREFERENCE_NOTIFICATION";
	private static UserPreferenceNotificationDAO instance;
	private static final String USERPREFERENCEID = "userPreferenceId";
	private static final String USERID = "userId";
	private static final String PRODID = "progId";
	private static final String NOTIFICATIONKEY = "notificationKey";
	
	private UserPreferenceNotificationDAO() {
		super(tableName);
	}
	
	public static synchronized UserPreferenceNotificationDAO getUserPreferenceNotificationDAOInstance() {
		if(instance == null) {
			instance = new UserPreferenceNotificationDAO();
		}
		return instance;
	}

	@Override
	protected List<UserPreferenceNotification> mapDataIntoObject(ResultSet returnSelect, Connection connection) throws SQLException {
		
		List<UserPreferenceNotification> userPrefNotitication = new ArrayList<UserPreferenceNotification>(returnSelect.getFetchSize());
		
		while(returnSelect.next()) {
			Long userPreferenceId = returnSelect.getLong(USERPREFERENCEID);
			Long userId = returnSelect.getLong(USERID);
			
			UserPreference userPreference = UserPreferenceDAO.getUserPreferenceDAOInstance().getEntityById(userPreferenceId,connection);
			TelegramUser user = TelegramUserDAO.getBlueSheepTelegramUserDAOInstance().getEntityById(userId, connection);
			int prodId = returnSelect.getInt(PRODID);
			long id = returnSelect.getLong(ID);
			String notificationKey = returnSelect.getString(NOTIFICATIONKEY);
			Timestamp createTimestamp = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTimestamp = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			userPrefNotitication.add(new UserPreferenceNotification(userPreference, user, id, prodId, createTimestamp, updateTimestamp, notificationKey));
		}
		
		return userPrefNotitication;
	}

	@Override
	protected String getAllColumnValuesFromEntity(UserPreferenceNotification entity) {
		return "("
				+ "'" + entity.getUserPreference().getId() + "'" + BlueSheepConstants.REGEX_COMMA
				+ "'" + entity.getUser().getId() + "'" + BlueSheepConstants.REGEX_COMMA
				+ entity.getProdId() + BlueSheepConstants.REGEX_COMMA 
				+ "'" + entity.getNotificationKey() + "'" + BlueSheepConstants.REGEX_COMMA 
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?)";
	}
	
	public List<UserPreferenceNotification> getNotificationsSentByUserPreference(UserPreference up, Connection connection) throws SQLException {
		String query = getBasicSelectQuery() + WHERE + USERPREFERENCEID + " = " + up.getId();
		
		return getMappedObjectBySelect(query, connection);
	}

	public void deleteUserPrefNotificationFromUP(UserPreference up, Connection connection) throws SQLException {
		
		String query = DELETE + tableName + WHERE + USERPREFERENCEID + " = ?";
		
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setLong(1, up.getId());
		
		BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeUpdate(ps);
		
	}

}
