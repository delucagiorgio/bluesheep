package it.bluesheep.comparatore.io.datainput.operationmanager.service.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.record.BetfairSportbookInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.util.json.BetfairBluesheepJsonConverter;

public class BetfairSportbookInputMappingProcessor extends AbstractBetfairInputMappingProcessor {

	public BetfairSportbookInputMappingProcessor() {
		super();
		this.logger = Logger.getLogger(BetfairSportbookInputMappingProcessor.class);
	}
	
	@Override
	public List<AbstractInputRecord> mapInputRecordIntoAbstractInputRecord(String jsonString, Scommessa scommessaTipo, Sport sport) {
		JSONObject jsonObject = new JSONObject(jsonString);
		
		//Ottieni risultati della chiamata
		JSONArray resultArrayJSONObject = BetfairBluesheepJsonConverter.getChildNodeArrayByKey(jsonObject, RESULT_JSON_STRING);
		
		JSONObject resultJSONObject = null;
		JSONArray marketNode = null;
		
		List<AbstractInputRecord> recordsToBeReturned = new ArrayList<AbstractInputRecord>();
		
		//per ogni risultato --> un "result" come oggetto rappresenta la risposta del servizio 
		//e contiene tutte le quote relative ad un mercato (marketId)
		for(int i = 0; i < resultArrayJSONObject.length(); i++) {
			try {
				resultJSONObject = resultArrayJSONObject.getJSONObject(i);
				marketNode = resultJSONObject.optJSONArray(MARKETDETAILS_JSON_STRING);
				if(marketNode != null) {
					for(int k = 0; k < marketNode.length(); k++) {
						JSONObject marketJSONObject = marketNode.getJSONObject(i);
						String marketNodeString = marketJSONObject.getString(MARKETID_JSON_STRING);

						BetfairSportbookInputRecord tempRecord = new BetfairSportbookInputRecord(null, sport, null, null, null, marketNodeString);
						
						AbstractInputRecord[] recordToBeMapped = mapOddsIntoAbstractInputRecord(tempRecord, marketJSONObject, scommessaTipo, sport);
						if(recordToBeMapped != null) {
							if(recordToBeMapped[0] != null) {
								recordsToBeReturned.add(recordToBeMapped[0]);
							}
						}
					}
				}
			}catch(Exception e) {
				logger.error("Error during data extraction from JSON: exception is " + e.getMessage(), e);	
			}
		}
		
		return recordsToBeReturned;
	}

	@Override
	protected AbstractInputRecord[] mapOddsIntoAbstractInputRecord(AbstractInputRecord tempRecord,
			JSONObject resultJSONObject, Scommessa scommessaTipo, Sport sport) {

		AbstractInputRecord[] sportbookRecords = new BetfairSportbookInputRecord[1];
		
		BetfairSportbookInputRecord recordToBeMapped  = null;

		//Solitamente più di uno, rappresentano i dettagli delle quote
		JSONArray runnerJSONArray = BetfairBluesheepJsonConverter.getChildNodeArrayByKey(resultJSONObject, RUNNERSDETAILS_JSON_STRING);
		int correctOddIndexByScommessa = getCorrectOddIndexInJSONObjectByScommessa(scommessaTipo);
		if(runnerJSONArray.length() != 0) {
			//per ogni runner
			JSONObject runnerJSONObject = runnerJSONArray.getJSONObject(correctOddIndexByScommessa);
			//prendo le informazioni relative alle quote dello sportbook
			JSONObject sportbookOddsJSONObject = BetfairBluesheepJsonConverter.getChildNodeByKey(runnerJSONObject, WINRUNNERODDS_JSON_STRING);
			
			//prendo le informazioni relative al lato "Banco"
			Double decimalOddsJSONArray = sportbookOddsJSONObject.getDouble(DECIMAL_JSON_STRING);
			
			if(decimalOddsJSONArray != null) {
				//mappo le informazioni nel record di input generico
				recordToBeMapped = new BetfairSportbookInputRecord(tempRecord);
				recordToBeMapped.setQuota(decimalOddsJSONArray);
				recordToBeMapped.setLiquidita(-1);
				recordToBeMapped.setSport(sport);
				recordToBeMapped.setTipoScommessa(scommessaTipo);
			}
		}
		
		sportbookRecords[0] = recordToBeMapped;
		
		return sportbookRecords;
	}

}
