package com.betfair.foe.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.betfair.foe.FoeDemo;
import com.betfair.foe.entities.MarketFilter;
import com.betfair.foe.entities.PriceProjection;
import com.betfair.foe.enums.ApiNgOperation;
import com.betfair.foe.enums.MarketProjection;
import com.betfair.foe.enums.MarketSort;
import com.betfair.foe.enums.MatchProjection;
import com.betfair.foe.enums.OrderProjection;
import com.betfair.foe.exceptions.BetFairAPIException;
import com.betfair.foe.util.JsonConverter;
import com.betfair.foe.util.JsonrpcRequest;


public class ApiNgJsonRpcOperations extends ApiNgOperations{

    private static ApiNgJsonRpcOperations instance = null;

    private ApiNgJsonRpcOperations(){}

    public static ApiNgJsonRpcOperations getInstance(){
        if (instance == null){
            instance = new ApiNgJsonRpcOperations();
        }
        return instance;
    }

    public String listEventTypes(MarketFilter filter, String appKey, String ssoId) throws BetFairAPIException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(FILTER, filter);
        params.put(LOCALE, locale);
        String result = getInstance().makeRequest(ApiNgOperation.LISTEVENTTYPES.getOperationName(), params, appKey, ssoId);

        return result;

    }

    public String listMarketBook(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection,
                                           MatchProjection matchProjection, String currencyCode, String appKey, String ssoId) throws BetFairAPIException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_IDS, marketIds);
        params.put(PRICE_PROJECTION, priceProjection);
        params.put(ORDER_PROJECTION, orderProjection);
        params.put(MATCH_PROJECTION, matchProjection);
        params.put("currencyCode", currencyCode);
        String result = getInstance().makeRequest(ApiNgOperation.LISTMARKETBOOK.getOperationName(), params, appKey, ssoId);

        return result;


    }

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

    protected String makeRequest(String operation, Map<String, Object> params, String appKey, String ssoToken) {
        String requestString;
        //Handling the JSON-RPC request
        JsonrpcRequest request = new JsonrpcRequest();
        request.setId("1");
        request.setMethod(FoeDemo.getProperties().getProperty("SPORTS_APING_V1_0") + operation);
        request.setParams(params);

        requestString =  JsonConverter.convertToJson(request);

        //We need to pass the "sendPostRequest" method a string in util format:  requestString
        HttpUtil requester = new HttpUtil();
        return requester.sendPostRequestRescript(requestString, operation, appKey, ssoToken);

       }

}

