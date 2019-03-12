package it.bluesheep.comparatore.entities.input.record;

import java.util.Date;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.util.IKeyEventoComparator;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.serviceapi.Service;

public class BetfairSportbookInputRecord extends AbstractInputRecord implements IKeyEventoComparator {

	public BetfairSportbookInputRecord(AbstractInputRecord record) {
		super(record);
		this.bookmakerName = "Betfair SB";
		this.source = Service.BETFAIR_SB_SERVICENAME;
	}

	public BetfairSportbookInputRecord(Date dataOraEvento, Sport sport, String campionato, String partecipante1, String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		this.bookmakerName = "Betfair SB";
		this.source = Service.BETFAIR_SB_SERVICENAME;
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
	
	

}
