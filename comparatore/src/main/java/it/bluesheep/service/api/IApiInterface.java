package it.bluesheep.service.api;

import java.util.List;

public interface IApiInterface {
	
	/**
	 * Il metodo ritorna una lista di JSON relativa alla richiesta sottomessa
	 * @param sport lo sport richiesto
	 * @param oddsType la tipologia di scommessa richiesta
	 * @return ritorna una lista di JSON contenente tutti i dati relativi agli eventi discriminati
	 * secondi i parametri passati
	 */
	public List<String> getData(String sport, String oddsType);	

}
