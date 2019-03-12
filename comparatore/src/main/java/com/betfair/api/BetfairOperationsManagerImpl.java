package com.betfair.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.betfair.entities.BasicRequestParams;
import com.betfair.entities.ListMarketCatalogueRequestParams;
import com.betfair.entities.ListMarketPricesRequestParams;
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


public class BetfairOperationsManagerImpl extends BetfairOperationsManager{

    private static BetfairOperationsManagerImpl instance = null;
    private static int requestCount;

    private BetfairOperationsManagerImpl(){
    	requestCount = 0;
    }

    public static BetfairOperationsManagerImpl getInstance(){
        if (instance == null){
            instance = new BetfairOperationsManagerImpl();
        }
        return instance;
    }



    @Override
    public String listMarketBook(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection,
    				MatchProjection matchProjection, String currencyCode, String appKey, String ssoId, String urlBase, String suffixUrl, String endpoint, boolean methodParamName) throws BetFairAPIException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_IDS, marketIds);
        if(priceProjection != null) {
        	params.put(PRICE_PROJECTION, priceProjection);
        }
        params.put(ORDER_PROJECTION, orderProjection);
        params.put(MATCH_PROJECTION, matchProjection);
        params.put("currencyCode", currencyCode);
        String result = makeRequest(ApiNgOperation.LISTMARKETBOOK.getOperationName(), params, appKey, ssoId, urlBase, suffixUrl, endpoint);

        return result;
    }
    
    @Override
    public String listMarketPrice(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection,
    				MatchProjection matchProjection, String currencyCode, String appKey, String ssoId, String urlBase, String suffixUrl, String endpoint) throws BetFairAPIException {
        Map<String, Object> params = new HashMap<String, Object>();
    	ListMarketPricesRequestParams lmrp = new ListMarketPricesRequestParams();
        lmrp.setMarketIds(marketIds);
        params.put(LIST_MARKET_PRICES_PARAMS, lmrp);
        String result = makeRequest(ApiNgOperation.LIST_MARKET_PRICES.getOperationName(), params, appKey, ssoId, urlBase, suffixUrl, endpoint);

        return result;
    }

    @Override
    public String listMarketCatalogue(MarketFilter filter, Set<MarketProjection> marketProjection,
                                                     MarketSort sort, String maxResult, String appKey, String ssoId, String urlBase, String suffixUrl, String endpoint, boolean methodParamName, Set<String> marketTypes) throws BetFairAPIException {
        Map<String, Object> params = new HashMap<String, Object>();
        if(methodParamName) {
        	ListMarketCatalogueRequestParams lmcrp = new ListMarketCatalogueRequestParams();
        	lmcrp.setLocale(locale);
        	filter.setMarketTypes(marketTypes);
        	filter.setMarketTypeCodes(null);
        	lmcrp.setMarketFilter(filter);
        	lmcrp.setMarketProjection(marketProjection);
        	lmcrp.setMaxResults(Integer.parseInt(maxResult));
        	lmcrp.setSort(sort);
        	params.put(LIST_MARKET_CATALOGUE_PARAMS, lmcrp);
        }else {
	        params.put(LOCALE, locale);
	        params.put(FILTER, filter);
	        params.put(SORT, sort);
	        params.put(MAX_RESULT, maxResult);
	        params.put(MARKET_PROJECTION, marketProjection);
        }
        String result = makeRequest(ApiNgOperation.LISTMARKETCATALOGUE.getOperationName(), params, appKey, ssoId, urlBase, suffixUrl, endpoint);

        return result;
    }
    
	@Override
	public String listEvents(MarketFilter filter,String appKey, String ssoId, String urlBase, String suffixUrl, String endpoint, boolean methodParamName) throws BetFairAPIException {
        Map<String, Object> params = new HashMap<String, Object>();
        if(methodParamName) {
	        BasicRequestParams basicReqParam = new BasicRequestParams();
	        basicReqParam.setMarketFilter(filter);
	        basicReqParam.setLocale(locale);
	        params.put(LIST_EVENTS_PARAMS, basicReqParam);
        }else {
	        params.put(FILTER, filter);
	        params.put(LOCALE, locale);
        }
        String result = makeRequest(ApiNgOperation.LISTEVENTS.getOperationName(), params, appKey, ssoId, urlBase, suffixUrl, endpoint);

        return result;
	}

    protected String makeRequest(String operation, Map<String, Object> params, String appKey, String ssoToken, String urlBase, String suffixUrl, String endpoint) {
        String requestString;
        JsonrpcRequest request = new JsonrpcRequest();
        requestCount++;
        request.setId(""+requestCount);
        request.setMethod(endpoint + operation);
        request.setParams(params);

        requestString =  JsonConverter.convertToJson(request);

        //We need to pass the "sendPostRequest" method a string in util format:  requestString
        HttpUtil requester = new HttpUtil();
        return requester.sendPostRequestRescript(requestString, operation, appKey, ssoToken, urlBase, suffixUrl);
       }

	@Override
	public String listCompetitions(MarketFilter filter, String appKey, String sessionToken, String urlBase, String endpoint, String suffixUrl) throws BetFairAPIException {
        Map<String, Object> params = new HashMap<String, Object>();
		BasicRequestParams basicReqParam = new BasicRequestParams();
        basicReqParam.setMarketFilter(filter);
        basicReqParam.setLocale(locale);
        params.put(LIST_COMPETITIONS_PARAMS, basicReqParam);
        
        return makeRequest(ApiNgOperation.LISTCOMPETITIONS.getOperationName(), params, appKey, sessionToken, urlBase, suffixUrl, endpoint);
	}
}

