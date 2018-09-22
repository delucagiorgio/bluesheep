package it.bluesheep.comparatore.serviceapi.multirequesthandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.betfair.entities.MarketFilter;

import it.bluesheep.comparatore.serviceapi.util.BetfairRequestThreadHelper;

public class BetfairRequestHandler extends AbstractRequestHandler {
	
	private static final int QUERY_SIZE_MARKET = 40;
	private MarketFilter filter;
	
	public BetfairRequestHandler(int maxThreadPoolSize, MarketFilter filter, String sessionToken) {
		super(maxThreadPoolSize / QUERY_SIZE_MARKET  + 1, sessionToken);
		this.filter = filter;
		this.logger = Logger.getLogger(BetfairRequestHandler.class);
	}

	@Override
	protected List<String> runThreadRequests(List<String> marketIdsList) {
				
		List<String> returnJsonResponseList = new ArrayList<String>();

		//inizializzazione variabili query paginata
		int cyclesQuery = 0;
		do {
			MarketFilter marketFilter = new MarketFilter();
			marketFilter = filter;
			
			List<String> idsSublist = getPortionIdsBySize(QUERY_SIZE_MARKET, marketIdsList, cyclesQuery);
			
			marketFilter.setEventIds(new HashSet<String>(idsSublist));
			
			executor.submit(new BetfairRequestThreadHelper(new HashSet<String>(idsSublist), idsSublist, marketFilter, mapThreadResponse, token));
			
			cyclesQuery++;
		} while(cyclesQuery * QUERY_SIZE_MARKET < marketIdsList.size());
		
		boolean timeoutReached = true;
		try {
			executor.shutdown();

			timeoutReached = !executor.awaitTermination(60, TimeUnit.SECONDS);
			if(timeoutReached) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			if(!executor.isShutdown()) {
				executor.shutdownNow();
			}
		}
		
		if(timeoutReached) {
			logger.warn("" + this.getClass().getSimpleName() + " timeout reached = " + timeoutReached);
		}
		
		for(String idJSON : mapThreadResponse.keySet()) {
			returnJsonResponseList.add(mapThreadResponse.get(idJSON));
		}
		
		mapThreadResponse.clear();
		
		return returnJsonResponseList;
	}
	
	/**
	 * GD - 25/04/2018
	 * Paginazione delle interrogazioni. Il metodo fornisce un subset di elementi rispetto ai dati inziali rispetto ai parametri passati.
	 * @param querySize la dimensione desiderata del subset paginato
	 * @param idsList la lista di partenza
	 * @param cyclesQuery i cicli di iterazioni attualmente eseguiti
	 * @return il subset della grandezza richiesta
	 */
	private List<String> getPortionIdsBySize(int querySize, List<String> idsList, int cyclesQuery) {
		int startIndex = cyclesQuery * querySize;
		int endIndex = startIndex + querySize;
		List<String> idsSublist = null;
				
		if(idsList.size() <= querySize) {		
			idsSublist = idsList;
		}else {
			startIndex = cyclesQuery * querySize;
			endIndex = startIndex + querySize;
			if (endIndex >= idsList.size()) {
				endIndex = idsList.size();
			}
			idsSublist = idsList.subList(startIndex, endIndex);
		}
		return idsSublist;
	}

}
