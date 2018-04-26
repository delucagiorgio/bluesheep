package it.bluesheep.service.api.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.betfair.api.BetfairExchangeOperationsManagerImpl;
import com.betfair.api.HttpClientNonInteractiveLoginSSO;
import com.betfair.api.IBetfairExchangeOperationsManager;
import com.betfair.entities.MarketFilter;
import com.betfair.entities.PriceProjection;
import com.betfair.enums.dao.MarketBettingTypeEnumDao;
import com.betfair.enums.types.MarketProjection;
import com.betfair.enums.types.MarketSort;
import com.betfair.enums.types.MatchProjection;
import com.betfair.enums.types.PriceData;
import com.betfair.exceptions.BetFairAPIException;
import com.betfair.util.ISO8601DateTypeAdapter;

import it.bluesheep.entities.input.util.EventoBetfair;
import it.bluesheep.entities.input.util.MercatoEventoBetfairMap;
import it.bluesheep.entities.util.ScommessaUtilManager;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.service.api.IApiInterface;
import it.bluesheep.util.AbstractBluesheepJsonConverter;
import it.bluesheep.util.BetfairBluesheepJsonConverter;

/**
  	//ottenere gli eventi relativi alla scommessa da voler analizzare e del determinato sport
	//parsare i dati JSON ritornati per mappare i dati nelle classi EventoBetfair
	//aggiungere gli eventi alla mappa mercatoEventoBetfairMap
	//per ogni evento si vada a prendere il relativo marketId e lo si inserisca nella mappa, tramite la funzione 
	//mercatoEventoBetfairMap.addEventoBetfairMercatoByTipoScommessa(evento, scommessaTipo, marketId);
	//ottenere tutti i dati relativi ai marketId collezionati e ritornare il JSON contenente i dati delle quote
 * @author 
 *
 */
public class BetFairApiImpl implements IApiInterface {
	
	private final static String SOCCERCODE = "1";
	private final static String TENNISCODE = "2";
	private final static String THREEWAY = "MATCH_ODDS";
	private final static String MONEYLINE = "MATCH_ODDS";
	private final static String UO05 = "OVER_UNDER_05";
	private final static String UO15 = "OVER_UNDER_15";
	private final static String UO25 = "OVER_UNDER_25";
	private final static String UO35 = "OVER_UNDER_35";
	private final static String UO45 = "OVER_UNDER_45";
	private final static String GGNG = "BOTH_TEAMS_TO_SCORE";
	private static final String APPKEY ="txarSy4JZTpbX8OD";
	private static final String RESULT_JSON_STRING = "result";
	private static final String TOTALMATCHED_JSON_STRING = "totalMatched";
	private static final String EVENT_JSON_STRING = "event";
	private static final String ID_JSON_STRING = "id";
	private static final String COMPETITION_JSON_STRING = "competition";
	private static final String NAME_JSON_STRING = "name";
	private static final String MARKETID_JSON_STRING = "marketId";
	private static final String OPENDATE_JSON_STRING = "openDate";
	private static final String DEFAULT_REGEX_NAME_EVENT = " v ";
	private static final String ALTER_REGEX_NAME_EVENT = " - ";
	private static final int QUERY_SIZE_EVENT = 200;
	private static final int QUERY_SIZE_MARKET = 40;
	private String sessionToken;
	private MercatoEventoBetfairMap mercatoEventoBetfairMap;
	private MarketFilter filter;
	
	//Inizializzazione variabili
	private IBetfairExchangeOperationsManager beom;
	

	@Override
	public List<String> getData(Sport sport, Scommessa scommessa) {
		
		System.out.println("Login with Betfair.it");
		login();
		
		String sportCode = identifyCorrectGameCode(sport);
		String oddsType = identifyCorrectBetCode(scommessa, sport);
		
		beom = BetfairExchangeOperationsManagerImpl.getInstance();
		mercatoEventoBetfairMap = new MercatoEventoBetfairMap();

		System.out.println("Richiedo la lista degli eventi...");
		String resultEventsJSON = listEvents(sportCode, oddsType);
		
		//Mapping preliminare delle informazioni degli eventi		
		List<EventoBetfair> eventoList = mapEventsIntoEventoBetfairClass(resultEventsJSON);
		
		//Salvo gli id degli eventi per poter effettuare le chiamate sul marketCatalogue
		Map<String, EventoBetfair> idEventoMap = new HashMap<String, EventoBetfair>();
		for(EventoBetfair evento : eventoList) {
			idEventoMap.put(evento.getId(), evento);
		}
		
		if (sportCode == "1") {
			System.out.println("There are " + idEventoMap.keySet().size() + " events in Betfair exchange for sport SOCCER and odd type " + oddsType);
		} else if (sportCode == "2"){			
			System.out.println("There are " + idEventoMap.keySet().size() + " events in Betfair exchange for sport TENNIS and odd type " + oddsType);
		}


		System.out.println("Searching for markets on retrieved events");
		List<String> marketIdsList = getMarkets(idEventoMap);

		System.out.println("Searching for odds related to retrivied markets");
		List<String> returnJsonResponseList = getMarketsInfo(marketIdsList);
		
		return returnJsonResponseList;	
	}
	
	private List<String> getMarketsInfo(List<String> marketIdsList) {

		//Preparazione del filtro per la chiamata sul marketBook
		PriceProjection priceProjection = new PriceProjection();
		
		Set<PriceData> priceDataSet = new HashSet<PriceData>();
		priceDataSet.add(PriceData.EX_BEST_OFFERS);
		priceProjection.setPriceData(priceDataSet);
		
		List<String> returnJsonResponseList = new ArrayList<String>();
		
		//inizializzazione variabili query paginata
		int cyclesQuery = 0;
		do {
			String responseJson = null;
			List<String> idsSublist = getPortionIdsBySize(QUERY_SIZE_MARKET, marketIdsList, cyclesQuery);
			
			filter.setEventIds(new HashSet<String>(idsSublist));
			
			//chiamata sul marketBook 
			try {				
				responseJson = beom.listMarketBook(idsSublist, priceProjection, null, MatchProjection.ROLLED_UP_BY_PRICE, null, APPKEY, sessionToken);
			} catch (BetFairAPIException e) {
				e.printStackTrace();
			}
			
			//colleziono JSON da ritornare
			returnJsonResponseList.add(responseJson);
			
			cyclesQuery++;
		} while(cyclesQuery * QUERY_SIZE_MARKET < marketIdsList.size());
		
		return returnJsonResponseList;
	}

	/**
	 * Ottiene i mercati relativi ad una lista di eventi
	 * @param idEventoMap mappa relativa agli eventi di cui si vogliono ottenere determinati mercati
	 * @return gli ids dei mercati relativi agli eventi passati come parametro
	 */
	private List<String> getMarkets(Map<String, EventoBetfair> idEventoMap) {

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
				resultMarketIdJSON = beom.listMarketCatalogue(filter, marketProjection, MarketSort.FIRST_TO_START, "200", APPKEY, sessionToken);
			} catch (BetFairAPIException e) {
				e.printStackTrace();
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
	 * Paginazione delle interrogazioni. Il metodo fornisce un subset di elementi rispetto ai dati inziali rispetto ai parametri passati.
	 * @param querySize la dimensione desiderata del subset paginato
	 * @param idsList la lista di partenza
	 * @param cyclesQuery i cicli di iterazioni attualmente eseguiti
	 * @return il subset della grandezza richiesta
	 */
	private List<String> getPortionIdsBySize(int querySize, List<String> idsList, int cyclesQuery) {
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
	 * GD - 25/04/2018
	 * Mappa le informazioni relative ai mercati aperti negli eventi attualmente disponibili, e ritorna una lista di stringhe
	 * contenente tutti gli id relativi ai mercati aperti.
	 * @param idEventoMap la mappa contenente l'id dell'evento e il relativo oggetto evento
	 * @param resultMarketIdJSON il file JSON relativo ai mercati
	 * @return la lista di ids relativi ai mercati attualmente aperti
	 */
	private List<String> mergeInfoEventoAndReturnMarketIdsList(Map<String, EventoBetfair> idEventoMap, String resultMarketIdJSON) {
		
		AbstractBluesheepJsonConverter jsonUtil = BetfairBluesheepJsonConverter.getBetfairBluesheepJsonConverter();
		List<String> marketIds = new ArrayList<String>();
		if(idEventoMap != null && !idEventoMap.isEmpty()) {
			JSONObject catMarketJSON = new JSONObject(resultMarketIdJSON);
			JSONArray resultJSONArray = jsonUtil.getChildNodeArrayByKey(catMarketJSON, RESULT_JSON_STRING);
			
			for(int i = 0; i < resultJSONArray.length(); i++) {
				JSONObject resultJSONObject = resultJSONArray.getJSONObject(i);
				double totalMatched = resultJSONObject.getDouble(TOTALMATCHED_JSON_STRING);
				
				if(totalMatched > 0) {
					JSONObject eventoJSONObject = resultJSONObject.getJSONObject(EVENT_JSON_STRING);
					String idEvento = eventoJSONObject.getString(ID_JSON_STRING);
					
					EventoBetfair eventoBetfairById = idEventoMap.get(idEvento);
					if(eventoBetfairById != null) {
						JSONObject competitionJSONObject = jsonUtil.getChildNodeByKey(resultJSONObject, COMPETITION_JSON_STRING);
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
		}
		return marketIds;
	}
	
	/**
	 * GD - 25/04/2018
	 * Metodo che salva le informazioni relative agli eventi offerti dal servizio
	 * @param resultEventsJSON il file JSON relativo agli eventi
	 * @return una collezione di eventi mappati con le informazioni necessarie
	 */
	private List<EventoBetfair> mapEventsIntoEventoBetfairClass(String resultEventsJSON) {
		AbstractBluesheepJsonConverter jsonUtil = BetfairBluesheepJsonConverter.getBetfairBluesheepJsonConverter();
		
		List<EventoBetfair> mappedEvents = new ArrayList<EventoBetfair>();
		
		if(resultEventsJSON != null && !resultEventsJSON.isEmpty()) {
			JSONObject jsonObject = new JSONObject(resultEventsJSON);
			
			JSONArray resultArrayJSON = jsonUtil.getChildNodeArrayByKey(jsonObject, RESULT_JSON_STRING);
			for(int i = 0; i < resultArrayJSON.length(); i++) {
				//i-esimo evento nella lista ritornata
				JSONObject resultJSONObject = resultArrayJSON.getJSONObject(i);
				JSONObject eventJSONObject = jsonUtil.getChildNodeByKey(resultJSONObject, EVENT_JSON_STRING);				
				
				EventoBetfair evento = new EventoBetfair();
				String dataOraEventoString = eventJSONObject.getString(OPENDATE_JSON_STRING);
				Date dataOraEvento = null;
				try {
					dataOraEvento = (new ISO8601DateTypeAdapter()).getDateFromString(dataOraEventoString);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				evento.setDataOraEvento(dataOraEvento);
				
				String regexSplitName = DEFAULT_REGEX_NAME_EVENT;
				String partitaString = eventJSONObject.getString(NAME_JSON_STRING);
				if(!partitaString.contains(regexSplitName)) {
					regexSplitName = ALTER_REGEX_NAME_EVENT;
				}
				String[] splittedPartitaString = partitaString.split(regexSplitName);
				String partecipante1 = splittedPartitaString[0].trim();
				String partecipante2 = splittedPartitaString[1].trim();
				
				evento.setPartecipante1(partecipante1);
				evento.setPartecipante2(partecipante2);
				
				evento.setId(eventJSONObject.getString(ID_JSON_STRING));
				
				mappedEvents.add(evento);
			}
		}
		
		return mappedEvents;	
	}
	
	private String listEvents(String sport, String oddsType) {
		//Impostazione del filtro base
		filter = new MarketFilter();
		Set<String> eventTypesId = new HashSet<String>();
		Set<String> marketTypeCodes = MarketBettingTypeEnumDao.getRelatedMarketBettingTypeByBetType(oddsType);
		eventTypesId.add(sport);
		filter.setEventTypeIds(eventTypesId);
		filter.setMarketTypeCodes(marketTypeCodes);
		
		//Chiamata al servizio per ottenere tutti gli eventi relativi allo sport e alla scommessa in considerazione 
		String resultEventsJSON = null;
		try {
			resultEventsJSON = beom.listEvents(filter, APPKEY, sessionToken);
		} catch (BetFairAPIException e) {
			e.printStackTrace();
		}
		
		return resultEventsJSON;
	}

	private void login() {
		if(APPKEY == null || sessionToken == null) {
	        HttpClientNonInteractiveLoginSSO loginHttpHelper = new HttpClientNonInteractiveLoginSSO();
	        try {
	        	sessionToken = loginHttpHelper.login();
	        	System.out.println("Login successfully into Betfair.it");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public MercatoEventoBetfairMap getMercatoEventoBetfairMap() {
		return mercatoEventoBetfairMap;
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
}