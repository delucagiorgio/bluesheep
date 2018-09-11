package it.bluesheep.arbitraggi.imagegeneration;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.arbitraggi.util.urlshortener.TinyUrlShortener;
import it.bluesheep.util.BlueSheepConstants;

/**
 * Input reader, come suggerisce lo stesso nome, è in grado di leggere l'ingresso, sottoforma di lista di stringhe, e di 
 * restituire una lista di oggetti Event più facilmente trattabili durante il resto dell'elaborazione. 
 * @author Fabio
 *
 */
public class InputReader {
	
	private final static String SOCCER = "CALCIO";
	private final static String TENNIS = "TENNIS";
	private final static String BASKET = "BASKET";	
	private final static String VOLLEY = "VOLLEY";	
	private final static String separator1 = "\\|";
	private static Logger logger = Logger.getLogger(InputReader.class);
	
	public List<Event> convert(List<ArbsRecord> inputRecords,  String extractionTime) {
	    List<Event> events = new ArrayList<Event>();
	    
		for (int i = 0; i < inputRecords.size(); i++) {
			boolean better_odd = false;
			ArbsRecord record = inputRecords.get(i);
    		String match = record.getKeyEvento();
    		String[] matchparts = match.split(separator1);
    		String participant1 = matchparts[0];
    		String participant2 = matchparts[1];
    		String date = record.getDate().toString();
    		String sport = record.getSport();	
    		String championship = record.getChampionship();	
    		String bookmaker1 = record.getBookmaker1();
    		String oddsType1 = record.getBet1();	
    		String odd1 = "" + record.getOdd1();
    		String country = record.getCountry();
    		
    		float decimalNumber = Float.parseFloat(odd1.replace(",", "."));
    		DecimalFormat df = new DecimalFormat();
    		df.setMinimumFractionDigits(2);
    		df.setMaximumFractionDigits(2);
    		odd1 = df.format(decimalNumber).toString();

    		String bookmaker2 = record.getBookmaker2();
    		String oddsType2 = record.getBet2();	
    		String odd2 = "" + record.getOdd2();
    		
    		decimalNumber = Float.parseFloat(odd2.replace(",", "."));
    		odd2 = df.format(decimalNumber).toString();

    		String money2 = null;
    		if(record.getLiquidita() > 0) {
	    		String temp = "" + record.getLiquidita();
		    	decimalNumber = Float.parseFloat(temp.replace(",", "."));
		    	money2 = df.format(decimalNumber).toString();
    		}
    		String linkBook1 = record.getLink1();
    		String linkBook2 = record.getLink2();
			try {
				if(!"null".equals(linkBook1)) {
					linkBook1 = TinyUrlShortener.getShortenedURLFromLongURL(linkBook1);
				}
				if(!"null".equals(linkBook2)) {
					linkBook2 = TinyUrlShortener.getShortenedURLFromLongURL(linkBook2);
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}

    		Event tempEvent = null;
    		if (sport.equals(SOCCER)) {
        		tempEvent = new SoccerEvent(participant1, participant2, date, sport, country, championship, extractionTime);
    		} else if (sport.equals(TENNIS)) {
        		tempEvent = new TennisEvent(participant1, participant2, date, sport, country, championship, extractionTime);    			
    		} else if (sport.equals(BASKET)) {
        		tempEvent = new BasketballEvent(participant1, participant2, date, sport, country, championship, extractionTime);    			
    		} else if (sport.equals(VOLLEY)) {
        		tempEvent = new VolleyballEvent(participant1, participant2, date, sport, country, championship, extractionTime);    			
    		}
    		
    		int index = -1;
    		for (int j = 0; j < events.size(); j++) {
    			Event iterationEvent = events.get(j);
    			List<String> bookmakerLinkList = iterationEvent.getLinkBook();
    			if (iterationEvent.isSameEvent(tempEvent)) {
    				if(!"null".equals(linkBook1) && !bookmakerLinkList.contains(bookmaker1 + BlueSheepConstants.KEY_SEPARATOR + linkBook1)) {
    					bookmakerLinkList.add(bookmaker1 + BlueSheepConstants.KEY_SEPARATOR + linkBook1);
    				}
    				if(!"null".equals(linkBook2) && !bookmakerLinkList.contains(bookmaker2 + BlueSheepConstants.KEY_SEPARATOR + linkBook2)) {
    					bookmakerLinkList.add(bookmaker2 + BlueSheepConstants.KEY_SEPARATOR + linkBook2);
    				}
    				index = j;
    			}
    		}
    		
    		if (index == -1) {
    			events.add(tempEvent);
				if(!"null".equals(linkBook1)) {
					tempEvent.getLinkBook().add(bookmaker1 + BlueSheepConstants.KEY_SEPARATOR + linkBook1);
				}
				if(!"null".equals(linkBook2)) {
					tempEvent.getLinkBook().add(bookmaker2 + BlueSheepConstants.KEY_SEPARATOR + linkBook2);
				}
    			index = events.size() - 1;
    		}

    		events.get(index).addRecord(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2, better_odd);		    	
	    }
		
		return events;

	}
}
