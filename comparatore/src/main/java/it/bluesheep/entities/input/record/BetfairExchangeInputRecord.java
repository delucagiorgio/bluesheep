package it.bluesheep.entities.input.record;

import java.util.Date;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.sport.Sport;

public class BetfairExchangeInputRecord extends AbstractInputRecord{

	private double liquidita;
	
	public BetfairExchangeInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1,String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		this.bookmakerName = "Betfair Exchange";
	}

	public BetfairExchangeInputRecord(BetfairExchangeInputRecord recordToBeMapped) {
		super(recordToBeMapped);
		this.bookmakerName = "Betfair Exchange";
	}

	public double getLiquidita() {
		return liquidita;
	}

	public void setLiquidita(double liquidita) {
		this.liquidita = liquidita;
	}
	
	@Override
	public boolean isSameEventAbstractInputRecord (AbstractInputRecord abstractInputRecord) {
		if(compareParticipants(this.partecipante1, this.partecipante2, abstractInputRecord.getPartecipante1(), abstractInputRecord.getPartecipante2())
				&& compareSport(this.getSport(), abstractInputRecord.getSport())
				&& compareDate(this.getDataOraEvento(), abstractInputRecord.getDataOraEvento())
				&& isSameScommessa(this.tipoScommessa, abstractInputRecord.getTipoScommessa())) {
			return true;
		}		
		return false;
	}
	
}
