package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.entities.Bookmaker;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.util.BlueSheepConstants;

public class BookmakerDAO extends AbstractDAO<Bookmaker> {

		private static BookmakerDAO instance;
		public static final String tableName = "BOOKMAKER";
		private static final String BOOKMAKERNAME = "bookmakerName";
		private static final String ACTIVE = "active";
	
	private BookmakerDAO() {
		super(tableName);
	}
	
	public static synchronized BookmakerDAO getBlueSheepBookmakerDAOInstance() {
		if(instance == null) {
			instance = new BookmakerDAO();
		}
		return instance;
	}
	@Override
	protected List<Bookmaker> mapDataIntoObject(ResultSet returnSelect, Connection connection) throws SQLException {
		List<Bookmaker> dataMapped = new ArrayList<Bookmaker>();
		
		while(returnSelect.next()) {
			String bookmakerName = returnSelect.getString(BOOKMAKERNAME);
			boolean active = returnSelect.getBoolean(ACTIVE);
			long id = returnSelect.getLong(ID);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			dataMapped.add(Bookmaker.getBlueSheepBookmakerFromDatabaseInfo(bookmakerName, id, active, createTime, updateTime));
		}
		return dataMapped;
	}

	@Override
	protected String getAllColumnValuesFromEntity(Bookmaker entity) {
		return "'" + entity.getBookmakerName() + BlueSheepConstants.REGEX_COMMA + ACTIVE + "'" + BlueSheepConstants.REGEX_COMMA +
				"?" + BlueSheepConstants.REGEX_COMMA +
				"?" +")";
	}
	
	public Bookmaker getBookmakerFromBookmakerName(String bookmakerName, Connection connection) throws MoreThanOneResultException {
		
		Bookmaker returnBookmaker = null;
		
		String query = getBasicSelectQuery() + 
					   WHERE + BOOKMAKERNAME + " = ?";
		
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, bookmakerName);
			if(ps != null) {
				returnBookmaker = getSingleResult(getMappedObjectBySelect(ps, connection));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return returnBookmaker;
	}
	
	public Bookmaker getActiveBookmakerFromBookmakerName(String bookmakerName, Connection connection) throws MoreThanOneResultException {
		Bookmaker bookmaker = getBookmakerFromBookmakerName(bookmakerName, connection);
		return bookmaker != null && bookmaker.isActive() ? bookmaker : null;
	}

	public List<Bookmaker> getAllActiveBookmakerOrderedByName(Connection connection) throws SQLException {
		return getMappedObjectBySelect(getBasicSelectQuery() + WHERE + ACTIVE + IS + true + 
																ORDERBY + BOOKMAKERNAME + ASC , connection);
	}

	public List<Bookmaker> getLikeBookmakerNameByInitalChar(String initialChar, Connection connection) throws SQLException {
		if(initialChar == null) {
			return getAllActiveBookmakerOrderedByName(connection);
		}else {
			return getMappedObjectBySelect(getBasicSelectQuery() + WHERE + BOOKMAKERNAME + LIKE + "'" + initialChar + "%'" 
															+ AND + ACTIVE + IS + true 
															+ ORDERBY + BOOKMAKERNAME + ASC, connection);
		}
	}

	public List<Bookmaker> getBookmakerPageByInitialChar(String initialChar, boolean isGreater, Connection connection) throws SQLException {
		
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement("SELECT B.* "
											+ "FROM " + tableName +" B" 
											+ WHERE + "B." + ACTIVE + IS + true 
											+ AND + "ASCII(SUBSTRING(B." + BOOKMAKERNAME + ", 1, 1))" + (isGreater ? GREAT : LESS) + "ASCII(?)" 
											+ ORDERBY + "B." + BOOKMAKERNAME 
											+ ASC);
			ps.setString(1, initialChar);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		
		return ps != null ? getMappedObjectBySelect(ps, connection) : null;
	}
	
}
