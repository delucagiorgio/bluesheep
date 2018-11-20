package it.bluesheep.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import it.bluesheep.util.BlueSheepConstants;

// La classe che gestisce un pool di connessioni
public class ConnectionPool {

	
    private static BasicDataSource dataSource = new BasicDataSource();

	// La variabile che gestisce l'unica istanza di ConnectionPool
	private static ConnectionPool instance;
	private static Vector<Connection> connectionVector;
	private static Logger logger = Logger.getLogger(ConnectionPool.class);
	
	// Costruttore della classe ConnectionPool
	private ConnectionPool() {
        dataSource.setUrl("jdbc:mysql://86.107.98.176:3306/bluesheepUsers?autoReconnect=true&useUnicode=true&characterEncoding=utf8");
        dataSource.setUsername(BlueSheepConstants.DATABASE_USER);
        dataSource.setPassword(BlueSheepConstants.DATABASE_PASSWORD);
		connectionVector = new Vector<Connection>();
	}
	
	public static synchronized Connection getConnection() throws SQLException {
		if(instance == null) {
			instance = new ConnectionPool();
		}
		
		Connection connection = null;
		if(!connectionVector.isEmpty()) {
			connection = connectionVector.remove(0);
			
			if(connection.isClosed()) {
				connection = dataSource.getConnection();
				logger.info("New connection to DB established");
			}
		}else {
			connection = dataSource.getConnection();
			logger.info("New connection to DB established");
		}
		connection.setAutoCommit(false);
		connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		return connection;
	}
	
	public static synchronized void releaseConnection(Connection connection) throws SQLException {
		if(!connection.isClosed()) {
			logger.info("Connection relased : " + connectionVector.add(connection));
		}
	}

}
