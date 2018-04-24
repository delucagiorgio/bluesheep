package it.bluesheep.io.datainput.operationmanager.impl;

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

import com.betfair.foe.api.BetfairExchangeOperationsManagerImpl;
import com.betfair.foe.api.HttpClientNonInteractiveLoginSSO;
import com.betfair.foe.api.IBetfairExchangeOperationsManager;
import com.betfair.foe.entities.MarketFilter;
import com.betfair.foe.entities.PriceProjection;
import com.betfair.foe.enums.dao.MarketBettingTypeEnumDao;
import com.betfair.foe.enums.types.MarketProjection;
import com.betfair.foe.enums.types.MarketSort;
import com.betfair.foe.enums.types.MatchProjection;
import com.betfair.foe.enums.types.PriceData;
import com.betfair.foe.exceptions.BetFairAPIException;
import com.betfair.foe.util.ISO8601DateTypeAdapter;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.EventoBetfair;
import it.bluesheep.entities.input.MercatoEventoBetfairMap;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.operationmanager.mapper.AbstractInputMappingProcessor;
import it.bluesheep.io.datainput.operationmanager.mapper.BetfairInputMappingProcessor;
import it.bluesheep.util.AbstractBluesheepJsonConverter;
import it.bluesheep.util.BetfairBluesheepJsonConverter;

public final class ExchangeVsBookmakerInputDataManagerImpl extends InputDataManagerImpl {

	private AbstractInputMappingProcessor processor;
	private MercatoEventoBetfairMap mercatoEventoBetfairMap;
	private String appKey ="txarSy4JZTpbX8OD";
	private String sessionToken;
	
	public ExchangeVsBookmakerInputDataManagerImpl() {
		super();
		processor = new BetfairInputMappingProcessor();
		mercatoEventoBetfairMap = new MercatoEventoBetfairMap();
	}
	
	@Override
	public List<String> getDataFromService(Scommessa scommessa, Sport sport) { 
		
		System.out.println("Starting Betfair process to retrieve information");
		
		if(appKey == null || sessionToken == null) {
	        HttpClientNonInteractiveLoginSSO loginHttpHelper = new HttpClientNonInteractiveLoginSSO();
	        try {
	        	JSONArray credentialJSONArray = new JSONArray(loginHttpHelper.login());
				sessionToken = new JSONObject(credentialJSONArray.getString(0)).getString("sessionToken");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		//ottenere gli eventi relativi alla scommessa da voler analizzare e del determinato sport
		
		//parsare i dati JSON ritornati per mappare i dati nelle classi EventoBetfair
		
		//aggiungere gli eventi alla mappa mercatoEventoBetfairMap
		
		//per ogni evento si vada a prendere il relativo marketId e lo si inserisca nella mappa, tramite la funzione 
		//mercatoEventoBetfairMap.addEventoBetfairMercatoByTipoScommessa(evento, scommessaTipo, marketId);
		
		//ottenere tutti i dati relativi ai marketId collezionati e ritornare il JSON contenente i dati delle quote
		
		//Inizializzazione variabili
		IBetfairExchangeOperationsManager beom = BetfairExchangeOperationsManagerImpl.getInstance();
		
		//Impostazione del filtro base
		MarketFilter filter = new MarketFilter();
		Set<String> eventTypesId = new HashSet<String>();
		Set<String> marketTypeCodes = MarketBettingTypeEnumDao.getCalcioExchangeOdds();
		eventTypesId.add("1");
		filter.setEventTypeIds(eventTypesId);
		filter.setMarketTypeCodes(marketTypeCodes);
		
		//Chiamata al servizio per ottenere tutti gli eventi relativi allo sport e alla scommessa in considerazione 
		String resultEventsJSON = null;
		try {
			resultEventsJSON = beom.listEvents(filter, appKey, sessionToken);
		} catch (BetFairAPIException e) {
			e.printStackTrace();
		}
		
		//Mapping preliminare delle informazioni degli eventi
		List<EventoBetfair> eventoList = mapEventsIntoEventoBetfairClass(resultEventsJSON);
		
		//Salvo gli id degli eventi per poter effettuare le chiamate sul marketCatalogue
		Map<String, EventoBetfair> idEventoMap = new HashMap<String, EventoBetfair>();
		for(EventoBetfair evento : eventoList) {
			idEventoMap.put(evento.getId(), evento);
		}
		
		System.out.println("There are " + idEventoMap.keySet().size() + " events in Betfair exchange for sport " + sport.getCode() + " and odd type " + scommessa.getCode());

		
		//Preparazione del filtro per la chiamata sul marketCatalogue
		Set<MarketProjection> marketProjection = new HashSet<MarketProjection>();
		marketProjection.add(MarketProjection.COMPETITION);
		marketProjection.add(MarketProjection.RUNNER_METADATA);
		marketProjection.add(MarketProjection.EVENT);
		
		//query paginata
		int cyclesQuery = 0;
		int querySize = 200;
		List<String> idsList = new ArrayList<String>(idEventoMap.keySet());
		List<String> marketIdsList = new ArrayList<String>();
		
		do {
			List<String> idsSublist = getPortionIdsBySize(querySize,idsList, cyclesQuery);
			
			filter.setEventIds(new HashSet<String>(idsSublist));

			String resultMarketIdJSON = null;
			
			//chiamata sul marketCatalogue su un set di ids pari a querySize
			try {
				resultMarketIdJSON = beom.listMarketCatalogue(filter, marketProjection, MarketSort.FIRST_TO_START, "200", appKey, sessionToken);
			} catch (BetFairAPIException e) {
				e.printStackTrace();
			} 
			
			//va a completare il mapping sugli oggetti EventoBetfair, 
			//popola la mappa mercatoEventoMap, 
			//ritorna il set paginato di marketIds legati agli eventi passati come parametro alla chiamata
			marketIdsList.addAll(mergeInfoEventoAndReturnMarketIdsList(idEventoMap, resultMarketIdJSON));
			
			cyclesQuery++;
		}
		while(cyclesQuery != idsList.size()/querySize);		
		
		//Preparazione del filtro per la chiamata sul marketBook
		PriceProjection priceProjection = new PriceProjection();
		
		Set<PriceData> priceDataSet = new HashSet<PriceData>();
		priceDataSet.add(PriceData.EX_BEST_OFFERS);
		priceProjection.setPriceData(priceDataSet);
		
		List<String> returnJsonResponseList = new ArrayList<String>();
		
		//inizializzazione variabili query paginata
		cyclesQuery = 0;
		querySize = 40;
		do {
			String responseJson = null;
			List<String> idsSublist = getPortionIdsBySize(querySize, marketIdsList, cyclesQuery);
			
			filter.setEventIds(new HashSet<String>(idsSublist));
			
			//chiamata sul marketBook 
			try {				
				responseJson = beom.listMarketBook(idsSublist, priceProjection, null, MatchProjection.ROLLED_UP_BY_PRICE, null, appKey, sessionToken);
			} catch (BetFairAPIException e) {
				e.printStackTrace();
			}
			
			//colleziono JSON da ritornare
			returnJsonResponseList.add(responseJson);
			
			cyclesQuery++;
		}while(cyclesQuery != marketIdsList.size()/querySize);		
		
		//ritorno tutti i JSON da calcolare
		return returnJsonResponseList;
	}

	private List<String> getPortionIdsBySize(int querySize, List<String> idsList, int cyclesQuery) {
		int startIndex = cyclesQuery * querySize;
		int endIndex = startIndex + querySize - 1;
		List<String> idsSublist = null;
		
		if(idsList.size() <= querySize) {
			idsSublist = idsList;
		}else {
			startIndex = cyclesQuery * querySize;
			endIndex = startIndex + querySize;
			if (endIndex >= idsList.size()) {
				endIndex = idsList.size() - 1;
			}
			idsSublist = idsList.subList(startIndex, endIndex);
		}
		return idsSublist;
	}

	private List<String> mergeInfoEventoAndReturnMarketIdsList(Map<String, EventoBetfair> idEventoMap, String resultMarketIdJSON) {
		
		AbstractBluesheepJsonConverter jsonUtil = BetfairBluesheepJsonConverter.getBetfairBluesheepJsonConverter();
		List<String> marketIds = new ArrayList<String>();
		if(idEventoMap != null && !idEventoMap.isEmpty()) {
			JSONObject catMarketJSON = new JSONObject(resultMarketIdJSON);
			JSONArray resultJSONArray = jsonUtil.getChildNodeArrayByKey(catMarketJSON, "result");
			
			for(int i = 0; i < resultJSONArray.length(); i++) {
				JSONObject resultJSONObject = resultJSONArray.getJSONObject(i);
				double totalMatched = resultJSONObject.getDouble("totalMatched");
				
				if(totalMatched > 0) {
					JSONObject eventoJSONObject = resultJSONObject.getJSONObject("event");
					String idEvento = eventoJSONObject.getString("id");
					
					EventoBetfair eventoBetfairById = idEventoMap.get(idEvento);
					if(eventoBetfairById != null) {
						JSONObject competitionJSONObject = jsonUtil.getChildNodeByKey(resultJSONObject, "competition");
						if(competitionJSONObject != null) {
							eventoBetfairById.setCampionato(competitionJSONObject.getString("name"));
						}
						String marketId = resultJSONObject.getString("marketId");
						eventoBetfairById.setMarketId(marketId);
						
						marketIds.add(marketId);
						mercatoEventoBetfairMap.put(marketId, eventoBetfairById);
					}
				}
			}
		}
		return marketIds;
	}

	private List<EventoBetfair> mapEventsIntoEventoBetfairClass(String resultEventsJSON) {
		BetfairBluesheepJsonConverter jsonUtil = (BetfairBluesheepJsonConverter)BetfairBluesheepJsonConverter.getBetfairBluesheepJsonConverter();
		
		List<EventoBetfair> mappedEvents = new ArrayList<EventoBetfair>();
		
		if(resultEventsJSON != null && !resultEventsJSON.isEmpty()) {
			JSONObject jsonObject = new JSONObject(resultEventsJSON);
			
			JSONArray resultArrayJSON = jsonUtil.getChildNodeArrayByKey(jsonObject, "result");
			for(int i = 0; i < resultArrayJSON.length(); i++) {
				//i-esimo evento nella lista ritornata
				JSONObject resultJSONObject = resultArrayJSON.getJSONObject(i);
				JSONObject eventJSONObject = jsonUtil.getChildNodeByKey(resultJSONObject, "event");				
				
				EventoBetfair evento = new EventoBetfair();
				String dataOraEventoString = eventJSONObject.getString("openDate");
				Date dataOraEvento = null;
				try {
					dataOraEvento = (new ISO8601DateTypeAdapter()).getDateFromString(dataOraEventoString);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				evento.setDataOraEvento(dataOraEvento);
				
				String regexSplitName = " v ";
				String partitaString = eventJSONObject.getString("name");
				if(!partitaString.contains(regexSplitName)) {
					regexSplitName = " - ";
				}
				String[] splittedPartitaString = partitaString.split(regexSplitName);
				String partecipante1 = splittedPartitaString[0].trim();
				String partecipante2 = splittedPartitaString[1].trim();
				
				evento.setPartecipante1(partecipante1);
				evento.setPartecipante2(partecipante2);
				
				evento.setId(eventJSONObject.getString("id"));
				
				mappedEvents.add(evento);
			}
		}
		
		return mappedEvents;	
	}

	@Override
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport) {
		
		List<AbstractInputRecord> returnItemsList = new ArrayList<AbstractInputRecord>();
		
		returnItemsList = processor.mapInputRecordIntoAbstractInputRecord(jsonString, tipoScommessa, sport);
		
		return returnItemsList;
	}

}
