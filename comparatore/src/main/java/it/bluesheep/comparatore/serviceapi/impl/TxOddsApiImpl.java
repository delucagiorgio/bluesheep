package it.bluesheep.comparatore.serviceapi.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.util.ScommessaUtilManager;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.serviceapi.IApiInterface;
import it.bluesheep.comparatore.serviceapi.multirequesthandler.TxOddsRequestHandler;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

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
		logger = Logger.getLogger(TxOddsApiImpl.class);
		daysInterval = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.TXODDS_DAYS_INTERVAL);
	}
	
	public List<String> getData(Sport sport, Scommessa scommessa) {
		
		String sportCode = identifyCorrectGameCode(sport);
		String oddsType = identifyCorrectBetCode(scommessa, sport);
		
		logger.debug("Setting parameters for TxOdds API request");
		
		String[] dayIntervalSplitted = daysInterval.split(",");		
		int endDay = Integer.parseInt(dayIntervalSplitted[1]);
		
		List<String> result = new ArrayList<String>();
		
		String u = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.TXODDS_USER);
		String p = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.TXODDS_PASSWORD);
		txOddsRequestHandler = new TxOddsRequestHandler(endDay, u + BlueSheepConstants.REGEX_CSV + p);
		
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
	
	public String getInactiveMarketsFromTimestamp(Long timestampLastRequest) {
		String u = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.TXODDS_USER);
		String p = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.TXODDS_PASSWORD);
		
		String baseUrl = "https://xml2.txodds.com/feed/boid_states.php?ident=" + u + "&passwd=" + p + "&last=" + timestampLastRequest;
		String result = null;
		try {
			URL url;
			HttpsURLConnection con;
			url = new URL(baseUrl);
			con = (HttpsURLConnection)url.openConnection();
		   //dump all the content
			result = get_result(con);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return result;
	}
	
	private String get_result(HttpsURLConnection con){
		String result = "";

		if(con!=null){	
			try {
			   BufferedReader br = 
				new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			
			   String input;
			   while ((input = br.readLine()) != null){
				   result += input;
			   }
			   br.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return result;
	}	
}