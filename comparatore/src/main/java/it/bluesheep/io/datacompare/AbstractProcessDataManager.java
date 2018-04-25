package it.bluesheep.io.datacompare;

import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;

public abstract class AbstractProcessDataManager implements IProcessDataManager {

	@Override
	public abstract List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport);
	
	/**
	 * GD - 18/04/18
	 * Metodo che verifica il rating in base al minimo richiesto da business
	 * @param scommessaInputRecord1 record scommessa
	 * @param scommessaInputRecord2 record scommessa opposta
	 * @return true, se il rating è >= al valore richiesto dal business, false altrimenti
	 */
	protected abstract double getRatingByScommessaPair(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2);
	
	/**
	 * GD - 25/04/18
	 * Mappa il record di output partendo dalle informazioni mappate input 
	 * @param scommessaInputRecord1 record 1
	 * @param scommessaInputRecord2 record 2
	 * @param rating il rating tra le due scommesse
	 * @return il record di output con le informazioni relative alle due scommesse e al loro rating
	 */
	protected abstract RecordOutput mapBookVsBookRecordOutput(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2, double rating);


}
