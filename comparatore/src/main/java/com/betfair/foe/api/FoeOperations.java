package com.betfair.foe.api;

import com.betfair.foe.entities.*;
import com.betfair.foe.enums.FoeOperation;
import com.betfair.foe.exceptions.FOEException;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public abstract class FoeOperations {

    static final String LIST_EVENT_TYPES_PARAMS = "listEventTypesRequestParams";
    static final String LIST_EVENTS_PARAMS = "listEventsRequestParams";
    static final String LIST_COMPETITIONS_PARAMS = "listCompetitionsRequestParams";
    static final String LIST_MARKET_CATALOGUE_PARAMS = "listMarketCatalogueRequestParams";
    static final String LIST_MARKET_PRICES_PARAMS = "listMarketPricesRequestParams";
    static final String BULK_LIST_MARKET_PRICES_PARAMS = "bulkListMarketPricesRequestParams";
    static final String BULK_LIST_MARKET_PRICES_INDEX_PARAMS = "bulkListMarketPricesIndexRequestParams";

    public abstract List<EventTypeResult> listEventTypes(BasicRequestParams params, String appKey, String ssoId) throws FOEException;

    public abstract List<EventResult> listEvents(BasicRequestParams params, String appKey, String ssoId) throws FOEException;

    public abstract List<CompetitionResult> listCompetitions(BasicRequestParams params, String appKey, String ssoId) throws FOEException;

    public abstract List<MarketCatalogue> listMarketCatalogue(ListMarketCatalogueRequestParams params, String appKey, String ssoId) throws FOEException;

    public abstract ListMarketPricesResult listMarketPrices(ListMarketPricesRequestParams params, String appKey, String ssoId) throws FOEException;

    public abstract ListMarketPricesResult bulkListMarketPrices(BulkListMarketPricesRequestParams params, String appKey, String ssoId) throws FOEException;

    public abstract List<BulkListMarketPricesIndex> bulkListMarketPricesIndex(BulkListMarketPricesIndexRequestParams params, String appKey, String ssoId) throws FOEException;

    protected abstract <T,R> R makeApiRequest(T params, String appKey, String ssoId, String parameterName, FoeOperation operationEnum, TypeToken typeToken) throws FOEException;

    protected abstract String doRequest(String operation, Map<String, Object> params, String appKey, String ssoToken) throws FOEException;
}
