package it.bluesheep.entities.output.subtype;

import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.ComparatoreConstants;

public class RecordBookmakerVsExchangeOdds extends RecordOutput {

	private double liquidita;
	
	public RecordBookmakerVsExchangeOdds() {
		super();
		this.bookmakerName2 = ComparatoreConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME;
	}

	public double getLiquidita() {
		return liquidita;
	}

	public void setLiquidita(double liquidita) {
		this.liquidita = liquidita;
	}
}
