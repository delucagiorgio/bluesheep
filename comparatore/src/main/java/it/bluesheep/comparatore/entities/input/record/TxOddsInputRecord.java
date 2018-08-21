package it.bluesheep.comparatore.entities.input.record;

import java.text.ParseException;
import java.util.Date;

import com.betfair.util.ISO8601DateTypeAdapter;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.sport.Sport;

public class TxOddsInputRecord extends AbstractInputRecord{
	
	private static final ISO8601DateTypeAdapter adapterDate = new ISO8601DateTypeAdapter();
	
	public TxOddsInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1, String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
	}
	
	public TxOddsInputRecord(TxOddsInputRecord recordToBeMapped) {
		super(recordToBeMapped);
	}

	public void setTimeOfInsertionInSystem(String stringFromJSON) {
		try {
			Date date = adapterDate.getDateFromString(stringFromJSON);
			this.setTimeInsertionInSystem(date.getTime());
		} catch (ParseException e) {
			this.setTimeInsertionInSystem(System.currentTimeMillis());
		}
	}
	
}