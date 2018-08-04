package it.bluesheep.comparatore.serviceapi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import it.bluesheep.util.BlueSheepLogger;

public class Bet365RequestThreadHelper extends AbstractRequestThreadHelper {
	
	private static Logger logger;
	private String urlRequest;
	
	public Bet365RequestThreadHelper(String urlRequest, String token, Map<String, String> mapThreadResponse) {
		super();
		logger = (new BlueSheepLogger(Bet365RequestThreadHelper.class)).getLogger();
		this.urlRequest = urlRequest;
		this.token = token;
		this.resultThreadRequest = mapThreadResponse;
	}
	
	@Override
	public void run() {
		try {
			String partialResult;
			URL url;
			HttpsURLConnection con = null;
			try {
				url = new URL(urlRequest);
				con = (HttpsURLConnection)url.openConnection();
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
			partialResult = get_result(con); 		
			addResultToApiCollection(partialResult);
		}catch(Exception e) {
			logger.log(Level.SEVERE, "ERRORE THREAD :: " + e.getMessage(), e);

		}
	}

	
	private void addResultToApiCollection(String partialResult) {
		resultThreadRequest.put(""+this.getId(), partialResult);
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
