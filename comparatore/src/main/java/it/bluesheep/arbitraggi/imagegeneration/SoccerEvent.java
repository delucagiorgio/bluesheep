package it.bluesheep.arbitraggi.imagegeneration;

/**
 * Evento di calcio
 * @author Fabio
 *
 */
public class SoccerEvent extends Event {

	private final static String UNOICSDUE = "1X2";
	private final static String UNO = "1";
	private final static String DUE = "2";
	private final static String ICS = "X";
	
	private final static String UNODUE = "12";
	private final static String UNOICS = "1X";
	private final static String ICSDUE = "X2";
	
	private final static String UNDEROVER65 = "UNDER/OVER 6.5";
	private final static String UNDER65 = "Under 6.5";
	private final static String OVER65 = "Over 6.5";
	private final static String UNDER65CODE = "U_6.5";
	private final static String OVER65CODE = "O_6.5";
	
	private final static String UNDEROVER55 = "UNDER/OVER 5.5";
	private final static String UNDER55 = "Under 5.5";
	private final static String OVER55 = "Over 5.5";
	private final static String UNDER55CODE = "U_5.5";
	private final static String OVER55CODE = "O_5.5";

	private final static String UNDEROVER45 = "UNDER/OVER 4.5";
	private final static String UNDER45 = "Under 4.5";
	private final static String OVER45 = "Over 4.5";
	private final static String UNDER45CODE = "U_4.5";
	private final static String OVER45CODE = "O_4.5";

	private final static String UNDEROVER35 = "UNDER/OVER 3.5";
	private final static String UNDER35 = "Under 3.5";
	private final static String OVER35 = "Over 3.5";
	private final static String UNDER35CODE = "U_3.5";
	private final static String OVER35CODE = "O_3.5";

	private final static String UNDEROVER25 = "UNDER/OVER 2.5";
	private final static String UNDER25 = "Under 2.5";
	private final static String OVER25 = "Over 2.5";
	private final static String UNDER25CODE = "U_2.5";
	private final static String OVER25CODE = "O_2.5";

	private final static String UNDEROVER15 = "UNDER/OVER 1.5";
	private final static String UNDER15 = "Under 1.5";
	private final static String OVER15 = "Over 1.5";
	private final static String UNDER15CODE = "U_1.5";
	private final static String OVER15CODE = "O_1.5";

	private final static String UNDEROVER05 = "UNDER/OVER 0.5";
	private final static String UNDER05 = "Under 0.5";
	private final static String OVER05 = "Over 0.5";
	private final static String UNDER05CODE = "U_0.5";
	private final static String OVER05CODE = "O_0.5";
	
	private final static String GOALNOGOAL = "GOAL vs NO GOAL";
	private final static String GOAL = "GOAL";
	private final static String NOGOAL = "NO GOAL";
	private final static String GOALCODE = "GOAL";
	private final static String NOGOALCODE = "NOGOAL";
	
	TwoOptionsBetSumUp bet_UO05;
	TwoOptionsBetSumUp bet_UO15;
	TwoOptionsBetSumUp bet_UO25;
	TwoOptionsBetSumUp bet_UO35;
	TwoOptionsBetSumUp bet_UO45;
	TwoOptionsBetSumUp bet_UO55;
	TwoOptionsBetSumUp bet_UO65;
	TwoOptionsBetSumUp bet_GGNG;
	ThreeOptionsBetSumUp bet_1X2;
	
	public SoccerEvent(String participant1, String participant2, String date, String sport, String country,
			String championship, String extractionTime) {
		super(participant1, participant2, date, sport, country, championship, extractionTime);
		bet_UO05 = new TwoOptionsBetSumUp(UNDEROVER05, UNDER05, OVER05, UNDER05CODE, OVER05CODE);
		bet_UO15 = new TwoOptionsBetSumUp(UNDEROVER15, UNDER15, OVER15, UNDER15CODE, OVER15CODE);
		bet_UO25 = new TwoOptionsBetSumUp(UNDEROVER25, UNDER25, OVER25, UNDER25CODE, OVER25CODE);
		bet_UO35 = new TwoOptionsBetSumUp(UNDEROVER35, UNDER35, OVER35, UNDER35CODE, OVER35CODE);
		bet_UO45 = new TwoOptionsBetSumUp(UNDEROVER45, UNDER45, OVER45, UNDER45CODE, OVER45CODE);
		bet_UO55 = new TwoOptionsBetSumUp(UNDEROVER55, UNDER55, OVER55, UNDER55CODE, OVER55CODE);
		bet_UO65 = new TwoOptionsBetSumUp(UNDEROVER65, UNDER65, OVER65, UNDER65CODE, OVER65CODE);
		bet_GGNG = new TwoOptionsBetSumUp(GOALNOGOAL, GOAL, NOGOAL, GOALCODE, NOGOALCODE);
		bet_1X2 = new ThreeOptionsBetSumUp(UNOICSDUE, UNO, ICS, DUE, UNODUE, UNOICS, ICSDUE, UNO, ICS, DUE, UNODUE, UNOICS, ICSDUE);
	}

	public TwoOptionsBetSumUp getBet_UO05() {
		return bet_UO05;
	}

	public TwoOptionsBetSumUp getBet_UO15() {
		return bet_UO15;
	}

	public TwoOptionsBetSumUp getBet_UO25() {
		return bet_UO25;
	}

	public TwoOptionsBetSumUp getBet_UO35() {
		return bet_UO35;
	}

	public TwoOptionsBetSumUp getBet_UO45() {
		return bet_UO45;
	}

	public TwoOptionsBetSumUp getBet_UO55() {
		return bet_UO55;
	}

	public TwoOptionsBetSumUp getBet_UO65() {
		return bet_UO65;
	}

	public TwoOptionsBetSumUp getBet_GGNG() {
		return bet_GGNG;
	}

	public ThreeOptionsBetSumUp getBet_1X2() {
		return bet_1X2;
	}

	@Override
	public void addRecord(String bookmaker1, String oddsType1, String odd1, String money1, String bookmaker2,
			String oddsType2, String odd2, String money2, boolean betterOdd) {
		
		if (oddsType1.equals("GOAL") || oddsType1.equals("NOGOAL")) {
			bet_GGNG.addRecord(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2, betterOdd);			
		} else if (oddsType1.equals("U_0.5") || oddsType1.equals("O_0.5")) {
			bet_UO05.addRecord(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2, betterOdd);			
		} else if (oddsType1.equals("U_1.5") || oddsType1.equals("O_1.5")) {
			bet_UO15.addRecord(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2, betterOdd);			
		} else if (oddsType1.equals("U_2.5") || oddsType1.equals("O_2.5")) {
			bet_UO25.addRecord(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2, betterOdd);			
		} else if (oddsType1.equals("U_3.5") || oddsType1.equals("O_3.5")) {
			bet_UO35.addRecord(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2, betterOdd);			
		} else if (oddsType1.equals("U_4.5") || oddsType1.equals("O_4.5")) {
			bet_UO45.addRecord(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2, betterOdd);			
		} else if (oddsType1.equals("U_5.5") || oddsType1.equals("O_5.5")) {
			bet_UO55.addRecord(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2, betterOdd);			
		} else if (oddsType1.equals("U_6.5") || oddsType1.equals("O_6.5")) {
			bet_UO65.addRecord(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2, betterOdd);			
		} else if ((oddsType1.equals("1") || oddsType1.equals("2") || oddsType1.equals("X")
				 || oddsType1.equals("1X")  || oddsType1.equals("X2")  || oddsType1.equals("12"))) {
			bet_1X2.addRecord(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2, betterOdd);			
		}		
	}

	@Override
	public String drawHeaderBallImage() {
		final String footballPath = "./img/soccer-ball.png";

		String result = "";
		result += "<img class=\"soccer-ball\" src=\"" + footballPath + "\" alt=\"Soccer\" />";
		result += "<img class=\"soccer-ball\" src=\"" + footballPath + "\" alt=\"Soccer\" />";
		result += "<img class=\"soccer-ball\" src=\"" + footballPath + "\" alt=\"Soccer\" />";
		
		return result; 
	}

	@Override
	protected String insertTables() {
		String result = "";
		result += bet_UO05.drawTable();
		result += bet_UO15.drawTable();
		result += bet_UO25.drawTable();
		result += bet_UO35.drawTable();
		result += bet_UO45.drawTable();
		result += bet_UO55.drawTable();
		result += bet_UO65.drawTable();
		result += bet_GGNG.drawTable();
		result += bet_1X2.drawTable();
		return result;
	}
}