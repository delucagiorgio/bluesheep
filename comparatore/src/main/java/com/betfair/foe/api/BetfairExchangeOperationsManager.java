package com.betfair.foe.api;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.betfair.foe.entities.MarketFilter;
import com.betfair.foe.entities.PriceProjection;
import com.betfair.foe.enums.types.MarketProjection;
import com.betfair.foe.enums.types.MarketSort;
import com.betfair.foe.enums.types.MatchProjection;
import com.betfair.foe.enums.types.OrderProjection;
import com.betfair.foe.exceptions.BetFairAPIException;


public abstract class BetfairExchangeOperationsManager implements IBetfairExchangeOperationsManager{
	protected final String FILTER = "filter";
    protected final String LOCALE = "locale";
    protected final String SORT = "sort";
    protected final String MAX_RESULT = "maxResults";
    protected final String MARKET_IDS = "marketIds";
    protected final String MARKET_ID = "marketId";
    protected final String INSTRUCTIONS = "instructions";
    protected final String CUSTOMER_REF = "customerRef";
    protected final String MARKET_PROJECTION = "marketProjection";
    protected final String PRICE_PROJECTION = "priceProjection";
    protected final String MATCH_PROJECTION = "matchProjection";
    protected final String ORDER_PROJECTION = "orderProjection";
    protected final String locale = Locale.ITALIAN.toString();

	public abstract String listEventTypes(MarketFilter filter, String appKey, String ssoId) throws BetFairAPIException;

	public abstract String listMarketBook(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection,
						MatchProjection matchProjection, String currencyCode, String appKey, String ssoId) throws BetFairAPIException;

    public abstract String listMarketCatalogue(MarketFilter filter, Set<MarketProjection> marketProjection,
        MarketSort sort, String maxResult, String appKey, String ssoId) throws BetFairAPIException;
    
    public abstract String listEvents(MarketFilter filter, String appKey, String ssoId) throws BetFairAPIException;

    protected abstract String makeRequest(String operation, Map<String, Object> params, String appKey, String ssoToken) throws  BetFairAPIException;

}

