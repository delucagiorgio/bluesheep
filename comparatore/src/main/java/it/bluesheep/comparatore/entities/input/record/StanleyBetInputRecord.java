package it.bluesheep.comparatore.entities.input.record;

import java.util.Date;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.AbstractScrapingInputRecord;
import it.bluesheep.comparatore.entities.input.util.IKeyEventoComparator;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.util.CosineSimilarityUtil;
import it.bluesheep.comparatore.serviceapi.Service;

public class StanleyBetInputRecord extends AbstractScrapingInputRecord implements IKeyEventoComparator{
	
	private static Logger logger = Logger.getLogger(StanleyBetInputRecord.class);
	
	public StanleyBetInputRecord(Date dataOraEvento, Sport sport, String campionato, String partecipante1,
			String partecipante2, String filler) {
		super(dataOraEvento, sport, campionato, partecipante1, partecipante2, filler);
		this.source = Service.STANLEYBET_SERVICENAME;
		this.liquidita = -1D;
		this.bookmakerName = "StanleyBet";
	}
	
	public StanleyBetInputRecord(AbstractInputRecord record) {
		super(record);
		this.source = Service.STANLEYBET_SERVICENAME;
		this.liquidita = -1D;
		this.bookmakerName = "StanleyBet";
	}
	
	@Override
	public boolean isSameEventAbstractInputRecord(Date obj, String sport, String partecipante1, String partecipante2)
			throws Exception {
		
		logger.debug("Starting identification event for record " + this.keyEvento);
		
		String thisP1_LC = this.partecipante1.toLowerCase();
		String thisP2_LC = this.partecipante2.toLowerCase();
		String P1_LC = partecipante1.toLowerCase();
		String P2_LC = partecipante2.toLowerCase();
		
		logger.debug("Partecipante1 of record = " + thisP1_LC + "; Partecipante2 of record = " + thisP2_LC
				+ "; Partecipante1 of TxOddsRecord = " + P1_LC  + "; Partecipante2 of TxOddsRecord = " + P2_LC +";");
		
		boolean allCheckOk = true;
		
		allCheckOk = allCheckOk && (compareParticipants(thisP1_LC, thisP2_LC, P1_LC, P2_LC));
		if(!allCheckOk) {
			logger.debug("Comparison players failed: values in record are : P1 = " + thisP1_LC + ";  P2 = " + thisP2_LC + 
					"; values of TxOdds Events are : P1 " + P1_LC + ";  P2 = " + P2_LC);
			logger.debug("Trying to check similarity on players >= 0.8");
			
			CosineSimilarityUtil csu = new CosineSimilarityUtil();
			double cosSimPartecipant1 = csu.similarity(thisP1_LC, P1_LC);
			double cosSimPartecipant2 = csu.similarity(thisP2_LC, P2_LC);
			logger.debug("Comparing players similarity: similarity for P1 = " + cosSimPartecipant1 + "; similarity for P2 = " + cosSimPartecipant2);
			if(cosSimPartecipant1 < 0.8 || cosSimPartecipant2 < 0.8) {
				logger.debug("Similarity check doesn't achieve minimum score");
				return false;
			}else {
				allCheckOk = true;
			}
			logger.debug("Similarity check achieves minimum score : events are matched for record = "  + this.keyEvento);
		}
		
		logger.debug("Comparison of players successfully passed");
		
		logger.info(this.keyEvento + " has been matched with a TxOdds Event") ;

		
		return allCheckOk;	
	}
}
