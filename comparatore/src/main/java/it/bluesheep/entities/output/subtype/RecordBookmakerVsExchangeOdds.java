package it.bluesheep.entities.output.subtype;

import it.bluesheep.entities.output.RecordOutput;

public class RecordBookmakerVsExchangeOdds extends RecordOutput {

	private final static String BETFAIREXCHANGE_NAME = "BetFairExchange";

	private double liquidità;
	
	public RecordBookmakerVsExchangeOdds() {
		super();
		this.bookmakerName2 = BETFAIREXCHANGE_NAME;
	}

	public double getLiquidità() {
		return liquidità;
	}

	public void setLiquidità(double liquidità) {
		this.liquidità = liquidità;
	}
	
}
