package it.bluesheep.comparatore.serviceapi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.json.AbstractBluesheepJsonConverter;
import it.bluesheep.util.json.TxOddsBluesheepJsonConverter;

public class TxOddsRequestThreadHelper extends AbstractRequestThreadHelper {

	private static Logger logger;
	private String urlRequest;
	
	public TxOddsRequestThreadHelper(String urlRequest, Map<String, String> mapThreadResponse) {
		super();
		this.urlRequest = urlRequest;
		logger = Logger.getLogger(TxOddsRequestThreadHelper.class);
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
			logger.error("ERRORE THREAD :: " + e.getMessage(), e);
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
				logger.error(e.getMessage(), e);
			}
		}
		return result;
	}	

}
