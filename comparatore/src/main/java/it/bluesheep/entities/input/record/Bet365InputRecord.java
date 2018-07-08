package it.bluesheep.entities.input.record;

import java.util.Date;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.util.IKeyEventoComparator;
import it.bluesheep.entities.util.sport.Sport;

public class Bet365InputRecord extends AbstractInputRecord implements IKeyEventoComparator{

	public Bet365InputRecord(Date dataOraEvento, Sport sport, String campionato, String partecipante1, String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		this.bookmakerName = "Bet365";
	}
	
	public Bet365InputRecord(Bet365InputRecord recordToBeMapped) {
		super(recordToBeMapped);
		this.bookmakerName = "Bet365";
	}
	
	@Override
	public boolean isSameEventAbstractInputRecord(Date dataOraEvento, String sport, String partecipante1, String partecipante2) throws Exception {
		if(compareDate(this.dataOraEvento, dataOraEvento)
				&& compareParticipants(this.partecipante1, this.partecipante2, partecipante1, partecipante2)) {
			return true;
		}		
		return false;
	}
}
