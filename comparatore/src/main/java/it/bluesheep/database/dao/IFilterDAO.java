package it.bluesheep.database.dao;

import java.sql.Connection;
import java.util.List;

import it.bluesheep.database.entities.AbstractBlueSheepFilterEntity;
import it.bluesheep.database.exception.MoreThanOneResultException;

public interface IFilterDAO<T extends AbstractBlueSheepFilterEntity> {

	public List<T> getAllRowFromButtonText(String textButton, Connection connection);
	
	public T getSingleRowFromButtonText(String textButton, Connection connection) throws MoreThanOneResultException;
	
}