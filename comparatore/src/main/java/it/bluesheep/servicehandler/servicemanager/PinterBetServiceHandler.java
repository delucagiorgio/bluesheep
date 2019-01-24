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
import it.bluesheep.comparatore.entities.input.record.PinterBetInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.serviceapi.Service;

public class PinterBetServiceHandler extends ScrapedOddsServiceHandler {

	private static final String FILENAME = "pinterbet";
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
	
	protected PinterBetServiceHandler() {
		super(FILENAME);
		this.serviceName = Service.PINTERBET_SERVICENAME;
		logger = Logger.getLogger(PinterBetServiceHandler.class);
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
				AbstractInputRecord pinterBetRecord = new PinterBetInputRecord(dateParsed, Sport.CALCIO, league, home, away, null);
				Double odd1 = jsonObj.optDouble(ODD1);
				Double oddX = jsonObj.optDouble(ODDX);
				Double odd2 = jsonObj.optDouble(ODD2);
				Double oddO25 = jsonObj.optDouble(ODD_O25);
				Double oddU25 = jsonObj.optDouble(ODD_U25);
				Double oddGoal = jsonObj.optDouble(GOAL);
				Double oddNoGoal = jsonObj.optDouble(NOGOAL);
				
				if(odd1 != null && odd1.compareTo(0D) != 0 && !odd1.isNaN()) {
					AbstractInputRecord pinterBetOdd1 = new PinterBetInputRecord(pinterBetRecord);
					pinterBetOdd1.setQuota(odd1);
					pinterBetOdd1.setTipoScommessa(Scommessa.SFIDANTE1VINCENTE_1);
					returnList.add(pinterBetOdd1);
				}
				
				if(odd2 != null && odd2.compareTo(0D) != 0 && !odd2.isNaN()) {
					AbstractInputRecord pinterBetOdd2 = new PinterBetInputRecord(pinterBetRecord);
					pinterBetOdd2.setQuota(odd2);
					pinterBetOdd2.setTipoScommessa(Scommessa.SFIDANTE2VINCENTE_2);
					returnList.add(pinterBetOdd2);
				}
				
				if(oddX != null && oddX.compareTo(0D) != 0 && !oddX.isNaN()) {
					AbstractInputRecord pinterBetOddX = new PinterBetInputRecord(pinterBetRecord);
					pinterBetOddX.setQuota(oddX);
					pinterBetOddX.setTipoScommessa(Scommessa.PAREGGIO_X);
					returnList.add(pinterBetOddX);
				}
				
				if(oddO25 != null && oddO25.compareTo(0D) != 0 && !oddO25.isNaN()) {
					AbstractInputRecord pinterBetOddO25 = new PinterBetInputRecord(pinterBetRecord);
					pinterBetOddO25.setQuota(oddO25);
					pinterBetOddO25.setTipoScommessa(Scommessa.ALMENO3GOAL_O2X5);
					returnList.add(pinterBetOddO25);
				}
				
				if(oddGoal != null && oddGoal.compareTo(0D) != 0 && !oddGoal.isNaN()) {
					AbstractInputRecord pinterBetOddGoal = new PinterBetInputRecord(pinterBetRecord);
					pinterBetOddGoal.setQuota(oddGoal);
					pinterBetOddGoal.setTipoScommessa(Scommessa.ENTRAMBISEGNANO_GOAL);
					returnList.add(pinterBetOddGoal);
				}
				
				if(oddNoGoal != null && oddNoGoal.compareTo(0D) != 0 && !oddNoGoal.isNaN()) {
					AbstractInputRecord pinterBetOddNoGoal = new PinterBetInputRecord(pinterBetRecord);
					pinterBetOddNoGoal.setQuota(oddNoGoal);
					pinterBetOddNoGoal.setTipoScommessa(Scommessa.NESSUNOSEGNA_NOGOAL);
					returnList.add(pinterBetOddNoGoal);
				}
				
				if(oddU25 != null && oddU25.compareTo(0D) != 0 && !oddU25.isNaN()) {
					AbstractInputRecord pinterBetOddU25 = new PinterBetInputRecord(pinterBetRecord);
					pinterBetOddU25.setQuota(oddU25);
					pinterBetOddU25.setTipoScommessa(Scommessa.ALPIU2GOAL_U2X5);
					returnList.add(pinterBetOddU25);
				}
			}
		}
		
		return returnList;
	}

}
