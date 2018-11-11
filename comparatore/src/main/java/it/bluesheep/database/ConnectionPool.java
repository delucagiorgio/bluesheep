package it.bluesheep.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;

import it.bluesheep.database.exception.ConnectionPoolException;
import it.bluesheep.util.BlueSheepConstants;

// La classe che gestisce un pool di connessioni
public class ConnectionPool {

	// La variabile che gestisce l'unica istanza di ConnectionPool
	private static ConnectionPool connectionPool = null;
	private static Logger logger = Logger.getLogger(ConnectionPool.class);
	
	private Vector<Connection> freeConnections; // La coda di connessioni libere
	private String dbUrl; // Il nome del database
	private String dbLogin; // Il login per il database
	private String dbPassword; // La password di accesso al database

	// Costruttore della classe ConnectionPool
	private ConnectionPool() throws ConnectionPoolException {
		freeConnections = new Vector<Connection>(); // Costruisce la coda delle connessioni libere
		loadParameters(); // Carica I parametric per l'accesso alla base di dati
//		loadDriver(); // Carica il driver del database
	}

	// Funzione privata che carica i parametri per l'accesso al database
	private void loadParameters() {
		dbUrl = "jdbc:mysql:"
				+ "//86.107.98.176:3306"
				+ "/bluesheepUsers?"
				+ "useSSL=false"
				+ "&autoReconnect=true"
				+ "&useUnicode=yes"
				+ "&connectTimeout=0"
				+ "&socketTimeout=0"
				+ "&characterEncoding=UTF-8"; // Url per un database locale
		dbLogin = BlueSheepConstants.DATABASE_USER; // Login della base di dati
		dbPassword = BlueSheepConstants.DATABASE_PASSWORD; // Password per l'accesso al database
	}

//	// Funzione privata che carica il driver per l'accesso al database.
//	// In caso di errore durante il caricamento del driver solleva un'eccezione.
//	private void loadDriver() throws ConnectionPoolException {
//		try {
//			java.lang.Class.forName(dbDriver + "?user=" + dbLogin + "&password=" + dbPassword);
//		} catch (Exception e) {
//			throw new ConnectionPoolException();
//		}
//	}

	public static synchronized ConnectionPool getConnectionPool() throws ConnectionPoolException {
		if (connectionPool == null) {
			connectionPool = new ConnectionPool();
		}
		return connectionPool;
	}

	// Il metodo getConnection restituisce una connessione libera prelevandola
	// dalla coda freeConnections oppure se non ci sono connessioni disponibili
	// creandone una nuova con una chiamata a newConnection
	@SuppressWarnings("resource")
	public synchronized Connection getConnection() throws ConnectionPoolException {
		Connection con = null;

		if (freeConnections.size() > 0) { // Se la coda delle connessioni libere non Ë vuota
			con = (Connection) freeConnections.firstElement(); // Preleva il primo elemento
			freeConnections.removeElementAt(0); // e lo cancella dalla coda
			try {
				if (con.isClosed()) { // Verifica se la connessione non Ë pi˘ valida
					con = getConnection(); // Richiama getConnection ricorsivamente
				}
			} catch (SQLException e) { // Se c'Ë un errore
				con = getConnection();// richiama getConnection ricorsivamente
//				logger.error(e.getMessage(), e);
			}
		} else { // se la coda delle connessioni libere Ë vuota
			con = newConnection(); // crea una nuova connessione
		}
		return con; // restituisce una connessione valida
	}

	// Il metodo newConnection restituisce una nuova connessione
	private Connection newConnection() throws ConnectionPoolException {
		Connection con = null;

		try {
			con = DriverManager.getConnection(dbUrl + "&user=" + dbLogin + "&password=" + dbPassword); // crea la connessione
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			logger.info("Connection to DB established");
		} catch (SQLException e) { // in caso di errore
			throw new ConnectionPoolException(e); // solleva un'eccezione
		}
		return con; // restituisce la nuova connessione
	}

	// Il metodo releaseConnection rilascia una connessione inserendola
	// nella coda delle connessioni libere
	public synchronized void releaseConnection(Connection con) {
		freeConnections.add(con); // Inserisce la connessione nella coda
	}
}
