package it.bluesheep.comparatore.entities.input.record;

import java.util.Date;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.util.IKeyEventoComparator;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepConstants;

public class BetfairExchangeInputRecord extends AbstractInputRecord implements IKeyEventoComparator{

	private boolean isLayRecord;
	
	public BetfairExchangeInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1,String partecipante2, String filler, double liquidita, boolean isLayRecord) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		this.isLayRecord = isLayRecord;
		this.liquidita = liquidita;
		if(isLayRecord) {
			this.bookmakerName = BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME_LAY;
		}else {
			this.bookmakerName = BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME_BACK;
		}
		this.source = Service.BETFAIR_SERVICENAME;
	}

	public BetfairExchangeInputRecord(BetfairExchangeInputRecord recordToBeMapped, boolean isLayRecord) {
		super(recordToBeMapped);
		this.isLayRecord = isLayRecord;
		if(isLayRecord) {
			this.bookmakerName = BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME_LAY;
		}else {
			this.bookmakerName = BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME_BACK;
		}	
		this.source = Service.BETFAIR_SERVICENAME;
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
