package it.bluesheep.datainput.operationmanager;

import java.util.List;

import it.bluesheep.datainput.IInputDataManager;
import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;
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
	public abstract String getDataFromService();
	
	public abstract List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, String serviceName, Scommessa tipoScommessa);

}
