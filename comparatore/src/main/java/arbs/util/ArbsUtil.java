package arbs.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.output.subtype.RecordBookmakerVsBookmakerOdds;
import it.bluesheep.entities.output.subtype.RecordBookmakerVsExchangeOdds;

public class ArbsUtil {

	private ArbsUtil() {};
	
	public static String getKeyArbsFromStoredRunRecord(String toBeSplittedLine) {
	   
		String[] keyArbsAllValues = toBeSplittedLine.split(ArbsConstants.VALUE_SEPARATOR);
		String key = keyArbsAllValues[0] + ArbsConstants.VALUE_SEPARATOR +
			   keyArbsAllValues[1] + ArbsConstants.VALUE_SEPARATOR + 
			   keyArbsAllValues[2] + ArbsConstants.VALUE_SEPARATOR + 
			   keyArbsAllValues[3] + ArbsConstants.VALUE_SEPARATOR +
			   keyArbsAllValues[4] + ArbsConstants.VALUE_SEPARATOR +
			   keyArbsAllValues[5] + ArbsConstants.VALUE_SEPARATOR +
			   keyArbsAllValues[6];
		
		return key;
	}
	
	public static Map<String, Map<String, Map<String, String>>> initializePreviousRunRecordsMap(List<String> linesFromFile){
		Map<String, Map<String, Map<String, String>>> alreadySentArbsOdds = new TreeMap<String, Map<String, Map<String, String>>>();

		//Per ogni linea calcolo la chiave del record e l'aggiungo alla mappa secondo i criteri
	   for(String line : linesFromFile) {
		   String[] splittedLine = line.split(ArbsConstants.KEY_SEPARATOR);
		   String runId = splittedLine[0];
		   Map<String, Map<String, String>> keyArbsMap = alreadySentArbsOdds.get(runId);
		   if(keyArbsMap == null) {
			   keyArbsMap = new HashMap<String, Map<String, String>>();
			   alreadySentArbsOdds.put(runId, keyArbsMap);
		   }
		   
		   String key = ArbsUtil.getKeyArbsFromStoredRunRecord(splittedLine[1]);

		   Map<String, String> arbsRatingMap = keyArbsMap.get(key);
		   
		   if(arbsRatingMap == null) {
			   arbsRatingMap = new HashMap<String, String>();
			   String[] ratings = splittedLine[2].split(ArbsConstants.VALUE_SEPARATOR);
			   arbsRatingMap.put(ArbsConstants.RATING1, ratings[0]);
			   if(ratings.length == 2) {
				   arbsRatingMap.put(ArbsConstants.RATING2, ratings[1]);
			   }
			   keyArbsMap.put(key, arbsRatingMap);
		   }
	   }
	   return alreadySentArbsOdds; 
	}
	
	public static String getKeyArbsFromOutputRecord(RecordOutput record) {
		String rating2 = "";
		String liquidita = "";
		
		if(record instanceof RecordBookmakerVsBookmakerOdds) {
			rating2 += ((RecordBookmakerVsBookmakerOdds)record).getRating2();
		}
		
		if(record instanceof RecordBookmakerVsExchangeOdds) {
			liquidita += ((RecordBookmakerVsExchangeOdds) record).getLiquidita();
		}
		
		return record.getEvento() + ArbsConstants.VALUE_SEPARATOR +
				record.getDataOraEvento() + ArbsConstants.VALUE_SEPARATOR +
				record.getSport() + ArbsConstants.VALUE_SEPARATOR + 
				record.getNazione()+ ArbsConstants.VALUE_SEPARATOR + 
				record.getCampionato() + ArbsConstants.VALUE_SEPARATOR + 
				record.getBookmakerName1() + ArbsConstants.VALUE_SEPARATOR + 
				record.getScommessaBookmaker1() + ArbsConstants.VALUE_SEPARATOR + 
				record.getQuotaScommessaBookmaker1() + ArbsConstants.VALUE_SEPARATOR + 
				record.getBookmakerName2() + ArbsConstants.VALUE_SEPARATOR + 
				record.getScommessaBookmaker2() + ArbsConstants.VALUE_SEPARATOR + 
				record.getQuotaScommessaBookmaker2() + ArbsConstants.KEY_SEPARATOR + 
				record.getRating() + (rating2.isEmpty() ? "" : ArbsConstants.VALUE_SEPARATOR + 
				rating2) + (liquidita.isEmpty() ? "" : ArbsConstants.KEY_SEPARATOR + 
				liquidita);
	}


	
	public static String getScommessaStringURL(String scommessaBookmaker1) {
		String result = scommessaBookmaker1;
		if(result != null && (result.startsWith("U") || result.startsWith("O"))) {
			String tmp = result.substring(0,1);
			switch(tmp) {
			case "U":
				result = "Under " + result.substring(1) + "_";
				break;
			case "O":
				result = "Over " + result.substring(1) + "_";
				break;
			default:
				break;
			}
		}
		
		return result;
	}

	public static String createArbsKeyFromRecordKey(String string) {
		String[] arbSplittedValues = string.split(ArbsConstants.VALUE_SEPARATOR);
		String key = arbSplittedValues[0] + ArbsConstants.VALUE_SEPARATOR +
				arbSplittedValues[1] + ArbsConstants.VALUE_SEPARATOR + 
				arbSplittedValues[2] + ArbsConstants.VALUE_SEPARATOR + 
				arbSplittedValues[5] + ArbsConstants.VALUE_SEPARATOR +
				arbSplittedValues[6] + ArbsConstants.VALUE_SEPARATOR +
				arbSplittedValues[8] + ArbsConstants.VALUE_SEPARATOR +
				arbSplittedValues[9];
		
		return key;
	}
	
	public static String getTelegramBoldString(String string) {
		return "*" + string + "*";
	}
	
}
