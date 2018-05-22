package it.bluesheep.serviceapi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import it.bluesheep.util.BlueSheepLogger;

public class Bet365RequestThreadHelper extends Thread {
	
	private Map<String, String> resultThreadRequest;
	private static Logger logger;
	private int iteration;
	private List<String> eventsIds;
	private String token;
	
	public Bet365RequestThreadHelper(int iteration, List<String> eventsIds, String token, Map<String, String> mapThreadResponse) {
		super();
		logger = (new BlueSheepLogger(Bet365RequestThreadHelper.class)).getLogger();
		this.iteration = iteration;
		this.eventsIds = eventsIds;
		this.token = token;
		this.resultThreadRequest = mapThreadResponse;
	}
	
	@Override
	public void run() {
		String https_url = "https://api.betsapi.com/v1/bet365/start_sp?token="+token+"&event_id="+eventsIds.get(iteration)+"&charset=utf-8";
		
		String partialResult;
		URL url;
		HttpsURLConnection con = null;
		try {
			url = new URL(https_url);
			con = (HttpsURLConnection)url.openConnection();
		} catch (IOException e) {
			logger.severe(e.getMessage());
		}

		partialResult = get_result(con); 		
		addResultToApiCollection(partialResult);
	}

	
	private synchronized void addResultToApiCollection(String partialResult) {
		resultThreadRequest.put(""+this.getId(),partialResult);
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
				logger.severe("Error during request data on Bet365. Error is " + e.getMessage());
			}
				
		}
	
		return result;
	}

}
