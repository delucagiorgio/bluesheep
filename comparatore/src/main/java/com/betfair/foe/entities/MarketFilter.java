package com.betfair.foe.entities;


import com.betfair.foe.enums.MarketBettingType;

import java.util.Set;


public class MarketFilter {

    private String textQuery;
    private Set<String> eventTypeIds;
    private Set<String> eventIds;
    private Set<String> competitionIds;
    private Set<String> marketIds;
    private Set<String> venues;
    private Boolean inPlayOnly;
    private Boolean bspOnly;
    private Boolean turnInPlayEnabled;
    private Set<MarketBettingType> marketBettingTypes;
    private Set<String> marketCountries;
    private Set<String> marketTypeCodes;
    private TimeRange marketStartTime;

    public String getTextQuery() {
        return textQuery;
    }

    public void setTextQuery(String textQuery) {
        this.textQuery = textQuery;
    }

    public Set<String> getEventTypeIds() {
        return eventTypeIds;
    }

    public void setEventTypeIds(Set<String> eventTypeIds) {
        this.eventTypeIds = eventTypeIds;
    }

    public Set<String> getMarketIds() {
        return marketIds;
    }

    public void setMarketIds(Set<String> marketIds) {
        this.marketIds = marketIds;
    }

    public Boolean getInPlayOnly() {
        return inPlayOnly;
    }

    public void setInPlayOnly(Boolean inPlayOnly) {
        this.inPlayOnly = inPlayOnly;
    }

    public Set<String> getEventIds() {
        return eventIds;
    }

    public void setEventIds(Set<String> eventIds) {
        this.eventIds = eventIds;
    }

    public Set<String> getCompetitionIds() {
        return competitionIds;
    }

    public void setCompetitionIds(Set<String> competitionIds) {
        this.competitionIds = competitionIds;
    }

    public Set<String> getVenues() {
        return venues;
    }

    public void setVenues(Set<String> venues) {
        this.venues = venues;
    }

    public Boolean getBspOnly() {
        return bspOnly;
    }

    public void setBspOnly(Boolean bspOnly) {
        this.bspOnly = bspOnly;
    }

    public Boolean getTurnInPlayEnabled() {
        return turnInPlayEnabled;
    }

    public void setTurnInPlayEnabled(Boolean turnInPlayEnabled) {
        this.turnInPlayEnabled = turnInPlayEnabled;
    }

    public Set<MarketBettingType> getMarketBettingTypes() {
        return marketBettingTypes;
    }

    public void setMarketBettingTypes(Set<MarketBettingType> marketBettingTypes) {
        this.marketBettingTypes = marketBettingTypes;
    }

    public Set<String> getMarketCountries() {
        return marketCountries;
    }

    public void setMarketCountries(Set<String> marketCountries) {
        this.marketCountries = marketCountries;
    }

    public Set<String> getMarketTypeCodes() {
        return marketTypeCodes;
    }

    public void setMarketTypeCodes(Set<String> marketTypeCodes) {
        this.marketTypeCodes = marketTypeCodes;
    }

    public TimeRange getMarketStartTime() {
        return marketStartTime;
    }

    public void setMarketStartTime(TimeRange marketStartTime) {
        this.marketStartTime = marketStartTime;
    }

    public String toString() {
        return "{" + "" + "textQuery=" + getTextQuery() + "," + "eventTypeIds=" + getEventTypeIds()
                + "," + "eventIds=" + getEventIds() + "," + "competitionIds="
                + getCompetitionIds() + "," + "marketIds=" + getMarketIds()
                + "," + "venues=" + getVenues() + "," + "bspOnly="
                + getBspOnly() + "," + "turnInPlayEnabled="
                + getTurnInPlayEnabled() + "," + "inPlayOnly="
                + getInPlayOnly() + "," + "marketBettingTypes="
                + getMarketBettingTypes() + "," + "marketCountries="
                + getMarketCountries() + "," + "marketTypeCodes="
                + getMarketTypeCodes() + "," + "marketStartTime="
                + getMarketStartTime() + "," + "}";
    }

}
