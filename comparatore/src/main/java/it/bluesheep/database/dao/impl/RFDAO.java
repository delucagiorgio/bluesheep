package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.entities.RF;
import it.bluesheep.util.BlueSheepConstants;

public class RFDAO extends AbstractDAO<RF> {

	private static RFDAO instance;
	public static final String tableName = "RF";
	private static final String RFTEXT = "rfText";
	private static final String RFCODE = "rfCode";
	private static final String ACTIVE = "active";
	
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
			
			rfList.add(RF.getRFFromDatabaseInfo(rfText, rfCode, active, id));
		}
		
		return rfList;
	}

	@Override
	protected String getAllColumnValuesFromEntity(RF entity) {
		return "("
				+ RFTEXT + BlueSheepConstants.REGEX_COMMA
				+ RFCODE + BlueSheepConstants.REGEX_COMMA
				+ ACTIVE + ")";
	}

}
