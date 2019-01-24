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
import it.bluesheep.comparatore.entities.input.record.StanleyBetInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.serviceapi.Service;

public class StanleyBetServiceHandler extends ScrapedOddsServiceHandler {

	private static final String FILENAME = "stanleybet";
	private final String LEAGUE = "league";
	private final String DATE = "date";
	private final String HOME = "home";
	private final String AWAY = "away";
	private final String ODD1 = "Odds1";
	private final String ODDX = "OddsX";
	private final String ODD2 = "Odds2";
	private final String ODD_O25 = "OddsOv25";
	private final String ODD_U25 = "OddsUn25";
	private final String GOAL = "OddsGoal";
	private final String NOGOAL = "OddsNoGoal";
	
	protected StanleyBetServiceHandler() {
		super(FILENAME);
		this.serviceName = Service.STANLEYBET_SERVICENAME;
		logger = Logger.getLogger(StanleyBetServiceHandler.class);
	}
	
	@Override
	protected List<AbstractInputRecord> mapInformationFromFileJSON(String outputLine) {
		List<AbstractInputRecord> returnList = null;

		if(outputLine != null && !StringUtils.isEmpty(outputLine)) {
			JSONArray jsonArray = new JSONArray(outputLine);
			returnList = new ArrayList<AbstractInputRecord>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
				AbstractInputRecord stanleyBetRecord = new StanleyBetInputRecord(dateParsed, Sport.CALCIO, league, home, away, null);
				Double odd1 = jsonObj.optDouble(ODD1);
				Double oddX = jsonObj.optDouble(ODDX);
				Double odd2 = jsonObj.optDouble(ODD2);
				Double oddO25 = jsonObj.optDouble(ODD_O25);
				Double oddU25 = jsonObj.optDouble(ODD_U25);
				Double oddGoal = jsonObj.optDouble(GOAL);
				Double oddNoGoal = jsonObj.optDouble(NOGOAL);
				
				if(odd1 != null && odd1.compareTo(0D) != 0 && !odd1.isNaN()) {
					AbstractInputRecord stanleyBetOdd1 = new StanleyBetInputRecord(stanleyBetRecord);
					stanleyBetOdd1.setQuota(odd1);
					stanleyBetOdd1.setTipoScommessa(Scommessa.SFIDANTE1VINCENTE_1);
					returnList.add(stanleyBetOdd1);
				}
				
				if(odd2 != null && odd2.compareTo(0D) != 0 && !odd2.isNaN()) {
					AbstractInputRecord stanleyBetOdd2 = new StanleyBetInputRecord(stanleyBetRecord);
					stanleyBetOdd2.setQuota(odd2);
					stanleyBetOdd2.setTipoScommessa(Scommessa.SFIDANTE2VINCENTE_2);
					returnList.add(stanleyBetOdd2);
				}
				
				if(oddX != null && oddX.compareTo(0D) != 0 && !oddX.isNaN()) {
					AbstractInputRecord stanleyBetOddX = new StanleyBetInputRecord(stanleyBetRecord);
					stanleyBetOddX.setQuota(oddX);
					stanleyBetOddX.setTipoScommessa(Scommessa.PAREGGIO_X);
					returnList.add(stanleyBetOddX);
				}
				
				if(oddO25 != null && oddO25.compareTo(0D) != 0 && !oddO25.isNaN()) {
					AbstractInputRecord stanleyBetOddO25 = new StanleyBetInputRecord(stanleyBetRecord);
					stanleyBetOddO25.setQuota(oddO25);
					stanleyBetOddO25.setTipoScommessa(Scommessa.ALMENO3GOAL_O2X5);
					returnList.add(stanleyBetOddO25);
				}
				
				if(oddGoal != null && oddGoal.compareTo(0D) != 0 && !oddGoal.isNaN()) {
					AbstractInputRecord stanleyBetOddGoal = new StanleyBetInputRecord(stanleyBetRecord);
					stanleyBetOddGoal.setQuota(oddGoal);
					stanleyBetOddGoal.setTipoScommessa(Scommessa.ENTRAMBISEGNANO_GOAL);
					returnList.add(stanleyBetOddGoal);
				}
				
				if(oddNoGoal != null && oddNoGoal.compareTo(0D) != 0 && !oddNoGoal.isNaN()) {
					AbstractInputRecord stanleyBetOddNoGoal = new StanleyBetInputRecord(stanleyBetRecord);
					stanleyBetOddNoGoal.setQuota(oddNoGoal);
					stanleyBetOddNoGoal.setTipoScommessa(Scommessa.NESSUNOSEGNA_NOGOAL);
					returnList.add(stanleyBetOddNoGoal);
				}
				
				if(oddU25 != null && oddU25.compareTo(0D) != 0 && !oddU25.isNaN()) {
					AbstractInputRecord stanleyBetOddU25 = new StanleyBetInputRecord(stanleyBetRecord);
					stanleyBetOddU25.setQuota(oddU25);
					stanleyBetOddU25.setTipoScommessa(Scommessa.ALPIU2GOAL_U2X5);
					returnList.add(stanleyBetOddU25);
				}
			}
		}
		
		return returnList;
	}

}
