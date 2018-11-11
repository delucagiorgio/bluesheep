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
	
//	public Connection connectToDatabase() {
//		status = false;
//		try {
//			if(connection == null) {
//				connection = DriverManager
//				        .getConnection("jdbc:mysql:"
//				        		+ "//86.107.98.176:3306"
//				        		+ "/bluesheepUsers?"
//				        		+ "useSSL=false" 
//				        		+ "&autoReconnect=true" 
//				        		+ "&useUnicode=yes" 
//				        		+ "&connectTimeout=0"
//				        		+ "&socketTimeout=0"
//				        		+ "&characterEncoding=UTF-8"
//				                + "&user=" 
//				        		+ BlueSheepConstants.DATABASE_USER
//				        		+ "&password=" 
//				        		+ BlueSheepConstants.DATABASE_PASSWORD);
//				connection.createStatement().execute("USE bluesheepUsers");
//				connection.setAutoCommit(false);
//				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
//				logger.info("Connected successfully to database of USERS");
//				status = true;
//			}
//        }catch(Exception e) {
//        	logger.error(e.getMessage(), e);
//        }
//		return connection;
//	}

	public static synchronized BlueSheepDatabaseManager getBlueSheepDatabaseManagerInstance() {
		if(instance == null) {
			instance = new BlueSheepDatabaseManager();
		}
		return instance;
	}
	
	@Override
	public void executeUpdate(String updateQuery, Connection connection) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(updateQuery);
			logger.info("Updated count for query is " + statement.getUpdateCount());
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}

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
	public boolean executeInsert(String insertQuery, Connection connection) {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(insertQuery);
			logger.info("Updated count for query is " + stmt.getUpdateCount());
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
