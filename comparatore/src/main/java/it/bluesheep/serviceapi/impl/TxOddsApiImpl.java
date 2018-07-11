package it.bluesheep.serviceapi.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.entities.util.ScommessaUtilManager;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.serviceapi.IApiInterface;
import it.bluesheep.serviceapi.multirequesthandler.TxOddsRequestHandler;
import it.bluesheep.util.BlueSheepLogger;

/**
 * In questa classe si stabilisce la connessione con il servizio txodds e si ottiene il file json contenente le
 * quote relative allo sport e al tipo di scommessa richiesti
 * 
 * @author Fabio Catania
 *
 */
public class TxOddsApiImpl implements IApiInterface {

	private static Logger logger;
	private final static String SOCCERCODE = "1";
	private final static String TENNISCODE = "5";
	private final static String THREEWAY = "0";
	private final static String MONEYLINE = "1";
	private final static String TOTALS = "4";
	private final static String GGNG = "11534337";
	
	private TxOddsRequestHandler txOddsRequestHandler;
	private String daysInterval;
	
	public TxOddsApiImpl() {
		logger = (new BlueSheepLogger(TxOddsApiImpl.class)).getLogger();
		daysInterval = BlueSheepComparatoreMain.getProperties().getProperty("TXODDS_DAYS");
	}
	
	public List<String> getData(Sport sport, Scommessa scommessa) {
		
		String sportCode = identifyCorrectGameCode(sport);
		String oddsType = identifyCorrectBetCode(scommessa, sport);
		
		logger.info("Setting parameters for TxOdds API request");
		
		String[] dayIntervalSplitted = daysInterval.split(",");		
		int endDay = Integer.parseInt(dayIntervalSplitted[1]);
		
		List<String> result = new ArrayList<String>();
		
		String u = BlueSheepComparatoreMain.getProperties().getProperty("TXODDS_USER");
		String p = BlueSheepComparatoreMain.getProperties().getProperty("TXODDS_PASSWORD");
		txOddsRequestHandler = new TxOddsRequestHandler(endDay, u + ";" + p);
		
		result.addAll(txOddsRequestHandler.startMultithreadMarketRequests(
				Arrays.asList(sportCode, 
							  oddsType, 
							  dayIntervalSplitted[0],
							  dayIntervalSplitted[1])));
		
		return result;	
	}
	
	@Override
	public String identifyCorrectBetCode(Scommessa scommessa, Sport sport) {
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
	public String identifyCorrectGameCode(Sport sport) {
		String game = null;
		if (sport == Sport.CALCIO) {
			game = SOCCERCODE;
		} else if (sport == Sport.TENNIS) {
			game = TENNISCODE;
		}	
		return game;
	}
}