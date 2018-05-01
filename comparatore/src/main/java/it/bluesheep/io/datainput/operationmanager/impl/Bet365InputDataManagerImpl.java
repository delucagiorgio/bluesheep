package it.bluesheep.io.datainput.operationmanager.impl;

import java.util.ArrayList;
import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.ScommessaUtilManager;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;

public final class Bet365InputDataManagerImpl extends InputDataManagerImpl {
	
	@Override
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport) {
		
		
		
		return null;
	}

	@Override
	protected List<Scommessa> getCombinazioniSportScommessa(Sport sport) {
		List<Scommessa> scommessaList = new ArrayList<Scommessa>();
		
		if(sport.equals(Sport.TENNIS)) {
			scommessaList = ScommessaUtilManager.getScommessaListTennis2WayOdds();
		}else if(sport.equals(Sport.CALCIO)) {
			scommessaList = ScommessaUtilManager.getScommessaListCalcioAllOdds();
		}
		
		return scommessaList;
	}

}
