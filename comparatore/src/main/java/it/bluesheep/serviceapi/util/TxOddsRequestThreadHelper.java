package it.bluesheep.serviceapi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import it.bluesheep.util.BlueSheepLogger;

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

		URL url;
		HttpsURLConnection con;
		try {
			
			url = new URL(urlRequest);
			con = (HttpsURLConnection)url.openConnection();
		     
		   //dump all the content
			resultThreadRequest.put("" + this.getId(), get_result(con));
				
		} catch (Exception e) {
		   logger.log(Level.SEVERE, "Error during request data on TxOdds. Error is " + e.getMessage(), e);
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
				logger.log(Level.SEVERE, "Error during request data on TxOdds. Error is " + e.getMessage(), e);
			}
		}
	
		return result;
	}	

}
