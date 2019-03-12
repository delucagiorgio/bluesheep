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
import com.betfair.enums.dao.MarketBettingTypeEnumDao;
import com.betfair.enums.types.MarketProjection;
import com.betfair.enums.types.MarketSort;
import com.betfair.exceptions.BetFairAPIException;
import com.betfair.util.ISO8601DateTypeAdapter;

import it.bluesheep.comparatore.entities.input.util.betfair.EventoBetfair;
import it.bluesheep.comparatore.entities.input.util.betfair.MercatoEventoBetfairMap;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.json.BetfairBluesheepJsonConverter;

public class BetFairSportbookApiImpl extends AbstractBetfairApi {
	
	protected static final String RESULT_JSON_STRING = "result";
	protected static final String MARKETID_JSON_STRING = "marketId";
	protected static final String MARKETDETAILS_JSON_STRING = "marketDetails";
	protected static final String EVENTOID_JSON_STRING = "eventId";
	protected static final String COMPETITIONID_JSON_STRING = "competitionId";
	protected static final String COMPETITION_JSON_STRING = "competition";
	protected static final String ID_JSON_STRING = "id";
	protected static final String NAME_JSON_STRING = "name";
	
	public BetFairSportbookApiImpl() {
		super(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_APPKEY_SB), 
				BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_BASE_URL_SB),
				BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_RESCRIPT_SUFFIX_SB),
				BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_SB_ENDPOINT), true);
		logger = Logger.getLogger(BetFairSportbookApiImpl.class);
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
		
		String championshipJson = listCompetitions();
		
		idEventoMap = substisuteChampionshipValue(championshipJson, idEventoMap);

		List<String> marketIdsList = getMarkets(idEventoMap, oddsType);

		return getMarketsInfo(marketIdsList, idEventoMap);	
	}
	
	private Map<String, EventoBetfair> substisuteChampionshipValue(String championshipJson, Map<String, EventoBetfair> idEventoMap) {
		
		if(championshipJson != null) {
			Map<String, String> idNameMap = new HashMap<String, String>();
			JSONObject jsonObject = new JSONObject(championshipJson);
			JSONArray resultJSONArray = jsonObject.getJSONArray(RESULT_JSON_STRING);
			for(int i = 0; i < resultJSONArray.length(); i++) {
				JSONObject obj = resultJSONArray.getJSONObject(i);
				JSONObject competition = obj.getJSONObject(COMPETITION_JSON_STRING);
				String id = competition.getString(ID_JSON_STRING);
				String name = competition.getString(NAME_JSON_STRING);
				idNameMap.put(id, name);
			}
				
			for(String eventoId : idEventoMap.keySet()) {
				EventoBetfair evento = idEventoMap.get(eventoId);
				String name = idNameMap.get(evento.getCampionato());
				if(name != null) {
					evento.setCampionato(name);
				}
			}
			
		}
		
		return idEventoMap;
	}

	private String listCompetitions() {
		
		filter.setMarketTypeCodes(null);
		String returnString = null;
		try {
			returnString = beom.listCompetitions(filter, appKey, sessionToken, urlBase, endpoint, suffixUrl);
		}catch(BetFairAPIException e) {
			logger.error(e.getMessage(), e);
		}
		
		return returnString;
	}

	/**
	 * Ottiene i mercati relativi ad una lista di eventi
	 * @param idEventoMap mappa relativa agli eventi di cui si vogliono ottenere determinati mercati
	 * @return gli ids dei mercati relativi agli eventi passati come parametro
	 */
	@Override
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
				resultMarketIdJSON = beom.listMarketCatalogue(filter, marketProjection, MarketSort.FIRST_TO_START, "200", appKey, sessionToken, urlBase, suffixUrl, endpoint, methodParamName, MarketBettingTypeEnumDao.getRelatedMarketBettingTypeByBetType(oddsType));
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
		JSONObject catMarketJSON = new JSONObject(resultMarketIdJSON);
		JSONArray resultJSONArray = BetfairBluesheepJsonConverter.getChildNodeArrayByKey(catMarketJSON, RESULT_JSON_STRING);
		
		for(int i = 0; i < resultJSONArray.length(); i++) {
			JSONObject resultJSONObject = resultJSONArray.getJSONObject(i);
			
			String marketId = resultJSONObject.optString(MARKETID_JSON_STRING);
			
			if(marketId != null) {
				marketIds.add(marketId);
			}
		}
		return marketIds;
	}
	
	@Override
	protected List<String> getMarketsInfo(List<String> marketIdsList, Map<String, EventoBetfair> mapEventoIdEventoBetfair) {
		List<String> returnList = super.getMarketsInfo(marketIdsList, mapEventoIdEventoBetfair);
		if(returnList != null && !returnList.isEmpty()) {
			for(String json : returnList) {
				try {
					mapEventoToMarketId(json, mapEventoIdEventoBetfair);
				}catch(Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return returnList;
	}

	private void mapEventoToMarketId(String json, Map<String, EventoBetfair> mapEventoIdEventoBetfair) {
		JSONObject jsonObject = new JSONObject(json);
		
		//Ottieni risultati della chiamata
		JSONArray resultArrayJSONObject = BetfairBluesheepJsonConverter.getChildNodeArrayByKey(jsonObject, RESULT_JSON_STRING);
		
		JSONObject resultJSONObject = null;
		JSONArray marketNode = null;
		
		//per ogni risultato --> un "result" come oggetto rappresenta la risposta del servizio 
		//e contiene tutte le quote relative ad un mercato (marketId)
		for(int i = 0; i < resultArrayJSONObject.length(); i++) {
			try {
				resultJSONObject = resultArrayJSONObject.getJSONObject(i);
				marketNode = resultJSONObject.optJSONArray(MARKETDETAILS_JSON_STRING);
				if(marketNode != null) {
					for(int k = 0; k < marketNode.length(); k++) {
						JSONObject marketJSONObject = marketNode.getJSONObject(i);
						String marketNodeString = marketJSONObject.getString(MARKETID_JSON_STRING);
						String eventId = marketJSONObject.getString(EVENTOID_JSON_STRING);
						
						if(eventId != null && mapEventoIdEventoBetfair.get(eventId) != null) {
							EventoBetfair evento = mapEventoIdEventoBetfair.get(eventId);
							evento.setMarketId(marketNodeString);
							mercatoEventoBetfairMap.put(marketNodeString, evento);
						}
					}
				}
			}catch(Exception e) {
				logger.error("Error during data extraction from JSON: exception is " + e.getMessage(), e);	
			}
		}
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
					
					evento.setCampionato(eventJSONObject.optString(COMPETITIONID_JSON_STRING));
					
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
}
