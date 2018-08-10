package it.bluesheep.comparatore.io.datainput.operationmanager.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.util.bet365.EventoBet365;
import it.bluesheep.comparatore.entities.util.ScommessaUtilManager;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datainput.operationmanager.service.mapper.AbstractInputMappingProcessor;
import it.bluesheep.comparatore.io.datainput.operationmanager.service.mapper.Bet365InputMappingProcessor;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.comparatore.serviceapi.impl.Bet365ApiImpl;
import it.bluesheep.util.BlueSheepConstants;

public final class Bet365InputDataManagerImpl extends InputDataManagerImpl {
	
	private AbstractInputMappingProcessor processor;
	private Map<String,Map<String, EventoBet365>> eventoIdEventoBet365Map;
	
	protected Bet365InputDataManagerImpl(Sport sport) {
		super(sport);
		processor = new Bet365InputMappingProcessor();
		apiServiceInterface = new Bet365ApiImpl();
		eventoIdEventoBet365Map = new HashMap<String, Map<String, EventoBet365>>();
		this.serviceName = Service.BET365_SERVICENAME;
		this.logger = Logger.getLogger(Bet365InputDataManagerImpl.class);
	}
	
	@Override
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport) {
		
		List<AbstractInputRecord> returnItemsList = new ArrayList<AbstractInputRecord>();
		if(jsonString != null && !jsonString.isEmpty()) {
			returnItemsList = processor.mapInputRecordIntoAbstractInputRecord(jsonString, tipoScommessa, sport);
	
			String oddsType = apiServiceInterface.identifyCorrectBetCode(tipoScommessa, sport);
			Map<String, EventoBet365> mercatoEventoBet365 = eventoIdEventoBet365Map.get(oddsType + "_" + sport);
		    if(mercatoEventoBet365 == null) {
		    	mercatoEventoBet365 = ((Bet365ApiImpl) apiServiceInterface).getEventoIdMap();
		    	eventoIdEventoBet365Map.put(oddsType + "_" + sport, mercatoEventoBet365);
		    }
		    returnItemsList = mergeInfoEventoBet365WithInfoOdds(returnItemsList, mercatoEventoBet365);
		}
		return returnItemsList;
	}

	/**
	 * GD - 01/04/2018
	 * Esegue il merge delle informazioni delle quote relative ad uno specifico evento per ogni evento
	 * @param returnItemsList la lista di record contenenti le quote relative agli eventi
	 * @param mercatoEventoBet365 la mappa contenente le informazioni relative alle partite 
	 * @return i record delle quote aggiornati con le informazioni delle partite
	 */
	private List<AbstractInputRecord> mergeInfoEventoBet365WithInfoOdds(List<AbstractInputRecord> returnItemsList, Map<String, EventoBet365> mercatoEventoBet365) {
		for(AbstractInputRecord record : returnItemsList) {
			String eventoId = record.getFiller();
			EventoBet365 evento = mercatoEventoBet365.get(eventoId);
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
