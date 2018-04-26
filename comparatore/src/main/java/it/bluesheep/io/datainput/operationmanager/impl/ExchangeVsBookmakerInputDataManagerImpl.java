package it.bluesheep.io.datainput.operationmanager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.util.EventoBetfair;
import it.bluesheep.entities.util.ScommessaUtilManager;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.operationmanager.mapper.AbstractInputMappingProcessor;
import it.bluesheep.io.datainput.operationmanager.mapper.BetfairInputMappingProcessor;
import it.bluesheep.service.api.impl.BetFairApiImpl;

public final class ExchangeVsBookmakerInputDataManagerImpl extends InputDataManagerImpl {
	
	private AbstractInputMappingProcessor processor;
	private Map<String, Map<String, EventoBetfair>> scommessaMapMarketIdEventoMap;


	public ExchangeVsBookmakerInputDataManagerImpl() {
		super();
		processor = new BetfairInputMappingProcessor();
		apiServiceInterface = new BetFairApiImpl();
		scommessaMapMarketIdEventoMap = new HashMap<String, Map<String,EventoBetfair>>();
	}


	@Override
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport) {
		
		List<AbstractInputRecord> returnItemsList = new ArrayList<AbstractInputRecord>();
		returnItemsList = processor.mapInputRecordIntoAbstractInputRecord(jsonString, tipoScommessa, sport);
		String oddsType = apiServiceInterface.identifyCorrectBetCode(tipoScommessa, sport);
		Map<String, EventoBetfair> mercatoEventoBetfairMap = scommessaMapMarketIdEventoMap.get(oddsType + "_" + sport);
	    if(mercatoEventoBetfairMap == null) {
	    	mercatoEventoBetfairMap = ((BetFairApiImpl) apiServiceInterface).getMercatoEventoBetfairMap();
	    	scommessaMapMarketIdEventoMap.put(oddsType + "_" + sport, mercatoEventoBetfairMap);
	    }
	    returnItemsList = mergeInfoEventoBetfairWithInfoOdds(returnItemsList, mercatoEventoBetfairMap);
	    
		return returnItemsList;
	}

	/**
	 * GD - 25/04/18
	 * Merge delle informazioni relative all'evento e alle sue quote in un unico record 
	 * @param returnItemsList i record relativi alle quote
	 * @param mercatoEventoBetfairMap 
	 * @return i record relativi alle quote comprensivi delle informazioni dell'evento
	 */
	private List<AbstractInputRecord> mergeInfoEventoBetfairWithInfoOdds(List<AbstractInputRecord> returnItemsList, Map<String, EventoBetfair> mercatoEventoBetfairMap) {
		for(AbstractInputRecord record : returnItemsList) {
			String marketId = record.getFiller();
			EventoBetfair evento = mercatoEventoBetfairMap.get(marketId);
			
			if(evento != null) {
				record.setCampionato(evento.getCampionato());
				record.setDataOraEvento(evento.getDataOraEvento());
				record.setPartecipante1(evento.getPartecipante1());
				record.setPartecipante2(evento.getPartecipante2());
				record.setKeyEvento("" + record.getDataOraEvento() + ":" + record.getSport() + ":" + record.getPartecipante1() + " vs " + record.getPartecipante2());
			}
		}
		return returnItemsList;
	}

	@Override
	protected List<Scommessa> getCombinazioniSportScommessa(Sport sport) {
		List<Scommessa> scommessaList = new ArrayList<Scommessa>();
		
		if(sport.equals(Sport.TENNIS)) {
			scommessaList = ScommessaUtilManager.getScommessaListTennis2WayOdds();
		}else if(sport.equals(Sport.CALCIO)) {
			scommessaList = ScommessaUtilManager.getScommessaListCalcioAllOdds();
		}
		
		return scommessaList;
	}
}