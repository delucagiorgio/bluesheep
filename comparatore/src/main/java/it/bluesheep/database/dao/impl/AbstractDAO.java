package it.bluesheep.database.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import it.bluesheep.database.BlueSheepDatabaseManager;
import it.bluesheep.database.entities.AbstractBlueSheepEntity;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.util.BlueSheepConstants;

public abstract class AbstractDAO<T extends AbstractBlueSheepEntity> {
	
	protected String tableName;
	protected static final String ID = "id";
	protected static final String CREATETIME = "createTimestamp";
	protected static final String UPDATETIME = "updateTimestamp";
	protected static final String WHERE = " WHERE ";
	protected static final String SELECT = " SELECT ";
	protected static final String SELECT_ALL = " SELECT * ";
	protected static final String FROM = " FROM ";
	protected static final String JOIN = " JOIN ";
	protected static final String ON = " ON ";
	protected static final String IS = " IS ";
	protected static final String NOT = " NOT ";
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
	private Class<T> type;

	
	@SuppressWarnings("unchecked")
	protected AbstractDAO(String tableName) {
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
	
	public List<T> getAllRows(Connection connection) throws SQLException{
		
		String queryStatement = SELECT_ALL + FROM  + tableName + ";";
		List<T> returnList = getMappedObjectBySelect(queryStatement, connection);
		
		return returnList;
	}
	
	public List<T> getAllActiveRows(Connection connection) throws SQLException{
		
		String queryStatement = getBasicSelectQuery() + WHERE + "active" + IS + true + ";";
		List<T> returnList = getMappedObjectBySelect(queryStatement, connection);
		
		return returnList;
	}
	
	private ResultSet getResultSelectFromQuery(String query, Connection connection) throws SQLException {
		return BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeSelect(query, connection);
	}
	
	private ResultSet getResultSelectFromQuery(PreparedStatement query, Connection connection) throws SQLException {
		return BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeSelect(query, connection);
	}
	
	/**
	 * GD - 16/11/18
	 * Mappa gli oggetti rispetto alle righe tornate dalla query
	 * @param returnSelect le righe della query 
	 * @return gli oggetti mappati
	 * @throws SQLException
	 */
	protected abstract List<T> mapDataIntoObject(ResultSet returnSelect, Connection connection) throws SQLException;
	
	public List<T> getMappedObjectBySelect(String query, Connection connection) throws SQLException{

		List<T> returnList = null;
		ResultSet returnSelect = getResultSelectFromQuery(query, connection);
		
		if(returnSelect != null) {
			returnList = mapDataIntoObject(returnSelect, connection);
		}else {
			logger.warn("Select query " + query + " has returned no value");
		}
		
		return returnList;
	}
	
	/**
	 * GD - 16/11/18
	 * Mappa gli oggetti rispetto alle righe tornate dalla query
	 * @param query la query da eseguire
	 * @return gli oggetti mappati
	 * @throws SQLException 
	 */
	public List<T> getMappedObjectBySelect(PreparedStatement query, Connection connection) throws SQLException{

		List<T> returnList = null;

		ResultSet returnSelect = getResultSelectFromQuery(query, connection);
		if(returnSelect != null) {
			returnList = new ArrayList<T>();
			returnList = mapDataIntoObject(returnSelect, connection);
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
		
		str = str + BlueSheepConstants.REGEX_COMMA + CREATETIME + BlueSheepConstants.REGEX_COMMA + UPDATETIME +")";
		
		return str;
	}
	
	public boolean insertRow(T entity, Connection connection) throws SQLException {
		
		String query = getinsertBaseTableNameQuery() + getAllColumnValuesFromEntity(entity);
		
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setTimestamp(1, entity.getCreateTime());
		ps.setTimestamp(2, entity.getUpdateTime());
		
		logger.info("Executing insert query " + query);
		return BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().executeInsert(ps);
	}
	
	protected String getinsertBaseTableNameQuery() {
		return INSERT + tableName + getAllColumnLabels() + VALUES;
	}
	
	public T getEntityById(Long entityId, Connection connection) throws SQLException {
		
		T returnEntity = null;
		String query = getBasicSelectQuery() + WHERE + ID + " = " + entityId.longValue();
		List<T> returnSet = getMappedObjectBySelect(query, connection);
		
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
	
	protected Timestamp getTimestampFromResultSet(ResultSet rs, String columnLabel) throws SQLException {
		return rs.getTimestamp(columnLabel);
	}
}
