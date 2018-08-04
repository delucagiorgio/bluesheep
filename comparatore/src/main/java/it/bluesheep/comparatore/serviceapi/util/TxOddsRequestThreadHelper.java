package it.bluesheep.comparatore.serviceapi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.json.AbstractBluesheepJsonConverter;
import it.bluesheep.util.json.TxOddsBluesheepJsonConverter;

public class TxOddsRequestThreadHelper extends AbstractRequestThreadHelper {

	private static Logger logger;
	private String urlRequest;
	
	public TxOddsRequestThreadHelper(String urlRequest, Map<String, String> mapThreadResponse) {
		super();
		this.urlRequest = urlRequest;
		
		logger = (new BlueSheepLogger(Bet365RequestThreadHelper.class)).getLogger();
		this.urlRequest = urlRequest;
		this.resultThreadRequest = mapThreadResponse;
	}
	
	@Override
	public void run() {
		try {
			URL url;
			HttpsURLConnection con;
			url = new URL(urlRequest);
			con = (HttpsURLConnection)url.openConnection();
		   //dump all the content
			String jsonResult = get_result(con);
			JSONObject jsonObjectResult = AbstractBluesheepJsonConverter.convertFromJSON(jsonResult);
			JSONObject attributesNode = TxOddsBluesheepJsonConverter.getAttributesNodeFromJSONObject(jsonObjectResult);
			resultThreadRequest.put("" + this.getId() + BlueSheepConstants.REGEX_CSV + attributesNode.getLong("timestamp"), jsonResult);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "ERRORE THREAD :: " + e.getMessage(), e);
		}
		
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
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return result;
	}	

}
