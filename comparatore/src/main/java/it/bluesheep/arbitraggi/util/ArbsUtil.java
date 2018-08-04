package it.bluesheep.arbitraggi.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.output.subtype.RecordBookmakerVsBookmakerOdds;
import it.bluesheep.comparatore.entities.output.subtype.RecordBookmakerVsExchangeOdds;
import it.bluesheep.util.BlueSheepConstants;

public class ArbsUtil {

	private ArbsUtil() {};
	
	public static String getKeyArbsFromStoredRunRecord(String toBeSplittedLine) {
	   
		String[] keyArbsAllValues = toBeSplittedLine.split(BlueSheepConstants.REGEX_CSV);
		String key = keyArbsAllValues[0] + BlueSheepConstants.REGEX_CSV +
			   keyArbsAllValues[1] + BlueSheepConstants.REGEX_CSV + 
			   keyArbsAllValues[2] + BlueSheepConstants.REGEX_CSV + 
			   keyArbsAllValues[3] + BlueSheepConstants.REGEX_CSV +
			   keyArbsAllValues[4] + BlueSheepConstants.REGEX_CSV +
			   keyArbsAllValues[5] + BlueSheepConstants.REGEX_CSV +
			   keyArbsAllValues[6];
		
		return key;
	}
	
	public static Map<String, Map<String, Map<String, String>>> initializePreviousRunRecordsMap(List<String> linesFromFile){
		Map<String, Map<String, Map<String, String>>> alreadySentArbsOdds = new TreeMap<String, Map<String, Map<String, String>>>();

		//Per ogni linea calcolo la chiave del record e l'aggiungo alla mappa secondo i criteri
	   for(String line : linesFromFile) {
		   String[] splittedLine = line.split(BlueSheepConstants.KEY_SEPARATOR);
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
			   String[] ratings = splittedLine[2].split(BlueSheepConstants.REGEX_CSV);
			   arbsRatingMap.put(BlueSheepConstants.RATING1, ratings[0]);
			   if(ratings.length == 2) {
				   arbsRatingMap.put(BlueSheepConstants.RATING2, ratings[1]);
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
		
		return record.getEvento() + BlueSheepConstants.REGEX_CSV +
				record.getDataOraEvento() + BlueSheepConstants.REGEX_CSV +
				record.getSport() + BlueSheepConstants.REGEX_CSV + 
				record.getNazione()+ BlueSheepConstants.REGEX_CSV + 
				record.getCampionato() + BlueSheepConstants.REGEX_CSV + 
				record.getBookmakerName1() + BlueSheepConstants.REGEX_CSV + 
				record.getScommessaBookmaker1() + BlueSheepConstants.REGEX_CSV + 
				record.getQuotaScommessaBookmaker1() + BlueSheepConstants.REGEX_CSV + 
				record.getBookmakerName2() + BlueSheepConstants.REGEX_CSV + 
				record.getScommessaBookmaker2() + BlueSheepConstants.REGEX_CSV + 
				record.getQuotaScommessaBookmaker2() + BlueSheepConstants.KEY_SEPARATOR + 
				record.getRating() + (rating2.isEmpty() ? "" : BlueSheepConstants.REGEX_CSV + 
				rating2) + (liquidita.isEmpty() ? "" : BlueSheepConstants.KEY_SEPARATOR + 
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
		String[] arbSplittedValues = string.split(BlueSheepConstants.REGEX_CSV);
		String key = arbSplittedValues[0] + BlueSheepConstants.REGEX_CSV +
				arbSplittedValues[1] + BlueSheepConstants.REGEX_CSV + 
				arbSplittedValues[2] + BlueSheepConstants.REGEX_CSV + 
				arbSplittedValues[5] + BlueSheepConstants.REGEX_CSV +
				arbSplittedValues[6] + BlueSheepConstants.REGEX_CSV +
				arbSplittedValues[8] + BlueSheepConstants.REGEX_CSV +
				arbSplittedValues[9];
		
		return key;
	}
	
	public static String getTelegramBoldString(String string) {
		return "*" + string + "*";
	}
	
}
