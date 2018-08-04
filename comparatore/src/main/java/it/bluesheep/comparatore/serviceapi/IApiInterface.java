package it.bluesheep.comparatore.serviceapi;

import java.util.List;

import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;

public interface IApiInterface {
	
	/**
	 * Il metodo ritorna una lista di JSON relativa alla richiesta sottomessa
	 * @param sport lo sport richiesto
	 * @param scommessa la tipologia di scommessa richiesta
	 * @return ritorna una lista di JSON contenente tutti i dati relativi agli eventi discriminati
	 * secondi i parametri passati
	 */
	public List<String> getData(Sport sport, Scommessa scommessa);	
	
	/**
	 * GD - 25/04/18
	 * Identifica e assegna i corretti codici da passare all'api
	 * @param scommessa la scommessa richiesta
	 * @param sport lo sport richiesto
	 * @return il codice da utilizzare nella chiamata dell'API
	 */
	public String identifyCorrectBetCode(Scommessa scommessa, Sport sport);
	
	/**
	 * GD - 25/04/18
	 * Identifica e assegna i corretti codici da passare all'api
	 * @param scommessa la scommessa richiesta
	 * @param sport lo sport richiesto
	 * @return il codice da utilizzare nella chiamata dell'API
	 */
	public String identifyCorrectGameCode(Sport sport);

}
