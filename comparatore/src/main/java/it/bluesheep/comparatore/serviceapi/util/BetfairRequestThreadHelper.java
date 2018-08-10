package it.bluesheep.comparatore.serviceapi.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.betfair.api.BetfairExchangeOperationsManagerImpl;
import com.betfair.api.IBetfairExchangeOperationsManager;
import com.betfair.entities.MarketFilter;
import com.betfair.entities.PriceProjection;
import com.betfair.enums.types.MatchProjection;
import com.betfair.enums.types.PriceData;
import com.betfair.exceptions.BetFairAPIException;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

public class BetfairRequestThreadHelper extends AbstractRequestThreadHelper {

	private static Logger logger;

	private List<String> idsSublist;
	private MarketFilter filter;
	private IBetfairExchangeOperationsManager beom;

	
	public BetfairRequestThreadHelper(HashSet<String> eventsIds, List<String> idsSublist, MarketFilter marketFilter, Map<String, String> resultThreadRequest, String sessionToken) {
		this.resultThreadRequest = resultThreadRequest;
		this.filter = marketFilter;
		this.filter.setEventIds(eventsIds);	
		this.token = sessionToken;
		this.idsSublist = idsSublist;
		logger = Logger.getLogger(BetfairRequestThreadHelper.class);
	}
	
	@Override
	public void run() {
		try {
			//Preparazione del filtro per la chiamata sul marketBook
			PriceProjection priceProjection = new PriceProjection();
			
			Set<PriceData> priceDataSet = new HashSet<PriceData>();
			priceDataSet.add(PriceData.EX_BEST_OFFERS);
			priceProjection.setPriceData(priceDataSet);
			
			beom = BetfairExchangeOperationsManagerImpl.getInstance();
			
			String responseJson = null;
			//chiamata sul marketBook 
			try {				
				responseJson = beom.listMarketBook(idsSublist, priceProjection, null, MatchProjection.ROLLED_UP_BY_PRICE, null, BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_APPKEY), token);
			} catch (BetFairAPIException e) {
				logger.error(e.getMessage(), e);
			}
			
			//colleziono JSON da ritornare
			resultThreadRequest.put("" + this.getId(), responseJson);
		}catch(Exception e) {
			logger.error("ERRORE THREAD :: " + e.getMessage(), e);

		}
	}

}
