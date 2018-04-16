package com.betfair.foe.entities;


import java.util.Date;

public class MarketDescription {

    private Boolean bspMarket;
    private Date marketTime;
    private Date suspendTime;
    private String bettingType;
    private Boolean turnInPlayEnabled;
    private String marketType;
    private String exchangeMarketId;
    private Integer betDelay;

    public Boolean getBspMarket() {
        return bspMarket;
    }

    public void setBspMarket(Boolean bspMarket) {
        this.bspMarket = bspMarket;
    }

    public Date getMarketTime() {
        return marketTime;
    }

    public void setMarketTime(Date marketTime) {
        this.marketTime = marketTime;
    }

    public Date getSuspendTime() {
        return suspendTime;
    }

    public void setSuspendTime(Date suspendTime) {
        this.suspendTime = suspendTime;
    }

    public String getBettingType() {
        return bettingType;
    }

    public void setBettingType(String bettingType) {
        this.bettingType = bettingType;
    }

    public Boolean getTurnInPlayEnabled() {
        return turnInPlayEnabled;
    }

    public void setTurnInPlayEnabled(Boolean turnInPlayEnabled) {
        this.turnInPlayEnabled = turnInPlayEnabled;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public String getExchangeMarketId() {
        return exchangeMarketId;
    }

    public void setExchangeMarketId(String exchangeMarketId) {
        this.exchangeMarketId = exchangeMarketId;
    }

    public Integer getBetDelay() {
        return betDelay;
    }

    public void setBetDelay(Integer betDelay) {
        this.betDelay = betDelay;
    }

    public String toString() {
        return "{" + "" + "bspMarket=" + getBspMarket() + "," + "marketTime="
                + getMarketTime() + "," + "suspendTime=" + getSuspendTime()
                + "," + "bettingType=" + getBettingType() + "," + "turnInPlayEnabled="
                + getTurnInPlayEnabled() + "," + "marketType="
                + getMarketType() + "," + "exchangeMarketId=" + getExchangeMarketId() + ","
                + "betDelay=" + getBetDelay() + "," + "}";
    }
}
