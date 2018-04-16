package com.betfair.foe.entities;

import com.betfair.foe.enums.ResponseFilter;

import java.util.Set;

public class BulkListMarketPricesRequestParams {

    private String eventTypeId;
    private String competitionId;
    private Set<ResponseFilter> responseFilter;

    public String getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(String eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(String competitionId) {
        this.competitionId = competitionId;
    }

    public Set<ResponseFilter> getResponseFilter() {
        return responseFilter;
    }

    public void setResponseFilter(Set<ResponseFilter> responseFilter) {
        this.responseFilter = responseFilter;
    }
}
