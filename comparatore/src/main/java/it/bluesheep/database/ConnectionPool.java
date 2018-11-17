package it.bluesheep.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

import it.bluesheep.util.BlueSheepConstants;

// La classe che gestisce un pool di connessioni
public class ConnectionPool {

	
    private static BasicDataSource dataSource = new BasicDataSource();

	// La variabile che gestisce l'unica istanza di ConnectionPool
	private static ConnectionPool instance;
	private static Connection connection;
	
	// Costruttore della classe ConnectionPool
	private ConnectionPool() {
        dataSource.setUrl("jdbc:mysql://86.107.98.176:3306/bluesheepUsers?autoReconnect=true&useUnicode=true&characterEncoding=utf8");
        dataSource.setUsername(BlueSheepConstants.DATABASE_USER);
        dataSource.setPassword(BlueSheepConstants.DATABASE_PASSWORD);
	}
	
	public static synchronized Connection getConnection() throws SQLException {
		if(instance == null) {
			instance = new ConnectionPool();
		}
		
		if(connection == null) {
	        connection = dataSource.getConnection();
	        connection.setAutoCommit(false);
	        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		}
		return connection;
	}
	
	public static void closeConnectionToDB() throws SQLException {
		connection.close();
	}

}
