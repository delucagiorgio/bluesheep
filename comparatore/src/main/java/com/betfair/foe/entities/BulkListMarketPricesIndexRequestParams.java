package com.betfair.foe.entities;

import java.util.Set;

public class BulkListMarketPricesIndexRequestParams {

    private Set<String> eventTypeIds;

    public Set<String> getEventTypeIds() {
        return eventTypeIds;
    }

    public void setEventTypeIds(Set<String> eventTypeIds) {
        this.eventTypeIds = eventTypeIds;
    }
}
