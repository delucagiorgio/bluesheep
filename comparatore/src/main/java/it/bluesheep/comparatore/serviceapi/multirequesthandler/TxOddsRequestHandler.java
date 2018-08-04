package it.bluesheep.comparatore.serviceapi.multirequesthandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import it.bluesheep.comparatore.serviceapi.util.TxOddsRequestThreadHelper;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepSharedResources;

public class TxOddsRequestHandler extends AbstractRequestHandler {

	private long lastTimestampUpdate;
	
	public TxOddsRequestHandler(int maxThreadPoolSize, String token) {
		super(maxThreadPoolSize, token);
		this.lastTimestampUpdate = BlueSheepSharedResources.getTxOddsUpdateTimestamp();
	}
	

	@Override
	protected List<String> runThreadRequests(List<String> requestIdsList) {
		
		String oddsType = requestIdsList.get(1);
		int startDay = Integer.parseInt(requestIdsList.get(2));
		int endDay = Integer.parseInt(requestIdsList.get(3));
		String sportCode = requestIdsList.get(0);
		String[] splittedToken = token.split(BlueSheepConstants.REGEX_CSV);
		String u = splittedToken[0];
		String p = splittedToken[1];
		
		List<String> result = new ArrayList<String>();
		
		String active = "1";
		String json = "1";
		String allOdds = "2";
		String odds_format = "0";
		String baseHttpsUrl = "https://xml2.txodds.com/feed/odds/xml.php?ident="+u+
				"&passwd="+p+
				"&active="+active+
				"&spid="+sportCode+
				"&ot="+oddsType+
				"&json="+json+
				"&all_odds="+allOdds+
				"&odds_format="+odds_format;

		
		//Lancia tutte le richieste
		do {
			String days = "" + startDay + "," + 1;
			String https_url = baseHttpsUrl +"&days="+days;
			if(lastTimestampUpdate > 0) {
				https_url = https_url + "&last=" + lastTimestampUpdate;
			}
			executor.submit(new TxOddsRequestThreadHelper(https_url, mapThreadResponse));
			
			startDay++;
		}while(startDay <= endDay);
		
		boolean timeoutReached = true;
		
		try {
			executor.shutdown();
			
			timeoutReached = !executor.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		if(timeoutReached) {
			logger.log(Level.WARNING, "" + this.getClass().getSimpleName() + " timeout reached = " + timeoutReached);
		}
		
		long tempUpdateTimestamp = -1;
		
		for(String idJSON : mapThreadResponse.keySet()) {
			String[] splittedThreadIdTimestamp = idJSON.split(BlueSheepConstants.REGEX_CSV);
			if(splittedThreadIdTimestamp.length == 2){
				Long timestampFromJSON = new Long(splittedThreadIdTimestamp[1]);
				if(tempUpdateTimestamp < 0 || timestampFromJSON.longValue() < tempUpdateTimestamp) {
					tempUpdateTimestamp = timestampFromJSON.longValue();
					BlueSheepSharedResources.setTxOddsNowMinimumUpdateTimestamp(timestampFromJSON);
				}
			}
			result.add(mapThreadResponse.get(idJSON));
		}
		
		
		mapThreadResponse.clear();
		
		return result;
	}
	
}
