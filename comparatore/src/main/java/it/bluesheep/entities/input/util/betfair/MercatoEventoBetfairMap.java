package it.bluesheep.entities.input.util.betfair;

import java.util.TreeMap;

/**
 * Questa mappa è necessaria per mantenere il legame che c'è tra le informazioni di un EventoBetfair e 
 * le sue relative informazioni riguardanti le quote:
 * 1. Gli eventi vengono mappati 1 a 1 con il loro relativo marketId (UNICO PER TIPOLOGIA DI SCOMMESSA)
 * 	1.1 L'acquisizione dei dati avviene nella seguente modalità:
 * 		- Query sugli eventi --> ottengo tutti gli eventi;
 * 		- Per ogni evento, data la mia tipologia di scommessa in analisi, faccio una query filtrando 
 * 		per tipologia di scommessa sui mercati --> ottengo il marketId
 * 		- Salvo le informazioni nella mappa secondo il metodo implementato sotto
 * @author Giorgio De Luca
 *
 */
public final class MercatoEventoBetfairMap extends TreeMap<String, EventoBetfair> {
	
	private static final long serialVersionUID = 1L;
	
	public MercatoEventoBetfairMap() {
		super();
	}
	
	public void addEventoBetfairByMarketId(EventoBetfair evento, String marketId) {
		
		//mappa relativa alla corrispondenza scommessa - mercato su un determinato evento
		EventoBetfair eventoByMarketID = get(marketId);
		
		//se non esiste questo evento nella mappa
		if(eventoByMarketID == null) {
			this.put(marketId, evento);	
		}
	}

}
