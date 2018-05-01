package it.bluesheep.entities.input.util;

import java.util.Date;

public interface IKeyEventoComparator {
	/**
	 * GD - 30/04/2018
	 * Metodo che compara le informazioni relative alla chiave evento tra due record eterogenei
	 * @param date la data
	 * @param sport lo sport
	 * @param partecipante1 il partecipante 1
	 * @param partecipante2 il partecipante 2
	 * @return true se i due record fanno riferimento allo stesso record, false altrimenti
	 * @throws Exception
	 */
	public boolean isSameEventAbstractInputRecord(Date obj, String sport, String partecipante1, String partecipante2) throws Exception;


}
