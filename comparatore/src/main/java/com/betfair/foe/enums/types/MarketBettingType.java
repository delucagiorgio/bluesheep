package com.betfair.foe.enums.types;

public enum MarketBettingType {
    ODDS("MATCH_ODDS"),
    OVERUNDER_05("OVER_UNDER_05"),
    OVERUNDER_15("OVER_UNDER_15"),
    OVERUNDER_25("OVER_UNDER_25"),
    OVERUNDER_35("OVER_UNDER_35"),
    OVERUNDER_45("OVER_UNDER_45"),
    GOAL_NOGOAL("BOTH_TEAMS_TO_SCORE");

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
