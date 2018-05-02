package it.bluesheep.io.datainput.operationmanager.mapper;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.record.BetfairExchangeInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.util.json.AbstractBluesheepJsonConverter;
import it.bluesheep.util.json.BetfairBluesheepJsonConverter;

public final class BetfairInputMappingProcessor extends AbstractInputMappingProcessor{
	
	private static final String RESULT_JSON_STRING = "result";
	private static final String MARKETID_JSON_STRING = "marketId";
	private static final String RUNNERS_JSON_STRING = "runners";
	private static final String EXCHANGE_JSON_STRING = "ex";
	private static final String LAY_SIDE_JSON_STRING = "availableToLay";
	private static final String PRICE_JSON_STRING = "price";
	private static final String SIZE_JSON_STRING = "size";

	@Override
	public List<AbstractInputRecord> mapInputRecordIntoAbstractInputRecord(String jsonString, Scommessa scommessaTipo, Sport sport) {
				
		AbstractBluesheepJsonConverter jsonConverter = BetfairBluesheepJsonConverter.getBetfairBluesheepJsonConverter();
		
		JSONObject jsonObject = new JSONObject(jsonString);
		
		//Ottieni risultati della chiamata
		JSONArray resultArrayJSONObject = jsonConverter.getChildNodeArrayByKey(jsonObject, RESULT_JSON_STRING);
		
		JSONObject resultJSONObject = null;
		String marketNode = null;
		
		List<AbstractInputRecord> recordsToBeReturned = new ArrayList<AbstractInputRecord>();
		
		//per ogni risultato --> un "result" come oggetto rappresenta la risposta del servizio 
		//e contiene tutte le quote relative ad un mercato (marketId)
		for(int i = 0; i < resultArrayJSONObject.length(); i++) {
			try {
				resultJSONObject = resultArrayJSONObject.getJSONObject(i);
				
				marketNode = resultJSONObject.getString(MARKETID_JSON_STRING);
				BetfairExchangeInputRecord tempRecord = new BetfairExchangeInputRecord(null, sport, null, null, null, marketNode);
				
				AbstractInputRecord recordToBeMapped = mapOddsIntoAbstractInputRecord(tempRecord, resultJSONObject, scommessaTipo, sport);
				if(recordToBeMapped != null) {
					recordsToBeReturned.add(recordToBeMapped);
				}
			}catch(Exception e) {
				logger.severe("Error during data extraction from JSON: exception is " + e.getMessage());	
			}
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
	private AbstractInputRecord mapOddsIntoAbstractInputRecord(BetfairExchangeInputRecord tempRecord, 
			JSONObject resultJSONObject, Scommessa scommessaTipo, Sport sport) {
		
		AbstractBluesheepJsonConverter jsonConverter = BetfairBluesheepJsonConverter.getBetfairBluesheepJsonConverter();
		
		BetfairExchangeInputRecord recordToBeMapped  = null;
		//Solitamente più di uno, rappresentano i dettagli delle quote
		JSONArray runnerJSONArray = jsonConverter.getChildNodeArrayByKey(resultJSONObject, RUNNERS_JSON_STRING);
		int correctOddIndexByScommessa = getCorrectOddIndexInJSONObjectByScommessa(scommessaTipo);
		if(runnerJSONArray.length() != 0) {
			//per ogni runner
			JSONObject runnerJSONObject = runnerJSONArray.getJSONObject(correctOddIndexByScommessa);
			//prendo le informazioni relative alle quote di exchange
			JSONObject exchangeOddsJSONObject = jsonConverter.getChildNodeByKey(runnerJSONObject, EXCHANGE_JSON_STRING);
			
			//prendo le informazioni relative al lato "Banco"
			JSONArray laySideOddsJSONArray = jsonConverter.getChildNodeArrayByKey(exchangeOddsJSONObject, LAY_SIDE_JSON_STRING);
			
			if(laySideOddsJSONArray.length() > 0) {
				
				//prendo il prezzo più basso
				JSONObject bestPriceLayOddsJSONObject = laySideOddsJSONArray.optJSONObject(0);
				
				if(bestPriceLayOddsJSONObject != null) {
					double quotaLayMin = bestPriceLayOddsJSONObject.getDouble(PRICE_JSON_STRING);
					double liquidita = bestPriceLayOddsJSONObject.getDouble(SIZE_JSON_STRING);
					
					//mappo le informazioni nel record di input generico
					recordToBeMapped = new BetfairExchangeInputRecord(tempRecord);
					recordToBeMapped.setQuota(quotaLayMin);
					recordToBeMapped.setLiquidita(liquidita);
					recordToBeMapped.setSport(sport);
					recordToBeMapped.setTipoScommessa(scommessaTipo);
					
				}
			}
		}
		
		return recordToBeMapped;
	}
	
	/**
	 * GD - 22/04/2018
	 * Calcola in base al tipo di scommessa il corretto indice da andare a leggere per ottenere le informazioni
	 * esatte riguardanti la scommessa passata come parametro
	 * @param scommessaTipo la scommessa
	 * @return l'indice corretto da leggere, -1 in caso di errore
	 */
	private int getCorrectOddIndexInJSONObjectByScommessa(Scommessa scommessaTipo) {
		int index = -1;
		switch(scommessaTipo) {
		case NESSUNGOAL_U0X5:
		case ALPIU1GOAL_U1X5:
		case ALPIU2GOAL_U2X5:
		case ALPIU3GOAL_U3X5:
		case ALPIU4GOAL_U4X5:
		case ENTRAMBISEGNANO_GOAL:
		case SFIDANTE1VINCENTE_1:
			index = 0;
			break;
		case ALMENO1GOAL_O0X5:
		case ALMENO2GOAL_O1X5:
		case ALMENO3GOAL_O2X5:
		case ALMENO4G0AL_O3X5:
		case ALMENO5GOAL_O4X5:
		case NESSUNOSEGNA_NOGOAL:
		case SFIDANTE2VINCENTE_2:
			index = 1;
			break;
		case PAREGGIO_X:
			index = 2;
			break;
		}
		return index;
	}
}
