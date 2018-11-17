package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.dao.IFilterDAO;
import it.bluesheep.database.entities.RFType;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.util.BlueSheepConstants;

public class RFTypeDAO extends AbstractDAO<RFType> implements IFilterDAO<RFType>{

	private static RFTypeDAO instance;
	private static final String tableName = "RF_TYPE";
	private static final String REFUNDPERCENTAGE = "refundPercentage";
	private static final String REFUNDTEXT = "refundText";
	private static final String ACTIVE = "active";
	
	private RFTypeDAO(Connection connection) {
		super(tableName, connection);
	}
	
	public static synchronized RFTypeDAO getRFTypeDAOInstance(Connection connection) {
		if(instance == null) {
			instance = new RFTypeDAO(connection);
		}
		return instance;
	} 

	@Override
	protected List<RFType> mapDataIntoObject(ResultSet returnSelect) throws SQLException {
		
		List<RFType> dataMapped = new ArrayList<RFType>();
		
		while(returnSelect.next()) {
			long id = returnSelect.getLong(ID);
			Double refundPercentage = returnSelect.getDouble(REFUNDPERCENTAGE) == 0 ? null : returnSelect.getDouble(REFUNDPERCENTAGE);
			String refundText = returnSelect.getString(REFUNDTEXT);
			boolean active = returnSelect.getBoolean(ACTIVE);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);

			dataMapped.add(RFType.getRFTypeFromDatabaseInfo(refundPercentage, refundText, active, createTime, updateTime, id));
		}
		
		return dataMapped;
	}

	@Override
	protected String getAllColumnValuesFromEntity(RFType entity) {
		return "(" +
				entity.getRefundPercentage() + BlueSheepConstants.REGEX_COMMA +
				entity.getRefundText() + BlueSheepConstants.REGEX_COMMA +
				entity.isActive() + BlueSheepConstants.REGEX_COMMA +
				"?" + BlueSheepConstants.REGEX_COMMA +
				"?" +")";
	}

	@Override
	public List<RFType> getAllRowFromButtonText(String textButton) {
		
		List<RFType> rfTypeList = null;
		
		String query = getBasicSelectQuery() + WHERE + REFUNDTEXT + " = ?";
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, textButton);
			rfTypeList = getMappedObjectBySelect(ps);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		
		return rfTypeList;
	}

	@Override
	public RFType getSingleRowFromButtonText(String textButton) throws MoreThanOneResultException {
		return getSingleResult(getAllRowFromButtonText(textButton));
	}
	
	

}
