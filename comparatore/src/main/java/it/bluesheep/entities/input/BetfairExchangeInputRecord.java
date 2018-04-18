package it.bluesheep.entities.input;

import java.util.Date;

import it.bluesheep.entities.util.sport.Sport;

public class BetfairExchangeInputRecord extends AbstractInputRecord{

	public BetfairExchangeInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1,String partecipante2) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2);
		this.bookmakerName = "Betfair Exchange";
	}

}
