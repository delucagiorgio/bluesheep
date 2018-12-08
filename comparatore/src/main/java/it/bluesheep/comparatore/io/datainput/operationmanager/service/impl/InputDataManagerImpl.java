package it.bluesheep.comparatore.io.datainput.operationmanager.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datainput.IInputDataManager;
import it.bluesheep.comparatore.io.datainput.operationmanager.service.util.CSVEventRenameHelper;
import it.bluesheep.comparatore.serviceapi.IApiInterface;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepSharedResources;

/**
 * Classe di interfaccia tra il servizio e l'applicazione, mediante la quale si ottengono i dati richiesti
 * avanzando la richiesta in base alla specializzazione della classe in utilizzo.
 * @author Giorgio De Luca
 *
 */
public abstract class InputDataManagerImpl implements IInputDataManager {
	
	protected IApiInterface apiServiceInterface;
	protected Map<String, List<String>> scommessaJsonListMap;
	protected Logger logger;
	protected Sport sport;
	protected Service serviceName;
	
	protected InputDataManagerImpl(Sport sport) {
		this.sport = sport;
	}

	/**
	 * Il metodo stabilisce quali sono i parametri da passare all'API e interroga il servizio restituendo 
	 * i dati relativi alla sottomissione
	 * @param scommessa la tipologia di scommessa da analizzare
	 * @param sport lo sport da analizzare
	 * @return la lista di JSON contenente i dati richiesti
	 */
	protected List<String> getDataFromService(Scommessa scommessa, Sport sport){	
		List<String> result = new ArrayList<String>();
		
		String bet = apiServiceInterface.identifyCorrectBetCode(scommessa, sport);
		
	    if (bet != null) {
		    scommessaJsonListMap.put(bet + "_" + sport, result);
			logger.debug("Call API " + apiServiceInterface.getClass().getName() + " to retrieve data for service codeDB bet = " + bet);
	    	result.addAll(apiServiceInterface.getData(sport, scommessa));
			logger.debug("Data retrivied successfully");
	    }
	    
	    return result;
	}
		
	@Override
	public abstract List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport);
	
	/**
	 * GD - 18/04/18
	 * Processa tutte le tipologie di quote realtive ad uno sport.
	 * @param sport lo sport di cui si vogliono ottenere le quote
	 * @return una lista di AbstractInputRecord relativi allo sport passato come parametro 
	 * 		   e alle tipologie di scommessa previste per tale sport
	 */
	public List<AbstractInputRecord> processAllData(){
				
		logger.debug("Starting processing data for sport " + sport);
		
		//la lista di scommesse filtrata per possibili combinazioni sul determinato sport
		List<Scommessa> sportScommessaList = getCombinazioniSportScommessa(sport);
		
		List<AbstractInputRecord> recordToBeReturned = new ArrayList<AbstractInputRecord>();
		
		List<String> resultJSONList = new ArrayList<String>();
		scommessaJsonListMap = new HashMap<String,List<String>>();
		//per ogni tipologia di scommessa
		for(Scommessa scommessa : sportScommessaList) {
			logger.debug("Query on data for scommessa " + scommessa);

			resultJSONList = scommessaJsonListMap.get(apiServiceInterface.identifyCorrectBetCode(scommessa, sport) + "_" + sport);
			if (resultJSONList == null) {
				//chiamo il servizio per ottenere i dati sullo sport e la relativa tipologia di scommessa
				resultJSONList = getDataFromService(scommessa, sport);
			}
			
			if (resultJSONList != null && !resultJSONList.isEmpty()) {
				for(String resultJSON : resultJSONList) {
					//salvo i risultati in un unico oggetto da ritornare poi per le successive analisi
					List<AbstractInputRecord> mappedRecords = mapJsonToAbstractInputRecord(resultJSON, scommessa, sport);
					for(AbstractInputRecord record : mappedRecords) {
						record.setPartecipante1(CSVEventRenameHelper.getTranslationPlayerNameIfAvailable(record.getPartecipante1()));
						record.setPartecipante2(CSVEventRenameHelper.getTranslationPlayerNameIfAvailable(record.getPartecipante2()));
					}
					recordToBeReturned.addAll(mappedRecords);				}	
			}
			
		}
		
		scommessaJsonListMap = null;
		
		return recordToBeReturned;
	}
	
	/**
	 * GD - 18/04/18
	 * Metodo per filtrare le tipologie di scommesse in base allo sport
	 * @param sport lo sport di cui si vogliono vedere le quote
	 * @param scommessaTipoList la lista di tutte le possibili quote volute
	 * @return la lista di scommesse filtrate per lo sport passato come parametro
	 */
	protected abstract List<Scommessa> getCombinazioniSportScommessa(Sport sport);
	
	@Override
	public void run() {
		try {
			List<AbstractInputRecord> resultList = processAllData();
			
			Map<Sport, List<AbstractInputRecord>> sportByManagerName = BlueSheepSharedResources.getAllServiceApiMapResult().get(serviceName);
			
			if(sportByManagerName == null) {
				sportByManagerName = new HashMap<Sport, List<AbstractInputRecord>>();
				BlueSheepSharedResources.getAllServiceApiMapResult().put(serviceName, sportByManagerName);
			}
			
			sportByManagerName.put(sport, resultList);
			logger.info("Data retrieval from class " + this.getClass().getSimpleName() + " for sport " + sport + " completed. Mapped records are " + resultList.size());
		}catch(Exception e) {
			logger.error("ERRORE THREAD :: " + e.getMessage(), e);
		}
	}
	
}
