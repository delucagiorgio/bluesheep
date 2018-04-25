package com.betfair.entities;

import java.util.Date;
import java.util.List;

import com.betfair.enums.types.LegType;
import com.betfair.enums.types.MarketStatus;

public class MarketDetails {

    private String marketId;
    private String eventId;
    private String marketName;
    private String marketType;
    private MarketStatus marketStatus;
    private Date marketStartTime;
    private Boolean inPlay;
    private Boolean livePriceAvailable;
    private Boolean guaranteedPriceAvailable;
    private List<RunnerDetails> runnerDetails;
    private Boolean eachWayAvailable;
    private Integer numberOfPlaces;
    private Integer placeFractionNumerator;
    private Integer placeFractionDenominator;
    private List<LegType> legTypes;
    private String linkedMarketId;
    private List<Rule4Deduction> rule4Deductions;


    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public MarketStatus getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(MarketStatus marketStatus) {
        this.marketStatus = marketStatus;
    }

    public Date getMarketStartTime() {
        return marketStartTime;
    }

    public void setMarketStartTime(Date marketStartTime) {
        this.marketStartTime = marketStartTime;
    }

    public Boolean getInPlay() {
        return inPlay;
    }

    public void setInPlay(Boolean inPlay) {
        this.inPlay = inPlay;
    }

    public Boolean getLivePriceAvailable() {
        return livePriceAvailable;
    }

    public void setLivePriceAvailable(Boolean livePriceAvailable) {
        this.livePriceAvailable = livePriceAvailable;
    }

    public Boolean getGuaranteedPriceAvailable() {
        return guaranteedPriceAvailable;
    }

    public void setGuaranteedPriceAvailable(Boolean guaranteedPriceAvailable) {
        this.guaranteedPriceAvailable = guaranteedPriceAvailable;
    }

    public List<RunnerDetails> getRunnerDetails() {
        return runnerDetails;
    }

    public void setRunnerDetails(List<RunnerDetails> runnerDetails) {
        this.runnerDetails = runnerDetails;
    }

    public Boolean getEachWayAvailable() {
        return eachWayAvailable;
    }

    public void setEachWayAvailable(Boolean eachWayAvailable) {
        this.eachWayAvailable = eachWayAvailable;
    }

    public Integer getNumberOfPlaces() {
        return numberOfPlaces;
    }

    public void setNumberOfPlaces(Integer numberOfPlaces) {
        this.numberOfPlaces = numberOfPlaces;
    }

    public Integer getPlaceFractionNumerator() {
        return placeFractionNumerator;
    }

    public void setPlaceFractionNumerator(Integer placeFractionNumerator) {
        this.placeFractionNumerator = placeFractionNumerator;
    }

    public Integer getPlaceFractionDenominator() {
        return placeFractionDenominator;
    }

    public void setPlaceFractionDenominator(Integer placeFractionDenominator) {
        this.placeFractionDenominator = placeFractionDenominator;
    }

    public List<LegType> getLegTypes() {
        return legTypes;
    }

    public void setLegTypes(List<LegType> legTypes) {
        this.legTypes = legTypes;
    }

    public String getLinkedMarketId() {
        return linkedMarketId;
    }

    public void setLinkedMarketId(String linkedMarketId) {
        this.linkedMarketId = linkedMarketId;
    }

    public List<Rule4Deduction> getRule4Deductions() {
        return rule4Deductions;
    }

    public void setRule4Deductions(List<Rule4Deduction> rule4Deductions) {
        this.rule4Deductions = rule4Deductions;
    }
}
