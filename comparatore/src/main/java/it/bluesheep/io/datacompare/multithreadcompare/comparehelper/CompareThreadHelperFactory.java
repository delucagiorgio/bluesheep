package it.bluesheep.io.datacompare.multithreadcompare.comparehelper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.ComparatoreConstants;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;

public class CompareThreadHelperFactory {

	private CompareThreadHelperFactory() {}
	
	public static CompareThreadHelper getCorrectCompareThreadHelperByString(String comparisonType, 
			List<Date> keyList, 
			Map<Date, Map<String, Map<Scommessa, List<AbstractInputRecord>>>> dataMap,
			Map<String, List<RecordOutput>> oddsComparisonThreadMap, 
			Sport sport) {
		if(ComparatoreConstants.TX_ODDS_SERVICENAME.equalsIgnoreCase(comparisonType)) {
			return new PuntaPuntaCompareThreadHelper(oddsComparisonThreadMap, keyList, dataMap, sport);
		}else if(ComparatoreConstants.BETFAIR_SERVICENAME.equalsIgnoreCase(comparisonType)) {
			return new PuntaBancaCompareThreadHelper(oddsComparisonThreadMap, keyList, dataMap, sport);
		}
		return null;
	}
	
}
