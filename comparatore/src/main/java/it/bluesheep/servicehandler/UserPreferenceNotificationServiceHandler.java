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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
	private ExecutorService executor;
	private int maxNotificationPerUserPreference;
	
	public UserPreferenceNotificationServiceHandler() {
		super();
		executor = Executors.newFixedThreadPool(1);
		maxNotificationPerUserPreference = Integer.parseInt(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.MAX_NOTIFICATION_USER_PREF));
	}
	
	@Override
	public void run() {
		
		SaveOddProcessHistoryDAO dao = null;
		try {
			connection = ConnectionPool.getConnection();
			startTime = System.currentTimeMillis();
			logger.info("Starting check notification user preference");
			dao = SaveOddProcessHistoryDAO.getSaveOddProcessHistoryDAOInstance();
			
			dao.insertRow(new SaveOddProcessHistory(Service.USERPREFNOTIFICATION_SERVICE, ProcessStatus.RUNNING, null, 0, new Timestamp(System.currentTimeMillis()), null), connection);
			connection.commit();
			List<UserPreference> userPreferenceList = UserPreferenceDAO.getUserPreferenceDAOInstance().getAllActiveRows(connection);
			
			Map<String, List<UserPreference>> bookmakerUserPreferenceMap = new TreeMap<String, List<UserPreference>>();
			
			//Se ci sono preferenze attive, raggruppa per bookmakerName le liste di preferenze
			if(userPreferenceList != null && userPreferenceList.size() > 0) {
				for(UserPreference up : userPreferenceList) {
					List<UserPreference> upBookmaker = bookmakerUserPreferenceMap.get(up.getBookmaker().getBookmakerName());
					if(upBookmaker == null) {
						upBookmaker = new ArrayList<UserPreference>();
					}
					upBookmaker.add(up);
					bookmakerUserPreferenceMap.put(up.getBookmaker().getBookmakerName(), upBookmaker);
				}
				
				
				//Prende le quote del comparatore
				List<PBOdd> ppOddsList = PBOddDAO.getPBOddDAOInstance()
								.getPPOddListFromBookmakerList(new ArrayList<String>(bookmakerUserPreferenceMap.keySet()), connection);
				
				//Le ordina in maniera decrescente per rating
				Collections.sort(ppOddsList, new Comparator<PBOdd>() {

					@Override
					public int compare(PBOdd o1, PBOdd o2) {
						return o2.getRating1().compareTo(o1.getRating1());
					}
				});
				
				//Se ci sono quote
				if(!ppOddsList.isEmpty()) {
					
					for(PBOdd odd : ppOddsList) {
						//Lista corrispondente del primo book della quota del comparatore
						List<UserPreference> preferenceBook1 = bookmakerUserPreferenceMap.get(odd.getBookmakerName1());
						//Lista corrispondente del secondo book della quota del comparatore
						List<UserPreference> preferenceBook2 = bookmakerUserPreferenceMap.get(odd.getBookmakerName2());
						
						//Se esistono preferenze per il bookmaker nella lista 1
						if(preferenceBook1 != null) {
							for(UserPreference up : preferenceBook1) {
								//Se attiva
								if(up.isActive()) {
									List<UserPreferenceNotification> preferenceSent = 
											UserPreferenceNotificationDAO.getUserPreferenceNotificationDAOInstance()
											.getNotificationsSentByUserPreference(up, connection);
									
									int currentNotificationGroupSize = 0;
									
									for(UserPreferenceNotification upn : preferenceSent) {
										if(upn.isCurrentNotificationGroup()) {
											currentNotificationGroupSize++;
										}
									}
									
									if(currentNotificationGroupSize < maxNotificationPerUserPreference) {
										//Se non è stata già inviata
										if(!alreadySent(preferenceSent, odd)) {
											processOperationAndRequirementsOfNotification(odd, up, currentNotificationGroupSize);
										}
									}else {
										UserPreferenceDAO.getUserPreferenceDAOInstance().deactivateUserPreference(up, connection);
										UserPreferenceNotificationDAO.getUserPreferenceNotificationDAOInstance().setCurrentNotificationGroupOff(up, connection);
										up.setActive(false);
										connection.commit();
										break;
									}
								}
							}
						}
						
						if(preferenceBook2 != null) {
							for(UserPreference up : preferenceBook2) {
								//Se attiva
								if(up.isActive()) {
									List<UserPreferenceNotification> preferenceSent = 
											UserPreferenceNotificationDAO.getUserPreferenceNotificationDAOInstance()
											.getNotificationsSentByUserPreference(up, connection);
									
									
									int currentNotificationGroupSize = 0;
									
									for(UserPreferenceNotification upn : preferenceSent) {
										if(upn.isCurrentNotificationGroup()) {
											currentNotificationGroupSize++;
										}
									}
									if(currentNotificationGroupSize < maxNotificationPerUserPreference) {
										//Se non è stata già inviata
										if(!alreadySent(preferenceSent, odd)) {
											processOperationAndRequirementsOfNotification(odd, up, currentNotificationGroupSize);
										}
									}else {
										UserPreferenceDAO.getUserPreferenceDAOInstance().deactivateUserPreference(up, connection);
										UserPreferenceNotificationDAO.getUserPreferenceNotificationDAOInstance().setCurrentNotificationGroupOff(up, connection);
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
			
			dao.updateLastRun(Service.USERPREFNOTIFICATION_SERVICE, null, connection);
			connection.commit();
			ConnectionPool.releaseConnection(connection);
			logger.info("Excecution completed in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds");
			
			executor.shutdown();
			
			logger.info("UserPreferenceNotification executor terminated: " + executor.awaitTermination(30, TimeUnit.SECONDS));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			try {
				connection.rollback();
				
				dao.updateLastRun(Service.USERPREFNOTIFICATION_SERVICE, e, connection);
				connection.commit();
				ConnectionPool.releaseConnection(connection);
				
				executor.shutdown();
				
				logger.info("UserPreferenceNotification executor terminated: " + executor.awaitTermination(30, TimeUnit.SECONDS));
			} catch (Exception e1) {
				logger.error(e1.getMessage(), e);
			}
		}
	}

	private void processOperationAndRequirementsOfNotification(PBOdd odd, UserPreference up, int currentNotificationGroupSize) throws SQLException {

		boolean ratingOK = true && up.getRating() == null;
		boolean rfOK = true && up.getRfType() == null && up.getRfValue() == null;
		boolean eventOK = true && up.getEvent() == null;
		boolean sizeOK = true && up.getLiquidita() == null;
		boolean minOddOK = true && up.getMinOddValue() == null;
		
		//Se il rating è rispettato
		if(!ratingOK && up.getRating() <= odd.getRating1()) {
			ratingOK = true;
		}
		
		if(!eventOK && up.getEvent().equalsIgnoreCase(odd.getEvento())) {
			eventOK = true;
		}
		
		if(!minOddOK && up.getMinOddValue() <= odd.getQuotaScommessaBookmaker1()) {
			minOddOK = true;
		}
		
		if(!rfOK && odd.minRfRespected(up)) {
			rfOK = true;
		}
		
		if(!sizeOK && up.getLiquidita() <= odd.getLiquidita2()) {
			sizeOK = true;
		}
			
			
		if(ratingOK && eventOK && minOddOK && rfOK && sizeOK) {
			int nextProdId = currentNotificationGroupSize + 1;
			String text = odd.getTelegramButtonText(up, odd, nextProdId, maxNotificationPerUserPreference);		
			logger.info("Sending notification #" + nextProdId + " to user " + up.getUser().getUserName());
			
			UserPreferenceNotification upn = new UserPreferenceNotification(up, up.getUser(), 0, nextProdId, null, null, odd.getNotificationKey(), true);
			
			UserPreferenceNotificationDAO.getUserPreferenceNotificationDAOInstance().insertRow(upn, connection);

			executor.submit(new MessageSender("" + up.getUser().getChatId(), 
												ArbsUtil.getTelegramBoldString("Notifica #" + nextProdId) 
												+ " per il bookmaker " 
												+ ArbsUtil.getTelegramBoldString(up.getBookmaker().getBookmakerName()) 
												+ System.lineSeparator() + text));
			connection.commit();
		}else {
			if(logger.isDebugEnabled()) {
				logger.debug(up.getUserPreferenceManifest());
				logger.info("Rating: " + ratingOK + "; RF: " + rfOK + "; MinOdd: " + minOddOK + "; Size: " + sizeOK + "; Event: " + eventOK);
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
