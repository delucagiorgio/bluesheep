package com.betfair.foe.entities;

public class BulkListMarketIndex {

    private String eventTypeId;
    private String competitionId;

    public BulkListMarketIndex() {}

    public BulkListMarketIndex(String eventTypeId, String competitionId) {
        this.eventTypeId = eventTypeId;
        this.competitionId = competitionId;
    }

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
}
