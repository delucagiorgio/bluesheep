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
import it.bluesheep.util.BlueSheepConstants;

public class SaveOddProcessHistoryDAO extends AbstractDAO<SaveOddProcessHistory> {

	private static SaveOddProcessHistoryDAO instance;
	public static final String tableName = "SAVEODDPROCESSHISTORY";
	private static final String SERVICETYPE = "serviceType";
	private static final String STATUS = "status";
	private static final String ERRORMESSAGE = "errorMessage";
	
	private SaveOddProcessHistoryDAO() {
		super(tableName);
	}
	
	public static synchronized SaveOddProcessHistoryDAO getSaveOddProcessHistoryDAOInstance() {
		if(instance == null) {
			instance = new SaveOddProcessHistoryDAO();
		}
		return instance;
	}

	@Override
	protected List<SaveOddProcessHistory> mapDataIntoObject(ResultSet returnSelect, Connection connection) throws SQLException {
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

	public boolean stillRunningProcess(Service service, Connection connection) throws SQLException {
		String query  = "SELECT EXISTS (" + getBasicSelectQuery() 
				+ WHERE + SERVICETYPE + " = '" 
				+ service.getCode() + "'" + AND 
				+ STATUS + " = '" + ProcessStatus.RUNNING.getCode() + "') as emptyTable";
		
		Statement st = connection.createStatement();
		boolean returnValue = false;
		
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()) {
			returnValue = rs.getBoolean("emptyTable");
		}
		
		rs.close();
		st.close();
		
		return returnValue;
	}

	public void updateLastRun(Service service, Exception e, Connection connection) {
		String query = getBasicSelectQuery() 
				+ WHERE + SERVICETYPE + " = '" + service.getCode() + "'"
				+ AND + STATUS + " = '" + ProcessStatus.RUNNING.getCode() + "'";
		
		try {
			List<SaveOddProcessHistory> lastRunList = getMappedObjectBySelect(query, connection);
			for(SaveOddProcessHistory lastRun : lastRunList) {
				if(lastRun != null) {
					if(e == null) {
						lastRun.setStatus(ProcessStatus.COMPLETED.getCode());
					}else{
						lastRun.setStatus(ProcessStatus.ERROR.getCode());
						lastRun.setErrorMessage(e.getMessage());
					}
					String updateQuery = UPDATE + tableName 
							+ SET + ERRORMESSAGE + " = ?, " 
							+ STATUS + " = ? " + WHERE + ID + " = " 
							+ lastRun.getId();
					
					PreparedStatement ps = connection.prepareStatement(updateQuery);
					ps.setString(1, lastRun.getErrorMessage());
					ps.setString(2, lastRun.getStatus());
					
					int updatedCount = ps.executeUpdate();
					
					ps.close();
					logger.info("Updated process for service " + service + " with status " + lastRun.getStatus() + ": update count " + updatedCount);
				}
			}
		} catch (SQLException e1) {
			logger.error(e1.getMessage(), e1);
		}
	}
	
	@Override
	public boolean insertRow(SaveOddProcessHistory entity, Connection connection) throws SQLException {
		logger.info("Starting process " + entity.getServiceType());
		return super.insertRow(entity, connection);
	}

}
