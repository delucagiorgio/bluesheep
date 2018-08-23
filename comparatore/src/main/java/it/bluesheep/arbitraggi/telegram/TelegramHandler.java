package it.bluesheep.arbitraggi.telegram;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import it.bluesheep.arbitraggi.util.TelegramHandlerUtility;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

/**
 * This program demonstrates a usage of the TelegramHandlerUtility class and completely manages the dispatch 
 * of the output messages and images to the telegram users.
 * 
 * @author Fabio Catania
 *
 */
public class TelegramHandler {

	private final static String sendPhoto = "sendPhoto";
	private final static String telegramBasicURL = "https://api.telegram.org/bot";
	private static String botKey = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.TELEGRAMBOTKEY);
	private final static String MARKDOWN = "Markdown";
	private final static String SLASH = "/";
	private static Logger logger = Logger.getLogger(TelegramHandler.class);

	public List<String> getTelegramUsersIds(){
		List<String> ids = new ArrayList<String>();
		String telegramUsersFile = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.TERMINALUSERSFILE);
	
	    try (BufferedReader br = new BufferedReader(new FileReader(telegramUsersFile))){	 
	        String sCurrentLine;
	        while ((sCurrentLine = br.readLine()) != null){
	        	if (!sCurrentLine.startsWith("#")) {
		        	ids.add(sCurrentLine);	        		
	        	}
	        }
	    }
	    catch (IOException e){
	        logger.error(e.getMessage(), e);
	    }
		
		return ids;
	}
	
	public void sendPicture(String picture, String caption, List<String> chat_ids) {
		String response = sendNewPicture(picture, caption, chat_ids.get(0));
		if (response != null && chat_ids.size() > 1) {			
			ExecutorService executorService = Executors.newFixedThreadPool(chat_ids.size()-1);

			for (int i = 1; i < chat_ids.size(); i++) {
				executorService.execute(new PhotoSender(chat_ids.get(i), response, caption));
			}
			
			executorService.shutdown();
			
			try {
			    if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
			        executorService.shutdownNow();
			    } 
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			    executorService.shutdownNow();
			}
		}
	}

	public void sendMessage (String text, List<String> chat_ids) {
		if(!chat_ids.isEmpty()) {
			ExecutorService executorService = Executors.newFixedThreadPool(chat_ids.size());
			
			for (int i = 0; i < chat_ids.size(); i++) {
				executorService.execute(new MessageSender(chat_ids.get(i), text));
			}

			executorService.shutdown();
			
			try {
			    if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
			        executorService.shutdownNow();
			    } 
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			    executorService.shutdownNow();
			}
		}
	}
		
	private String sendNewPicture(String pictureName, String caption, String chat_id) {
		
		File picture = new File(pictureName);
		String requestURL = telegramBasicURL + botKey + SLASH  + sendPhoto;
		String file_id = null;

		try {
			TelegramHandlerUtility multipart = new TelegramHandlerUtility(requestURL, BlueSheepConstants.ENCODING_UTF_8);
			
			multipart.addHeaderField("User-Agent", "USER_AGENT");
			multipart.addHeaderField("Test-Header", "Header-Value");
			
			multipart.addFormField("chat_id", chat_id);
			multipart.addFormField("parse_mode", MARKDOWN);
			multipart.addFormField("caption", caption);
			multipart.addFilePart("photo", picture);

			List<String> response = multipart.finish();
			
			logger.debug("SERVER REPLIED:");
			String r = "";
			for (String line : response) {
				r += line;
				logger.debug(line);
			}
			JSONObject obj = new JSONObject(r);
			boolean status = obj.getBoolean("ok");
			final int PICNUMBER = 3; // The biggest one
			if (status) {
				file_id = obj.getJSONObject("result").getJSONArray("photo").getJSONObject(PICNUMBER).getString("file_id");
			}

		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
		
		return file_id;
	}
}

class PhotoSender implements Runnable {

	private final static String sendPhoto = "sendPhoto";
	private final static String telegramBasicURL = "https://api.telegram.org/bot";
	private static String botKey = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.TELEGRAMBOTKEY);
	private final static String SLASH = "/";
	private final static String QUESTIONMARK = "?";
	private final static String MARKDOWN = "Markdown";
	private final static String AND = "&";
	private String chat_id;
	private String picture;
	private String caption;
	private static Logger logger = Logger.getLogger(PhotoSender.class);

	public PhotoSender(String chat_id, String picture, String caption) {
		super();
		this.chat_id = chat_id;
		this.caption = caption;
		this.picture = picture;
	}
	
	private void sendPictureAgain(String pictureCode, String caption, String chat_id) {
		String urlString = telegramBasicURL + "%s" + SLASH + sendPhoto + QUESTIONMARK + "chat_id=%s" + AND + "parse_mode=%s" + AND + "photo=%s" + AND + "caption=%s";
		urlString = String.format(urlString, botKey, chat_id, MARKDOWN, pictureCode, caption.replaceAll(" ", "%20"));
		
		URL url;
		try {
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
		sendPictureAgain(picture, caption, chat_id);
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