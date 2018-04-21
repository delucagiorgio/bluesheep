package it.bluesheep.io.datainput.operationmanager.impl;

import java.util.ArrayList;
import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.ScommessaUtilManager;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.IInputDataManager;
import it.bluesheep.service.api.IApiInterface;

/**
 * Classe di interfaccia tra il servizio e l'applicazione, mediante la quale si ottengono i dati richiesti
 * avanzando la richiesta in base alla specializzazione della classe in utilizzo.
 * @author Giorgio De Luca
 *
 */
public abstract class InputDataManagerImpl implements IInputDataManager {
	//private static final String BETFAIR_EXCHANGE_API = "BETFAIR_EXCHANGE";
	
	protected IApiInterface apiServiceInterface;
	
	
	// TODO Da capire come gestire le azioni comuni
	/**
	 * 1. preparazione dei dati relativi alla connessione (metodo astratto da implementare nella sottoclasse)
	 * 2. connessione (metodo astratto da implementare nella sottoclasse)
	 * 3. elaborazione della risposta ed eventuale gestione di eccezioni
	 * 4. collezionare i dati in maniera da renderli omogenei
	 */
	@Override
	public abstract String getDataFromService(Scommessa scommessa, Sport sport);
	
	
	@Override
	public abstract List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport);
	
	/**
	 * GD - 18/04/18
	 * Processa tutte le tipologie di quote realtive ad uno sport.
	 * @param sport lo sport di cui si vogliono ottenere le quote
	 * @param scommessaTipoList la lista di tutte le scommesse richieste dal business
	 * @return una lista di AbstractInputRecord relativi allo sport passato come parametro 
	 * 		   e alle tipologie di scommessa previste per tale sport
	 */
	public List<AbstractInputRecord> processAllData(Sport sport){
						
		//la lista di scommesse filtrata per possibili combinazioni sul determinato sport
		List<Scommessa> sportScommessaList = getCombinazioniSportScommessa(sport);
		
		List<AbstractInputRecord> recordToBeReturned = new ArrayList<AbstractInputRecord>();
		
		//per ogni tipologia di scommessa
		for(Scommessa scommessa : sportScommessaList) {
			
			//chiamo il servizio per ottenere i dati sullo sport e la relativa tipologia di scommessa
			String resultJSON = getDataFromService(scommessa, sport);
			
			//salvo i risultati in un unico oggetto da ritornare poi per le successive analisi
			recordToBeReturned.addAll(mapJsonToAbstractInputRecord(resultJSON, scommessa, sport));
		}
		
		return recordToBeReturned;
	}

	/**
	 * GD - 18/04/18
	 * Metodo per filtrare le tipologie di scommesse in base allo sport
	 * @param sport lo sport di cui si vogliono vedere le quote
	 * @param scommessaTipoList la lista di tutte le possibili quote volute
	 * @return la lista di scommesse filtrate per lo sport passato come parametro
	 */
	private List<Scommessa> getCombinazioniSportScommessa(Sport sport) {
		
		List<Scommessa> scommessaList = new ArrayList<Scommessa>();
		
		if(sport.equals(Sport.TENNIS)) {
			scommessaList = ScommessaUtilManager.getScommessaListTennis2WayOdds();
		}else if(sport.equals(Sport.CALCIO)) {
			scommessaList = ScommessaUtilManager.getScommessaListCalcioAllOdds();
		}
		
		return scommessaList;
	}

}
