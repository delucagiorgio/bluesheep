package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	
	private BookmakerDAO(Connection connection) {
		super(tableName, connection);
	}
	
	public static synchronized BookmakerDAO getBlueSheepBookmakerDAOInstance(Connection connection) {
		if(instance == null) {
			instance = new BookmakerDAO(connection);
		}
		return instance;
	}
	@Override
	protected List<Bookmaker> mapDataIntoObject(ResultSet returnSelect) throws SQLException {
		List<Bookmaker> dataMapped = new ArrayList<Bookmaker>();
		
		while(returnSelect.next()) {
			String bookmakerName = returnSelect.getString(BOOKMAKERNAME);
			boolean active = returnSelect.getBoolean(ACTIVE);
			long id = returnSelect.getLong(ID);
			
			dataMapped.add(Bookmaker.getBlueSheepBookmakerFromDatabaseInfo(bookmakerName, id, active));
		}
		return dataMapped;
	}

	@Override
	protected String getAllColumnValuesFromEntity(Bookmaker entity) {
		return "('" + entity.getBookmakerName() + BlueSheepConstants.REGEX_COMMA + ACTIVE + "')";
	}
	
	public Bookmaker getBookmakerFromBookmakerName(String bookmakerName) throws MoreThanOneResultException {
		
		Bookmaker returnBookmaker = null;
		
		String query = getBasicSelectQuery() + 
					   WHERE + BOOKMAKERNAME + " = ?";
		
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, bookmakerName);
			if(ps != null) {
				returnBookmaker = getSingleResult(getMappedObjectBySelect(ps));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return returnBookmaker;
	}
	
	public Bookmaker getActiveBookmakerFromBookmakerName(String bookmakerName) throws MoreThanOneResultException {
		Bookmaker bookmaker = getBookmakerFromBookmakerName(bookmakerName);
		return bookmaker != null && bookmaker.isActive() ? bookmaker : null;
	}

	public List<Bookmaker> getAllActiveBookmakerOrderedByName() {
		return getMappedObjectBySelect(getBasicSelectQuery() + WHERE + ACTIVE + IS + true + 
																ORDERBY + BOOKMAKERNAME + ASC);
	}

	public List<Bookmaker> getLikeBookmakerNameByInitalChar(String initialChar) {
		if(initialChar == null) {
			return getAllActiveBookmakerOrderedByName();
		}else {
			return getMappedObjectBySelect(getBasicSelectQuery() + WHERE + BOOKMAKERNAME + LIKE + "'" + initialChar + "%'" 
															+ AND + ACTIVE + IS + true 
															+ ORDERBY + BOOKMAKERNAME + ASC);
		}
	}

	public List<Bookmaker> getBookmakerPageByInitialChar(String initialChar, boolean isGreater) {
		
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
		
		return ps != null ? getMappedObjectBySelect(ps) : null;
	}
	
}
