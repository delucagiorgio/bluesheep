package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.entities.Bookmaker;
import it.bluesheep.database.exception.MoreThanOneResultException;

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
		return "('" + entity.getBookmakerName() + "')";
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

	public List<Bookmaker> getAllActiveBookmaker() {
		return getMappedObjectBySelect(getBasicSelectQuery() + WHERE + ACTIVE + IS + true);
	}
}
