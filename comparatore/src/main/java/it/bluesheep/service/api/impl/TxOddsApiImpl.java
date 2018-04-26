package it.bluesheep.service.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import it.bluesheep.entities.util.ScommessaUtilManager;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.service.api.IApiInterface;

/**
 * In questa classe si stabilisce la connessione con il servizio txodds e si ottiene il file json contenente le
 * quote relative allo sport e al tipo di scommessa richiesti
 * 
 * @author random
 *
 */
public class TxOddsApiImpl implements IApiInterface {

	private final static String SOCCERCODE = "1";
	private final static String TENNISCODE = "5";
	private final static String THREEWAY = "0";
	private final static String MONEYLINE = "1";
	private final static String TOTALS = "4";
	private final static String GGNG = "11534337";
	
	public List<String> getData(Sport sport, Scommessa scommessa) {
		
		String sportCode = identifyCorrectGameCode(sport);
		String oddsType = identifyCorrectBetCode(scommessa, sport);
		
		String u = "fabiodisante";
		// random 
		String p = "1282mdm38cjnbdcjcnddfdsa19932933ncd".substring(20, 28);
		String days = "0,7";

		String active = "1";
		String json = "1";
		String allOdds = "2";
		String odds_format = "0";
		
		String https_url = "https://xml2.txodds.com/feed/odds/xml.php?ident="+u+"&passwd="+p+"&active="+active+"&spid="+sportCode+"&ot="+oddsType+"&days="+days+"&json="+json+"&all_odds="+ allOdds + "&odds_format=" + odds_format;
		List<String> result = new ArrayList<String>();
		
		URL url;
		HttpsURLConnection con;
		try {
		
		   url = new URL(https_url);
		   con = (HttpsURLConnection)url.openConnection();
		     
		   //dump all the content
		   result.add(get_result(con));
				
		} catch (MalformedURLException e) {
		   e.printStackTrace();
		} catch (IOException e) {
		   e.printStackTrace();
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
			   
			  // Print in console just to test 
		      //System.out.println(result);		   
			} catch (IOException e) {
			   e.printStackTrace();
			}
				
		}
	
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