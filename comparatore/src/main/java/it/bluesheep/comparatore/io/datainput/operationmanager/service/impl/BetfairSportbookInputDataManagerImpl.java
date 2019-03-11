package it.bluesheep.comparatore.io.datainput.operationmanager.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.util.betfair.EventoBetfair;
import it.bluesheep.comparatore.entities.util.ScommessaUtilManager;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datainput.operationmanager.service.mapper.AbstractInputMappingProcessor;
import it.bluesheep.comparatore.io.datainput.operationmanager.service.mapper.BetfairInputMappingProcessor;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.comparatore.serviceapi.impl.BetFairExchangeApiImpl;
import it.bluesheep.comparatore.serviceapi.impl.BetFairSportbookApiImpl;
import it.bluesheep.util.BlueSheepConstants;

public class BetfairSportbookInputDataManagerImpl extends InputDataManagerImpl {

	private AbstractInputMappingProcessor processor;
	private Map<String, Map<String, EventoBetfair>> scommessaMapMarketIdEventoMap;
	
	protected BetfairSportbookInputDataManagerImpl(Sport sport) {
		super(sport);
		processor = new BetfairInputMappingProcessor();
		apiServiceInterface = new BetFairSportbookApiImpl();
		scommessaMapMarketIdEventoMap = new HashMap<String, Map<String,EventoBetfair>>();
		this.serviceName = Service.BETFAIR_SB_SERVICENAME;
		this.logger = Logger.getLogger(BetfairSportbookInputDataManagerImpl.class);
	}

	@Override
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa,
			Sport sport) {
		
		List<AbstractInputRecord> returnItemsList = new ArrayList<AbstractInputRecord>();
		if(jsonString != null && !jsonString.isEmpty()) {
			returnItemsList = processor.mapInputRecordIntoAbstractInputRecord(jsonString, tipoScommessa, sport);
			String oddsType = apiServiceInterface.identifyCorrectBetCode(tipoScommessa, sport);
			Map<String, EventoBetfair> mercatoEventoBetfairMap = scommessaMapMarketIdEventoMap.get(oddsType + "_" + sport);
		    if(mercatoEventoBetfairMap == null) {
		    	mercatoEventoBetfairMap = ((BetFairExchangeApiImpl) apiServiceInterface).getMercatoEventoBetfairMap();
		    	scommessaMapMarketIdEventoMap.put(oddsType + "_" + sport, mercatoEventoBetfairMap);
		    }
		    returnItemsList = mergeInfoEventoBetfairWithInfoOdds(returnItemsList, mercatoEventoBetfairMap);
		}
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
				record.setKeyEvento("" + record.getDataOraEvento() + BlueSheepConstants.REGEX_PIPE + record.getSport() + BlueSheepConstants.REGEX_PIPE + record.getPartecipante1() + BlueSheepConstants.REGEX_VERSUS + record.getPartecipante2());
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
