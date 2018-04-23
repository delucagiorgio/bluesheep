package it.bluesheep.io.datainput.operationmanager.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.betfair.foe.api.BetfairExchangeOperationsManagerImpl;
import com.betfair.foe.api.HttpClientNonInteractiveLoginSSO;
import com.betfair.foe.api.IBetfairExchangeOperationsManager;
import com.betfair.foe.entities.MarketFilter;
import com.betfair.foe.enums.dao.MarketBettingTypeEnumDao;
import com.betfair.foe.enums.types.MarketProjection;
import com.betfair.foe.enums.types.MarketSort;
import com.betfair.foe.exceptions.BetFairAPIException;
import com.betfair.foe.util.ISO8601DateTypeAdapter;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.EventoBetfair;
import it.bluesheep.entities.input.EventoBetfairMercatoTipoScommessaMap;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.operationmanager.mapper.AbstractInputMappingProcessor;
import it.bluesheep.io.datainput.operationmanager.mapper.BetfairInputMappingProcessor;
import it.bluesheep.util.BetfairBluesheepJsonConverter;

public final class ExchangeVsBookmakerInputDataManagerImpl extends InputDataManagerImpl {

	private AbstractInputMappingProcessor processor;
	private EventoBetfairMercatoTipoScommessaMap eventoBetfairMercatoTipoScommessaMap;
	private String appKey ="txarSy4JZTpbX8OD";
	private String sessionToken;
	
	public ExchangeVsBookmakerInputDataManagerImpl() {
		super();
		processor = new BetfairInputMappingProcessor();
		eventoBetfairMercatoTipoScommessaMap = new EventoBetfairMercatoTipoScommessaMap();
	}
	
	@Override
	public String getDataFromService(Scommessa scommessa, Sport sport) { 
		String inputJson = "";
		
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
		
		//aggiungere gli eventi alla mappa eventoBetfairMercatoTipoScommessaMap
		
		//per ogni evento si vada a prendere il relativo marketId e lo si inserisca nella mappa, tramite la funzione 
		//eventoBetfairMercatoTipoScommessaMap.addEventoBetfairMercatoByTipoScommessa(evento, scommessaTipo, marketId);
		
		//ottenere tutti i dati relativi ai marketId collezionati e ritornare il JSON contenente i dati delle quote
		IBetfairExchangeOperationsManager beom = BetfairExchangeOperationsManagerImpl.getInstance();
		MarketFilter filter = new MarketFilter();
		Set<String> eventTypesId = new HashSet<String>();
		Set<String> marketTypeCodes = MarketBettingTypeEnumDao.getCalcioExchangeOdds();
		eventTypesId.add("1");
		filter.setEventTypeIds(eventTypesId);
		filter.setMarketTypeCodes(marketTypeCodes);
		
		
		String resultEventsJSON = null;
		try {
			resultEventsJSON = beom.listEvents(filter, appKey, sessionToken);
		} catch (BetFairAPIException e) {
			e.printStackTrace();
		}
		
		List<EventoBetfair> eventoList = mapEventsIntoEventoBetfairClass(resultEventsJSON);
		
		List<String> idsList = new ArrayList<String>();
		
		for(EventoBetfair evento : eventoList) {
			idsList.add(evento.getId());
		}
		
		Set<MarketProjection> marketProjection = new HashSet<MarketProjection>();
		marketProjection.add(MarketProjection.COMPETITION);
		marketProjection.add(MarketProjection.RUNNER_METADATA);
		
		int i = 0;
		int querySize = 200;
		int startIndex = 0;
		int endIndex = 0;
		do {
			
			List<String> idsSublist = null;
			if(idsList.size() <= 200) {
				idsSublist = idsList;
			}else {
				startIndex = i * querySize;
				endIndex = startIndex + querySize - 1;
				if (endIndex >= idsList.size()) {
					endIndex = idsList.size() - 1;
				}
				idsSublist = idsList.subList(startIndex, endIndex);
			}
			
			filter.setEventIds(new HashSet<String>(idsSublist));
			String resultMarketIdJSON = null;
			
			try {
				resultMarketIdJSON = beom.listMarketCatalogue(filter, marketProjection, MarketSort.FIRST_TO_START, "200", appKey, sessionToken);
			} catch (BetFairAPIException e) {
				e.printStackTrace();
			} 
			
			i++;
		}while(endIndex != idsList.size() - 1);
		
		
//		InputStream inStream = null;
//		BufferedReader br = null;
//		try {
//			inStream = BookmakerVsBookmakerInputDataManagerImpl.class.getResourceAsStream("/JSONExmapleRequestMatchODDS_TEST.txt");
//			br = new BufferedReader(new InputStreamReader(inStream));
//			String inputLine = br.readLine();
//			while(inputLine != null) {
//				inputJson = inputJson + inputLine;
//				inputLine = br.readLine();
//			}
//			br.close();
//			inStream.close();
//		}catch(Exception e) {
//			System.out.println("Exception is " + e.getMessage());
//		}
		return inputJson;
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
