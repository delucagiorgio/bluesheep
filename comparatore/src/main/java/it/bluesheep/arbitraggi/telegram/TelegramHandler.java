package it.bluesheep.arbitraggi.telegram;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

import it.bluesheep.arbitraggi.util.TelegramHandlerUtility;
import it.bluesheep.util.BlueSheepConstants;

public class TelegramHandler {

	private final static String SEND_PHOTO = "sendPhoto";
	private final static String SEND_MESSAGE = "sendMessage";
	private final static String TELEGRAM_BASIC_URL = "https://api.telegram.org/bot";
	private final static String MARKDOWN_URL = "Markdown";
	private final static String BOT_KEY = "618342797:AAEHIeL4dxNgp4_giX8C6VU9bOraCu-n7S8";
	private final static String SLASH = "/";
	
	private static Logger logger;
	
	public TelegramHandler() {
		logger = Logger.getLogger(TelegramHandler.class);
	}
	

	public void sendPicture(String pictureName, String caption, String chat_id) {
		File picture = new File(pictureName);
		String requestURL = TELEGRAM_BASIC_URL + BOT_KEY + SLASH + SEND_PHOTO;
		
		try {
			TelegramHandlerUtility multipart = new TelegramHandlerUtility(requestURL, BlueSheepConstants.ENCODING_UTF_8);
			
			multipart.addHeaderField("User-Agent", "USER_AGENT");
			multipart.addHeaderField("Test-Header", "Header-Value");
			
			multipart.addFormField("chat_id", chat_id);
			multipart.addFormField("parse_mode", MARKDOWN_URL);
			multipart.addFormField("caption", caption);
			multipart.addFilePart("photo", picture);

			List<String> response = multipart.finish();
			
			for (String line : response) {
				logger.debug(line);
			}
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}


	public void sendMessage(String caption, String chatId) {
		try {
			caption = URLEncoder.encode(caption, BlueSheepConstants.ENCODING_UTF_8);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		
		if(caption != null) {
            String url = TELEGRAM_BASIC_URL + BOT_KEY + SLASH + SEND_MESSAGE + 
            		"?chat_id=" + chatId  + 
            		"&parse_mode=Markdown" +
            		"&disable_web_page_preview=true" + 
            		"&text=" + caption;
            
            BufferedReader in = null;
            try {
				HttpsURLConnection httpsConnection = (HttpsURLConnection) new URL(url).openConnection();
	            in = new BufferedReader(
	            		new InputStreamReader(httpsConnection.getInputStream()));
	            
	            String inputLine;
	            while ((inputLine = in.readLine()) != null) {
	                logger.debug(inputLine);
	            }
	            in.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
    }
}
