package it.bluesheep.entities.input;

import java.util.Date;

public class BetfairExchangeInputRecord extends AbstractInputRecord{

	public BetfairExchangeInputRecord(Date dataOraEvento, String campionato, String partecipante1,String partecipante2) {
		super(dataOraEvento, campionato, partecipante1, partecipante2);
		this.bookmakerName = "Betfair Exchange";
	}

}
