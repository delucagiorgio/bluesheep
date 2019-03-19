package it.bluesheep.comparatore.io.datainput.operationmanager.service.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.record.BetfairExchangeInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.util.json.BetfairBluesheepJsonConverter;

public class BetfairExchangeInputMappingProcessor extends AbstractBetfairInputMappingProcessor {

	public BetfairExchangeInputMappingProcessor() {
		super();
		this.logger = Logger.getLogger(BetfairExchangeInputMappingProcessor.class);
	}

	@Override
	public List<AbstractInputRecord> mapInputRecordIntoAbstractInputRecord(String jsonString, Scommessa scommessaTipo,
			Sport sport) {

		JSONObject jsonObject = new JSONObject(jsonString);

		// Ottieni risultati della chiamata
		JSONArray resultArrayJSONObject = BetfairBluesheepJsonConverter.getChildNodeArrayByKey(jsonObject,
				RESULT_JSON_STRING);

		JSONObject resultJSONObject = null;
		String marketNode = null;

		List<AbstractInputRecord> recordsToBeReturned = new ArrayList<AbstractInputRecord>();

		// per ogni risultato --> un "result" come oggetto rappresenta la risposta del
		// servizio
		// e contiene tutte le quote relative ad un mercato (marketId)
		for (int i = 0; i < resultArrayJSONObject.length(); i++) {
			try {
				resultJSONObject = resultArrayJSONObject.getJSONObject(i);

				marketNode = resultJSONObject.getString(MARKETID_JSON_STRING);
				BetfairExchangeInputRecord tempRecord = new BetfairExchangeInputRecord(null, sport, null, null, null,
						marketNode, -1, false);

				AbstractInputRecord[] recordToBeMapped = mapOddsIntoAbstractInputRecord(tempRecord, resultJSONObject,
						scommessaTipo, sport);
				if (recordToBeMapped != null) {
					if (recordToBeMapped[0] != null) {
						recordsToBeReturned.add(recordToBeMapped[0]);
					}
					if (recordToBeMapped[1] != null) {
						recordsToBeReturned.add(recordToBeMapped[1]);
					}
				}
			} catch (Exception e) {
				logger.error("Error during data extraction from JSON: exception is " + e.getMessage(), e);
			}
		}

		return recordsToBeReturned;
	}

	@Override
	protected AbstractInputRecord[] mapOddsIntoAbstractInputRecord(AbstractInputRecord tempRecord,
			JSONObject resultJSONObject, Scommessa scommessaTipo, Sport sport) {
		AbstractInputRecord[] exchangeRecords = new BetfairExchangeInputRecord[2];

		BetfairExchangeInputRecord recordLayToBeMapped = null;
		BetfairExchangeInputRecord recordBackToBeMapped = null;

		// Solitamente più di uno, rappresentano i dettagli delle quote
		JSONArray runnerJSONArray = BetfairBluesheepJsonConverter.getChildNodeArrayByKey(resultJSONObject,
				RUNNERS_JSON_STRING);
		int correctOddIndexByScommessa = getCorrectOddIndexInJSONObjectByScommessa(scommessaTipo);
		if (runnerJSONArray.length() != 0) {
			// per ogni runner
			JSONObject runnerJSONObject = runnerJSONArray.getJSONObject(correctOddIndexByScommessa);
			// prendo le informazioni relative alle quote di exchange
			JSONObject exchangeOddsJSONObject = BetfairBluesheepJsonConverter.getChildNodeByKey(runnerJSONObject,
					EXCHANGE_JSON_STRING);

			// prendo le informazioni relative al lato "Banco"
			JSONArray laySideOddsJSONArray = BetfairBluesheepJsonConverter
					.getChildNodeArrayByKey(exchangeOddsJSONObject, LAY_SIDE_JSON_STRING);
			// prendo le informazioni relative al lato "Punta"
			JSONArray backSideOddsJSONArray = BetfairBluesheepJsonConverter
					.getChildNodeArrayByKey(exchangeOddsJSONObject, BACK_SIDE_JSON_STRING);

			if (laySideOddsJSONArray.length() > 0) {

				// prendo il prezzo più basso
				JSONObject bestPriceLayOddsJSONObject = laySideOddsJSONArray.optJSONObject(0);

				if (bestPriceLayOddsJSONObject != null) {
					double quotaLayMin = bestPriceLayOddsJSONObject.getDouble(PRICE_JSON_STRING);
					double liquidita = bestPriceLayOddsJSONObject.getDouble(SIZE_JSON_STRING);

					// mappo le informazioni nel record di input generico
					recordLayToBeMapped = new BetfairExchangeInputRecord(tempRecord, true);
					recordLayToBeMapped.setQuota(quotaLayMin);
					recordLayToBeMapped.setLiquidita(liquidita);
					recordLayToBeMapped.setSport(sport);
					recordLayToBeMapped.setTipoScommessa(scommessaTipo);
				}
			}

			if (backSideOddsJSONArray.length() > 0) {

				// prendo il prezzo più basso
				JSONObject bestPricebackOddsJSONObject = backSideOddsJSONArray.optJSONObject(0);

				if (bestPricebackOddsJSONObject != null) {
					double quotaLayMin = bestPricebackOddsJSONObject.getDouble(PRICE_JSON_STRING);
					double liquidita = bestPricebackOddsJSONObject.getDouble(SIZE_JSON_STRING);

					// mappo le informazioni nel record di input generico
					recordBackToBeMapped = new BetfairExchangeInputRecord(tempRecord, false);
					recordBackToBeMapped.setQuota(quotaLayMin);
					recordBackToBeMapped.setLiquidita(liquidita);
					recordBackToBeMapped.setSport(sport);
					recordBackToBeMapped.setTipoScommessa(scommessaTipo);
				}
			}
		}

		exchangeRecords[0] = recordLayToBeMapped;
		exchangeRecords[1] = recordBackToBeMapped;

		return exchangeRecords;
	}
	
	/**
	 * GD - 22/04/2018
	 * Calcola in base al tipo di scommessa il corretto indice da andare a leggere per ottenere le informazioni
	 * esatte riguardanti la scommessa passata come parametro
	 * @param scommessaTipo la scommessa
	 * @return l'indice corretto da leggere, -1 in caso di errore
	 */
	protected int getCorrectOddIndexInJSONObjectByScommessa(Scommessa scommessaTipo) {
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
