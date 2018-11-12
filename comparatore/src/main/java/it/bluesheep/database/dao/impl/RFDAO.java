package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.entities.RF;
import it.bluesheep.util.BlueSheepConstants;

public class RFDAO extends AbstractDAO<RF> {

	private static RFDAO instance;
	public static final String tableName = "RF_FILTER";
	private static final String RFTEXT = "rfText";
	private static final String RFCODE = "rfCode";
	private static final String ACTIVE = "active";
	private static final String RFVALUE = "rfValue";
	
	protected RFDAO(Connection connection) {
		super(tableName, connection);
	}
	
	public static synchronized RFDAO getRFDAOInstance(Connection connection) {
		if(instance == null) {
			instance = new RFDAO(connection);
		}
		return instance;
	}

	@Override
	protected List<RF> mapDataIntoObject(ResultSet returnSelect) throws SQLException {
		
		List<RF> rfList = new ArrayList<RF>();
		
		while(returnSelect.next()) {
			String rfText = returnSelect.getString(RFTEXT);
			String rfCode = returnSelect.getString(RFCODE);
			boolean active = returnSelect.getBoolean(ACTIVE);
			long id = returnSelect.getShort(ID);
			Double rfValue = returnSelect.getDouble(RFVALUE) == 0 ? null : returnSelect.getDouble(RFVALUE);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			rfList.add(RF.getRFFromDatabaseInfo(rfText, rfCode, active, id, rfValue, createTime, updateTime));
		}
		
		return rfList;
	}

	@Override
	protected String getAllColumnValuesFromEntity(RF entity) {
		return "("
				+ RFTEXT + BlueSheepConstants.REGEX_COMMA
				+ RFCODE + BlueSheepConstants.REGEX_COMMA
				+ ACTIVE + BlueSheepConstants.REGEX_COMMA
				+ RFVALUE + BlueSheepConstants.REGEX_COMMA +
				"?" + BlueSheepConstants.REGEX_COMMA +
				"?" +")";
	}

}
