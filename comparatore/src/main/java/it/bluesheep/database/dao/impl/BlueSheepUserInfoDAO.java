package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.entities.BlueSheepUserInfo;
import it.bluesheep.util.BlueSheepConstants;

public class BlueSheepUserInfoDAO extends AbstractDAO<BlueSheepUserInfo> {

	private static BlueSheepUserInfoDAO instance;
	public static final String tableName = "BLUESHEEP_USER";
	private static final String TELEGRAM_UN = "telegramUsername";
	private static final String BS_UN = "bluesheepUsername";
	private static final String GROUP = "bluesheepGroup";
	
	protected BlueSheepUserInfoDAO() {
		super(tableName);
	}
	
	public static synchronized BlueSheepUserInfoDAO getBlueSheepUserInfoDAOInstance() {
		if(instance == null) {
			instance = new BlueSheepUserInfoDAO();
		}
		return instance;
	}

	@Override
	protected List<BlueSheepUserInfo> mapDataIntoObject(ResultSet returnSelect, Connection connection) throws SQLException {
		List<BlueSheepUserInfo> returnUsers = new ArrayList<BlueSheepUserInfo>();
		
		while(returnSelect.next()) {
			long id = returnSelect.getLong(ID);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			String telegramUsername = returnSelect.getString(TELEGRAM_UN) != null ? 
					(returnSelect.getString(TELEGRAM_UN).isEmpty() ? 
							null 
							: returnSelect.getString(TELEGRAM_UN)) 
					: null;
			String bsUsername = returnSelect.getString(BS_UN);
			String group = returnSelect.getString(GROUP);
			
			returnUsers.add(new BlueSheepUserInfo(telegramUsername, group, bsUsername, id, createTime, updateTime));
		}
		return returnUsers;
	}

	@Override
	protected String getAllColumnValuesFromEntity(BlueSheepUserInfo entity) {
		return "("
				+ "'" + entity.getTelegramUsername() + "'" + BlueSheepConstants.REGEX_COMMA
				+ "'" + entity.getGroup() + "'" + BlueSheepConstants.REGEX_COMMA
				+ "'" + entity.getBluesheepUsername() + "'" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA 
				+ "?)";
	}

	public void deleteTable(Connection connection) throws SQLException {
		String query = DELETE + tableName;
		
		Statement st = connection.createStatement();
		st.executeUpdate(query);
	}
	
	public void insertMultipleRows(List<BlueSheepUserInfo> bluesheepUserList, Connection connection) throws SQLException {
		
		int countEntity = bluesheepUserList.size();
		
		int i = 0;
		int page = 50;
		do {
			logger.debug("Executing query page " + (i + 1) + " of " + (countEntity / page + (countEntity % page == 0 ? 0 : 1)));
			int startIndex = i * page;
			int endIndex =  startIndex + page;
			List<BlueSheepUserInfo> subList = bluesheepUserList.subList(startIndex, Math.min(countEntity, endIndex));
			String query = getinsertBaseTableNameQuery() + getAllColumnValuesFromEntityMultipleInsert();
			PreparedStatement ps = connection.prepareStatement(query);
			
			for(BlueSheepUserInfo entity : subList) {
				String telegramUser = entity.getTelegramUsername();
				String bsUser = entity.getBluesheepUsername();
				String group = entity.getGroup();
				
				ps.setString(1, telegramUser != null && !telegramUser.isEmpty() ? telegramUser : null);
				ps.setString(2, group != null && !group.isEmpty() ? group : null);
				ps.setString(3, bsUser != null && !bsUser.isEmpty() ? bsUser : null);
				
				ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
				ps.setTimestamp(5, null);
				
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
				+ "?)";
	}
	
	public List<BlueSheepUserInfo> getAllTelegramPayUser(Connection connection) throws SQLException{
		String query = getBasicSelectQuery() + WHERE + TELEGRAM_UN + IS + NOT + " NULL";
		
		return getMappedObjectBySelect(query, connection);
	}

}
