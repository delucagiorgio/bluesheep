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
import it.bluesheep.comparatore.entities.input.record.StarVegasInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.serviceapi.Service;

public class StarVegasServiceHandler extends ScrapedOddsServiceHandler {

	private final static String FILENAME = "starvegas";
	private final String LEAGUE = "league";
	private final String DATE = "date";
	private final String HOME = "home";
	private final String AWAY = "away";
	private final String ODD1 = "Odds1";
	private final String ODDX = "OddsX";
	private final String ODD2 = "Odds2";
	private final String ODD_O15 = "OddsOv15";
	private final String ODD_U15 = "OddsUn15";
	private final String ODD_O25 = "OddsOv25";
	private final String ODD_U25 = "OddsUn25";
	private final String ODD_O35 = "OddsOv35";
	private final String ODD_U35 = "OddsUn35";
	
	protected StarVegasServiceHandler() {
		super(FILENAME);
		logger = Logger.getLogger(StarVegasServiceHandler.class);
		this.serviceName = Service.STARVEGAS_SERVICENAME;
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
				AbstractInputRecord starVegasRecord = new StarVegasInputRecord(dateParsed, Sport.CALCIO, league, home, away, null);
				Double odd1 = jsonObj.optDouble(ODD1);
				Double oddX = jsonObj.optDouble(ODDX);
				Double odd2 = jsonObj.optDouble(ODD2);
				Double oddO15 = jsonObj.optDouble(ODD_O15);
				Double oddU15 = jsonObj.optDouble(ODD_U15);
				Double oddO25 = jsonObj.optDouble(ODD_O25);
				Double oddU25 = jsonObj.optDouble(ODD_U25);
				Double oddO35 = jsonObj.optDouble(ODD_O35);
				Double oddU35 = jsonObj.optDouble(ODD_U35);
				
				if(odd1 != null && odd1.compareTo(0D) != 0 && !odd1.isNaN()) {
					AbstractInputRecord starVegasOdd1 = new StarVegasInputRecord(starVegasRecord);
					starVegasOdd1.setQuota(odd1);
					starVegasOdd1.setTipoScommessa(Scommessa.SFIDANTE1VINCENTE_1);
					returnList.add(starVegasOdd1);
				}
				
				if(odd2 != null && odd2.compareTo(0D) != 0 && !odd2.isNaN()) {
					AbstractInputRecord starVegasOdd2 = new StarVegasInputRecord(starVegasRecord);
					starVegasOdd2.setQuota(odd2);
					starVegasOdd2.setTipoScommessa(Scommessa.SFIDANTE2VINCENTE_2);
					returnList.add(starVegasOdd2);
				}
				
				if(oddX != null && oddX.compareTo(0D) != 0 && !oddX.isNaN()) {
					AbstractInputRecord starVegasOddX = new StarVegasInputRecord(starVegasRecord);
					starVegasOddX.setQuota(oddX);
					starVegasOddX.setTipoScommessa(Scommessa.PAREGGIO_X);
					returnList.add(starVegasOddX);
				}
				
				if(oddO15 != null && oddO15.compareTo(0D) != 0 && !oddO15.isNaN()) {
					AbstractInputRecord starVegasOddO15 = new StarVegasInputRecord(starVegasRecord);
					starVegasOddO15.setQuota(oddO15);
					starVegasOddO15.setTipoScommessa(Scommessa.ALMENO2GOAL_O1X5);
					returnList.add(starVegasOddO15);
				}
				
				if(oddU15 != null && oddU15.compareTo(0D) != 0 && !oddU15.isNaN()) {
					AbstractInputRecord starVegasOddU15 = new StarVegasInputRecord(starVegasRecord);
					starVegasOddU15.setQuota(oddU15);
					starVegasOddU15.setTipoScommessa(Scommessa.ALPIU1GOAL_U1X5);
					returnList.add(starVegasOddU15);
				}
				
				if(oddO25 != null && oddO25.compareTo(0D) != 0 && !oddO25.isNaN()) {
					AbstractInputRecord starVegasOddO25 = new StarVegasInputRecord(starVegasRecord);
					starVegasOddO25.setQuota(oddO25);
					starVegasOddO25.setTipoScommessa(Scommessa.ALMENO3GOAL_O2X5);
					returnList.add(starVegasOddO25);
				}
				
				if(oddU25 != null && oddU25.compareTo(0D) != 0 && !oddU25.isNaN()) {
					AbstractInputRecord starVegasOddU25 = new StarVegasInputRecord(starVegasRecord);
					starVegasOddU25.setQuota(oddU25);
					starVegasOddU25.setTipoScommessa(Scommessa.ALPIU2GOAL_U2X5);
					returnList.add(starVegasOddU25);
				}
				
				if(oddO35 != null && oddO35.compareTo(0D) != 0 && !oddO35.isNaN()) {
					AbstractInputRecord starVegasOddO35 = new StarVegasInputRecord(starVegasRecord);
					starVegasOddO35.setQuota(oddU35);
					starVegasOddO35.setTipoScommessa(Scommessa.ALMENO4G0AL_O3X5);
					returnList.add(starVegasOddO35);
				}
				
				if(oddU35 != null && oddU35.compareTo(0D) != 0 && !oddU35.isNaN()) {
					AbstractInputRecord starVegasOddU35 = new StarVegasInputRecord(starVegasRecord);
					starVegasOddU35.setQuota(oddU35);
					starVegasOddU35.setTipoScommessa(Scommessa.ALPIU3GOAL_U3X5);
					returnList.add(starVegasOddU35);
				}
			}
		}
		
		return returnList;
	}

}
