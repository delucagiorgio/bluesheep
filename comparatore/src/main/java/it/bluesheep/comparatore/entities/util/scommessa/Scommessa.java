package it.bluesheep.comparatore.entities.util.scommessa;

/**
 * Classe per la definizione della tipologia di scommessa
 * @author Giorgio De Luca
 */
public enum Scommessa {

	SFIDANTE1VINCENTE_1("1"),  			//1
	SFIDANTE2VINCENTE_2("2"),			//2
	PAREGGIO_X("X"), 					//X
	ALMENO1GOAL_O0X5("O_0.5"), 			//Over 0,5
	NESSUNGOAL_U0X5("U_0.5"), 			//Under 0,5
	ALMENO2GOAL_O1X5("O_1.5"), 			//Over 1,5
	ALPIU1GOAL_U1X5("U_1.5"),			//Under 1,5
	ALMENO3GOAL_O2X5("O_2.5"),			//Over 2,5
	ALPIU2GOAL_U2X5("U_2.5"),			//Under 2,5
	ALMENO4G0AL_O3X5("O_3.5"),			//Over 3,5
	ALPIU3GOAL_U3X5("U_3.5"),			//Under 3,5
	ALMENO5GOAL_O4X5("O_4.5"),			//Over 4,5
	ALPIU4GOAL_U4X5("U_4.5"),			//Under 4,5
	ENTRAMBISEGNANO_GOAL("GOAL"),		//Goal
	NESSUNOSEGNA_NOGOAL("NOGOAL");		//No goal
	
	private String code;
	
	private Scommessa(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public static Object getScommessaByCode(String scommessaBookmaker) {
		for(Scommessa scommessa : Scommessa.values()) {
			if(scommessa.getCode().equalsIgnoreCase(scommessaBookmaker)) {
				return scommessa;
			}
		}
		return null;
	}
}
