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
//        dataSource.setUrl("jdbc:mysql://86.107.98.176:3306/bluesheepUsers?autoReconnect=true&useUnicode=true&characterEncoding=utf8");
        dataSource.setUrl("jdbc:mysql://localhost:3306/bluesheepUsers?autoReconnect=true&useUnicode=true&characterEncoding=utf8");
        dataSource.setUsername(BlueSheepConstants.DATABASE_USER);
        dataSource.setPassword(BlueSheepConstants.DATABASE_PASSWORD);
		connectionVector = new Vector<Connection>();
	}
	
	public static Connection getConnection() throws SQLException {
		return executeOperationOnConnectionVector("GET", null);
	}
	
	private static synchronized Connection executeOperationOnConnectionVector(String string, Connection conn) throws SQLException {
		if(instance == null) {
			instance = new ConnectionPool();
		}
		
		if("GET".equals(string)) {
			Connection connection = null;
			if(!connectionVector.isEmpty()) {
				connection = connectionVector.lastElement();
				connectionVector.remove(connection);
				if(connection.isClosed()) {
					connection = dataSource.getConnection();
					logger.info("New connection to DB established");
				}
			}else {
				connection = dataSource.getConnection();
				logger.info("New connection to DB established");
			}
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			return connection;
			
		}else if("RELEASE".equals(string)) {
			if(!conn.isClosed()) {
				logger.info("Connection relased : " + connectionVector.add(conn));
			}
		}else if("CLOSE".equals(string)) {
			for(Connection relConn : connectionVector) {
				relConn.close();
			}
			connectionVector = null;
		}
		
		return null;
	}

	public static void releaseConnection(Connection connection) throws SQLException {
		executeOperationOnConnectionVector("RELEASE", connection);
	}

	public static void closeAllConnection() throws SQLException {
		executeOperationOnConnectionVector("CLOSE", null);
	}

}
