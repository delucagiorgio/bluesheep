package it.bluesheep.servicehandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.database.ConnectionPool;
import it.bluesheep.database.ProcessStatus;
import it.bluesheep.database.dao.impl.BlueSheepUserInfoDAO;
import it.bluesheep.database.dao.impl.SaveOddProcessHistoryDAO;
import it.bluesheep.database.dao.impl.TelegramUserDAO;
import it.bluesheep.database.entities.BlueSheepUserInfo;
import it.bluesheep.database.entities.SaveOddProcessHistory;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.util.BlueSheepConstants;

public class BlueSheepUserUpdateServiceHandler extends AbstractBlueSheepService {

	private static Logger logger = Logger.getLogger(BlueSheepUserUpdateServiceHandler.class);
	private long startTime;
	private Connection connection;
	private static final String REG_GROUP = "Registrati paganti";
	private static final String SU_GROUP = "Super Users";
	private static final String MANAGER_GROUP = "Manager";
	private static final String GROUP_LAB = "gruppoUtente";
	private static final String TELEGRAM_LAB = "telegram";
	private static final String BS_USERNAME_LAB = "username";
	
	@Override
	public void run() {
		SaveOddProcessHistoryDAO dao = null;
		
		try {

			startTime = System.currentTimeMillis();
			connection = ConnectionPool.getConnection();
			logger.info("Starting user information update from Bluesheep");
			dao = SaveOddProcessHistoryDAO.getSaveOddProcessHistoryDAOInstance();
			
			dao.insertRow(new SaveOddProcessHistory(Service.UPDATE_DB_BLUESHEEP_USER, ProcessStatus.RUNNING, null, 0, new Timestamp(System.currentTimeMillis()), null), connection);
			connection.commit();

			//AGGIORNO I DATI DA BLUESHEEP
			updateDabataseInformation();
			
			dao.updateLastRun(Service.UPDATE_DB_BLUESHEEP_USER, null, connection);
			connection.commit();
			
			dao.insertRow(new SaveOddProcessHistory(Service.UPDATE_DB_NOTIFICATION_USER, ProcessStatus.RUNNING, null, 0, new Timestamp(System.currentTimeMillis()), null), connection);
			connection.commit();

			//AGGIORNO LE TABELLE DELLE NOTIFICHE DEL BOT
			try {
				updateNotificationUserDabataseInformation();
			}catch(Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
			dao.updateLastRun(Service.UPDATE_DB_NOTIFICATION_USER, null, connection);
			connection.commit();
			
			
			ConnectionPool.releaseConnection(connection);
			
			logger.info("Execution user information update from Bluesheep completed in " + ((System.currentTimeMillis() - startTime) / 1000L) + " seconds");
		
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			try {
				connection.rollback();
				dao.updateLastRun(Service.UPDATE_DB_BLUESHEEP_USER, e, connection);
				dao.updateLastRun(Service.UPDATE_DB_NOTIFICATION_USER, e, connection);
				connection.commit();
				ConnectionPool.releaseConnection(connection);
			} catch (SQLException e1) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}

	private void updateNotificationUserDabataseInformation() throws SQLException {
		
		TelegramUserDAO userDAO = TelegramUserDAO.getBlueSheepTelegramUserDAOInstance();
		BlueSheepUserInfoDAO bsUserDAO = BlueSheepUserInfoDAO.getBlueSheepUserInfoDAOInstance();

		List<TelegramUser> activeUsers = userDAO.getActiveUsers(connection);
		List<TelegramUser> inactiveUsers = userDAO.getInactiveUsers(connection);
		List<BlueSheepUserInfo> bluesheepUserList = bsUserDAO.getAllRows(connection);
		
		if(bluesheepUserList != null && !bluesheepUserList.isEmpty()) {
			
			//Lista degli utenti da disabilitare
			List<TelegramUser> toBeDisabledList = getToBeDisabled(activeUsers, bluesheepUserList);
			
			if(!toBeDisabledList.isEmpty()) {
				userDAO.disableTelegramUserFromList(toBeDisabledList, connection);
			}
			
			//Lista degli utenti da abilitare
			List<TelegramUser> toBeEnabledList = getToBeEnabled(inactiveUsers, bluesheepUserList);

			if(!toBeEnabledList.isEmpty()) {
				userDAO.enableTelegramUserFromList(toBeEnabledList, connection);
			}
			
			//Lista nuovi utenti
			List<TelegramUser> toBeAddList = getToBeAdd(activeUsers, inactiveUsers, bluesheepUserList);
			if(!toBeAddList.isEmpty()) {
				userDAO.insertMultipleRows(toBeAddList, true, connection);
			}
			
		}else {
			logger.warn("No user from Bluesheep found");
		}
		
	}

	private List<TelegramUser> getToBeAdd(List<TelegramUser> activeUsers, List<TelegramUser> inactiveUsers, List<BlueSheepUserInfo> bluesheepUserList) throws SQLException {
		
		List<TelegramUser> returnList = new ArrayList<TelegramUser>();
		Set<String> bluesheepUsernameAlreadyIn = new HashSet<String>();
		
		for(TelegramUser inactiveUser : inactiveUsers) {
			bluesheepUsernameAlreadyIn.add(inactiveUser.getBluesheepUsername());
		}
		
		for(TelegramUser activeUser : activeUsers) {
			bluesheepUsernameAlreadyIn.add(activeUser.getBluesheepUsername());
		}
		
		for(BlueSheepUserInfo bsUI : bluesheepUserList) {
			if(!bluesheepUsernameAlreadyIn.contains(bsUI.getBluesheepUsername()) 
					&& bsUI.getTelegramUsername() != null 
					&& !bsUI.getTelegramUsername().isEmpty()) {
				returnList.add(TelegramUser.getBlueSheepTelegramUserFromBlueSheepInfo(bsUI, true));
			}
		}
		
		return returnList;
	}

	private List<TelegramUser> getToBeEnabled(List<TelegramUser> inactiveUsers, List<BlueSheepUserInfo> bluesheepUserList) {
		
		List<TelegramUser> returnList = new ArrayList<TelegramUser>();
		
		for(TelegramUser inactiveUser : inactiveUsers) {
			boolean found = false;
			for(BlueSheepUserInfo bluesheepUser : bluesheepUserList) {
				if(bluesheepUser.getBluesheepUsername().equals(inactiveUser.getBluesheepUsername())) {
					found = true;
					break;
				}
			}
			if(found) {
				returnList.add(inactiveUser);
			}
		}
		
		return returnList;
	}

	private List<TelegramUser> getToBeDisabled(List<TelegramUser> activeUsers, List<BlueSheepUserInfo> bluesheepUserList) {
		
		List<TelegramUser> returnList = new ArrayList<TelegramUser>();
		
		for(TelegramUser activeUser : activeUsers) {
			boolean found = false;
			for(BlueSheepUserInfo bluesheepUser : bluesheepUserList) {
				if(bluesheepUser.getBluesheepUsername().equals(activeUser.getBluesheepUsername())) {
					found = true;
					break;
				}
			}
			if(!found) {
				returnList.add(activeUser);
			}
		}
		
		return returnList;
	}

	private void updateDabataseInformation() throws SQLException {
		
		List<BlueSheepUserInfo> usersList = getDataFromBluesheepWS();
		
		if(usersList != null && !usersList.isEmpty()) {
			
			BlueSheepUserInfoDAO dao = BlueSheepUserInfoDAO.getBlueSheepUserInfoDAOInstance();
			dao.deleteTable(connection);
			dao.insertMultipleRows(usersList, connection);
		}
	}
	
	private List<BlueSheepUserInfo> getDataFromBluesheepWS() {
		URL url;
		HttpsURLConnection con;
		String result = null;
		List<BlueSheepUserInfo> returnList = null;
		try {
			String https_url = "https://www.bluesheep.it/ws/users/?psswd=" + BlueSheepConstants.WS_USERS_PWD;
			url = new URL(https_url);
			con = (HttpsURLConnection) url.openConnection();
			result = get_result(con);
			logger.info("Finished");
			logger.info("Starting JSONObject conversion");
			
			JSONArray jArr = new JSONArray(result);
			logger.info("Finish JSONObject conversion.");
			returnList = new ArrayList<BlueSheepUserInfo>();
			for(int i = 0; i < jArr.length(); i++) {
				JSONObject jobj = jArr.getJSONObject(i);
				if(jobj != null) {
					String gr = jobj.getString(GROUP_LAB);
					String tu = jobj.getString(TELEGRAM_LAB);
					String bsUser = jobj.getString(BS_USERNAME_LAB);
					if(gr != null && !StringUtils.isEmpty(gr)
							&& bsUser != null && !StringUtils.isEmpty(bsUser) 
							&& (REG_GROUP.equalsIgnoreCase(gr) || SU_GROUP.equalsIgnoreCase(gr) || MANAGER_GROUP.equalsIgnoreCase(gr))) {
						
						if(tu != null 
								&& !StringUtils.isEmpty(tu)) {
							if(tu.length() > 4 && tu.contains("@")) {
								tu = tu.replaceAll("@", "");
							}
						}else {
							logger.debug("BS_USER: " + bsUser + " GROUP: " + gr + " has no telegram username!");
							tu = null;
						}
						
						BlueSheepUserInfo user = new BlueSheepUserInfo(tu, gr, bsUser, 0, new Timestamp(System.currentTimeMillis()), null);
						returnList.add(user);
					}else {
						logger.debug("User not accepted since group is ." + gr + ".");
					}
				}else {
					logger.warn("JSONObject is null. Something goes wrong!");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}	
	
		return returnList;
	}

	private String get_result(HttpsURLConnection con){
		String result = "";
		if(con!=null){	
			try {
			   BufferedReader br = 
				new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			
			   String input;
			   while ((input = br.readLine()) != null){
				   result += input;
			   }
			   br.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return result;
	}

}
