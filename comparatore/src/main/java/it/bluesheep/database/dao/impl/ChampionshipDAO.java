package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.entities.Championship;
import it.bluesheep.util.BlueSheepConstants;

public class ChampionshipDAO extends AbstractDAO<Championship> {

	private static ChampionshipDAO instance;
	public static final String tableName = "CHAMPIONSHIP";
	private static final String CHAMPIONSHIPNAME = "championshipName";
	private static final String ACTIVE = "active";
	
	protected ChampionshipDAO(Connection connection) {
		super(tableName, connection);
	}
	
	public static synchronized ChampionshipDAO getChampionshipDAOInstance(Connection connection) {
		if(instance == null) {
			instance = new ChampionshipDAO(connection);
		}
		return instance;
	}

	@Override
	protected List<Championship> mapDataIntoObject(ResultSet returnSelect) throws SQLException {
		
		List<Championship> championshipList = new ArrayList<Championship>();
		
		while(returnSelect.next()) {
			String championshipName = returnSelect.getString(CHAMPIONSHIPNAME);
			boolean active = returnSelect.getBoolean(ACTIVE);
			long id = returnSelect.getLong(ID);
			
			championshipList.add(Championship.getChampionshipFromDatabaseInfo(championshipName, id, active));
		}
		
		return championshipList;
	}

	@Override
	protected String getAllColumnValuesFromEntity(Championship entity) {
		return "(" + 
				CHAMPIONSHIPNAME + BlueSheepConstants.REGEX_COMMA + 
				ACTIVE + ")";
	}

}
