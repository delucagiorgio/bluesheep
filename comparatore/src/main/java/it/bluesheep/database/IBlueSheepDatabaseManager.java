package it.bluesheep.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface IBlueSheepDatabaseManager {

	public void executeUpdate(String updateQuery, Connection connection);
	
	public boolean executeInsert(String insertQuery, Connection connection);
	
	public ResultSet executeSelect(String selectQuery, Connection connection);
	
	public ResultSet executeSelect(PreparedStatement selectQuery);

	public void executeUpdate(PreparedStatement updateQuery);
	
	public boolean executeInsert(PreparedStatement insertQuery);
}
