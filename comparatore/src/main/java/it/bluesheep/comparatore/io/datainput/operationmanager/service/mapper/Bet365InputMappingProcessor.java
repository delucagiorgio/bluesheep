package it.bluesheep.comparatore.io.datainput.operationmanager.service.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.record.Bet365InputRecord;
import it.bluesheep.comparatore.entities.util.ScommessaUtilManager;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.util.json.Bet365BluesheepJsonConverter;

public class Bet365InputMappingProcessor extends AbstractInputMappingProcessor{

	private static final String RESULT_JSON_STRING = "results";
	private static final String SUCCESS_JSON_STRING = "success";
	private static final String EVENTO_ID_JSON_STRING = "event_id";
	private static final String SP_JSON_STRING = "sp";
	private static final String GOALS_CAT_JSON_STRING = "goals";
	private static final String SCHEDULE_CAT_JSON_STRING = "schedule";
	private static final String GOALS_UNDER_OVER_25_JSON_STRING = "goals_over_under";
	private static final String ALTERNATIVE_TOTAL_GOALS_JSON_STRING = "alternative_total_goals";
	private static final String BOTH_TEAMS_TO_SCORE_JSON_STRING = "both_teams_to_score";
	private static final String FULL_TIME_RESULT_JSON_STRING = "full_time_result";
	private static final String ODDS_JSON_STRING = "odds";
	private static final String HEADER_JSON_STRING = "header";
	private static final String TO_WIN_MATCH_JSON_STRING = "to_win_match";
	private static final String MAIN_CAT_JSON_STRING = "main";
	
	public Bet365InputMappingProcessor() {
		super();
		this.logger = Logger.getLogger(Bet365InputMappingProcessor.class);
	}
	
	
	@Override
	public List<AbstractInputRecord> mapInputRecordIntoAbstractInputRecord(String jsonString, Scommessa scommessaTipo, Sport sport) {
		
		JSONObject jsonObject = new JSONObject(jsonString);
		
		//Ottieni risultati della chiamata relativi alle quote
		JSONArray resultArrayJSONObject = Bet365BluesheepJsonConverter.getChildNodeArrayByKey(jsonObject, RESULT_JSON_STRING);
		int successString = jsonObject.getInt(SUCCESS_JSON_STRING);

		String eventoId = null;

		List<AbstractInputRecord> recordsToBeReturned = new ArrayList<AbstractInputRecord>();
		
		if(successString == 1) {
			for(int i = 0; i < resultArrayJSONObject.length(); i++) {
				JSONObject resultJSONObject = resultArrayJSONObject.getJSONObject(i);
				try {
					eventoId = resultJSONObject.getString(EVENTO_ID_JSON_STRING);
					
					Bet365InputRecord tempRecord = new Bet365InputRecord(null, sport, null, null, null, eventoId);
					
					AbstractInputRecord recordToBeMapped = mapOddsIntoAbstractInputRecord(tempRecord, resultJSONObject, scommessaTipo, sport);
					
					if(recordToBeMapped != null) {
						recordsToBeReturned.add(recordToBeMapped);
					}
					
				}catch(Exception e) {
					logger.error(e.getMessage() + "\nJSON is " + resultJSONObject, e);
				}
			}
		}
		
		return recordsToBeReturned;
	}

	
	/**
	 * GD - 01/05/18
	 * Metodo che serve a mappare le informazioni relative alla quota per ogni array di runner (le informazioni 
	 * relative all'evento vengono mappate successivamente attraverso un mapping 1-1 con l'idEvento)
	 * @param tempRecord il record contenente il riferimento all'evento (eventoId)
	 * @param resultJSONObject il JSONObject rappresentante la risposta su quel market
	 * @param scommessaTipo tipo di scommessa
	 * @param sport lo sport
	 * @return insieme di record in cui sono mappate le informazioni relative alle quote e alla loro tipologia
	 */
	private AbstractInputRecord mapOddsIntoAbstractInputRecord(Bet365InputRecord tempRecord, JSONObject resultJSONObject, Scommessa scommessaTipo, Sport sport) {

		Bet365InputRecord recordToBeMapped = null;
		
		String correctCategory = getCorrectCategoryByScommessa(scommessaTipo);
		String correctSubCategory = getCorrectSubCategoryByScommessa(scommessaTipo, sport);
		int specificTypeOdds = getCorrectSpecificTypeByScommessa(scommessaTipo, sport);
		String underOverHandicap = getCorrectCodeHandicap(scommessaTipo);
	
		if(correctCategory != null && correctSubCategory != null && (specificTypeOdds > -1 || underOverHandicap != null)) {		
			JSONObject correctCategoryJSONObject = Bet365BluesheepJsonConverter.getChildNodeByKey(resultJSONObject, correctCategory);
			
			if(correctCategoryJSONObject != null) {
				JSONObject spJSONObject = Bet365BluesheepJsonConverter.getChildNodeByKey(correctCategoryJSONObject, SP_JSON_STRING);
				if(spJSONObject == null) {
					correctCategoryJSONObject = Bet365BluesheepJsonConverter.getChildNodeByKey(resultJSONObject, SCHEDULE_CAT_JSON_STRING);
					spJSONObject = Bet365BluesheepJsonConverter.getChildNodeByKey(correctCategoryJSONObject, SP_JSON_STRING);
				}
				if(spJSONObject != null) {
					JSONArray subCategoryJSONArray = Bet365BluesheepJsonConverter.getChildNodeArrayByKey(spJSONObject, correctSubCategory);
					
					if(subCategoryJSONArray != null && subCategoryJSONArray.length() > 0) {
						Long updatedTime = System.currentTimeMillis();
						if(specificTypeOdds > -1) {
							JSONObject oddsJSONObject = subCategoryJSONArray.getJSONObject(specificTypeOdds);
							if(oddsJSONObject != null) {
								double quota = oddsJSONObject.getDouble(ODDS_JSON_STRING);
								recordToBeMapped = new Bet365InputRecord(tempRecord);
								recordToBeMapped.setQuota(quota);
								recordToBeMapped.setSport(sport);
								recordToBeMapped.setTipoScommessa(scommessaTipo);
								recordToBeMapped.setTimeInsertionInSystem(updatedTime);
							}
						}else if(underOverHandicap != null) {
							for(int i = 0; i < subCategoryJSONArray.length(); i++) {
								JSONObject underOverJSONObject = subCategoryJSONArray.getJSONObject(i);
								
								String[] splittedHandicap = underOverHandicap.split("_");
								
								String headerString = underOverJSONObject.getString(HEADER_JSON_STRING).trim();
								
								if(splittedHandicap[1].equals(underOverJSONObject.getString(GOALS_CAT_JSON_STRING)) &&
										splittedHandicap[0].equals(headerString)) {
									double quota = underOverJSONObject.getDouble(ODDS_JSON_STRING);
									recordToBeMapped = new Bet365InputRecord(tempRecord);
									recordToBeMapped.setQuota(quota);
									recordToBeMapped.setSport(sport);
									recordToBeMapped.setTipoScommessa(scommessaTipo);
									recordToBeMapped.setTimeInsertionInSystem(updatedTime);
								}
							}
						}
					}
				}
			}
		}
		
		return recordToBeMapped;
	}

	
	private String getCorrectCodeHandicap(Scommessa scommessaTipo) {
		String scommessaHandicap = null;
		if(ScommessaUtilManager.getScommessaListCalcioTotalOdds().contains(scommessaTipo)) {
			if(ScommessaUtilManager.getScommessaListCalcioUnderOdds().contains(scommessaTipo)) {
				scommessaHandicap = "Under";
			}else if(ScommessaUtilManager.getScommessaListCalcioOverOdds().contains(scommessaTipo)) {
				scommessaHandicap = "Over";
			}
			
			switch(scommessaTipo) {
			case ALMENO3GOAL_O2X5:
			case ALPIU2GOAL_U2X5:
				scommessaHandicap += "_2.5";
				break;
			case ALMENO1GOAL_O0X5:
			case NESSUNGOAL_U0X5:
				scommessaHandicap += "_0.5";
				break;
			case ALMENO2GOAL_O1X5:
			case ALPIU1GOAL_U1X5:
				scommessaHandicap += "_1.5";
				break;
			case ALMENO4G0AL_O3X5:
			case ALPIU3GOAL_U3X5:
				scommessaHandicap += "_3.5";
				break;
			case ALMENO5GOAL_O4X5:
			case ALPIU4GOAL_U4X5:
				scommessaHandicap += "_4.5";
				break;
			default:
				break;
			}
		}
		return scommessaHandicap;
	}


	/**
	 * GD - 01/04/18
	 * Stabilisce il corretto indice della scommessa da analizzare all'interno del JSON
	 * @param scommessaTipo la tipologia di scommessa che si vuole ottenere
	 * @return l'indice corrispondente alla quota desiderata
	 */
	private int getCorrectSpecificTypeByScommessa(Scommessa scommessaTipo, Sport sport) {
		int correctIndex = -1;
		switch(scommessaTipo) {
		case SFIDANTE1VINCENTE_1:
		case ENTRAMBISEGNANO_GOAL:
			correctIndex = 0;
			break;
		case PAREGGIO_X:
		case NESSUNOSEGNA_NOGOAL:
			correctIndex = 1;
			break;
		case SFIDANTE2VINCENTE_2:
			if(sport == Sport.CALCIO) {
				correctIndex = 2;
			}else if(sport == Sport.TENNIS) {
				correctIndex = 1;
			}
			break;
		default:
			break;
		}
		return correctIndex;
	}

	/**
	 * GD - 01/04/18
	 * Stabilisce la corretta sottocategoria di scommessa da analizzare all'interno del JSON
	 * @param scommessaTipo la tipologia di scommessa che si vuole ottenere
	 * @param sport 
	 * @return la sottocategoria da analizzare 
	 */
	private String getCorrectSubCategoryByScommessa(Scommessa scommessaTipo, Sport sport) {
		String subCategory = null;
		switch(scommessaTipo) {
		case ALMENO3GOAL_O2X5:
		case ALPIU2GOAL_U2X5:
			subCategory = GOALS_UNDER_OVER_25_JSON_STRING;
			break;
		case ALMENO1GOAL_O0X5:
		case NESSUNGOAL_U0X5:
		case ALMENO2GOAL_O1X5:
		case ALPIU1GOAL_U1X5:
		case ALMENO4G0AL_O3X5:
		case ALPIU3GOAL_U3X5:
		case ALMENO5GOAL_O4X5:
		case ALPIU4GOAL_U4X5: 
			subCategory = ALTERNATIVE_TOTAL_GOALS_JSON_STRING;
			break;
		case ENTRAMBISEGNANO_GOAL:
		case NESSUNOSEGNA_NOGOAL:
			subCategory = BOTH_TEAMS_TO_SCORE_JSON_STRING;
			break;
		case PAREGGIO_X:
		case SFIDANTE1VINCENTE_1:
		case SFIDANTE2VINCENTE_2:
			if(sport == Sport.CALCIO) {
				subCategory = FULL_TIME_RESULT_JSON_STRING;
			}else if(sport == Sport.TENNIS) {
				subCategory = TO_WIN_MATCH_JSON_STRING;
			}
			break;
		}
		return subCategory;
	}

	/**
	 * GD - 01/04/18
	 * Stabilisce la corretta categoria di scommessa da analizzare all'interno del JSON
	 * @param scommessaTipo la tipologia di scommessa che si vuole ottenere
	 * @return la categoria da analizzare 
	 */
	private String getCorrectCategoryByScommessa(Scommessa scommessaTipo) {
		String category = null;
		switch(scommessaTipo) {
		case ALMENO1GOAL_O0X5:
		case ALMENO2GOAL_O1X5:
		case ALMENO3GOAL_O2X5:
		case ALMENO4G0AL_O3X5:
		case ALMENO5GOAL_O4X5:
		case ALPIU1GOAL_U1X5:
		case ALPIU2GOAL_U2X5:
		case ALPIU3GOAL_U3X5:
		case ALPIU4GOAL_U4X5:
		case ENTRAMBISEGNANO_GOAL:
		case NESSUNGOAL_U0X5:
		case NESSUNOSEGNA_NOGOAL:
			category = GOALS_CAT_JSON_STRING;
			break;
		case PAREGGIO_X:
		case SFIDANTE1VINCENTE_1:
		case SFIDANTE2VINCENTE_2:
			category = MAIN_CAT_JSON_STRING;
			break;
		}
		return category;
	}

}
