package com.betfair.entities;

public class BasicRequestParams {

    private MarketFilter marketFilter;
    private String locale;

    public MarketFilter getMarketFilter() {
        return marketFilter;
    }

    public void setMarketFilter(MarketFilter marketFilter) {
        this.marketFilter = marketFilter;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
