package it.bluesheep.comparatore.io.datainput.operationmanager.service.mapper;

import java.util.List;

import org.json.JSONObject;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.util.json.BetfairBluesheepJsonConverter;

public abstract class AbstractBetfairInputMappingProcessor extends AbstractInputMappingProcessor{
	
	protected static final String RESULT_JSON_STRING = "result";
	protected static final String MARKETID_JSON_STRING = "marketId";
	protected static final String MARKETDETAILS_JSON_STRING = "marketDetails";
	protected static final String RUNNERS_JSON_STRING = "runners";
	protected static final String RUNNERSDETAILS_JSON_STRING = "runnerDetails";
	protected static final String EXCHANGE_JSON_STRING = "ex";
	protected static final String WINRUNNERODDS_JSON_STRING = "winRunnerOdds";
	protected static final String LAY_SIDE_JSON_STRING = "availableToLay";
	protected static final String BACK_SIDE_JSON_STRING = "availableToBack";
	protected static final String DECIMAL_JSON_STRING = "decimal";
	protected static final String PRICE_JSON_STRING = "price";
	protected static final String SIZE_JSON_STRING = "size";

	public AbstractBetfairInputMappingProcessor() {
		super();
		jsonConverter = new BetfairBluesheepJsonConverter();
	}
	
	@Override
	public abstract List<AbstractInputRecord> mapInputRecordIntoAbstractInputRecord(String jsonString, Scommessa scommessaTipo, Sport sport);

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
	protected abstract AbstractInputRecord[] mapOddsIntoAbstractInputRecord(AbstractInputRecord tempRecord, 
			JSONObject resultJSONObject, Scommessa scommessaTipo, Sport sport);	
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
