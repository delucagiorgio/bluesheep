package com.betfair.api;

import java.util.List;
import java.util.Set;

import com.betfair.entities.MarketFilter;
import com.betfair.entities.PriceProjection;
import com.betfair.enums.types.MarketProjection;
import com.betfair.enums.types.MarketSort;
import com.betfair.enums.types.MatchProjection;
import com.betfair.enums.types.OrderProjection;
import com.betfair.exceptions.BetFairAPIException;

public interface IBetfairOperationsManager {
	
	public String listMarketBook(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection,
										  MatchProjection matchProjection, String currencyCode, String appKey, String ssoId, String urlBase, String suffixUrl, String endpoint, boolean methodParamName) throws BetFairAPIException;

    public String listMarketCatalogue(MarketFilter filter, Set<MarketProjection> marketProjection, 
    										   MarketSort sort, String maxResult, String appKey, String ssoId, String urlBase, String suffixUrl, String endpoint, boolean methodParamName) throws BetFairAPIException;
    
    public String listEvents(MarketFilter filter, String appKey, String ssoId, String urlBase, String suffixUrl, String endpoint, boolean methodParamName) throws BetFairAPIException;
    
//    public String listEventsSportbook(MarketFilter filter, String appKey, String ssoId, String urlBase, String suffixUrl) throws BetFairAPIException;
//    
//    public String listMarketCatalogueSportbook(MarketFilter filter, Set<MarketProjection> marketProjection, MarketSort sort, String maxResult, String appKey, String ssoId, String urlBase, String suffixUrl) throws BetFairAPIException;
//
//    public String listMarketPrices(ListMarketPricesRequestParams params, String appKey, String ssoId, String urlBase, String suffixUrl) throws BetFairAPIException;

}
