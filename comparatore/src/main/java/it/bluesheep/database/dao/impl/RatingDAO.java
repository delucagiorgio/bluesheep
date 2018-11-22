package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.dao.IFilterDAO;
import it.bluesheep.database.entities.Rating;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.util.BlueSheepConstants;

public class RatingDAO extends AbstractDAO<Rating> implements IFilterDAO<Rating> {

	private static RatingDAO instance;
	public static final String tableName = "RATING_FILTER";
	private static final String RATINGVALUE = "ratingValue";
	private static final String RATINGTEXT = "ratingText";
	private static final String RATINGCODE = "ratingCode";
	private static final String ACTIVE = "active";
	
	private RatingDAO() {
		super(tableName);
	}

	public static synchronized RatingDAO getRatingDAOInstance() {
		if(instance == null) {
			instance = new RatingDAO();
		}
		return instance;
	}
	
	@Override
	public List<Rating> getAllRowFromButtonText(String textButton, Connection connection) {
		
		List<Rating> returnList = null;
		String query = getBasicSelectQuery() + WHERE + RATINGTEXT + " = ? " + AND + ACTIVE + IS + true;
		
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
	public Rating getSingleRowFromButtonText(String textButton, Connection connection) throws MoreThanOneResultException {
		return getSingleResult(getAllRowFromButtonText(textButton, connection));
	}

	@Override
	protected List<Rating> mapDataIntoObject(ResultSet returnSelect, Connection connection) throws SQLException {
		List<Rating> ratingList = new ArrayList<Rating>();
		
		while(returnSelect.next()) {
			Double ratingValue = returnSelect.getDouble(RATINGVALUE) == 0 ? null : returnSelect.getDouble(RATINGVALUE);
			String ratingText = returnSelect.getString(RATINGTEXT);
			String ratingCode = returnSelect.getString(RATINGCODE);
			boolean active = returnSelect.getBoolean(ACTIVE);
			long id = returnSelect.getLong(ID);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			ratingList.add(Rating.getRatingFromDatabaseInfo(ratingValue, ratingText, ratingCode, active, id, createTime, updateTime));
		}
		
		return ratingList;
	}

	@Override
	protected String getAllColumnValuesFromEntity(Rating entity) {
		return "("
				+ entity.getRatingValue() + BlueSheepConstants.REGEX_COMMA
				+ entity.getRatingText() + BlueSheepConstants.REGEX_COMMA
				+ entity.getRatingCode() + BlueSheepConstants.REGEX_COMMA
				+ entity.isActive() + BlueSheepConstants.REGEX_COMMA +
				"?" + BlueSheepConstants.REGEX_COMMA +
				"?" +")";
	}

}
