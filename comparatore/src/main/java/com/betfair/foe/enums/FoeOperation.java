package com.betfair.foe.enums;

public enum FoeOperation {
    LIST_EVENT_TYPES("listEventTypes"),
    LIST_EVENTS("listEvents"),
    LIST_COMPETITIONS("listCompetitions"),
    LIST_MARKET_CATALOGUE("listMarketCatalogue"),
    LIST_MARKET_PRICES("listMarketPrices"),
    BULK_LIST_MARKET_PRICES("bulkListMarketPrices"),
    BULK_LIST_MARKET_PRICES_INDEX("bulkListMarketPricesIndex");

    private String operationName;

    FoeOperation(String operationName){
        this.operationName = operationName;
    }

    public String getOperationName() {
        return operationName;
    }

}
