package it.bluesheep.comparatore.io.datacompare.util;

import java.util.List;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;

/**
 * Questa interfaccia permette di aggiungere alla propria classe il metodo per la comparazione delle informazioni
 * degli eventi e di renderli omogenei tra loro ritornando il set di AbstractInputRecord con le informazioni
 * aggiornate
 * @author Giorgio De Luca
 *
 */
public interface ICompareInformationEvents {
	
	/**
	 * GD - 30/04/18
	 * Identifica e rende omogenee le informazioni degli eventi ottenute da TxOdds con quelle del bookmaker
	 * @param bookmakerList lista delle quote relative agli eventi ottenuti dal 
	 * @param eventiTxOddsMap mappa delle quote relative agli eventi ottenuti da TxOdds
	 * @return la lista di record del bookmaker mappata secondo le informazioni trovate negli eventi di TxOdds
	 * @throws Exception 
	 */
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds
	(List<AbstractInputRecord> bookmakerList, ChiaveEventoScommessaInputRecordsMap eventiTxOddsMap) throws Exception;

}
