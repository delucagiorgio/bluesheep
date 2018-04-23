package it.bluesheep.entities.input;

import java.util.Date;

import it.bluesheep.entities.util.sport.Sport;

public class BetfairExchangeInputRecord extends AbstractInputRecord{

	private double liquidita;
	
	public BetfairExchangeInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1,String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		this.bookmakerName = "Betfair Exchange";
	}

	public BetfairExchangeInputRecord(BetfairExchangeInputRecord recordToBeMapped) {
		super(recordToBeMapped);
	}

	public double getLiquidita() {
		return liquidita;
	}

	public void setLiquidita(double liquidita) {
		this.liquidita = liquidita;
	}
	
}
