package it.bluesheep.database.exception;

public abstract class BlueSheepDatabaseException extends Exception {

	public BlueSheepDatabaseException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
