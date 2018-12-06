package it.bluesheep.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IBlueSheepDatabaseManager {

	public ResultSet executeSelect(PreparedStatement selectQuery, Connection connection) throws SQLException;

	public void executeUpdate(PreparedStatement updateQuery) throws SQLException;
	
	public boolean executeInsert(PreparedStatement insertQuery) throws SQLException;
	
	public void executeDelete(PreparedStatement ps) throws SQLException;
}
