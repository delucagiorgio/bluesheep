package it.bluesheep.comparatore.io.datacompare.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.util.BlueSheepConstants;

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
public final class ChiaveEventoScommessaInputRecordsMap extends TreeMap<Sport,Map<Date,Map<String,Map<Scommessa, Map<String, AbstractInputRecord>>>>>{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger;
	
	public ChiaveEventoScommessaInputRecordsMap() {
		super();
		logger = Logger.getLogger(ChiaveEventoScommessaInputRecordsMap.class);
	}

	public void addToMapEventoScommessaRecord(AbstractInputRecord record) {
		
		//prendo lo sport del record
		Sport sport = record.getSport();
		
		//controllo che esista la chiave per lo sport del record in analisi
		Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> sportMap = get(sport);

		//se non esiste aggiungo la mappa relativa a tale sport
		if(sportMap == null) {
			sportMap = new TreeMap<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>>();
			this.put(sport, sportMap);
		}
		
		//controllo che esista la chiave per la data del record in analisi
		Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>> dateMap = sportMap.get(record.getDataOraEvento());
		
		//se non esiste aggiungo la mappa relativa a tale data
		if(dateMap == null) {
			dateMap = new TreeMap<String, Map<Scommessa, Map<String, AbstractInputRecord>>>();
			sportMap.put(record.getDataOraEvento(), dateMap);
		}
		
		//controllo che esista la chiave per l'evento del record in analisi
		Map<Scommessa, Map<String, AbstractInputRecord>> eventoScommessaRecordsMap = dateMap.get(record.getKeyEvento());
		
		//se non esiste aggiungo la mappa relativa a tale evento
		if(eventoScommessaRecordsMap == null) {
			eventoScommessaRecordsMap = new TreeMap<Scommessa, Map<String, AbstractInputRecord>>();
			dateMap.put(record.getKeyEvento(), eventoScommessaRecordsMap);
		}
		
		//controllo che esista la chiave per la scommessa del record in analisi
		Map<String, AbstractInputRecord> bookmakerRecordMap = eventoScommessaRecordsMap.get(record.getTipoScommessa());
		
		//se non esiste aggiungo la mappa relativa a tale scommessa
		if(bookmakerRecordMap == null) {
			bookmakerRecordMap = new TreeMap<String, AbstractInputRecord>();
			eventoScommessaRecordsMap.put(record.getTipoScommessa(), bookmakerRecordMap);
		}
		
		//controllo che esista la chiave per il bookmaker del record in analisi
		AbstractInputRecord recordOfBookmaker = bookmakerRecordMap.get(record.getBookmakerName());
		
		//se esiste il record, notifico la sovrascrittura con il nuovo aggiornamento
		if(recordOfBookmaker != null) {
			logger.debug("Record with key Evento " + 
					record.getKeyEvento() + " has been updated: " + 
					sport + ("" + BlueSheepConstants.REGEX_SLASH) +
					record.getDataOraEvento() + ("" + BlueSheepConstants.REGEX_SLASH) +
					record.getKeyEvento() + ("" + BlueSheepConstants.REGEX_SLASH) +
					record.getTipoScommessa() + ("" + BlueSheepConstants.REGEX_SLASH) +
					record.getBookmakerName() + ("" + BlueSheepConstants.REGEX_SLASH) + ("" + BlueSheepConstants.REGEX_SLASH) +
					recordOfBookmaker.getSport() + ("" + BlueSheepConstants.REGEX_SLASH) +
					recordOfBookmaker.getDataOraEvento() + ("" + BlueSheepConstants.REGEX_SLASH) +
					recordOfBookmaker.getKeyEvento() + ("" + BlueSheepConstants.REGEX_SLASH) +
					recordOfBookmaker.getTipoScommessa() + ("" + BlueSheepConstants.REGEX_SLASH) +
					recordOfBookmaker.getBookmakerName());
		}
		bookmakerRecordMap.put(record.getBookmakerName(), record);
	}

	public List<AbstractInputRecord> findDeleteAbstractInputRecordInMap(List<AbstractInputRecord> recordList) {
		List<AbstractInputRecord> returnRecord = new ArrayList<AbstractInputRecord>();
		for(AbstractInputRecord record : recordList) {
			if(record != null) {
				Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> dateMap = get(record.getSport());
				if(dateMap != null) {
					Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>> eventoMap = dateMap.get(record.getDataOraEvento());
					if(eventoMap != null) {
						Map<Scommessa, Map<String, AbstractInputRecord>> scommessaMap = eventoMap.get(record.getKeyEvento());
						if(scommessaMap != null) {
							Map<String, AbstractInputRecord> bookmakerMap = scommessaMap.get(record.getTipoScommessa());
							if(bookmakerMap != null) {
								AbstractInputRecord bookmakerRecord = bookmakerMap.get(record.getBookmakerName());
								if(bookmakerRecord != null) {
									returnRecord.add(bookmakerRecord);
								}
								bookmakerMap.remove(record.getBookmakerName());
								
								if(bookmakerMap.isEmpty()) {
									scommessaMap.remove(record.getTipoScommessa());
								
									if(scommessaMap.isEmpty()) {
										eventoMap.remove(record.getKeyEvento());
										
										if(eventoMap.isEmpty()) {
											dateMap.remove(record.getDataOraEvento());
											
											if(dateMap.isEmpty()) {
												remove(record.getSport());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return returnRecord;
	}
}
