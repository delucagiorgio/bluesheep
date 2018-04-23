package it.bluesheep.entities.input;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import it.bluesheep.entities.util.scommessa.Scommessa;

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
public final class EventoBetfairMercatoTipoScommessaMap extends TreeMap<EventoBetfair, Map<Scommessa,String>> {
	
	private static final long serialVersionUID = 1L;
	
	public EventoBetfairMercatoTipoScommessaMap() {
		super();
	}
	
	public void addEventoBetfairMercatoByTipoScommessa(EventoBetfair evento, Scommessa scommessaTipo, String marketId) {
		
		//mappa relativa alla corrispondenza scommessa - mercato su un determinato evento
		Map<Scommessa,String> scommessaStringMap = get(evento);
		
		//se non esiste questo evento nella mappa
		if(scommessaStringMap == null) {
			
			scommessaStringMap = new HashMap<Scommessa,String>();
			scommessaStringMap.put(scommessaTipo, marketId);
			this.put(evento, scommessaStringMap);
			
		}else {//se esiste già questo evento nella mappa
			
			//se non esiste già la tipologia di scommessa legata a questo evento
			if(scommessaStringMap.get(scommessaTipo) == null) {
				scommessaStringMap.put(scommessaTipo, marketId);
			}
			
		}
	}

}
