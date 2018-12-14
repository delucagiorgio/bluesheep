package it.bluesheep.servicehandler.servicemanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.record.BetflagInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.serviceapi.Service;

public class BetflagServiceHandler extends ScrapedOddsServiceHandler {

	private static final String FILENAME = "betflag";
	private final String LEAGUE = "league";
	private final String DATE = "date";
	private final String HOME = "home";
	private final String AWAY = "away";
	private final String ODD1 = "Odds1";
	private final String L_ODD1 = "Liquidity1";
	private final String ODDX = "OddsX";
	private final String L_ODDX = "LiquidityX";
	private final String ODD2 = "Odds2";
	private final String L_ODD2 = "Liquidity2";
	private final String GOAL = "OddsGoal";
	private final String L_GOAL = "liquidityGOAL";
	private final String NOGOAL = "OddsNoGoal";
	private final String L_NOGOAL = "liquidityNOGOAL";
	private final String ODD_O15 = "Over15";
	private final String L_O15 = "liquidityOver15";
	private final String ODD_U15 = "Under15";
	private final String L_U15 = "liquidityUnder15";
	private final String ODD_O25 = "Over25";
	private final String L_O25 = "liquidityOver25";
	private final String ODD_U25 = "Under25";
	private final String L_U25 = "liquidityUnder25";
	private final String ODD_O35 = "Over35";
	private final String L_O35 = "liquidityOver35";
	private final String ODD_U35 = "Under35";
	private final String L_U35 = "liquidityUnder35";
	private final String ODD_O45 = "Over45";
	private final String L_O45 = "liquidityOver45";
	private final String ODD_U45 = "Under45";
	private final String L_U45 = "liquidityUnder45";
	
	public BetflagServiceHandler() {
		super(FILENAME);
		this.serviceName = Service.BETFLAG_SERVICENAME;
		logger = Logger.getLogger(BetflagServiceHandler.class);
	}

	@Override
	protected List<AbstractInputRecord> mapInformationFromFileJSON(String outputLine) {
		List<AbstractInputRecord> returnList = null;

		if(outputLine != null && !StringUtils.isEmpty(outputLine)) {
			JSONArray jsonArray = new JSONArray(outputLine);
			returnList = new ArrayList<AbstractInputRecord>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				
				String league = jsonObj.optString(LEAGUE);
				String date = jsonObj.getString(DATE);
				Date dateParsed = null;
				try {
					dateParsed = sdf.parse(date);
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
					continue;
				}
				
				String home = jsonObj.getString(HOME);
				String away = jsonObj.getString(AWAY);
				AbstractInputRecord betflagRecord = new BetflagInputRecord(dateParsed, Sport.CALCIO, league, home, away, null);
				Double odd1 = jsonObj.optDouble(ODD1);
				Double size_odd1 = jsonObj.optDouble(L_ODD1);
				Double oddX = jsonObj.optDouble(ODDX);
				Double size_oddX = jsonObj.optDouble(L_ODDX);
				Double odd2 = jsonObj.optDouble(ODD2);
				Double size_odd2 = jsonObj.optDouble(L_ODD2);
				Double oddO15 = jsonObj.optDouble(ODD_O15);
				Double size_oddO15 = jsonObj.optDouble(L_O15);
				Double oddU15 = jsonObj.optDouble(ODD_U15);
				Double size_oddU15 = jsonObj.optDouble(L_U15);
				Double oddO25 = jsonObj.optDouble(ODD_O25);
				Double size_oddO25 = jsonObj.optDouble(L_O25);
				Double oddU25 = jsonObj.optDouble(ODD_U25);
				Double size_oddU25 = jsonObj.optDouble(L_U25);
				Double oddO35 = jsonObj.optDouble(ODD_O35);
				Double size_oddO35 = jsonObj.optDouble(L_O35);
				Double oddU35 = jsonObj.optDouble(ODD_U35);
				Double size_oddU35 = jsonObj.optDouble(L_U35);
				Double oddO45 = jsonObj.optDouble(ODD_O45);
				Double size_oddO45 = jsonObj.optDouble(L_O45);
				Double oddU45 = jsonObj.optDouble(ODD_U45);
				Double size_oddU45 = jsonObj.optDouble(L_U45);
				Double oddGoal = jsonObj.optDouble(GOAL);
				Double size_oddGoal = jsonObj.optDouble(L_GOAL);
				Double oddNoGoal = jsonObj.optDouble(NOGOAL);
				Double size_oddNoGoal = jsonObj.optDouble(L_NOGOAL);
				
				
				if(odd1 != null && odd1.compareTo(0D) != 0 && !odd1.isNaN()) {
					AbstractInputRecord betflagOdd1 = new BetflagInputRecord(betflagRecord);
					betflagOdd1.setQuota(odd1);
					betflagOdd1.setLiquidita(size_odd1);
					betflagOdd1.setTipoScommessa(Scommessa.SFIDANTE1VINCENTE_1);
					returnList.add(betflagOdd1);
				}
				
				if(odd2 != null && odd2.compareTo(0D) != 0 && !odd2.isNaN()) {
					AbstractInputRecord betflagOdd2 = new BetflagInputRecord(betflagRecord);
					betflagOdd2.setQuota(odd2);
					betflagOdd2.setLiquidita(size_odd2);
					betflagOdd2.setTipoScommessa(Scommessa.SFIDANTE2VINCENTE_2);
					returnList.add(betflagOdd2);
				}
				
				if(oddX != null && oddX.compareTo(0D) != 0 && !oddX.isNaN()) {
					AbstractInputRecord betflagOddX = new BetflagInputRecord(betflagRecord);
					betflagOddX.setQuota(oddX);
					betflagOddX.setLiquidita(size_oddX);
					betflagOddX.setTipoScommessa(Scommessa.PAREGGIO_X);
					returnList.add(betflagOddX);
				}
				
				if(oddO15 != null && oddO15.compareTo(0D) != 0 && !oddO15.isNaN()) {
					AbstractInputRecord betflagOddO15 = new BetflagInputRecord(betflagRecord);
					betflagOddO15.setQuota(oddO15);
					betflagOddO15.setLiquidita(size_oddO15);
					betflagOddO15.setTipoScommessa(Scommessa.ALMENO2GOAL_O1X5);
					returnList.add(betflagOddO15);
				}
				
				if(oddU15 != null && oddU15.compareTo(0D) != 0 && !oddU15.isNaN()) {
					AbstractInputRecord betflagOddU15 = new BetflagInputRecord(betflagRecord);
					betflagOddU15.setQuota(oddU15);
					betflagOddU15.setLiquidita(size_oddU15);
					betflagOddU15.setTipoScommessa(Scommessa.ALPIU1GOAL_U1X5);
					returnList.add(betflagOddU15);
				}
				
				if(oddO25 != null && oddO25.compareTo(0D) != 0 && !oddO25.isNaN()) {
					AbstractInputRecord betflagOddO25 = new BetflagInputRecord(betflagRecord);
					betflagOddO25.setQuota(oddO25);
					betflagOddO25.setLiquidita(size_oddO25);
					betflagOddO25.setTipoScommessa(Scommessa.ALMENO3GOAL_O2X5);
					returnList.add(betflagOddO25);
				}
				
				if(oddU25 != null && oddU25.compareTo(0D) != 0 && !oddU25.isNaN()) {
					AbstractInputRecord betflagOddU25 = new BetflagInputRecord(betflagRecord);
					betflagOddU25.setQuota(oddU25);
					betflagOddU25.setLiquidita(size_oddU25);
					betflagOddU25.setTipoScommessa(Scommessa.ALPIU2GOAL_U2X5);
					returnList.add(betflagOddU25);
				}
				
				if(oddO35 != null && oddO35.compareTo(0D) != 0 && !oddO35.isNaN()) {
					AbstractInputRecord betflagOddO35 = new BetflagInputRecord(betflagRecord);
					betflagOddO35.setQuota(oddO35);
					betflagOddO35.setLiquidita(size_oddO35);
					betflagOddO35.setTipoScommessa(Scommessa.ALMENO4G0AL_O3X5);
					returnList.add(betflagOddO35);
				}
				
				if(oddU35 != null && oddU35.compareTo(0D) != 0 && !oddU35.isNaN()) {
					AbstractInputRecord betflagOddU35 = new BetflagInputRecord(betflagRecord);
					betflagOddU35.setQuota(oddU35);
					betflagOddU35.setLiquidita(size_oddU35);
					betflagOddU35.setTipoScommessa(Scommessa.ALPIU3GOAL_U3X5);
					returnList.add(betflagOddU35);
				}
				
				if(oddO45 != null && oddO45.compareTo(0D) != 0 && !oddO45.isNaN()) {
					AbstractInputRecord betflagOddO45 = new BetflagInputRecord(betflagRecord);
					betflagOddO45.setQuota(oddO45);
					betflagOddO45.setLiquidita(size_oddO45);
					betflagOddO45.setTipoScommessa(Scommessa.ALMENO5GOAL_O4X5);
					returnList.add(betflagOddO45);
				}
				
				if(oddU45 != null && oddU45.compareTo(0D) != 0 && !oddU45.isNaN()) {
					AbstractInputRecord betflagOddU45 = new BetflagInputRecord(betflagRecord);
					betflagOddU45.setQuota(oddU45);
					betflagOddU45.setLiquidita(size_oddU45);
					betflagOddU45.setTipoScommessa(Scommessa.ALPIU4GOAL_U4X5);
					returnList.add(betflagOddU45);
				}
				
				if(oddNoGoal != null && oddNoGoal.compareTo(0D) != 0 && !oddNoGoal.isNaN()) {
					AbstractInputRecord betflagNoGoal = new BetflagInputRecord(betflagRecord);
					betflagNoGoal.setQuota(oddNoGoal);
					betflagNoGoal.setLiquidita(size_oddNoGoal);
					betflagNoGoal.setTipoScommessa(Scommessa.NESSUNOSEGNA_NOGOAL);
					returnList.add(betflagNoGoal);
				}

				if(oddGoal != null && oddGoal.compareTo(0D) != 0 && !oddGoal.isNaN()) {
					AbstractInputRecord betflagGoal = new BetflagInputRecord(betflagRecord);
					betflagGoal.setQuota(oddGoal);
					betflagGoal.setLiquidita(size_oddGoal);
					betflagGoal.setTipoScommessa(Scommessa.ENTRAMBISEGNANO_GOAL);
					returnList.add(betflagGoal);
				}
			}
		}
		
		return returnList;		
	}

}
