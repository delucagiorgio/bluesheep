package com.betfair.foe.entities;

import com.betfair.foe.enums.MarketProjection;
import com.betfair.foe.enums.MarketSort;

import java.util.Set;

public class ListMarketCatalogueRequestParams {

    private MarketFilter marketFilter;
    private Set<MarketProjection> marketProjection;
    private MarketSort sort;
    private Integer maxResults;
    private String locale;

    public MarketFilter getMarketFilter() {
        return marketFilter;
    }

    public void setMarketFilter(MarketFilter marketFilter) {
        this.marketFilter = marketFilter;
    }

    public Set<MarketProjection> getMarketProjection() {
        return marketProjection;
    }

    public void setMarketProjection(Set<MarketProjection> marketProjection) {
        this.marketProjection = marketProjection;
    }

    public MarketSort getSort() {
        return sort;
    }

    public void setSort(MarketSort sort) {
        this.sort = sort;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
