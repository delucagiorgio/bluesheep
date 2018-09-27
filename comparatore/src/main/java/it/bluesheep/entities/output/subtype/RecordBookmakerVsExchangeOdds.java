package it.bluesheep.entities.output.subtype;

import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.ComparatoreConstants;

public class RecordBookmakerVsExchangeOdds extends RecordOutput {
	
	public RecordBookmakerVsExchangeOdds() {
		super();
		this.bookmakerName2 = ComparatoreConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME;
	}
}
