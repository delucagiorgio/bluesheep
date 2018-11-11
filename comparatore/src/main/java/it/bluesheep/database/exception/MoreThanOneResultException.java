package it.bluesheep.database.exception;

public class MoreThanOneResultException extends BlueSheepDatabaseException {

	private static final long serialVersionUID = 1L;
	
	public MoreThanOneResultException(String tableName) {
		super("More than single result excepected from table " + tableName);
	}

}
