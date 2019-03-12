package it.bluesheep.database.exception;

public class PendingExecutionException extends BlueSheepDatabaseException {

	private static final long serialVersionUID = 1L;

	public PendingExecutionException() {
		super("Pending execution. Restore status process");
	}

}
