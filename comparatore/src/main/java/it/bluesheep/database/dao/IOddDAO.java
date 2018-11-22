package it.bluesheep.database.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.database.entities.AbstractOddEntity;

public interface IOddDAO<T extends AbstractOddEntity> {
	
	public void insertMultipleRows(List<RecordOutput> recordOutput, Connection connection) throws SQLException;
	
	public boolean checkEmptyTable(Connection connection) throws SQLException;
	
	public void deleteTable(Connection connection) throws SQLException;

}
