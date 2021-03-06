package it.bluesheep.entities.input.record;

import java.util.Date;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.util.IKeyEventoComparator;
import it.bluesheep.entities.util.ComparatoreConstants;
import it.bluesheep.entities.util.sport.Sport;

public class BetfairExchangeInputRecord extends AbstractInputRecord implements IKeyEventoComparator{

	private boolean isLayRecord;
	
	public BetfairExchangeInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1,String partecipante2, String filler, double liquidita, boolean isLayRecord) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		this.bookmakerName = ComparatoreConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME;
		this.isLayRecord = isLayRecord;
		this.liquidita = liquidita;
	}

	public BetfairExchangeInputRecord(BetfairExchangeInputRecord recordToBeMapped) {
		super(recordToBeMapped);
		this.bookmakerName = ComparatoreConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME;
		this.isLayRecord = recordToBeMapped.isLayRecord();
	}

	@Override
	public boolean isSameEventAbstractInputRecord (Date dataOraEvento, String sport, String partecipante1, String partecipante2) {
		if(compareParticipants(this.partecipante1, this.partecipante2, partecipante1, partecipante2)
				&& this.sport.getCode().equalsIgnoreCase(sport)
				&& compareDate(this.getDataOraEvento(), dataOraEvento)) {
			return true;
		}		
		return false;
	}

	public boolean isLayRecord() {
		return isLayRecord;
	}

	public void setLayRecord(boolean isLayRecord) {
		this.isLayRecord = isLayRecord;
	}
	
}
