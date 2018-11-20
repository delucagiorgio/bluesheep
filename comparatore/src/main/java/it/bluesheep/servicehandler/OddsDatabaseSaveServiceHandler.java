package it.bluesheep.servicehandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.database.ConnectionPool;
import it.bluesheep.database.ProcessStatus;
import it.bluesheep.database.dao.IOddDAO;
import it.bluesheep.database.dao.OddDAOFactory;
import it.bluesheep.database.dao.impl.SaveOddProcessHistoryDAO;
import it.bluesheep.database.entities.AbstractOddEntity;
import it.bluesheep.database.entities.SaveOddProcessHistory;

public class OddsDatabaseSaveServiceHandler extends AbstractBlueSheepService {

	private static Logger logger = Logger.getLogger(OddsDatabaseSaveServiceHandler.class);
	private List<RecordOutput> recordOutputList;
	private Service service;
	
	public OddsDatabaseSaveServiceHandler(List<RecordOutput> recordOutputList, Service service) {
		this.recordOutputList = recordOutputList;
		this.service = service;
	}
	
	@Override
	public void run() {

			
		Connection connection = null;
		SaveOddProcessHistoryDAO dao = null;
		try {			
			connection = ConnectionPool.getConnection();
			dao = SaveOddProcessHistoryDAO.getSaveOddProcessHistoryDAOInstance(connection);

			dao.insertRow(new SaveOddProcessHistory(service, ProcessStatus.RUNNING, null, 0, new Timestamp(System.currentTimeMillis()), null));
			connection.commit();
			long startTime = System.currentTimeMillis();
			saveRecordsOnDatabase(connection);
			
			logger.info("Execution completed in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds");
			
			dao.updateLastRun(service , null);
			connection.commit();
			
			
			if(!dao.stillRunningProcess(Service.USERPREFNOTIFICATION_SERVICE)) {
				BlueSheepServiceHandlerManager.executor.submit(new UserPreferenceNotificationServiceHandler());
				logger.info("Scanning odds for service " + service + " started");
			}else {
				logger.warn("Not executing " + service + " because previous one is still running");
			}

			ConnectionPool.releaseConnection(connection);
		
		} catch (Exception e) {
			try {
				if(connection != null) {
					connection.rollback();
					dao.updateLastRun(service , e);
					connection.commit();
					ConnectionPool.releaseConnection(connection);
				}
			} catch (SQLException e1) {
				logger.error(e1.getMessage(), e1);
			}
			logger.error(e.getMessage(), e);

		}
		
	}
	
	private void saveRecordsOnDatabase(Connection connection) throws SQLException {
		IOddDAO<? extends AbstractOddEntity> dao = OddDAOFactory.getCorrectDAOByService(service, connection);
		if(!dao.checkEmptyTable()) {
			dao.deleteTable();
		}
		
		if(recordOutputList.isEmpty()) {
			logger.error("No insertion performed");
			connection.rollback();
			SaveOddProcessHistoryDAO.getSaveOddProcessHistoryDAOInstance(connection).updateLastRun(service, null);
			ConnectionPool.releaseConnection(connection);
			return;
		}

		dao.insertMultipleRows(recordOutputList);
			
		logger.info("Rows successfully inserted");
	}

}
