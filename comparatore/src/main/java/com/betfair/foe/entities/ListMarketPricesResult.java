package com.betfair.foe.entities;

import java.util.List;

public class ListMarketPricesResult {

    private List<MarketDetails> marketDetails;

    public List<MarketDetails> getMarketDetails() {
        return marketDetails;
    }

    public void setMarketDetails(List<MarketDetails> marketDetails) {
        this.marketDetails = marketDetails;
    }
}
