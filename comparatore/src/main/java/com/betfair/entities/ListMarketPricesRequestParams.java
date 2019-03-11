package com.betfair.entities;

import java.util.List;
import java.util.Set;

import com.betfair.enums.types.ResponseFilter;

public class ListMarketPricesRequestParams {

    private List<String> marketIds;
    private Set<ResponseFilter> responseFilter;

    public List<String> getMarketIds() {
        return marketIds;
    }

    public void setMarketIds(List<String> marketIds) {
        this.marketIds = marketIds;
    }

    public Set<ResponseFilter> getResponseFilter() {
        return responseFilter;
    }

    public void setResponseFilter(Set<ResponseFilter> responseFilter) {
        this.responseFilter = responseFilter;
    }
}
