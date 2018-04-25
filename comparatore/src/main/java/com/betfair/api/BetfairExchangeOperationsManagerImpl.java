package com.betfair.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.betfair.entities.MarketFilter;
import com.betfair.entities.PriceProjection;
import com.betfair.enums.types.ApiNgOperation;
import com.betfair.enums.types.MarketProjection;
import com.betfair.enums.types.MarketSort;
import com.betfair.enums.types.MatchProjection;
import com.betfair.enums.types.OrderProjection;
import com.betfair.exceptions.BetFairAPIException;
import com.betfair.util.JsonConverter;
import com.betfair.util.JsonrpcRequest;

import it.bluesheep.BlueSheepComparatoreMain;


public class BetfairExchangeOperationsManagerImpl extends BetfairExchangeOperationsManager{

    private static BetfairExchangeOperationsManagerImpl instance = null;
    private static int requestCount;

    private BetfairExchangeOperationsManagerImpl(){
    	requestCount = 0;
    }

    public static BetfairExchangeOperationsManagerImpl getInstance(){
        if (instance == null){
            instance = new BetfairExchangeOperationsManagerImpl();
        }
        return instance;
    }

    @Override
    public String listEventTypes(MarketFilter filter, String appKey, String ssoId) throws BetFairAPIException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(FILTER, filter);
        params.put(LOCALE, locale);
        String result = getInstance().makeRequest(ApiNgOperation.LISTEVENTTYPES.getOperationName(), params, appKey, ssoId);

        return result;
    }

    @Override
    public String listMarketBook(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection,
    				MatchProjection matchProjection, String currencyCode, String appKey, String ssoId) throws BetFairAPIException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_IDS, marketIds);
        if(priceProjection != null) {
        	params.put(PRICE_PROJECTION, priceProjection);
        }
        params.put(ORDER_PROJECTION, orderProjection);
        params.put(MATCH_PROJECTION, matchProjection);
        params.put("currencyCode", currencyCode);
        String result = getInstance().makeRequest(ApiNgOperation.LISTMARKETBOOK.getOperationName(), params, appKey, ssoId);

        return result;
    }

    @Override
    public String listMarketCatalogue(MarketFilter filter, Set<MarketProjection> marketProjection,
                                                     MarketSort sort, String maxResult, String appKey, String ssoId) throws BetFairAPIException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(FILTER, filter);
        params.put(SORT, sort);
        params.put(MAX_RESULT, maxResult);
        params.put(MARKET_PROJECTION, marketProjection);
        String result = getInstance().makeRequest(ApiNgOperation.LISTMARKETCATALOGUE.getOperationName(), params, appKey, ssoId);

        return result;
    }
    
	@Override
	public String listEvents(MarketFilter filter,String appKey, String ssoId) throws BetFairAPIException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(FILTER, filter);
        params.put(LOCALE, locale);
        String result = getInstance().makeRequest(ApiNgOperation.LISTEVENTS.getOperationName(), params, appKey, ssoId);

        return result;
	}

    protected String makeRequest(String operation, Map<String, Object> params, String appKey, String ssoToken) {
        String requestString;
        JsonrpcRequest request = new JsonrpcRequest();
        requestCount++;
        request.setId(""+requestCount);
        request.setMethod(BlueSheepComparatoreMain.getProperties().getProperty("SPORTS_APING_V1_0") + operation);
        request.setParams(params);

        requestString =  JsonConverter.convertToJson(request);

        //We need to pass the "sendPostRequest" method a string in util format:  requestString
        HttpUtil requester = new HttpUtil();
        return requester.sendPostRequestRescript(requestString, operation, appKey, ssoToken);
       }
}

