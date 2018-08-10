package it.bluesheep.serviceapi.multirequesthandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import it.bluesheep.serviceapi.util.TxOddsRequestThreadHelper;

public class TxOddsRequestHandler extends AbstractRequestHandler {

	public TxOddsRequestHandler(int maxThreadPoolSize, String token) {
		super(maxThreadPoolSize, token);
	}

	@Override
	protected List<String> runThreadRequests(List<String> requestIdsList) {

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
		String baseHttpsUrl = "https://xml2.txodds.com/feed/odds/xml.php?ident=" + u + "&passwd=" + p + "&active="
				+ active + "&spid=" + sportCode + "&ot=" + oddsType + "&json=" + json + "&all_odds=" + allOdds
				+ "&odds_format=" + odds_format;

		// Lancia tutte le richieste
		do {
			String days = "" + startDay + "," + 1;
			String https_url = baseHttpsUrl + "&days=" + days;
			executor.submit(new TxOddsRequestThreadHelper(https_url, mapThreadResponse));

			startDay++;
		} while (startDay <= endDay);

		boolean timeoutReached = true;

		try {
			executor.shutdown();
			timeoutReached = !executor.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}	
		
		if (timeoutReached) {
			logger.log(Level.WARNING, "" + this.getClass().getSimpleName() + " timeout reached = " + timeoutReached);
		}

		for (String idJSON : mapThreadResponse.keySet()) {
			result.add(mapThreadResponse.get(idJSON));
		}

		mapThreadResponse.clear();

		return result;
	}

}
