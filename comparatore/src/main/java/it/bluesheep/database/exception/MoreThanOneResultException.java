package it.bluesheep.database.exception;

public class MoreThanOneResultException extends BlueSheepDatabaseException {

	private static final long serialVersionUID = 1L;
	
	public MoreThanOneResultException(String query) {
		super("More than single result excepected from query " + query);
	}

}
