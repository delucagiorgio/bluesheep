package it.bluesheep.arbitraggi.imagegeneration;

import java.io.IOException;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.arbitraggi.util.urlshortener.TinyUrlShortener;
import it.bluesheep.util.BlueSheepConstants;

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
	
	public VolleyballEvent(ArbsRecord arbsRecord, String extractionTime) {
		super(arbsRecord, extractionTime);
		bet_12 = new TwoOptionsBetSumUp(UNODUE, UNO, DUE, UNO, DUE);
	}
	
	public TwoOptionsBetSumUp getBet_12() {
		return bet_12;
	}

	@Override
	public void addRecord(ArbsRecord arbsRecord) {
		
		// Il volley ha come unica opzione di gioco UNODUE
		bet_12.addRecord(arbsRecord, false);
		
		String linkBook1 = arbsRecord.getLink1();
		String linkBook2 = arbsRecord.getLink2();
		try {
			if(!"null".equals(linkBook1) && linkBook1 != null && !linkBook1.isEmpty()) {
				linkBook1 = TinyUrlShortener.getShortenedURLFromLongURL(linkBook1);
			}
			if(!"null".equals(linkBook2)  && linkBook2 != null && !linkBook2.isEmpty()) {
				linkBook2 = TinyUrlShortener.getShortenedURLFromLongURL(linkBook2);
			}
		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
		}
		if(linkBook1 != null && !"null".equals(linkBook1) && !linkBook1.isEmpty() &&  linkBook2 != null && !"null".equals(linkBook2) && !linkBook2.isEmpty()) {
			this.getLinkBook().add(arbsRecord.getBookmaker1() + BlueSheepConstants.KEY_SEPARATOR + linkBook1);
			this.getLinkBook().add(arbsRecord.getBookmaker2() + BlueSheepConstants.KEY_SEPARATOR + linkBook2);
		}
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