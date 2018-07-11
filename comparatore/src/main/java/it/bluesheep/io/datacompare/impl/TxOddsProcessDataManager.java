package it.bluesheep.io.datacompare.impl;

import java.util.ArrayList;
import java.util.List;

import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.AbstractProcessDataManager;
import it.bluesheep.io.datacompare.multithreadcompare.OddsComparisonSplitter;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;

/**
 * Classe utilizzata per definire i metodi su cui si basa la comparazione di quote tra i vari
 * bookmaker di TxOdds. Il fine è quello di processare una determinata quota con la sua
 * opposta per poi valutarne la giustezza d'abbinamento tramite il calcolo del rating1 (> 70%) 
 * @author Giorgio De Luca
 *
 */
public class TxOddsProcessDataManager extends AbstractProcessDataManager {
	
	protected TxOddsProcessDataManager() {
		super();
	}
	
	@Override
	public List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap sportMap, Sport sport) {
		
		List<RecordOutput> mappedOutputRecord = new ArrayList<RecordOutput>();
		OddsComparisonSplitter oddsComparisonSplitter = new OddsComparisonSplitter();
		mappedOutputRecord = oddsComparisonSplitter.startComparisonOdds(sportMap, sport, "TX_ODDS");

		return mappedOutputRecord;
	}
}
