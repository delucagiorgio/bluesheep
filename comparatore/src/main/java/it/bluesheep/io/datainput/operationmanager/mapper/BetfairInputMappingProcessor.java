package it.bluesheep.io.datainput.operationmanager.mapper;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.BetfairExchangeInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.util.AbstractBluesheepJsonConverter;
import it.bluesheep.util.BetfairBluesheepJsonConverter;

public final class BetfairInputMappingProcessor extends AbstractInputMappingProcessor{

	@Override
	public List<AbstractInputRecord> mapInputRecordIntoAbstractInputRecord(String jsonString, Scommessa scommessaTipo, Sport sport) {
		
		AbstractBluesheepJsonConverter jsonConverter = BetfairBluesheepJsonConverter.getBetfairBluesheepJsonConverter();
		
		JSONObject jsonObject = new JSONObject(jsonString);
		
		//Ottieni risultati della chiamata
		JSONArray resultArrayJSONObject = jsonConverter.getChildNodeArrayByKey(jsonObject, "result");
		
		JSONObject resultJSONObject = null;
		String marketNode = null;
		
		List<AbstractInputRecord> recordsToBeReturned = new ArrayList<AbstractInputRecord>();
		
		//per ogni risultato --> un "result" come oggetto rappresenta la risposta del servizio 
		//e contiene tutte le quote relative ad un mercato (marketId)
		for(int i = 0; i < resultArrayJSONObject.length(); i++) {
			resultJSONObject = resultArrayJSONObject.getJSONObject(i);
			
			marketNode = resultJSONObject.getString("marketId");
			BetfairExchangeInputRecord tempRecord = new BetfairExchangeInputRecord(null, sport, null, null, null, marketNode);
			
			List<AbstractInputRecord> recordToBeMapped = mapOddsIntoAbstractInputRecord(tempRecord, resultJSONObject, scommessaTipo, sport);
			
			recordsToBeReturned.addAll(recordToBeMapped);
		}
		
		return recordsToBeReturned;
	}

	/**
	 * GD - 22/04/18
	 * Metodo che serve a mappare le informazioni relative alla quota per ogni array di runner (le informazioni 
	 * relative all'evento vengono mappate successivamente attraverso un mapping 1-1 con il tipo di mercato)
	 * @param tempRecord il record contenente il riferimento al mercato (marketId)
	 * @param resultJSONObject il JSONObject rappresentante la risposta su quel market
	 * @param scommessaTipo tipo di scommessa
	 * @param sport lo sport
	 * @return Insieme di record in cui sono mappate le informazioni relative alle quote e alla loro tipologia
	 */
	private List<AbstractInputRecord> mapOddsIntoAbstractInputRecord(BetfairExchangeInputRecord tempRecord, 
			JSONObject resultJSONObject, Scommessa scommessaTipo, Sport sport) {
		
		AbstractBluesheepJsonConverter jsonConverter = BetfairBluesheepJsonConverter.getBetfairBluesheepJsonConverter();
		
		List<AbstractInputRecord> recordsToBeReturned = new ArrayList<AbstractInputRecord>();
		
		//Solitamente più di uno, rappresentano i dettagli delle quote
		JSONArray runnerJSONArray = jsonConverter.getChildNodeArrayByKey(resultJSONObject, "runners");
		
		for(int j = 0; j < runnerJSONArray.length(); j++) {
			//per ogni runner
			JSONObject runnerJSONObject = runnerJSONArray.getJSONObject(j);
			//prendo le informazioni relative alle quote di exchange
			JSONObject exchangeOddsJSONObject = jsonConverter.getChildNodeByKey(runnerJSONObject, "ex");
			
			//prendo le informazioni relative al lato "Banco"
			JSONArray laySideOddsJSONArray = jsonConverter.getChildNodeArrayByKey(exchangeOddsJSONObject, "availableToLay");
			
			if(laySideOddsJSONArray.length() > 0) {
				int correctOddIndexByScommessa = getCorrectOddIndexInJSONObjectByScommessa(scommessaTipo);
				
				//prendo il prezzo più basso
				JSONObject bestPriceLayOddsJSONObject = laySideOddsJSONArray.getJSONObject(correctOddIndexByScommessa);
				
				double quotaLayMin = bestPriceLayOddsJSONObject.getDouble("price");
				double liquidità = bestPriceLayOddsJSONObject.getDouble("size");
				
				//mappo le informazioni nel record di input generico
				BetfairExchangeInputRecord recordToBeMapped = new BetfairExchangeInputRecord(tempRecord);
				recordToBeMapped.setQuota(quotaLayMin);
				recordToBeMapped.setLiquidita(liquidità);
				recordToBeMapped.setSport(sport);
				recordToBeMapped.setTipoScommessa(scommessaTipo);
				
				recordsToBeReturned.add(recordToBeMapped);
			}
		}
		
		return recordsToBeReturned;
	}
	
	/**
	 * GD - 22/04/2018
	 * Calcola in base al tipo di scommessa il corretto indice da andare a leggere per ottenere le informazioni
	 * esatte riguardanti la scommessa passata come parametro
	 * @param scommessaTipo la scommessa
	 * @return l'indice corretto da leggere, -1 in caso di errore
	 */
	private int getCorrectOddIndexInJSONObjectByScommessa(Scommessa scommessaTipo) {
//		int correctIndex = -1;
		return 0;
	}
}
