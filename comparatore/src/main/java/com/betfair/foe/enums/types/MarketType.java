package com.betfair.foe.enums.types;

//da pensare come valutare i possibili abbinamenti
public enum MarketType {

	OVER_UNDER_65(false),
	OVER_UNDER_55(false),
	OVER_UNDER_45(true),
	MATCH_ODDS(true),
	CORRECT_SCORE_IT(false),
	OVER_UNDER_15(true),
	OVER_UNDER_25(true),
	BOTH_TEAMS_TO_SCORE(true),
	OVER_UNDER_35(true),
	TEAM_B_TO_SCORE(false),
	FIRST_HALF_GOALS_25(false),
	FIRST_HALF_GOALS_15(false),
	TEAM_A_TO_SCORE(false),
	OVER_UNDER_05(true),
	FIRST_HALF_GOALS_05(false),
	HALF_TIME(false),
	HALF_TIME_SCORE(false),
	HALF_TIME_FULL_TIME(false),
	TO_QUALIFY(false),
	EXTRA_TIME(false),
	SET_WINNER(true),
	SET_BETTING(false);
	
	private boolean required;
	
	private MarketType(boolean required) {
		this.required = required;
	}
	
	public boolean isRequired() {
		return required;
	}
	
}
