package it.bluesheep.entities.input.record;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.util.IKeyEventoComparator;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.util.CosineSimilarityUtil;
import it.bluesheep.util.BlueSheepLogger;

public class CSVInputRecord extends AbstractInputRecord implements IKeyEventoComparator {

	private static Logger logger = (new BlueSheepLogger(CSVInputRecord.class)).getLogger();
	
	public CSVInputRecord(AbstractInputRecord record) {
		super(record);
		this.liquidita = -1;
	}
	
	public CSVInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1, String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		this.liquidita = -1;
	}

	@Override
	public boolean isSameEventAbstractInputRecord(Date date, String sport, String partecipante1, String partecipante2) throws Exception {

		logger.log(Level.INFO, "Starting identification event for CSV line with ID = "  + this.filler);
		
		logger.log(Level.INFO, "Partecipante1 of CSVInputRecord = " + this.partecipante1 + "; Partecipante2 of CSVInputRecord = " + this.partecipante2 
				+ "; Partecipante1 of TxOddsRecord = " + partecipante1  + "; Partecipante2 of TxOddsRecord = " + partecipante2 +";");
		
		boolean allCheckOk = true;
		
		allCheckOk = allCheckOk && (compareParticipants(this.partecipante1, this.partecipante2, partecipante1, partecipante2));
		if(!allCheckOk) {
			logger.log(Level.WARNING, "Comparison players failed: values in CSVInputRecord are : P1 = " + this.partecipante1 + ";  P2 = " + this.partecipante2 + 
					"; values of TxOdds Events are : P1 " + partecipante1 + ";  P2 = " + partecipante2);
			logger.log(Level.INFO, "Trying to check similarity on players >= 0.8");
			
			CosineSimilarityUtil csu = new CosineSimilarityUtil();
			double cosSimPartecipant1 = csu.similarity(this.partecipante1, partecipante1);
			double cosSimPartecipant2 = csu.similarity(this.partecipante2, partecipante2);
			logger.log(Level.INFO, "Comparing players similarity: similarity for P1 = " + cosSimPartecipant1 + "; similarity for P2 = " + cosSimPartecipant2);
			if(cosSimPartecipant1 < 0.8 || cosSimPartecipant2 < 0.8) {
				logger.log(Level.WARNING, "Similarity check doesn't achieve minimum score");
				return false;
			}else {
				allCheckOk = true;
			}
			logger.log(Level.INFO, "Similarity check achieves minimum score : events are matched for CSV line with ID = "  + this.filler);
		}
		
		logger.log(Level.INFO, "Comparison of players successfully passed");
		
		logger.log(Level.INFO, "Line with ID = " + this.filler + " has been matched with TxOdds Event") ;

		
		return allCheckOk;
	}

}
