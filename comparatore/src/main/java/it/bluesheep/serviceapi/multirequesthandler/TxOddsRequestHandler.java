package it.bluesheep.serviceapi.multirequesthandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import it.bluesheep.serviceapi.util.TxOddsRequestThreadHelper;

public class TxOddsRequestHandler extends AbstractRequestHandler {

	public TxOddsRequestHandler(int maxThreadPoolSize, String token) {
		super(maxThreadPoolSize, token);
	}

	@Override
	protected List<String> runThreadRequests(List<String> requestIdsList, int sizeWait) {
		
		String oddsType = requestIdsList.get(1);
		int startDay = Integer.parseInt(requestIdsList.get(2));
		int endDay = Integer.parseInt(requestIdsList.get(3));
		String sportCode = requestIdsList.get(0);
		String[] splittedToken = token.split(";");
		String u = splittedToken[0];
		String p = splittedToken[1];
		
		List<String> result = new ArrayList<String>();
		
		String active = "1";
		String json = "1";
		String allOdds = "2";
		String odds_format = "0";
		String baseHttpsUrl = "https://xml2.txodds.com/feed/odds/xml.php?ident="+u+"&passwd="+p+"&active="+active+"&spid="+sportCode+"&ot="+oddsType+"&json="+json+"&all_odds="+ allOdds + "&odds_format=" + odds_format;

		
		//Lancia tutte le richieste
		do {
			String days = "" + startDay + "," + 1;
			String https_url = baseHttpsUrl +"&days="+days;
			
			try {

				executor.submit(new TxOddsRequestThreadHelper(https_url, mapThreadResponse));

				Thread.sleep(500);
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Error during request number " + startDay + ". Error message : " + e.getMessage(), e);
			}
			
			startDay++;
		}while(startDay <= endDay);
		
		long startTime = System.currentTimeMillis();

		//Attende il tempo di timeout o la completa esecuzione corretta delle richieste
		while(mapThreadResponse.keySet().size() != (endDay + 1) && System.currentTimeMillis() - startTime < sizeWait * 2 * 1000L) {
			
			logger.log(Level.CONFIG, "WAITING FOR REQUESTS COMPLETION: Actual size of completed request list is " + mapThreadResponse.keySet().size() + "/" + (endDay + 1));
			logger.log(Level.CONFIG, "Remains " + (sizeWait * 2 - (System.currentTimeMillis() - startTime ) / 1000) + " seconds to close request pool"); 
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);				
			}
		}
		
		for(String idJSON : mapThreadResponse.keySet()) {
			result.add(mapThreadResponse.get(idJSON));
		}
		
		mapThreadResponse.clear();
		
		return result;
	}
	
}
