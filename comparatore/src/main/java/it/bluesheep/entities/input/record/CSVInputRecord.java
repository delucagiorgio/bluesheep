package it.bluesheep.entities.input.record;

import java.util.Date;
import java.util.logging.Logger;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.util.IKeyEventoComparator;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.util.CosineSimilarityUtil;
import it.bluesheep.util.BlueSheepLogger;

public class CSVInputRecord extends AbstractInputRecord implements IKeyEventoComparator {

	private static Logger logger;
	
	public CSVInputRecord(AbstractInputRecord record) {
		super(record);
		logger = (new BlueSheepLogger(CSVInputRecord.class)).getLogger();
	}
	
	public CSVInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1, String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		logger = (new BlueSheepLogger(CSVInputRecord.class)).getLogger();
	}

	@Override
	public boolean isSameEventAbstractInputRecord(Date date, String sport, String partecipante1, String partecipante2) throws Exception {

		logger.info("Starting identification event for CSV line with ID = "  + this.filler);
		
		logger.info("Partecipante1 of CSVInputRecord = " + this.partecipante1 + "; Partecipante2 of CSVInputRecord = " + this.partecipante2 
				+ "; Partecipante1 of TxOddsRecord = " + partecipante1  + "; Partecipante2 of TxOddsRecord = " + partecipante2 +";");
		
		boolean allCheckOk = true;
		
		logger.info("Comparing date: value in CSVInputRecord = " + date);
		allCheckOk = allCheckOk && compareDate(this.dataOraEvento, date);
		if(!allCheckOk) {
			logger.warning("Comparison date failed: value in CSVInputRecord = " + date + "; value of TxOdds Events = " + date);
			return false;
		}
		logger.info("Comparison of dates successfully passed");
		
		logger.info("Comparing sport: value in CSVInputRecord = " + sport);
		allCheckOk = allCheckOk && this.sport.getCode().equals(sport);
		if(!allCheckOk) {
			logger.warning("Comparison sport failed: value in CSVInputRecord = " + this.sport.getCode() + "; value of TxOdds Events = " + sport);
			return false;
		}
		logger.info("Comparison of sports successfully passed");

		
		allCheckOk = allCheckOk && (compareParticipants(this.partecipante1, this.partecipante2, partecipante1, partecipante2));
		if(!allCheckOk) {
			logger.warning("Comparison partecipants failed: values in CSVInputRecord are : P1 = " + this.partecipante1 + ";  P2 = " + this.partecipante2 + 
					"; values of TxOdds Events are : P1 " + partecipante1 + ";  P2 = " + partecipante2);
			logger.info("Trying to check similarity on partecipants >= 0.8");
			
			CosineSimilarityUtil csu = new CosineSimilarityUtil();
			double cosSimPartecipant1 = csu.similarity(this.partecipante1, partecipante1);
			double cosSimPartecipant2 = csu.similarity(this.partecipante2, partecipante2);
			logger.info("Comparing partecipants similarity: similarity for P1 = " + cosSimPartecipant1 + "; similarity for P2 = " + cosSimPartecipant2);
			if(cosSimPartecipant1 < 0.8 || cosSimPartecipant2 < 0.8) {
				logger.warning("Similarity check doesn't achieve minimum score");
				return false;
			}else {
				allCheckOk = true;
			}
			logger.info("Similarity check achieves minimum score : events are matched for CSV line with ID = "  + this.filler);
		}
		return allCheckOk;
	}

}
