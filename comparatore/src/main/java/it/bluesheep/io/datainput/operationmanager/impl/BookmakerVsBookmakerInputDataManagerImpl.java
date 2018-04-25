package it.bluesheep.io.datainput.operationmanager.impl;

import java.util.ArrayList;
import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.ScommessaUtilManager;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.operationmanager.mapper.AbstractInputMappingProcessor;
import it.bluesheep.io.datainput.operationmanager.mapper.TxOddsInputMappingProcessor;
import it.bluesheep.service.api.impl.TxOddsApiImpl;

public final class BookmakerVsBookmakerInputDataManagerImpl extends InputDataManagerImpl {
	
	private final static String SOCCERCODE = "1";
	private final static String TENNISCODE = "5";
	private final static String THREEWAY = "0";
	private final static String MONEYLINE = "1";
	private final static String TOTALS = "4";
	private final static String GGNG = "11534337";
	
	private AbstractInputMappingProcessor processor;
	
	public BookmakerVsBookmakerInputDataManagerImpl() {
		super();
		processor = new TxOddsInputMappingProcessor();
		apiServiceInterface = new TxOddsApiImpl();
	}

	/**
	 * GD - 17/04/18
	 * Metodo che prende come dati in input il JSON da parsare e il tipo di scommessa di cui si vogliono ottenere le quote,
	 * crea una lista di AbstractInputRecord contenente i dati contenenti le informazioni di output
	 * @param jsonString il JSON da parsare
	 * @param tipoScommessa la tipologia di scommessa per la quale si vogliono ottenere i risultati
	 * @return una lista di AbstractInputRecord contenente i dati relativi al tipo di scommessa scelto
	 */
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport) {

		List<AbstractInputRecord> abstractInputRecordsList = null;
	
		//esegui mapping secondo TXODDS
		abstractInputRecordsList = processor.mapInputRecordIntoAbstractInputRecord(jsonString, tipoScommessa, sport);
		
		return abstractInputRecordsList;
	}

	@Override
	protected String identifyCorrectBetCode(Scommessa scommessa, Sport sport) {
		String bet = null;
		if (sport == Sport.CALCIO) {
			if (ScommessaUtilManager.getScommessaListCalcio3WayOdds().contains(scommessa)) {
		    	bet = THREEWAY;
			} else if (ScommessaUtilManager.getScommessaListCalcioTotalOdds().contains(scommessa)) {
		    	bet = TOTALS;
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
			scommessaList = ScommessaUtilManager.getScommessaListCalcioTotalOdds();
		}
		
		return scommessaList;
	}
	
	
}