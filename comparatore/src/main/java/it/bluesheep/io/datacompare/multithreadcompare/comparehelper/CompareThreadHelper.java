package it.bluesheep.io.datacompare.multithreadcompare.comparehelper;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.util.BlueSheepLogger;

public abstract class CompareThreadHelper extends Thread {

	protected static Logger logger;
	protected static final int LOGGER_COMPARE_SIZE_PARTIAL_RESULT = 100;
	
	protected Map<String, List<RecordOutput>> oddsComparisonThreadMap;
	protected List<String> keyList;
	protected Map<String, Map<Scommessa, List<AbstractInputRecord>>> dataMap;
	protected Sport sport;
	protected int processedComparisonCounter;
	
	protected CompareThreadHelper(Map<String, List<RecordOutput>> oddsComparisonThreadMap, 
			List<String> keyList, 
			Map<String, Map<Scommessa, List<AbstractInputRecord>>> dataMap, 
			Sport sport) {
		super();
		this.keyList = keyList;
		this.dataMap = dataMap;
		this.oddsComparisonThreadMap = oddsComparisonThreadMap;
		this.sport = sport;
		logger = (new BlueSheepLogger(CompareThreadHelper.class)).getLogger();
	}
	
	@Override
	public abstract void run();
	
	/**
	 * GD - 25/04/18
	 * Mappa il record di output partendo dalle informazioni mappate input 
	 * @param scommessaInputRecord1 record 1
	 * @param scommessaInputRecord2 record 2
	 * @param rating1 il rating1 tra le due scommesse
	 * @return il record di output con le informazioni relative alle due scommesse e al loro rating1
	 * @throws Exception 
	 */
	protected abstract RecordOutput mapRecordOutput(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2, double rating);

	
	/**
	 * GD - 18/04/18
	 * Metodo che verifica il rating1 in base al minimo richiesto da business
	 * @param scommessaInputRecord1 record scommessa
	 * @param scommessaInputRecord2 record scommessa opposta
	 * @return true, se il rating1 è >= al valore richiesto dal business, false altrimenti
	 * @throws Exception 
	 */
	protected abstract double getRatingByScommessaPair(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2);
	
}
