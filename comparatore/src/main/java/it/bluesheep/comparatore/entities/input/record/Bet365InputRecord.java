package it.bluesheep.comparatore.entities.input.record;

import java.util.Date;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.util.IKeyEventoComparator;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.util.BlueSheepConstants;

public class Bet365InputRecord extends AbstractInputRecord implements IKeyEventoComparator{

	public Bet365InputRecord(Date dataOraEvento, Sport sport, String campionato, String partecipante1, String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		this.bookmakerName = BlueSheepConstants.BET365_BOOKMAKER_NAME;
	}
	
	public Bet365InputRecord(Bet365InputRecord recordToBeMapped) {
		super(recordToBeMapped);
		this.bookmakerName = BlueSheepConstants.BET365_BOOKMAKER_NAME;
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
