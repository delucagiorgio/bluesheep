package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import it.bluesheep.database.entities.AbstractOddEntity;

public abstract class AbstractOddDAO<T extends AbstractOddEntity> extends AbstractDAO<T> {

	protected AbstractOddDAO(String tableName) {
		super(tableName);
	}

	@Override
	protected abstract List<T> mapDataIntoObject(ResultSet returnSelect, Connection connection) throws SQLException;

	@Override
	protected abstract String getAllColumnValuesFromEntity(T entity);

}
