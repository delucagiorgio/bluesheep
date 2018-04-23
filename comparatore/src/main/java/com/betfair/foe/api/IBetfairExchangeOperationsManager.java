package com.betfair.foe.api;

import java.util.List;
import java.util.Set;

import com.betfair.foe.entities.MarketFilter;
import com.betfair.foe.entities.PriceProjection;
import com.betfair.foe.enums.types.MarketProjection;
import com.betfair.foe.enums.types.MarketSort;
import com.betfair.foe.enums.types.MatchProjection;
import com.betfair.foe.enums.types.OrderProjection;
import com.betfair.foe.exceptions.BetFairAPIException;

public interface IBetfairExchangeOperationsManager {
	
	public String listEventTypes(MarketFilter filter, String appKey, String ssoId) throws BetFairAPIException;

	public String listMarketBook(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection,
										  MatchProjection matchProjection, String currencyCode, String appKey, String ssoId) throws BetFairAPIException;

    public String listMarketCatalogue(MarketFilter filter, Set<MarketProjection> marketProjection, 
    										   MarketSort sort, String maxResult, String appKey, String ssoId) throws BetFairAPIException;
    
    public String listEvents(MarketFilter filter, String appKey, String ssoId) throws BetFairAPIException;
}
