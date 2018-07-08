package it.bluesheep.io.datacompare.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;

/**
 * Classe utile a collezionare i dati secondo il criterio:
 * -Chiave --> Evento sportivo (DataOra evento, Nome evento, Campionato, Lega, Sport)
 * -Mappa<Scommessa, List<AbstractInputRecord>> --> rappresenta una sub-collezione di elementi,
 * raggruppati per tipologia di scommessa: ogni scommessa ha una serie di input records a cui fa riferimento.
 * 
 * Questo raggruppamento permette di fare facilmente i confronti tra le quote dello stesso evento fornite da
 * Bookmakers diversi (nella lista finale sono presenti solo occorrenze di quote relative a bookmakers diversi tra loro)
 * 
 * ES: la scommessa di tipo 1 sull'evento X è quotata da Bet365, Bwin.it, Betfair, ecc : nessuno di loro però vende più di una
 * quota --> i bookmakers nella lista sono tutti differenti tra loro
 * @author Giorgio De Luca
 *
 */
public final class ChiaveEventoScommessaInputRecordsMap extends TreeMap<Sport,Map<Date,Map<String,Map<Scommessa, List<AbstractInputRecord>>>>>{
	
	private static final long serialVersionUID = 1L;
	
	public ChiaveEventoScommessaInputRecordsMap() {
		super();
	}

	public void addToMapEventoScommessaRecord(AbstractInputRecord record, Sport sport) {
		
		Map<Date, Map<String, Map<Scommessa,List<AbstractInputRecord>>>> sportMap = get(sport);
		
		if(sportMap == null) {
			sportMap = new TreeMap<Date, Map<String, Map<Scommessa,List<AbstractInputRecord>>>>();
			put(sport, sportMap);
		}
		
		Map<String, Map<Scommessa,List<AbstractInputRecord>>> dateMap = sportMap.get(record.getDataOraEvento());
		
		if(dateMap == null) {
			dateMap = new TreeMap<String, Map<Scommessa,List<AbstractInputRecord>>>();
			sportMap.put(record.getDataOraEvento(), dateMap);
		}
		
		//mappa relativa alle scommesse di un evento
		Map<Scommessa,List<AbstractInputRecord>> eventoScommessaRecordsMap = dateMap.get(record.getKeyEvento());
		
		//key non esiste, va aggiunta per la prima volta
		if(eventoScommessaRecordsMap == null) {
			
			List<AbstractInputRecord> newList = new ArrayList<AbstractInputRecord>();
			newList.add(record);
			Map<Scommessa, List<AbstractInputRecord>> scommessaRecordsMap = new HashMap<Scommessa, List<AbstractInputRecord>>();
			scommessaRecordsMap.put(record.getTipoScommessa(), newList);
			dateMap.put(record.getKeyEvento(), scommessaRecordsMap);
			
		}else{
			//ci sono già delle scommesse all'interno della mappa, il record di input 
			//attuale va aggiunto nella corretta sessione di scommesse e appeso all lista di riferimento
			
			List<AbstractInputRecord> inputRecordListScommessa = eventoScommessaRecordsMap.get(record.getTipoScommessa());
			
			//prima occorrenza di questa tipologia di scommessa
			if(inputRecordListScommessa == null) {
				inputRecordListScommessa = new ArrayList<AbstractInputRecord>();
				inputRecordListScommessa.add(record);
				eventoScommessaRecordsMap.put(record.getTipoScommessa(), inputRecordListScommessa);
			}else {
				//esistono già delle occorrenze per questo evento su questa scommessa: 
				//il record è aggiunto a fine lista
				inputRecordListScommessa.add(record);
			}
		}
	}

	public void addToMapEventoScommessaRecord(AbstractInputRecord csvRecord) {
		String keyEventoString = csvRecord.getKeyEvento();
		String[] splittedKeyEvento = keyEventoString.split("\\|");
		addToMapEventoScommessaRecord(csvRecord, Sport.valueOf(splittedKeyEvento[1]));
	}	
}
