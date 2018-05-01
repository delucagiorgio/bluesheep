package it.bluesheep.entities.input.util.bet365;

import java.util.TreeMap;

public class EventoIdMap extends TreeMap<String, EventoBet365>{

	private static final long serialVersionUID = 1L;

	public EventoIdMap() {
		super();
	}
	
	public void addEventoBet365ByEventoId(EventoBet365 evento, String eventoId) {
		
		//mappa relativa alla corrispondenza scommessa - mercato su un determinato evento
		EventoBet365 eventoByMarketID = get(eventoId);
		
		//se non esiste questo evento nella mappa
		if(eventoByMarketID == null) {
			this.put(eventoId, evento);	
		}
	}
}
