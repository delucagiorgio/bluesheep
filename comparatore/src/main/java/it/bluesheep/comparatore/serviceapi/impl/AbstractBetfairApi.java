package it.bluesheep.comparatore.serviceapi.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.betfair.api.BetfairOperationsManagerImpl;
import com.betfair.api.HttpClientNonInteractiveLoginSSO;
import com.betfair.api.IBetfairOperationsManager;
import com.betfair.entities.MarketFilter;
import com.betfair.enums.dao.MarketBettingTypeEnumDao;
import com.betfair.enums.types.MarketProjection;
import com.betfair.enums.types.MarketSort;
import com.betfair.enums.types.PriceData;
import com.betfair.exceptions.BetFairAPIException;
import com.betfair.util.ISO8601DateTypeAdapter;

import it.bluesheep.comparatore.entities.input.util.betfair.EventoBetfair;
import it.bluesheep.comparatore.entities.input.util.betfair.MercatoEventoBetfairMap;
import it.bluesheep.comparatore.entities.util.ScommessaUtilManager;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.serviceapi.IApiInterface;
import it.bluesheep.comparatore.serviceapi.multirequesthandler.BetfairRequestHandler;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.json.BetfairBluesheepJsonConverter;

public abstract class AbstractBetfairApi implements IApiInterface{
	
	protected final static String SOCCERCODE = "1";
	protected final static String TENNISCODE = "2";
	protected final static String THREEWAY = "MATCH_ODDS";
	protected final static String MONEYLINE = "MATCH_ODDS";
	protected final static String UO05 = "OVER_UNDER_05";
	protected final static String UO15 = "OVER_UNDER_15";
	protected final static String UO25 = "OVER_UNDER_25";
	protected final static String UO35 = "OVER_UNDER_35";
	protected final static String UO45 = "OVER_UNDER_45";
	protected final static String GGNG = "BOTH_TEAMS_TO_SCORE";
	protected static final String RESULT_JSON_STRING = "result";
	protected static final String EVENT_JSON_STRING = "event";
	protected static final String ID_JSON_STRING = "id";
	protected static final String COMPETITION_JSON_STRING = "competition";
	protected static final String NAME_JSON_STRING = "name";
	protected static final String MARKETID_JSON_STRING = "marketId";
	protected static final String OPENDATE_JSON_STRING = "openDate";
	protected static final String DEFAULT_REGEX_NAME_EVENT = " v ";
	protected static final String ALTER_REGEX_NAME_EVENT = " - ";
	protected static final int QUERY_SIZE_EVENT = 200;
	
	protected Long updateFrequencyDiff;
	protected String appKey;
	protected String sessionToken;
	protected Logger logger;
	protected MarketFilter filter;
	protected IBetfairOperationsManager beom;
	protected MercatoEventoBetfairMap mercatoEventoBetfairMap;
	protected String urlBase;
	protected String suffixUrl;
	protected BetfairRequestHandler betfairRequestHandler;
	protected String endpoint;
	protected boolean methodParamName;


	protected AbstractBetfairApi(String appKey, String urlBase, String suffixUrl, String endpoint, boolean methodParamName) {
		this.appKey = appKey;
		this.urlBase = urlBase;
		this.suffixUrl = suffixUrl;
		this.endpoint = endpoint;
		this.methodParamName = methodParamName;
		this.updateFrequencyDiff = Long.valueOf(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.UPDATE_FREQUENCY)) * 1000L * 60L;
	}
	
	@Override
	public List<String> getData(Sport sport, Scommessa scommessa) {
		
		login();
		
		String sportCode = identifyCorrectGameCode(sport);
		String oddsType = identifyCorrectBetCode(scommessa, sport);
		
		beom = BetfairOperationsManagerImpl.getInstance();
		mercatoEventoBetfairMap = new MercatoEventoBetfairMap();

		String resultEventsJSON = listEvents(sportCode, oddsType);
		
		//Mapping preliminare delle informazioni degli eventi		
		List<EventoBetfair> eventoList = mapEventsIntoEventoBetfairClass(resultEventsJSON);
		
		//Salvo gli id degli eventi per poter effettuare le chiamate sul marketCatalogue
		Map<String, EventoBetfair> idEventoMap = new HashMap<String, EventoBetfair>();
		for(EventoBetfair evento : eventoList) {
			idEventoMap.put(evento.getId(), evento);
		}

		List<String> marketIdsList = getMarkets(idEventoMap, oddsType);

		return getMarketsInfo(marketIdsList, idEventoMap);	
	}
	
	public MercatoEventoBetfairMap getMercatoEventoBetfairMap() {
		return mercatoEventoBetfairMap;
	}
	
	protected void login() {
		if(sessionToken == null) {
	        HttpClientNonInteractiveLoginSSO loginHttpHelper = new HttpClientNonInteractiveLoginSSO();
	        try {
	        	sessionToken = loginHttpHelper.login(appKey);
	        	logger.debug("SessionToken = " + sessionToken);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public String identifyCorrectBetCode(Scommessa scommessa, Sport sport) {
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
	public String identifyCorrectGameCode(Sport sport) {
		String game = null;
		if (sport == Sport.CALCIO) {
			game = SOCCERCODE;
		} else if (sport == Sport.TENNIS) {
			game = TENNISCODE;
		}			
		return game;
	}
	
	protected String listEvents(String sport, String oddsType) {
		//Impostazione del filtro base
		filter = new MarketFilter();
		
		Set<String> eventTypesId = new HashSet<String>();
		eventTypesId.add(sport);
		filter.setEventTypeIds(eventTypesId);
		
		Set<String> marketTypeCodes = MarketBettingTypeEnumDao.getRelatedMarketBettingTypeByBetType(oddsType);
		filter.setMarketTypeCodes(marketTypeCodes);
		
		//Chiamata al servizio per ottenere tutti gli eventi relativi allo sport e alla scommessa in considerazione 
		String resultEventsJSON = null;
		try {
			
			resultEventsJSON = beom.listEvents(filter, appKey, sessionToken, urlBase, suffixUrl, endpoint, methodParamName);
		} catch (BetFairAPIException e) {
			logger.error(e.getMessage(), e);
		}
		
		return resultEventsJSON;
	}
	
	/**
	 * GD - 25/04/2018
	 * Metodo che salva le informazioni relative agli eventi offerti dal servizio
	 * @param resultEventsJSON il file JSON relativo agli eventi
	 * @return una collezione di eventi mappati con le informazioni necessarie
	 */
	protected List<EventoBetfair> mapEventsIntoEventoBetfairClass(String resultEventsJSON) {
		List<EventoBetfair> mappedEvents = new ArrayList<EventoBetfair>();
		Date processingDate = new Date();
		if(resultEventsJSON != null && !resultEventsJSON.isEmpty()) {
			JSONObject jsonObject = new JSONObject(resultEventsJSON);
			
			JSONArray resultArrayJSON = BetfairBluesheepJsonConverter.getChildNodeArrayByKey(jsonObject, RESULT_JSON_STRING);
			for(int i = 0; i < resultArrayJSON.length(); i++) {
				//i-esimo evento nella lista ritornata
				JSONObject resultJSONObject = resultArrayJSON.getJSONObject(i);
				JSONObject eventJSONObject = BetfairBluesheepJsonConverter.getChildNodeByKey(resultJSONObject, EVENT_JSON_STRING);				
				
				EventoBetfair evento = new EventoBetfair();
				String dataOraEventoString = eventJSONObject.getString(OPENDATE_JSON_STRING);
				Date dataOraEvento = null;
				try {
					dataOraEvento = (new ISO8601DateTypeAdapter()).getDateFromString(dataOraEventoString);
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
				}
				
				if(dataOraEvento != null && (dataOraEvento.getTime() - processingDate.getTime() > updateFrequencyDiff)) {

					evento.setDataOraEvento(dataOraEvento);
					
					String regexSplitName = DEFAULT_REGEX_NAME_EVENT;
					String partitaString = eventJSONObject.getString(NAME_JSON_STRING);
					if(!partitaString.contains(regexSplitName)) {
						regexSplitName = ALTER_REGEX_NAME_EVENT;
					}
					String[] splittedPartitaString = partitaString.split(regexSplitName);
					if(splittedPartitaString.length == 2) {
						String partecipante1 = splittedPartitaString[0].trim();
						String partecipante2 = splittedPartitaString[1].trim();
						
						evento.setPartecipante1(partecipante1);
						evento.setPartecipante2(partecipante2);
						
						evento.setId(eventJSONObject.getString(ID_JSON_STRING));
						
						mappedEvents.add(evento);
					}
				}
			}
		}
		
		return mappedEvents;	
	}
	
	/**
	 * GD - 25/04/2018
	 * Paginazione delle interrogazioni. Il metodo fornisce un subset di elementi rispetto ai dati inziali rispetto ai parametri passati.
	 * @param querySize la dimensione desiderata del subset paginato
	 * @param idsList la lista di partenza
	 * @param cyclesQuery i cicli di iterazioni attualmente eseguiti
	 * @return il subset della grandezza richiesta
	 */
	protected List<String> getPortionIdsBySize(int querySize, List<String> idsList, int cyclesQuery) {
		int startIndex = cyclesQuery * querySize;
		int endIndex = startIndex + querySize;
		List<String> idsSublist = null;
				
		if(idsList.size() <= querySize) {		
			idsSublist = idsList;
		}else {
			startIndex = cyclesQuery * querySize;
			endIndex = startIndex + querySize;
			if (endIndex >= idsList.size()) {
				endIndex = idsList.size();
			}
			idsSublist = idsList.subList(startIndex, endIndex);
		}
		return idsSublist;
	}

	/**
	 * Ottiene i mercati relativi ad una lista di eventi
	 * @param idEventoMap mappa relativa agli eventi di cui si vogliono ottenere determinati mercati
	 * @param oddsType 
	 * @return gli ids dei mercati relativi agli eventi passati come parametro
	 */
	protected List<String> getMarkets(Map<String, EventoBetfair> idEventoMap, String oddsType) {

		//Preparazione del filtro per la chiamata sul marketCatalogue
		// che info voglio in aggiunta alla risposta
		Set<MarketProjection> marketProjection = new HashSet<MarketProjection>();
		marketProjection.add(MarketProjection.COMPETITION);
		marketProjection.add(MarketProjection.RUNNER_METADATA);
		marketProjection.add(MarketProjection.EVENT);
		
		//query paginata
		int cyclesQuery = 0;
		List<String> idsList = new ArrayList<String>(idEventoMap.keySet());
		List<String> marketIdsList = new ArrayList<String>();
		
		do {
			List<String> idsSublist = getPortionIdsBySize(QUERY_SIZE_EVENT,idsList, cyclesQuery);
			
			filter.setEventIds(new HashSet<String>(idsSublist));

			String resultMarketIdJSON = null;
			
			//chiamata sul marketCatalogue su un set di ids pari a querySize
			try {
				resultMarketIdJSON = beom.listMarketCatalogue(filter, marketProjection, MarketSort.FIRST_TO_START, "200", appKey, sessionToken, urlBase, suffixUrl, endpoint, methodParamName, null);
			} catch (BetFairAPIException e) {
				logger.error(e.getMessage(), e);
			}
			
			//va a completare il mapping sugli oggetti EventoBetfair, 
			//popola la mappa mercatoEventoMap, 
			//ritorna il set paginato di marketIds legati agli eventi passati come parametro alla chiamata
			marketIdsList.addAll(mergeInfoEventoAndReturnMarketIdsList(idEventoMap, resultMarketIdJSON));
			
			cyclesQuery++;
		}
		while(cyclesQuery * QUERY_SIZE_EVENT < idsList.size());		

		return marketIdsList;
	}
	
	/**
	 * GD - 25/04/2018
	 * Mappa le informazioni relative ai mercati aperti negli eventi attualmente disponibili, e ritorna una lista di stringhe
	 * contenente tutti gli id relativi ai mercati aperti.
	 * @param idEventoMap la mappa contenente l'id dell'evento e il relativo oggetto evento
	 * @param resultMarketIdJSON il file JSON relativo ai mercati
	 * @return la lista di ids relativi ai mercati attualmente aperti
	 */
	protected List<String> mergeInfoEventoAndReturnMarketIdsList(Map<String, EventoBetfair> idEventoMap, String resultMarketIdJSON) {
		
		List<String> marketIds = new ArrayList<String>();
		if(idEventoMap != null && !idEventoMap.isEmpty()) {
			JSONObject catMarketJSON = new JSONObject(resultMarketIdJSON);
			JSONArray resultJSONArray = BetfairBluesheepJsonConverter.getChildNodeArrayByKey(catMarketJSON, RESULT_JSON_STRING);
			
			for(int i = 0; i < resultJSONArray.length(); i++) {
				JSONObject resultJSONObject = resultJSONArray.getJSONObject(i);
				
				JSONObject eventoJSONObject = resultJSONObject.getJSONObject(EVENT_JSON_STRING);
				String idEvento = eventoJSONObject.getString(ID_JSON_STRING);
				
				EventoBetfair eventoBetfairById = idEventoMap.get(idEvento);
				if(eventoBetfairById != null) {
					JSONObject competitionJSONObject = BetfairBluesheepJsonConverter.getChildNodeByKey(resultJSONObject, COMPETITION_JSON_STRING);
					if(competitionJSONObject != null) {
						eventoBetfairById.setCampionato(competitionJSONObject.getString(NAME_JSON_STRING));
					}
					String marketId = resultJSONObject.getString(MARKETID_JSON_STRING);
					eventoBetfairById.setMarketId(marketId);
					
					marketIds.add(marketId);
					mercatoEventoBetfairMap.put(marketId, eventoBetfairById);
				}
			}
		}
		return marketIds;
	}
	
	protected List<String> getMarketsInfo(List<String> marketIdsList, Map<String, EventoBetfair> idEventoMap) {

		int sizePoolRequests = marketIdsList.size() / 30;
		
		List<String> returnJsonResponseList = new ArrayList<String>();
		betfairRequestHandler = new BetfairRequestHandler(sizePoolRequests, filter, sessionToken, PriceData.EX_BEST_OFFERS, urlBase, suffixUrl, appKey, endpoint, methodParamName);
	
		returnJsonResponseList.addAll(betfairRequestHandler.startMultithreadMarketRequests(marketIdsList));
		return returnJsonResponseList;
	}
}