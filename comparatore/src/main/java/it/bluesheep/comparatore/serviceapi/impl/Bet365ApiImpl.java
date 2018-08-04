package it.bluesheep.comparatore.serviceapi.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.comparatore.entities.input.util.bet365.EventoBet365;
import it.bluesheep.comparatore.entities.input.util.bet365.EventoIdMap;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.serviceapi.IApiInterface;
import it.bluesheep.comparatore.serviceapi.multirequesthandler.Bet365RequestHandler;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.json.Bet365BluesheepJsonConverter;

public class Bet365ApiImpl implements IApiInterface {

	// The logger
	private static Logger logger;
	
	// The bet365 sports' codification
	private static final String SOCCERCODE = "1";
	private static final String TENNISCODE = "13";

	// The json's nodes' names
	private static final String PAGER = "pager";
	private static final String PAGE = "page";
	private static final String PAGESIZE = "per_page";
	
	private static final String EVENTSNUMBER = "total";
	private static final String RESULT_JSON_STRING = "results";
	private static final String OPENDATE_JSON_STRING = "time";
	private static final String NAME_JSON_STRING = "name";
	private static final String HOME = "home";
	private static final String AWAY = "away";
	private static final String LEAGUE = "league";
	private static final String OUREVENTID = "our_event_id";
	
	private static Long updateFrequencyDiff;
	private static final int maxThreadPoolSize = 15;

	// Map useful to get the info about the events at a second step
	private EventoIdMap eventoIdMap;
	private Bet365RequestHandler bet365RequestHandler;
	
	/**
	 * Logger initialization
	 */
	public Bet365ApiImpl() {
		logger = (new BlueSheepLogger(Bet365ApiImpl.class)).getLogger();
		updateFrequencyDiff = Long.valueOf(BlueSheepComparatoreMain.getProperties().getProperty(BlueSheepConstants.UPDATE_FREQUENCY)) * 1000L * 60L;

	}
	
	/**
	 * This is the main method of the class and contains the whole flow of the remote call
	 */
	@Override
	public List<String> getData(Sport sport, Scommessa scommessa) {

		// Sport's code identification
		String sportCode = identifyCorrectGameCode(sport);
		// Obtaining the list of events for the chosen sport
		
		logger.log(Level.CONFIG, "Retrieving list events for sport " + sport);
		List<String> resultEventsJSON = listEvents(sportCode);
		
		//Mapping preliminare delle informazioni degli eventi		
		List<EventoBet365> eventoList = mapEventsIntoEventoClass(resultEventsJSON);
		logger.log(Level.CONFIG, "Available events for sport " + sport + " are " + eventoList.size());

		// Salvo gli id degli eventi per poter effettuare le chiamate sui relativi mercati
		// Mappo gli eventi a seconda del loro id
		List<String> ids = new ArrayList<String>();
		eventoIdMap = new EventoIdMap();
		for(EventoBet365 evento : eventoList) {
			eventoIdMap.addEventoBet365ByEventoId(evento, evento.getId());
			ids.add(evento.getId());
		}
		
		// Ottengo le quote degli eventi trovati
		logger.log(Level.CONFIG, "Searching for markets on retrieved events for sport " + sport);
		List<String> oddsList = getMarkets(ids);
		
		logger.log(Level.CONFIG, "Retrieved odds size for sport " + sport + " is " + oddsList.size());
		
		// restituisco una list di json con le quote di quegli eventi
		return oddsList;
	}
	
	/**
	 * Ottiene i mercati relativi ad una lista di eventi
	 * @param idEventoList mappa relativa agli eventi di cui si vogliono ottenere determinati mercati
	 * @return gli ids dei mercati relativi agli eventi passati come parametro
	 */
	private List<String> getMarkets(List<String> idEventoList) {
		
		// Ottengo le stringhe composte da (max) 10 id relativi agli eventi separati da virgole
		final int BET365APILIMIT = 10;
		List<String> ids = new ArrayList<String>();
		String temp = "";
		int idsListSize = idEventoList.size();
		
		for (int i = 0; i < idsListSize; i++) {
			temp = temp + idEventoList.get(i);
			if((i + 1) % BET365APILIMIT != 0 && (i + 1) < idsListSize) {
				temp = temp + ",";
			}else {
				ids.add(temp);
				temp = "";
			}
		}

		// The application token
		String token = BlueSheepComparatoreMain.getProperties().getProperty(BlueSheepConstants.BET365_TOKEN);
		
		// The retrieved pages collection containing all the requested available events
		List<String> result = new ArrayList<String>();
		bet365RequestHandler= new Bet365RequestHandler(maxThreadPoolSize, token);
		
		result.addAll(bet365RequestHandler.startMultithreadMarketRequests(ids));
		
		return result;
	}

	// It is not possible to distinguish by bet in this API service
	@Override
	public String identifyCorrectBetCode(Scommessa scommessa, Sport sport) {
		return "ALL ODDS";
	}

	/**
	 * It returns the sport's code
	 */
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
	
	/**
	 * This method returns the list of answers by the system structured as json files
	 * @param sport	the requested sport
	 * @return the list of answers by the system structured as json files
	 */
	private List<String> listEvents(String sport) {
		String token = BlueSheepComparatoreMain.getProperties().getProperty(BlueSheepConstants.BET365_TOKEN);		
		int numberOfDays = getNumberOfDays(BlueSheepComparatoreMain.getProperties().getProperty(BlueSheepConstants.BET365_DAYS_INTERVAL));
		
		// The retrieved pages collection
		List<String> result = new ArrayList<String>();
		// Temporal support for page handling
		String partialResult = null;
		
		for (int j = 0; j < numberOfDays; j++) {
			// the page number of the answer by the provider
			int i = 0;
			String date = getDate(j);
			
			try {
				do {
					i++;
					// URL composition
					String https_url = "https://api.betsapi.com/v1/bet365/upcoming?token="+token+"&sport_id="+sport+"&day="+date+"&page="+i+"&charset="+BlueSheepConstants.ENCODING_UTF_8.toLowerCase();
					URL url;
					HttpsURLConnection con;
				
						url = new URL(https_url);
						con = (HttpsURLConnection)url.openConnection();
						partialResult = get_result(con); 
						//dump all the content
						result.add(partialResult);
				} while((partialResult != null) && loopCheck(partialResult));			
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		return result;	
	}
	
	/*
	 * It returns the formatted date to insert in the api request wrt the days of delay starting from today
	 */
	private String getDate(int i) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");		
		Date date = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(date); 
		c.add(Calendar.DATE, i);
		date = c.getTime();
		return sdf.format(date);
	}

	/*
	 * It calculates the number of days the user wants to be informed about
	 */
	private int getNumberOfDays(String property) {
        String [] arrOfStr = property.split(",");
        return Integer.parseInt(arrOfStr[1]);
	}

	/**
	 * It checks whether to exit or not from the loop of API requests
	 *
	 */
	private static boolean loopCheck(String result) {
		JSONObject resultJSON = new JSONObject(result);
		JSONObject pager = Bet365BluesheepJsonConverter.getChildNodeByKey(resultJSON, PAGER);

		if (pager != null) {
			int page = pager.getInt(PAGE);
			int pageSize = pager.getInt(PAGESIZE);
			int eventsNumber = pager.getInt(EVENTSNUMBER);
					
			if (page * pageSize < eventsNumber) {
				return true;
			}			
		}
		return false;
	}
	
	/*
	 * It reads the returned results by the API
	 */
	private static String get_result(HttpsURLConnection con){
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
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
				
		}
	
		return result;
	}
	
	/**
	 * GD - 25/04/2018
	 * Metodo che salva le informazioni relative agli eventi offerti dal servizio
	 * @param resultEventsJSON la lista di file JSON relativi agli eventi
	 * @return una collezione di eventi mappati con le informazioni necessarie
	 */
	private List<EventoBet365> mapEventsIntoEventoClass(List<String> resultEventsJSON) {
		List<EventoBet365> mappedEvents = new ArrayList<EventoBet365>();
		Date processingDate = new Date();
		for (int j = 0; j < resultEventsJSON.size();j++) {
			JSONObject jsonObject = new JSONObject(resultEventsJSON.get(j));
			JSONArray resultArrayJSON = Bet365BluesheepJsonConverter.getChildNodeArrayByKey(jsonObject, RESULT_JSON_STRING);
			for(int i = 0; i < resultArrayJSON.length(); i++) {
				//i-esimo evento nella lista ritornata
				JSONObject eventJSONObject = resultArrayJSON.getJSONObject(i);
				EventoBet365 evento = new EventoBet365();
				
				String dataOraEventoString = eventJSONObject.getString(OPENDATE_JSON_STRING);
				long dataLong = Long.parseLong(dataOraEventoString) * 1000;				
				Date dataOraEvento = new Date(dataLong);
				if(dataOraEvento != null && 
						(dataOraEvento.getTime() - processingDate.getTime() > updateFrequencyDiff)) {
					String p1 = Bet365BluesheepJsonConverter.getChildNodeByKey(eventJSONObject, HOME).getString(NAME_JSON_STRING);
					String p2 = Bet365BluesheepJsonConverter.getChildNodeByKey(eventJSONObject, AWAY).getString(NAME_JSON_STRING);
					String league = Bet365BluesheepJsonConverter.getChildNodeByKey(eventJSONObject, LEAGUE).getString(NAME_JSON_STRING);
	
					evento.setDataOraEvento(dataOraEvento);
					evento.setPartecipante1(p1);
					evento.setPartecipante2(p2);
					evento.setCampionato(league);
					evento.setId(eventJSONObject.getString(OUREVENTID));
									
					mappedEvents.add(evento);
				}
			}
		}
		
		return mappedEvents;	
	}
	
	/*
	 * It returns mercatoEventoBet365Map
	 */
	public EventoIdMap getEventoIdMap() {
		return eventoIdMap;
	}
	
	
}