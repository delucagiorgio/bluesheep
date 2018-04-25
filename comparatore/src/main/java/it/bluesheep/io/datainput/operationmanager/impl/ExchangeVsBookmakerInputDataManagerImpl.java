package it.bluesheep.io.datainput.operationmanager.impl;

import java.util.ArrayList;
import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.util.EventoBetfair;
import it.bluesheep.entities.input.util.MercatoEventoBetfairMap;
import it.bluesheep.entities.util.ScommessaUtilManager;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.operationmanager.mapper.AbstractInputMappingProcessor;
import it.bluesheep.io.datainput.operationmanager.mapper.BetfairInputMappingProcessor;
import it.bluesheep.service.api.impl.BetFairApiImpl;

public final class ExchangeVsBookmakerInputDataManagerImpl extends InputDataManagerImpl {

	private final static String SOCCERCODE = "1";
	private final static String TENNISCODE = "2";
	private final static String THREEWAY = "MATCH_ODDS";
	private final static String MONEYLINE = "MATCH_ODDS";
	private final static String UO05 = "OVER_UNDER_05";
	private final static String UO15 = "OVER_UNDER_15";
	private final static String UO25 = "OVER_UNDER_25";
	private final static String UO35 = "OVER_UNDER_35";
	private final static String UO45 = "OVER_UNDER_45";
	private final static String GGNG = "BOTH_TEAMS_TO_SCORE";
	
	private AbstractInputMappingProcessor processor;
	private MercatoEventoBetfairMap mercatoEventoBetfairMap;

	public ExchangeVsBookmakerInputDataManagerImpl() {
		super();
		processor = new BetfairInputMappingProcessor();
		apiServiceInterface = new BetFairApiImpl();
	}


	@Override
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport) {
		
		List<AbstractInputRecord> returnItemsList = new ArrayList<AbstractInputRecord>();
		returnItemsList = processor.mapInputRecordIntoAbstractInputRecord(jsonString, tipoScommessa, sport);
	    mercatoEventoBetfairMap = ((BetFairApiImpl) apiServiceInterface).getMercatoEventoBetfairMap();   
	    returnItemsList = mergeInfoEventoBetfairWithInfoOdds(returnItemsList);
		
		return returnItemsList;
	}

	/**
	 * GD - 25/04/18
	 * Merge delle informazioni relative all'evento e alle sue quote in un unico record 
	 * @param returnItemsList i record relativi alle quote
	 * @return i record relativi alle quote comprensivi delle informazioni dell'evento
	 */
	private List<AbstractInputRecord> mergeInfoEventoBetfairWithInfoOdds(List<AbstractInputRecord> returnItemsList) {
		for(AbstractInputRecord record : returnItemsList) {
			String marketId = record.getFiller();
			EventoBetfair evento = mercatoEventoBetfairMap.get(marketId);
			
			if(evento != null) {
				record.setCampionato(evento.getCampionato());
				record.setDataOraEvento(evento.getDataOraEvento());
				record.setPartecipante1(evento.getPartecipante1());
				record.setPartecipante2(evento.getPartecipante2());
				record.setKeyEvento("" + record.getDataOraEvento() + ":" + record.getSport() + ":" + record.getPartecipante1() + " vs " + record.getPartecipante2());
			}
		}
		return returnItemsList;
	}


	@Override
	protected String identifyCorrectBetCode(Scommessa scommessa, Sport sport) {
		String bet = null;
		if (sport == Sport.CALCIO) {
			if (ScommessaUtilManager.getScommessaListCalcio3WayOdds().contains(scommessa)) {
		    	bet = THREEWAY;
			} else if (scommessa == Scommessa.ALMENO1GOAL_O0X5 || scommessa == Scommessa.NESSUNGOAL_U0X5) {
		    	bet = UO05;
			} else if (scommessa == Scommessa.ALMENO2GOAL_O1X5 || scommessa == Scommessa.ALPIU1GOAL_U1X5) {
		    	bet = UO15;
			} else if (scommessa == Scommessa.ALMENO3GOAL_O2X5 || scommessa == Scommessa.ALPIU2GOAL_U2X5) {
		    	bet = UO25;
			} else if (scommessa == Scommessa.ALMENO4G0AL_O3X5 || scommessa == Scommessa.ALPIU3GOAL_U3X5) {
		    	bet = UO35;
			} else if (scommessa == Scommessa.ALMENO5GOAL_O4X5 || scommessa == Scommessa.ALPIU4GOAL_U4X5) {
		    	bet = UO45;
			} else if (ScommessaUtilManager.getScommessaListCalcioGoalNoGoal().contains(scommessa)) {
		    	bet = GGNG;
			}	
		} else if (sport == Sport.TENNIS) {
			if (ScommessaUtilManager.getScommessaListTennis2WayOdds().contains(scommessa)) {
		    	bet = MONEYLINE;
			}
		}	
		return bet;
	}

	@Override
	protected String identifyCorrectGameCode(Sport sport) {
		String game = null;
		if (sport == Sport.CALCIO) {
			game = SOCCERCODE;
		} else if (sport == Sport.TENNIS) {
			game = TENNISCODE;
		}			
		return game;
	}


	@Override
	protected List<Scommessa> getCombinazioniSportScommessa(Sport sport) {
		List<Scommessa> scommessaList = new ArrayList<Scommessa>();
		
		if(sport.equals(Sport.TENNIS)) {
			scommessaList = ScommessaUtilManager.getScommessaListTennis2WayOdds();
		}else if(sport.equals(Sport.CALCIO)) {
			scommessaList = ScommessaUtilManager.getScommessaListCalcioAllOdds();
		}
		
		return scommessaList;
	}
}