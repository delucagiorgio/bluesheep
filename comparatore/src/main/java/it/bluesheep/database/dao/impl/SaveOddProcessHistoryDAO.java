package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.database.ProcessStatus;
import it.bluesheep.database.entities.SaveOddProcessHistory;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.util.BlueSheepConstants;

public class SaveOddProcessHistoryDAO extends AbstractDAO<SaveOddProcessHistory> {

	private static SaveOddProcessHistoryDAO instance;
	public static final String tableName = "SAVEODDPROCESSHISTORY";
	private static final String SERVICETYPE = "serviceType";
	private static final String STATUS = "status";
	private static final String ERRORMESSAGE = "errorMessage";
	
	protected SaveOddProcessHistoryDAO(Connection connection) {
		super(tableName, connection);
	}
	
	public static synchronized SaveOddProcessHistoryDAO getSaveOddProcessHistoryDAOInstance(Connection connection) {
		if(instance == null) {
			instance = new SaveOddProcessHistoryDAO(connection);
		}
		return instance;
	}

	@Override
	protected List<SaveOddProcessHistory> mapDataIntoObject(ResultSet returnSelect) throws SQLException {
		List<SaveOddProcessHistory> processList = new ArrayList<SaveOddProcessHistory>(returnSelect.getFetchSize());
		
		while(returnSelect.next()) {
			String serviceType = returnSelect.getString(SERVICETYPE);
			String status = returnSelect.getString(STATUS);
			String errorMessage = returnSelect.getString(ERRORMESSAGE);
			long id = returnSelect.getLong(ID);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			Service service = Service.getServiceFromString(serviceType);
			ProcessStatus processStatus = ProcessStatus.getProcessStatusFromCode(status);
			
			processList.add(new SaveOddProcessHistory(service, processStatus, errorMessage, id ,createTime, updateTime));
			
		}
		
		return processList;
	}

	@Override
	protected String getAllColumnValuesFromEntity(SaveOddProcessHistory entity) {
		return "("
				+ "'" + entity.getServiceType() + "'" + BlueSheepConstants.REGEX_COMMA
				+ "'" + entity.getStatus() + "'"  + BlueSheepConstants.REGEX_COMMA
				+ "'" + entity.getErrorMessage() + "'" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA 
				+ "?)";
	}

	public boolean stillRunningProcess(Service service) throws SQLException {
		String query  = "SELECT EXISTS (" + getBasicSelectQuery() 
				+ WHERE + SERVICETYPE + " = '" 
				+ service.getCode() + "'" + AND 
				+ STATUS + " = '" + ProcessStatus.RUNNING.getCode() + "') as emptyTable";
		
		Statement st = connection.createStatement();
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()) {
			return rs.getBoolean("emptyTable");
		}
		
		return false;
	}

	public void updateLastRun(Service service, Exception e) {
		String query = getBasicSelectQuery() 
				+ WHERE + SERVICETYPE + " = '" + service.getCode() + "'"
				+ AND + STATUS + " = '" + ProcessStatus.RUNNING.getCode() + "'";
		
		try {
			SaveOddProcessHistory lastRunInError = getSingleResult(getMappedObjectBySelect(query));

			if(e == null) {
				lastRunInError.setStatus(ProcessStatus.COMPLETED.getCode());
			}else{
				lastRunInError.setStatus(ProcessStatus.ERROR.getCode());
				lastRunInError.setErrorMessage(e.getMessage());
			}
			String updateQuery = UPDATE + tableName 
					+ SET + ERRORMESSAGE + " = ?, " 
					+ STATUS + " = ? " + WHERE + ID + " = " 
					+ lastRunInError.getId();
			
			PreparedStatement ps = connection.prepareStatement(updateQuery);
			ps.setString(1, lastRunInError.getErrorMessage());
			ps.setString(2, lastRunInError.getStatus());
			
			int updatedCount = ps.executeUpdate();
			
			logger.info("Updated process count for service " + service + " is " + updatedCount);
			
		} catch (MoreThanOneResultException | SQLException e1) {
			logger.error(e1.getMessage(), e1);
		}
		
	}

}
