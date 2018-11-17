package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.dao.IFilterDAO;
import it.bluesheep.database.entities.Championship;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.util.BlueSheepConstants;

public class ChampionshipDAO extends AbstractDAO<Championship> implements IFilterDAO<Championship> {

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
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			championshipList.add(Championship.getChampionshipFromDatabaseInfo(championshipName, id, active, createTime, updateTime));
		}
		
		return championshipList;
	}

	@Override
	protected String getAllColumnValuesFromEntity(Championship entity) {
		return "(" + 
				CHAMPIONSHIPNAME + BlueSheepConstants.REGEX_COMMA + 
				ACTIVE  + BlueSheepConstants.REGEX_COMMA +
				"?" + BlueSheepConstants.REGEX_COMMA +
				"?" +")";
	}

	@Override
	public List<Championship> getAllRowFromButtonText(String textButton) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Championship getSingleRowFromButtonText(String textButton) throws MoreThanOneResultException {
		// TODO Auto-generated method stub
		return null;
	}

}
