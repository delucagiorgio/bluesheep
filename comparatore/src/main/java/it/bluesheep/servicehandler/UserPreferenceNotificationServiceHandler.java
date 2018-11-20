package it.bluesheep.servicehandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.database.ConnectionPool;
import it.bluesheep.database.ProcessStatus;
import it.bluesheep.database.dao.impl.PBOddDAO;
import it.bluesheep.database.dao.impl.SaveOddProcessHistoryDAO;
import it.bluesheep.database.dao.impl.UserPreferenceDAO;
import it.bluesheep.database.dao.impl.UserPreferenceNotificationDAO;
import it.bluesheep.database.entities.PBOdd;
import it.bluesheep.database.entities.SaveOddProcessHistory;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.database.entities.UserPreferenceNotification;
import it.bluesheep.util.BlueSheepConstants;

public class UserPreferenceNotificationServiceHandler extends AbstractBlueSheepService {

	private static Logger logger = Logger.getLogger(UserPreferenceNotificationServiceHandler.class);
	private Connection connection;
	private long startTime;
	
	@Override
	public void run() {
		SaveOddProcessHistoryDAO dao = null;
		try {
			connection = ConnectionPool.getConnection();
			startTime = System.currentTimeMillis();
			logger.info("Starting check notification user preference");
			dao = SaveOddProcessHistoryDAO.getSaveOddProcessHistoryDAOInstance(connection);
			
			dao.insertRow(new SaveOddProcessHistory(Service.USERPREFNOTIFICATION_SERVICE, ProcessStatus.RUNNING, null, 0, new Timestamp(System.currentTimeMillis()), null));
			connection.commit();
			List<UserPreference> userPreferenceList = UserPreferenceDAO.getUserPreferenceDAOInstance(connection).getAllActiveRows();
			
			Map<String, List<UserPreference>> bookmakerUserPreferenceMap = new TreeMap<String, List<UserPreference>>();
			
			if(userPreferenceList != null && userPreferenceList.size() > 0) {
				for(UserPreference up : userPreferenceList) {
					List<UserPreference> upBookmaker = bookmakerUserPreferenceMap.get(up.getBookmaker().getBookmakerName());
					if(upBookmaker == null) {
						upBookmaker = new ArrayList<UserPreference>();
					}
					upBookmaker.add(up);
					bookmakerUserPreferenceMap.put(up.getBookmaker().getBookmakerName(), upBookmaker);
				}
				
				
				List<PBOdd> ppOddsList = PBOddDAO.getPBOddDAOInstance(connection)
								.getPPOddListFromBookmakerList(new ArrayList<String>(bookmakerUserPreferenceMap.keySet()));
				
				Collections.sort(ppOddsList, new Comparator<PBOdd>() {

					@Override
					public int compare(PBOdd o1, PBOdd o2) {
						return o2.getRating1().compareTo(o1.getRating1());
					}
				});
				
				if(!ppOddsList.isEmpty()) {
					for(PBOdd odd : ppOddsList) {
						List<UserPreference> preferenceBook1 = bookmakerUserPreferenceMap.get(odd.getBookmakerName1());
						List<UserPreference> preferenceBook2 = bookmakerUserPreferenceMap.get(odd.getBookmakerName2());
						
						if(preferenceBook1 != null) {
							for(UserPreference up : preferenceBook1) {
								if(up.getRating() <= odd.getRating1() && up.isActive()) {
									List<UserPreferenceNotification> preferenceSent = 
											UserPreferenceNotificationDAO.getUserPreferenceNotificationDAOInstance(connection)
											.getNotificationsSentByUserPreference(up);
									
									int nextProdId = preferenceSent.size() + 1;
									if(preferenceSent.size() < 5) {
										if(!alreadySent(preferenceSent, odd)) {
											
											String text = odd.getTelegramButtonText();		
											logger.info("Sending notification #" + nextProdId + " to user " + up.getUser().getUserName());
											
											UserPreferenceNotification upn = new UserPreferenceNotification(up, up.getUser(), 0, nextProdId, null, null, odd.getNotificationKey());
											
											UserPreferenceNotificationDAO.getUserPreferenceNotificationDAOInstance(connection).insertRow(upn);

											(new MessageSender("" + up.getUser().getChatId(), 
													ArbsUtil.getTelegramBoldString("Notifica #" + nextProdId) 
													+ " per il bookmaker " 
															+ ArbsUtil.getTelegramBoldString(up.getBookmaker().getBookmakerName()) 
															+ System.lineSeparator() + text)).run();
											connection.commit();
										}
									}else {
										UserPreferenceDAO.getUserPreferenceDAOInstance(connection).deactivateUserPreference(up);
										UserPreferenceNotificationDAO.getUserPreferenceNotificationDAOInstance(connection).deleteUserPrefNotificationFromUP(up);
										up.setActive(false);
										connection.commit();
										break;
									}
								}
							}
						}
						
						if(preferenceBook2 != null) {
							for(UserPreference up : preferenceBook2) {
								if(up.getRating() <= odd.getRating1() && up.isActive()) {
									List<UserPreferenceNotification> preferenceSent = 
											UserPreferenceNotificationDAO.getUserPreferenceNotificationDAOInstance(connection)
											.getNotificationsSentByUserPreference(up);
									
									int nextProdId = preferenceSent.size() + 1;
									if(preferenceSent.size() < 5) {
										if(!alreadySent(preferenceSent, odd)) {
											(new MessageSender("" + up.getUser().getChatId(), odd.getTelegramButtonText())).run();
											String text = odd.getTelegramButtonText();		
											logger.info("Sending notification #" + nextProdId + " to user " + up.getUser().getUserName());
											
											UserPreferenceNotification upn = new UserPreferenceNotification(up, up.getUser(), 0, nextProdId, null, null, odd.getNotificationKey());
											
											UserPreferenceNotificationDAO.getUserPreferenceNotificationDAOInstance(connection).insertRow(upn);

											(new MessageSender("" + up.getUser().getChatId(), 
													ArbsUtil.getTelegramBoldString("Notifica #" + nextProdId) 
													+ " per il bookmaker " 
															+ ArbsUtil.getTelegramBoldString(up.getBookmaker().getBookmakerName()) 
															+ System.lineSeparator() + text)).run();
											connection.commit();
										}
									}else {
										UserPreferenceDAO.getUserPreferenceDAOInstance(connection).deactivateUserPreference(up);
										UserPreferenceNotificationDAO.getUserPreferenceNotificationDAOInstance(connection).deleteUserPrefNotificationFromUP(up);
										up.setActive(false);
										connection.commit();
										break;
									}
								}
							}
						}
					}
				}else {
					logger.warn("Table is empty!!!");
				}
			}else {
				logger.info("No userPreference active at the moment");
			}
			
			dao.updateLastRun(Service.USERPREFNOTIFICATION_SERVICE, null);
			connection.commit();
			ConnectionPool.releaseConnection(connection);
			logger.info("Excecution completed in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			try {
				connection.rollback();
				
				dao.updateLastRun(Service.USERPREFNOTIFICATION_SERVICE, e);
				connection.commit();
				ConnectionPool.releaseConnection(connection);
			} catch (SQLException e1) {
				logger.error(e1.getMessage(), e);
			}
		}
	}

	private boolean alreadySent(List<UserPreferenceNotification> preferenceSent, PBOdd odd) {
		
		for(UserPreferenceNotification upn : preferenceSent) {
			if(upn.getNotificationKey().equals(odd.getNotificationKey())) {
				return true;
			}
		}
		return false;
	}

}

class MessageSender implements Runnable {

	private final static String sendMessage = "sendMessage";
	private final static String telegramBasicURL = "https://api.telegram.org/bot";
	private static String botKey = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.TELEGRAMBOTKEY);
	private static Logger logger = Logger.getLogger(MessageSender.class);

	private final static String SLASH = "/";
	private final static String QUESTIONMARK = "?";
	private final static String MARKDOWN = "Markdown";
	private final static String AND = "&";
	
	private String chat_id;
	private String text;
	
	public MessageSender(String chat_id, String text) {
		super();
		this.chat_id = chat_id;
		this.text = text;
	}
	
	private void sendSingleMessage(String text, String chat_id) {
		try {
			String urlString = telegramBasicURL + "%s" + SLASH + sendMessage + QUESTIONMARK + "chat_id=%s" + AND + "parse_mode=%s" + AND + "text=%s" + AND + "disable_web_page_preview=true";
			urlString = String.format(urlString, botKey, chat_id, MARKDOWN, URLEncoder.encode(text, BlueSheepConstants.ENCODING_UTF_8));
		
			URL url;

			url = new URL(urlString);
			URLConnection conn = url.openConnection();

			StringBuilder sb = new StringBuilder();
			InputStream is = new BufferedInputStream(conn.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
			    sb.append(inputLine);
			}
			String response = sb.toString();
			logger.debug(response);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}

	public void run(){
		sendSingleMessage(text, chat_id);
 	}
}
