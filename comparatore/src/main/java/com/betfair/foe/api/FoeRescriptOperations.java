package com.betfair.foe.api;

import com.betfair.foe.entities.*;
import com.betfair.foe.enums.FoeOperation;
import com.betfair.foe.exceptions.FOEException;
import com.betfair.foe.util.JsonConverter;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoeRescriptOperations extends FoeOperations {

    private final static FoeRescriptOperations INSTANCE = new FoeRescriptOperations();
    private long requestId = 1;

    private FoeRescriptOperations() {
        // prevent instantiation
    }

    public static FoeRescriptOperations getInstance() {
        return INSTANCE;
    }

    public List<EventTypeResult> listEventTypes(BasicRequestParams params, String appKey, String ssoId) throws FOEException {
        return makeApiRequest(params, appKey, ssoId, LIST_EVENT_TYPES_PARAMS, FoeOperation.LIST_EVENT_TYPES, new TypeToken<List<EventTypeResult>>(){});
    }

    public List<EventResult> listEvents(BasicRequestParams params, String appKey, String ssoId) throws FOEException {
        return makeApiRequest(params, appKey, ssoId, LIST_EVENTS_PARAMS, FoeOperation.LIST_EVENTS, new TypeToken<List<EventResult>>(){});
    }

    public List<CompetitionResult> listCompetitions(BasicRequestParams params, String appKey, String ssoId) throws FOEException {
        return makeApiRequest(params, appKey, ssoId, LIST_COMPETITIONS_PARAMS, FoeOperation.LIST_COMPETITIONS, new TypeToken<List<CompetitionResult>>(){});
    }

    public List<MarketCatalogue> listMarketCatalogue(ListMarketCatalogueRequestParams params, String appKey, String ssoId) throws FOEException {
        return makeApiRequest(params, appKey, ssoId, LIST_MARKET_CATALOGUE_PARAMS, FoeOperation.LIST_MARKET_CATALOGUE, new TypeToken<List<MarketCatalogue>>(){});
    }

    public ListMarketPricesResult listMarketPrices(ListMarketPricesRequestParams params, String appKey, String ssoId) throws FOEException {
        return makeApiRequest(params, appKey, ssoId, LIST_MARKET_PRICES_PARAMS, FoeOperation.LIST_MARKET_PRICES, new TypeToken<ListMarketPricesResult>(){});
    }

    public ListMarketPricesResult bulkListMarketPrices(BulkListMarketPricesRequestParams params, String appKey, String ssoId) throws FOEException {
        return makeApiRequest(params, appKey, ssoId, BULK_LIST_MARKET_PRICES_PARAMS, FoeOperation.BULK_LIST_MARKET_PRICES, new TypeToken<ListMarketPricesResult>(){});
    }

    public List<BulkListMarketPricesIndex> bulkListMarketPricesIndex(BulkListMarketPricesIndexRequestParams params, String appKey, String ssoId) throws FOEException {
        return makeApiRequest(params, appKey, ssoId, BULK_LIST_MARKET_PRICES_INDEX_PARAMS, FoeOperation.BULK_LIST_MARKET_PRICES_INDEX, new TypeToken<List<BulkListMarketPricesIndex>>(){});
    }

    protected <T,R> R makeApiRequest(T params, String appKey, String ssoId, String parameterName,
                                     FoeOperation operationEnum, TypeToken typeToken) throws FOEException {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put(parameterName, params);
        String result = doRequest(operationEnum.getOperationName(), requestParams, appKey, ssoId);

        return JsonConverter.convertFromJson(result, typeToken.getType());
    }

    protected String doRequest(String operation, Map<String, Object> params, String appKey, String ssoToken) throws FOEException {
        String requestString;
        params.put("requestId", requestId++);
        requestString = JsonConverter.convertToJson(params);

        HttpUtil requester = new HttpUtil();
        String response = requester.sendPostRequestRescript(requestString, operation, appKey, ssoToken);
        if (response != null) {
            return response;
        } else {
            throw new FOEException();
        }
    }
}
