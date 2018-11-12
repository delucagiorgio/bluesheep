package it.bluesheep.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;


public class BlueSheepDatabaseManager implements IBlueSheepDatabaseManager{

	private static BlueSheepDatabaseManager instance;
	private static Logger logger;
	private boolean status;
	
	private BlueSheepDatabaseManager() {
		logger = Logger.getLogger(BlueSheepDatabaseManager.class);
	}

	public static synchronized BlueSheepDatabaseManager getBlueSheepDatabaseManagerInstance() {
		if(instance == null) {
			instance = new BlueSheepDatabaseManager();
		}
		return instance;
	}
	
	@Override
	public ResultSet executeSelect(String selectQuery, Connection connection){
		ResultSet result = null;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			result = stmt.executeQuery(selectQuery);
		} catch (SQLException e) {
			logger.error("Error during select. Query is " + selectQuery);
			logger.error(e.getMessage(), e);
		} 
		
		return result;
	}
	
	@Override
	public ResultSet executeSelect(PreparedStatement selectQuery){
		ResultSet result = null;
		try {
			result = selectQuery.executeQuery();
		} catch (SQLException e) {
			logger.error("Error during select. Query is " + selectQuery);
			logger.error(e.getMessage(), e);
		} 
		
		return result;
	}
	
	@Override
	public boolean executeInsert(PreparedStatement ps, Connection connection) {
		try {
			int countInsert = ps.executeUpdate();		
			logger.info("Insert count for query is " + countInsert);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
	
	public boolean getStatus() {
		return status ;
	}

	@Override
	public void executeUpdate(PreparedStatement updateQuery) {
		try {
			updateQuery.executeUpdate();
			logger.info("Updated count for query is " + updateQuery.getUpdateCount());
		} catch (SQLException e) {
			logger.warn("No update performed");
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean executeInsert(PreparedStatement selectQuery) {
		try {
			selectQuery.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
	
}
