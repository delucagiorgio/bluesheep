package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.dao.IFilterDAO;
import it.bluesheep.database.entities.Size;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.util.BlueSheepConstants;

public class SizeDAO extends AbstractDAO<Size> implements IFilterDAO<Size> {

	private static SizeDAO instance;
	public static final String tableName = "SIZE_FILTER";
	private static final String SIZEVALUE = "sizeValue";
	private static final String SIZETEXT = "sizeText";
	private static final String SIZECODE = "sizeCode";
	private static final String ACTIVE = "active";
	
	private SizeDAO() {
		super(tableName);
	}
	
	public static synchronized SizeDAO getSizeDAOInstance() {
		if(instance == null) {
			instance = new SizeDAO();
		}
		return instance;
	}

	@Override
	public List<Size> getAllRowFromButtonText(String textButton, Connection connection) {
		
		List<Size> returnList = null;
		String query = getBasicSelectQuery() + WHERE + SIZETEXT + " = ? " + AND + ACTIVE + IS + true;
		
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, textButton);
			returnList = getMappedObjectBySelect(ps, connection);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return returnList;
	}

	@Override
	public Size getSingleRowFromButtonText(String textButton, Connection connection) throws MoreThanOneResultException {
		return getSingleResult(getAllRowFromButtonText(textButton, connection));
	}

	@Override
	protected List<Size> mapDataIntoObject(ResultSet returnSelect, Connection connection) throws SQLException {
		List<Size> sizeList = new ArrayList<Size>();
		
		while(returnSelect.next()) {
			Double sizeValue = returnSelect.getDouble(SIZEVALUE) == 0 ? null : returnSelect.getDouble(SIZEVALUE);
			String sizeText = returnSelect.getString(SIZETEXT);
			String sizeCode = returnSelect.getString(SIZECODE);
			boolean active = returnSelect.getBoolean(ACTIVE);
			long id = returnSelect.getLong(ID);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			sizeList.add(Size.getSizeFromDatabaseInfo(sizeText, sizeCode, sizeValue, active, id, createTime, updateTime));
		}
		
		return sizeList;
	}

	@Override
	protected String getAllColumnValuesFromEntity(Size entity) {
		return "("
				+ SIZEVALUE + BlueSheepConstants.REGEX_COMMA
				+ SIZETEXT + BlueSheepConstants.REGEX_COMMA
				+ SIZECODE + BlueSheepConstants.REGEX_COMMA
				+ ACTIVE + BlueSheepConstants.REGEX_COMMA +
				"?" + BlueSheepConstants.REGEX_COMMA +
				"?" +")";
	}

}
