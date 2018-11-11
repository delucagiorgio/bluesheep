package it.bluesheep.database.exception;

import java.sql.SQLException;

public class ConnectionPoolException extends BlueSheepDatabaseException {

	public ConnectionPoolException(SQLException e) {
		super(e.getMessage());
	}

	private static final long serialVersionUID = 1L;

}
