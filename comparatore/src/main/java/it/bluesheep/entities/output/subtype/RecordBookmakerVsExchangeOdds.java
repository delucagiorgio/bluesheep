package it.bluesheep.entities.output.subtype;

import it.bluesheep.entities.output.RecordOutput;

public class RecordBookmakerVsExchangeOdds extends RecordOutput {

	private final static String BETFAIREXCHANGE_NAME = "BetFairExchange";

	private double liquidita;
	
	public RecordBookmakerVsExchangeOdds() {
		super();
		this.bookmakerName2 = BETFAIREXCHANGE_NAME;
	}

	public double getLiquidita() {
		return liquidita;
	}

	public void setLiquidita(double liquidita) {
		this.liquidita = liquidita;
	}
}
