package it.bluesheep.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;


public class BlueSheepDatabaseManager implements IBlueSheepDatabaseManager{

	private static BlueSheepDatabaseManager instance;
	private static Logger logger;
	
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
	public ResultSet executeSelect(PreparedStatement selectQuery, Connection connection) throws SQLException{
		ResultSet result = null;
		try {
			logger.debug("Executing query " + selectQuery);
			result = selectQuery.executeQuery();
		} catch (SQLException e) {
			logger.error("Error during select. Query is " + selectQuery);
			throw e;
		} 
		
		return result;
	}

	@Override
	public void executeUpdate(PreparedStatement updateQuery) throws SQLException {
		try {
			logger.debug("Executing query " + updateQuery);
			updateQuery.executeUpdate();
			logger.debug("Updated count for query is " + updateQuery.getUpdateCount());
		} catch (SQLException e) {
			logger.warn("No update performed");
			throw e;
		}
	}

	@Override
	public boolean executeInsert(PreparedStatement insertQuery) throws SQLException {
		logger.debug("Executing query " + insertQuery);
		insertQuery.executeUpdate();
		logger.info("Inserted count for query is " + insertQuery.getUpdateCount());
		return true;
	}

	@Override
	public void executeDelete(PreparedStatement deleteQuery) throws SQLException {
		try {
			logger.debug("Executing query " + deleteQuery);
			deleteQuery.executeUpdate();
			logger.debug("Delete count for query is " + deleteQuery.getUpdateCount());
		} catch (SQLException e) {
			logger.warn("No update performed");
			throw e;
		}
	}
}
