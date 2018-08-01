package it.bluesheep.entities.input.record;

import java.util.Date;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.util.IKeyEventoComparator;
import it.bluesheep.entities.util.ComparatoreConstants;
import it.bluesheep.entities.util.sport.Sport;

public class Bet365InputRecord extends AbstractInputRecord implements IKeyEventoComparator{

	public Bet365InputRecord(Date dataOraEvento, Sport sport, String campionato, String partecipante1, String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		this.bookmakerName = ComparatoreConstants.BET365_BOOKMAKER_NAME;
	}
	
	public Bet365InputRecord(Bet365InputRecord recordToBeMapped) {
		super(recordToBeMapped);
		this.bookmakerName = ComparatoreConstants.BET365_BOOKMAKER_NAME;
	}
	
	@Override
	public boolean isSameEventAbstractInputRecord(Date dataOraEvento, String sport, String partecipante1, String partecipante2) throws Exception {
		if(compareParticipants(this.partecipante1, this.partecipante2, partecipante1, partecipante2)
				&& this.sport.getCode().equals(sport)
				&& compareDate(this.dataOraEvento, dataOraEvento)) {
			return true;
		}		
		return false;
	}
}
