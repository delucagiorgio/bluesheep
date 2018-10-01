package it.bluesheep.comparatore.entities.input.record;

import java.text.ParseException;
import java.util.Date;

import com.betfair.util.ISO8601DateTypeAdapter;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.serviceapi.Service;

public class TxOddsInputRecord extends AbstractInputRecord{
	
	private static final ISO8601DateTypeAdapter adapterDate = new ISO8601DateTypeAdapter();
	private String boid;
	
	public TxOddsInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1, String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		this.liquidita = -1;
		this.source = Service.TXODDS_SERVICENAME;
	}
	
	public TxOddsInputRecord(TxOddsInputRecord recordToBeMapped) {
		super(recordToBeMapped);
		this.liquidita = -1;
		this.source = Service.TXODDS_SERVICENAME;
	}

	public void setTimeOfInsertionInSystem(String stringFromJSON) {
		try {
			Date date = adapterDate.getDateFromString(stringFromJSON);
			this.setTimeInsertionInSystem(date.getTime());
		} catch (ParseException e) {
			this.setTimeInsertionInSystem(System.currentTimeMillis());
		}
	}

	public String getBoid() {
		return boid;
	}

	public void setBoid(String boid) {
		this.boid = boid;
	}
	
}
