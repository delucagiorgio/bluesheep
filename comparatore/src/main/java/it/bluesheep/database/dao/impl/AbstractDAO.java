package it.bluesheep.database.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import it.bluesheep.database.BlueSheepDatabaseManager;
import it.bluesheep.database.entities.AbstractBlueSheepEntity;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.util.BlueSheepConstants;

public abstract class AbstractDAO<T extends AbstractBlueSheepEntity> {
	
	protected String tableName;
	protected static String ID = "id";
	protected static final String WHERE = " WHERE ";
	protected static final String SELECT = " SELECT ";
	protected static final String SELECT_ALL = " SELECT * ";
	protected static final String FROM = " FROM ";
	protected static final String JOIN = " JOIN ";
	protected static final String ON = " ON ";
	protected static final String IS = " IS ";
	protected static final String INSERT = " INSERT INTO ";
	protected static final String VALUES = " VALUES ";
	protected static final String DELETE = " DELETE FROM ";
	protected static final String UPDATE = " UPDATE ";
	protected static final String AND = " AND ";
	protected static final String OR = " OR ";
	protected static final String SET = " SET ";
	protected static final String LIKE = " LIKE ";
	protected static final String ORDERBY = " ORDER BY ";
	protected static final String ASC = " ASC ";
	protected static final String DESC = " DESC ";
	protected static final String GREAT = " > ";
	protected static final String LESS = " < ";
	protected static final String GREAT_EQ = " => ";
	protected static final String LESS_EQ = " <= ";
	protected static Logger logger = Logger.getLogger(AbstractDAO.class);
	protected Connection connection;
	private Class<T> type;

	
	@SuppressWarnings("unchecked")
	protected AbstractDAO(String tableName, Connection connection) {
		this.connection = connection;
		this.tableName = tableName;
		this.type = (Class<T>) ((ParameterizedType) getClass()
	                .getGenericSuperclass()).getActualTypeArguments()[0];

	}

    public Class<T> getRuntimeType() {
    	return this.type;
    }	
	
	protected String getBasicSelectQuery() {
		return SELECT_ALL + 
				FROM + tableName;
	}
	
	public List<T> getAllRows(){
		
		String queryStatement = SELECT_ALL + FROM  + tableName + ";";
		List<T> returnList = getMappedObjectBySelect(queryStatement);
		
		return returnList;
	}
	
	private ResultSet getResultSelectFromQuery(String query) {
		return BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeSelect(query, connection);
	}
	
	private ResultSet getResultSelectFromQuery(PreparedStatement query) {
		return BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeSelect(query);
	}
	
	protected abstract List<T> mapDataIntoObject(ResultSet returnSelect) throws SQLException;
	
	public List<T> getMappedObjectBySelect(String query){

		List<T> returnList = null;

		ResultSet returnSelect = getResultSelectFromQuery(query);
		if(returnSelect != null) {
			try {
				returnList = mapDataIntoObject(returnSelect);
			} catch (SQLException e) {
				logger.error("Problem during mapping data into objects");
				logger.error(e.getMessage(), e);
			}
		}else {
			logger.warn("Select query " + query + " has returned no value");
		}
		
		return returnList;
	}
	
	public List<T> getMappedObjectBySelect(PreparedStatement query){

		List<T> returnList = null;

		ResultSet returnSelect = getResultSelectFromQuery(query);
		if(returnSelect != null) {
			returnList = new ArrayList<T>();
			try {
				returnList = mapDataIntoObject(returnSelect);
			} catch (SQLException e) {
				logger.error("Problem during mapping data into objects");
				logger.error(e.getMessage(), e);
			}
		}else {
			logger.warn("Select query " + query + " has returned no value");
		}
		
		return returnList;
	}
	
	
	
	protected abstract String getAllColumnValuesFromEntity(T entity);
	
	protected String getAllColumnLabels() {
		String str = null;
		
		for(Field field : getRuntimeType().getDeclaredFields()) {
			if(str == null) {
				str = "(";
			}else {
				str = str + BlueSheepConstants.REGEX_COMMA;
			}
			str = str + field.getName();
		}
		
		str = str + ")";
		
		return str;
	}
	
	public boolean insertRow(T entity) {
		
		String query = getinsertBaseTableNameQuery() + getAllColumnValuesFromEntity(entity);
		
		
		logger.info("Executing insert query " + query);
		return BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeInsert(query, connection);
	}
	
	protected String getinsertBaseTableNameQuery() {
		return INSERT + tableName + getAllColumnLabels() + VALUES;
	}
	
	public T getEntityById(Long entityId) {
		
		T returnEntity = null;
		String query = getBasicSelectQuery() + WHERE + ID + " = " + entityId.longValue();
		List<T> returnSet = getMappedObjectBySelect(query);
		
		if(returnSet != null && returnSet.size() == 1) {
			returnEntity = returnSet.get(0);
		}
		
		return returnEntity;
	}
	

	protected T getSingleResult(List<T> mappedObjectBySelect) throws MoreThanOneResultException {
		if(mappedObjectBySelect != null && mappedObjectBySelect.size() == 1) {
			return mappedObjectBySelect.get(0);
		}else if(mappedObjectBySelect == null || mappedObjectBySelect.isEmpty()) {
			return null;
		}
		
		throw new MoreThanOneResultException(tableName);
	}
}
