package it.bluesheep.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface IBlueSheepDatabaseManager {

	public boolean executeInsert(PreparedStatement ps, Connection connection);
	
	public ResultSet executeSelect(String selectQuery, Connection connection);
	
	public ResultSet executeSelect(PreparedStatement selectQuery);

	public void executeUpdate(PreparedStatement updateQuery);
	
	public boolean executeInsert(PreparedStatement insertQuery);
}
