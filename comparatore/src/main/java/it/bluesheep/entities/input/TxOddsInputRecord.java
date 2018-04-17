package it.bluesheep.entities.input;

import java.util.Date;

public class TxOddsInputRecord extends AbstractInputRecord{

	public TxOddsInputRecord(Date dataOraEvento, String campionato, String partecipante1, String partecipante2) {
		super(dataOraEvento, campionato, partecipante1, partecipante2);
	}
	
	public TxOddsInputRecord(TxOddsInputRecord recordToBeMapped) {
		super(recordToBeMapped);
	}

}
