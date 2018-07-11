package it.bluesheep.io.datainput.operationmanager.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.util.bet365.EventoBet365;
import it.bluesheep.entities.util.ScommessaUtilManager;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.operationmanager.service.mapper.AbstractInputMappingProcessor;
import it.bluesheep.io.datainput.operationmanager.service.mapper.Bet365InputMappingProcessor;
import it.bluesheep.serviceapi.impl.Bet365ApiImpl;

public final class Bet365InputDataManagerImpl extends InputDataManagerImpl {
	
	private AbstractInputMappingProcessor processor;
	private Map<String,Map<String, EventoBet365>> eventoIdEventoBet365Map;
	
	protected Bet365InputDataManagerImpl(Sport sport, Map<String, Map<Sport,List<AbstractInputRecord>>> allServiceApiMapResult) {
		super(sport, allServiceApiMapResult);
		processor = new Bet365InputMappingProcessor();
		apiServiceInterface = new Bet365ApiImpl();
		eventoIdEventoBet365Map = new HashMap<String, Map<String, EventoBet365>>();
		this.serviceName = "BET365";
	}
	
	@Override
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport) {
		
		List<AbstractInputRecord> returnItemsList = new ArrayList<AbstractInputRecord>();
		if(jsonString != null && !jsonString.isEmpty()) {
			returnItemsList = processor.mapInputRecordIntoAbstractInputRecord(jsonString, tipoScommessa, sport);
	
			logger.info("Mapping JSON completed : events mapped from input JSON are " + returnItemsList.size());
	
			logger.info("Merging events information with odds information");
			String oddsType = apiServiceInterface.identifyCorrectBetCode(tipoScommessa, sport);
			Map<String, EventoBet365> mercatoEventoBet365 = eventoIdEventoBet365Map.get(oddsType + "_" + sport);
		    if(mercatoEventoBet365 == null) {
		    	mercatoEventoBet365 = ((Bet365ApiImpl) apiServiceInterface).getEventoIdMap();
		    	eventoIdEventoBet365Map.put(oddsType + "_" + sport, mercatoEventoBet365);
		    }
		    returnItemsList = mergeInfoEventoBet365WithInfoOdds(returnItemsList, mercatoEventoBet365);
		    
			logger.info("Merge events information with odds information completed successfully");
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
				record.setKeyEvento("" + record.getDataOraEvento() + "|" + record.getSport() + "|" + record.getPartecipante1() + " vs " + record.getPartecipante2());
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
