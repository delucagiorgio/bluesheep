package it.bluesheep.comparatore.serviceapi.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.betfair.api.BetfairOperationsManagerImpl;
import com.betfair.api.IBetfairOperationsManager;
import com.betfair.entities.MarketFilter;
import com.betfair.entities.PriceProjection;
import com.betfair.enums.types.MatchProjection;
import com.betfair.enums.types.PriceData;
import com.betfair.exceptions.BetFairAPIException;

public class BetfairRequestThreadHelper extends AbstractRequestThreadHelper {

	private static Logger logger;

	private List<String> idsSublist;
	private MarketFilter filter;
	private IBetfairOperationsManager beom;
	private PriceData priceType;
	private String urlBase;
	private String suffixUrl;
	private String appKey;
	private String endpoint;
	private boolean methodParamName;

	
	public BetfairRequestThreadHelper(HashSet<String> eventsIds, List<String> idsSublist, MarketFilter marketFilter, Map<String, String> resultThreadRequest, String sessionToken, PriceData priceType, String urlBase, String suffixUrl, String appKey, String endpoint, boolean methodParamName) {
		this.resultThreadRequest = resultThreadRequest;
		this.filter = marketFilter;
		this.filter.setEventIds(eventsIds);	
		this.token = sessionToken;
		this.idsSublist = idsSublist;
		logger = Logger.getLogger(BetfairRequestThreadHelper.class);
		this.priceType = priceType;
		this.urlBase = urlBase;
		this.suffixUrl = suffixUrl;
		this.appKey = appKey;
		this.endpoint = endpoint;
		this.methodParamName = methodParamName;
	}
	
	@Override
	public void run() {
		try {
			//Preparazione del filtro per la chiamata sul marketBook
			PriceProjection priceProjection = new PriceProjection();
			
			Set<PriceData> priceDataSet = new HashSet<PriceData>();
			priceDataSet.add(priceType);
			priceProjection.setPriceData(priceDataSet);
			
			beom = BetfairOperationsManagerImpl.getInstance();
			
			String responseJson = null;
			//chiamata sul marketBook 
			try {				
				responseJson = beom.listMarketBook(idsSublist, priceProjection, null, MatchProjection.ROLLED_UP_BY_PRICE, null, appKey, token, urlBase, suffixUrl, endpoint, methodParamName);
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
