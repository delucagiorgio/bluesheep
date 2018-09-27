package it.bluesheep.entities.input.record;

import java.util.Date;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.sport.Sport;

public class TxOddsInputRecord extends AbstractInputRecord{

	public TxOddsInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1, String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		this.liquidita = -1;
	}
	
	public TxOddsInputRecord(TxOddsInputRecord recordToBeMapped) {
		super(recordToBeMapped);
		this.liquidita = -1;
	}

}
