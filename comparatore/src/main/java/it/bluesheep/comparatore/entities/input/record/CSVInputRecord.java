package it.bluesheep.comparatore.entities.input.record;

import java.util.Date;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.util.IKeyEventoComparator;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.util.CosineSimilarityUtil;

public class CSVInputRecord extends AbstractInputRecord implements IKeyEventoComparator {

	private static Logger logger;
	
	public CSVInputRecord(AbstractInputRecord record) {
		super(record);
		logger = Logger.getLogger(CSVInputRecord.class);
	}
	
	public CSVInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1, String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
	}

	@Override
	public boolean isSameEventAbstractInputRecord(Date date, String sport, String partecipante1, String partecipante2) throws Exception {

		logger.info("Starting identification event for CSV line with ID = "  + this.filler);
		
		logger.info("Partecipante1 of CSVInputRecord = " + this.partecipante1 + "; Partecipante2 of CSVInputRecord = " + this.partecipante2 
				+ "; Partecipante1 of TxOddsRecord = " + partecipante1  + "; Partecipante2 of TxOddsRecord = " + partecipante2 +";");
		
		boolean allCheckOk = true;
		
		allCheckOk = allCheckOk && (compareParticipants(this.partecipante1, this.partecipante2, partecipante1, partecipante2));
		if(!allCheckOk) {
			logger.warn("Comparison players failed: values in CSVInputRecord are : P1 = " + this.partecipante1 + ";  P2 = " + this.partecipante2 + 
					"; values of TxOdds Events are : P1 " + partecipante1 + ";  P2 = " + partecipante2);
			logger.info("Trying to check similarity on players >= 0.8");
			
			CosineSimilarityUtil csu = new CosineSimilarityUtil();
			double cosSimPartecipant1 = csu.similarity(this.partecipante1, partecipante1);
			double cosSimPartecipant2 = csu.similarity(this.partecipante2, partecipante2);
			logger.info("Comparing players similarity: similarity for P1 = " + cosSimPartecipant1 + "; similarity for P2 = " + cosSimPartecipant2);
			if(cosSimPartecipant1 < 0.8 || cosSimPartecipant2 < 0.8) {
				logger.warn("Similarity check doesn't achieve minimum score");
				return false;
			}else {
				allCheckOk = true;
			}
			logger.debug("Similarity check achieves minimum score : events are matched for CSV line with ID = "  + this.filler);
		}
		
		logger.debug("Comparison of players successfully passed");
		
		logger.info("Line with ID = " + this.filler + " has been matched with a TxOdds Event") ;

		
		return allCheckOk;
	}

}
