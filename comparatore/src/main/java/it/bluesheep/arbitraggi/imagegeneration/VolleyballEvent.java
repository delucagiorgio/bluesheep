package it.bluesheep.arbitraggi.imagegeneration;

/**
 * Evento di volley
 * @author Fabio
 *
 */
public class VolleyballEvent extends Event {

	private final static String UNODUE = "1 vs 2";
	private final static String UNO = "1";
	private final static String DUE = "2";

	TwoOptionsBetSumUp bet_12;
	
	public VolleyballEvent(String participant1, String participant2, String date, String sport, String country,
			String championship, String extractionTime) {
		super(participant1, participant2, date, sport, country, championship, extractionTime);
		bet_12 = new TwoOptionsBetSumUp(UNODUE, UNO, DUE, UNO, DUE);
	}
	
	public TwoOptionsBetSumUp getBet_12() {
		return bet_12;
	}

	@Override
	public void addRecord(String bookmaker1, String oddsType1, String odd1, String money1, String bookmaker2,
			String oddsType2, String odd2, String money2, boolean betterOdd) {
		
		// Il volley ha come unica opzione di gioco UNODUE
		bet_12.addRecord(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2, betterOdd);
	}

	@Override
	public String drawHeaderBallImage() {
		final String volleyBallPath = "./img/volleyball-ball.png";

		String result = "";
		result += "<img class=\"volley-ball\" src=\"" + volleyBallPath + "\" alt=\"Volley\" />";
		result += "<img class=\"volley-ball\" src=\"" + volleyBallPath + "\" alt=\"Volley\" />";
		result += "<img class=\"volley-ball\" src=\"" + volleyBallPath + "\" alt=\"Volley\" />";
		
		return result; 		
	}
	
	@Override
	protected String insertTables() {
		String result = "";
		result += bet_12.drawTable();
		return result;
	}

}