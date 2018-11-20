package it.bluesheep.servicehandler;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

import it.bluesheep.database.ConnectionPool;
import it.bluesheep.util.BlueSheepConstants;

public class BlueSheepUserUpdateServiceHandler extends AbstractBlueSheepService {

	private static Logger logger = Logger.getLogger(BlueSheepUserUpdateServiceHandler.class);
	private long startTime;
	private Connection connection;
	
	@Override
	public void run() {
		try {

			startTime = System.currentTimeMillis();
			connection = ConnectionPool.getConnection();
			logger.info("Starting user information update from Bluesheep");
			
			updateDabataseInformation();
			
			connection.commit();
		ConnectionPool.releaseConnection(connection);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			try {
				connection.rollback();
				ConnectionPool.releaseConnection(connection);
			} catch (SQLException e1) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}

	private void updateDabataseInformation() {
		URL url;
		HttpsURLConnection con;
		String result = null;
		try {
			String https_url = "https://www.bluesheep.it/ws/users/?psswd=" + BlueSheepConstants.WS_USERS_PWD;

//			result = readUrl(https_url);
			logger.info("Finished");
			
			logger.info(result);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
