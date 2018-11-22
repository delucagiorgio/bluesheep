package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.dao.IFilterDAO;
import it.bluesheep.database.entities.RFValue;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.util.BlueSheepConstants;

public class RFValueDAO extends AbstractDAO<RFValue> implements IFilterDAO<RFValue>{

	private static RFValueDAO instance;
	public static final String tableName = "RF_FILTER";
	private static final String RFTEXT = "rfText";
	private static final String RFCODE = "rfCode";
	private static final String ACTIVE = "active";
	private static final String RFVALUE = "rfValue";
	
	private RFValueDAO() {
		super(tableName);
	}
	
	public static synchronized RFValueDAO getRFDAOInstance() {
		if(instance == null) {
			instance = new RFValueDAO();
		}
		return instance;
	}

	@Override
	protected List<RFValue> mapDataIntoObject(ResultSet returnSelect, Connection connection) throws SQLException {
		
		List<RFValue> rfList = new ArrayList<RFValue>();
		
		while(returnSelect.next()) {
			
			String rfText = returnSelect.getString(RFTEXT);
			String rfCode = returnSelect.getString(RFCODE);
			boolean active = returnSelect.getBoolean(ACTIVE);
			long id = returnSelect.getShort(ID);
			Double rfValue = returnSelect.getDouble(RFVALUE) == 0 ? null : returnSelect.getDouble(RFVALUE);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			rfList.add(RFValue.getRFFromDatabaseInfo(rfText, rfCode, active, id, rfValue, createTime, updateTime));
		}
		
		return rfList;
	}

	@Override
	protected String getAllColumnValuesFromEntity(RFValue entity) {
		return "("
				+ RFTEXT + BlueSheepConstants.REGEX_COMMA
				+ RFCODE + BlueSheepConstants.REGEX_COMMA
				+ ACTIVE + BlueSheepConstants.REGEX_COMMA
				+ RFVALUE + BlueSheepConstants.REGEX_COMMA +
				"?" + BlueSheepConstants.REGEX_COMMA +
				"?" +")";
	}

	@Override
	public List<RFValue> getAllRowFromButtonText(String textButton, Connection connection) {
		List<RFValue> rfValueList = null;
		
		String query = getBasicSelectQuery() + WHERE + RFTEXT + " = ?";
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, textButton);
			rfValueList = getMappedObjectBySelect(ps, connection);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		
		return rfValueList;
	}

	@Override
	public RFValue getSingleRowFromButtonText(String textButton, Connection connection) throws MoreThanOneResultException {
		return getSingleResult(getAllRowFromButtonText(textButton, connection));
	}
}
