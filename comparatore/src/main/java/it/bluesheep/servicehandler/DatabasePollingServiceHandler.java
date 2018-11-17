package it.bluesheep.servicehandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import it.bluesheep.database.ConnectionPool;

public class DatabasePollingServiceHandler extends AbstractBlueSheepService {

	private static Logger logger = Logger.getLogger(DatabasePollingServiceHandler.class);
	private static DatabasePollingServiceHandler instance;
	
	@Override
	public void run() {
		Connection connection = null;
		try {
			connection = ConnectionPool.getConnection();
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery("/* ping */ SELECT 1");
			if(rs != null) {
				logger.info("DB connection OK");
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			logger.info("DB connection KO");
			connection = null;
		}
	}
	
	public static synchronized DatabasePollingServiceHandler getDatabasePollingServiceHandlerInstance() {
		if(instance == null) {
			instance = new DatabasePollingServiceHandler();
		}
		return instance;
	}
	
}
