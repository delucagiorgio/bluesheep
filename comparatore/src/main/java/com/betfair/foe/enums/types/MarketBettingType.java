package com.betfair.foe.enums.types;

public enum MarketBettingType {
    ODDS("MATCH_ODDS"),
    LINE("LINE"),
    FIXED_ODDS("FIXED_ODDS"),
    MOVING_HANDICAP("MOVING_HANDICAP");

    private String value;

    private MarketBettingType(String value) {
        this.value=value;
    }

    private MarketBettingType(int id) {
        value=String.format("%04d", id);
    }

    public String getCode() {
        return value;
    }
}
