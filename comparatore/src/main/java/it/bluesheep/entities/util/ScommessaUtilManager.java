package it.bluesheep.entities.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;

/**
 * Classe da utilizzare per aggiungere metodi comuni e utili all'elaborazione e alla stesura del codice
 * @author Giorgio De Luca
 *
 */
public class ScommessaUtilManager {
	
	private ScommessaUtilManager() {}
	
	/**
	 * GD - 18/04/18
	 * Ritorna la lista di scommesse per lo sport TENNIS
	 * @return la lista di scommesse per lo sport TENNIS
	 */
	public static List<Scommessa> getScommessaListTennis2WayOdds(){
		return Arrays.asList(Scommessa.SFIDANTE1VINCENTE_1,Scommessa.SFIDANTE2VINCENTE_2);
	}
	
	/**
	 * GD - 18/04/18
	 * Ritorna la lista di scommesse per lo sport CALCIO
	 * @return la lista di scommesse per lo sport CALCIO
	 */
	public static List<Scommessa> getScommessaListCalcioAllOdds(){
		List<Scommessa> returnList = new ArrayList<Scommessa>();
		
		returnList.addAll(getScommessaListCalcio3WayOdds());
		returnList.addAll(getScommessaListCalcioTotalOdds());
		returnList.addAll(getScommessaListCalcioGoalNoGoal());
		
		return returnList;
	}
	
	/**
	 * GD - 18/04/18
	 * Ritorna la lista di scommesse per lo sport CALCIO del tipo 1,X,2
	 * @return la lista di scommesse per lo sport CALCIO del tipo 1,X,2
	 */
	public static List<Scommessa> getScommessaListCalcio3WayOdds(){
		return Arrays.asList(Scommessa.SFIDANTE1VINCENTE_1,Scommessa.SFIDANTE2VINCENTE_2, Scommessa.PAREGGIO_X);
	}
	
	/**
	 * GD - 18/04/18
	 * Ritorna la lista di scommesse per lo sport CALCIO del tipo UNDER/OVER e GOAL/NOGOAL
	 * @return la lista di scommesse per lo sport CALCIO del tipo UNDER/OVER e GOAL/NOGOAL
	 */
	public static List<Scommessa> getScommessaListCalcioTotalOdds(){
		return Arrays.asList(Scommessa.ALMENO1GOAL_O0X5, 
				Scommessa.ALMENO2GOAL_O1X5, 
				Scommessa.ALMENO3GOAL_O2X5,
				Scommessa.ALMENO4G0AL_O3X5,
				Scommessa.ALMENO5GOAL_O4X5,
				Scommessa.ALPIU1GOAL_U1X5,
				Scommessa.ALPIU2GOAL_U2X5,
				Scommessa.ALPIU3GOAL_U3X5,
				Scommessa.ALPIU4GOAL_U4X5,
				Scommessa.NESSUNGOAL_U0X5);
	}
	
	/**
	 * Ritorna la lista di scommesse per lo sport CALCIO del tipo GOAL/NOGOAL
	 * @return la lista di scommesse per lo sport CALCIO del tipo GOAL/NOGOAL
	 */
	public static List<Scommessa> getScommessaListCalcioGoalNoGoal(){
		return Arrays.asList(Scommessa.ENTRAMBISEGNANO_GOAL, 
				Scommessa.NESSUNOSEGNA_NOGOAL);
	}
	
	/**
	 * GD - 18/04/18
	 * Ritorna la lista di scommesse per lo sport CALCIO del tipo OVER
	 * @return la lista di scommesse per lo sport CALCIO del tipo OVER
	 */
	public static List<Scommessa> getScommessaListCalcioOverOdds(){
		return Arrays.asList(Scommessa.ALMENO1GOAL_O0X5, 
				Scommessa.ALMENO2GOAL_O1X5, 
				Scommessa.ALMENO3GOAL_O2X5,
				Scommessa.ALMENO4G0AL_O3X5,
				Scommessa.ALMENO5GOAL_O4X5);
	}
	
	/**
	 * GD - 18/04/18
	 * Ritorna la lista di scommesse per lo sport CALCIO del tipo UNDER
	 * @return la lista di scommesse per lo sport CALCIO del tipo UNDER
	 */
	public static List<Scommessa> getScommessaListCalcioUnderOdds(){
		return Arrays.asList(Scommessa.ALMENO1GOAL_O0X5, 
				Scommessa.ALMENO2GOAL_O1X5, 
				Scommessa.ALMENO3GOAL_O2X5,
				Scommessa.ALMENO4G0AL_O3X5,
				Scommessa.ALMENO5GOAL_O4X5);
	}
	
	/**
	 * GD - 18/04/18
	 * NB: LA SCOMMESSA DEVE ESSERE DI TIPO TOTAL
	 * Ritorna la scommessa di tipo TOTAL opposta alla scommessa passata come parametro
	 * @param scommessa la scommessa di cui si vuole ottenere l'opposta
	 * @param sport lo sport dell'evento
	 * @return la scommessa di tipo TOTAL opposta alla scommessa passata come parametro, null se non trovata
	 */
	public static Scommessa getOppositeScommessaByScommessa(Scommessa scommessa, Sport sport) {		
		return getOppositeScommessaTotalOdds(scommessa, sport);
	}

	/**
	 * GD - 18/04/18	
	 * Mapping 1-a-1 delle scommesse opposte tra loro
	 * @param scommessa la scommessa di cui si vuole ottenere l'opposto
	 * @return la scommessa opposta
	 */
	private static Scommessa getOppositeScommessaTotalOdds(Scommessa scommessa, Sport sport) {
		Scommessa opposite = null;
		if(Sport.CALCIO.equals(sport)) {
			switch(scommessa) {
			case ALMENO1GOAL_O0X5:
				opposite = Scommessa.NESSUNGOAL_U0X5;
				break;
			case ALMENO2GOAL_O1X5:
				opposite = Scommessa.ALPIU1GOAL_U1X5;
				break;
			case ALMENO3GOAL_O2X5:
				opposite = Scommessa.ALPIU2GOAL_U2X5;
				break;
			case ALMENO4G0AL_O3X5:
				opposite = Scommessa.ALPIU3GOAL_U3X5;
				break;
			case ALMENO5GOAL_O4X5:
				opposite = Scommessa.ALPIU4GOAL_U4X5;
				break;
			case NESSUNGOAL_U0X5:
				opposite = Scommessa.ALMENO1GOAL_O0X5;
				break;
			case ALPIU1GOAL_U1X5:
				opposite = Scommessa.ALMENO2GOAL_O1X5;
				break;
			case ALPIU2GOAL_U2X5:
				opposite = Scommessa.ALMENO3GOAL_O2X5;
				break;
			case ALPIU3GOAL_U3X5:
				opposite = Scommessa.ALMENO4G0AL_O3X5;
				break;
			case ALPIU4GOAL_U4X5:
				opposite = Scommessa.ALMENO5GOAL_O4X5;
				break;
			case ENTRAMBISEGNANO_GOAL:
				opposite = Scommessa.NESSUNOSEGNA_NOGOAL;
				break;
			case NESSUNOSEGNA_NOGOAL:
				opposite = Scommessa.ENTRAMBISEGNANO_GOAL;
				break;
			default:
				break;
			}
		}else if(Sport.TENNIS.equals(sport)) {
			switch(scommessa) {
			case SFIDANTE2VINCENTE_2:
				opposite = Scommessa.SFIDANTE1VINCENTE_1;
				break;
			case SFIDANTE1VINCENTE_1:
				opposite = Scommessa.SFIDANTE2VINCENTE_2;
				break;
			default:
				break;
			}
		}
		return opposite;
	}
}