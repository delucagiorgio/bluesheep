package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.dao.IFilterDAO;
import it.bluesheep.database.entities.MinOddValue;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.util.BlueSheepConstants;

public class MinOddValueDAO extends AbstractDAO<MinOddValue> implements IFilterDAO<MinOddValue>{

	private static MinOddValueDAO instance;
	public static final String tableName = "MINODDVALUE_FILTER";
	private static final String MINODDVALUE = "minOddValue";
	private static final String MINODDTEXT = "minOddText";
	private static final String MINODDCODE = "minOddCode";
	private static final String ACTIVE = "active";
	
	
	protected MinOddValueDAO(Connection connection) {
		super(tableName, connection);
	}
	
	public static synchronized MinOddValueDAO getMinOddValueDAOInstance(Connection connection) {
		if(instance == null) {
			instance = new MinOddValueDAO(connection);
		}
		return instance;
	}

	@Override
	public List<MinOddValue> getAllRowFromButtonText(String textButton) {
		
		List<MinOddValue> returnList = null;
		String query = getBasicSelectQuery() + WHERE + MINODDTEXT + " = ? " + AND + ACTIVE + IS + true;
		
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, textButton);
			returnList = getMappedObjectBySelect(ps);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return returnList;
	}

	@Override
	public MinOddValue getSingleRowFromButtonText(String textButton) throws MoreThanOneResultException {
		return getSingleResult(getAllRowFromButtonText(textButton));
	}

	@Override
	protected List<MinOddValue> mapDataIntoObject(ResultSet returnSelect) throws SQLException {
		List<MinOddValue> ratingList = new ArrayList<MinOddValue>();
		
		while(returnSelect.next()) {
			Double minOddValue = returnSelect.getDouble(MINODDVALUE) == 0 ? null : returnSelect.getDouble(MINODDVALUE);
			String minOddText = returnSelect.getString(MINODDTEXT);
			String minOddCode = returnSelect.getString(MINODDCODE);
			boolean active = returnSelect.getBoolean(ACTIVE);
			long id = returnSelect.getLong(ID);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			ratingList.add(MinOddValue.getMinOddValueFromDatabaseInfo(minOddValue, minOddText, minOddCode, active, id, createTime, updateTime));
		}
		
		return ratingList;
	}

	@Override
	protected String getAllColumnValuesFromEntity(MinOddValue entity) {
		return "("
				+ MINODDVALUE + BlueSheepConstants.REGEX_COMMA
				+ MINODDTEXT + BlueSheepConstants.REGEX_COMMA
				+ MINODDCODE + BlueSheepConstants.REGEX_COMMA
				+ ACTIVE + BlueSheepConstants.REGEX_COMMA +
				"?" + BlueSheepConstants.REGEX_COMMA +
				"?" +")";
	}
}
