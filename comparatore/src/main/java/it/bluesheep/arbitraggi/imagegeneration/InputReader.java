package it.bluesheep.arbitraggi.imagegeneration;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import it.bluesheep.arbitraggi.util.urlshortener.TinyUrlShortener;
import it.bluesheep.util.BlueSheepConstants;

/**
 * Input reader, come suggerisce lo stesso nome, � in grado di leggere l'ingresso, sottoforma di lista di stringhe, e di 
 * restituire una lista di oggetti Event pi� facilmente trattabili durante il resto dell'elaborazione. 
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
	
	public List<Event> convert(List<String> inputRecords) {
	    List<Event> events = new ArrayList<Event>();
	    String extractionTime = null;
	    
	    if (inputRecords.size() > 0) {
		    extractionTime = inputRecords.get(0);	    	
	    }
	    
		for (int i = 1; i < inputRecords.size(); i++) {
			boolean better_odd = false;
		    String[] splittedKeyPart = null;		    

		    if (inputRecords.get(i).startsWith("BETTER_ODD")) {
		    	better_odd = true;
		    	splittedKeyPart = inputRecords.get(i).substring(10).split(BlueSheepConstants.KEY_SEPARATOR);
		    } else {
		    	splittedKeyPart = inputRecords.get(i).split(BlueSheepConstants.KEY_SEPARATOR);;		    	
		    }
		    
		    if (splittedKeyPart != null) {
	    		String[] parts = splittedKeyPart[0].replaceAll("&", "and").split(BlueSheepConstants.REGEX_CSV);
	    		String match = parts[0];    		
	    		String[] matchparts = match.split(separator1);
	    		String participant1 = matchparts[0];
	    		String participant2 = matchparts[1];
	    		String date = parts[1];
	    		String sport = parts[2];	
	    		String country = parts[3];
	    		String championship = parts[4];	
	    		String bookmaker1 = parts[5];
	    		String oddsType1 = parts[6];	
	    		String odd1 = parts[7];
	    		
	    		float decimalNumber = Float.parseFloat(odd1.replace(",", "."));
	    		DecimalFormat df = new DecimalFormat();
	    		df.setMinimumFractionDigits(2);
	    		df.setMaximumFractionDigits(2);
	    		odd1 = df.format(decimalNumber).toString();

	    		String bookmaker2 = parts[8];
	    		String oddsType2 = parts[9];	
	    		String odd2 = parts[10];
	    		
	    		decimalNumber = Float.parseFloat(odd2.replace(",", "."));
	    		odd2 = df.format(decimalNumber).toString();

	    		String money2 = null;
	    		if (splittedKeyPart.length == 4) {
		    		money2 = splittedKeyPart[2];
		    		
		    		decimalNumber = Float.parseFloat(money2.replace(",", "."));
		    		money2 = df.format(decimalNumber).toString();
	    		}
	    		String linkBook1 = "";
	    		String linkBook2 = "";
	    		String[] linksSplitted = splittedKeyPart[splittedKeyPart.length - 1].split(BlueSheepConstants.REGEX_CSV);
				try {
					if(!"null".equals(linksSplitted[0])) {
						linkBook1 = TinyUrlShortener.getShortenedURLFromLongURL(linksSplitted[0]);
					}
					if(!"null".equals(linksSplitted[1])) {
						linkBook2 = TinyUrlShortener.getShortenedURLFromLongURL(linksSplitted[1]);
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
		}
		
		return events;

	}
}
