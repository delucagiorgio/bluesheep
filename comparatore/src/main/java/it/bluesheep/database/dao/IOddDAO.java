package it.bluesheep.database.dao;

import java.sql.SQLException;
import java.util.List;

import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.database.entities.AbstractOddEntity;

public interface IOddDAO<T extends AbstractOddEntity> {
	
	public void insertMultipleRows(List<RecordOutput> recordOutput) throws SQLException;
	
	public boolean checkEmptyTable() throws SQLException;
	
	public void deleteTable() throws SQLException;

}
