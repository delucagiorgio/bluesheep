package it.bluesheep.comparatore.entities.output.subtype;

import it.bluesheep.comparatore.entities.output.RecordOutput;

public class RecordBookmakerVsExchangeOdds extends RecordOutput {

	private final static String BETFAIREXCHANGE_NAME = "BetFairExchange";

	
	public RecordBookmakerVsExchangeOdds() {
		super();
		this.bookmakerName2 = BETFAIREXCHANGE_NAME;
	}
}
