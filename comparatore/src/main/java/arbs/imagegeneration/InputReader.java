package arbs.imagegeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import arbs.util.ArbsConstants;
import arbs.util.urlshortener.TinyUrlShortener;
import it.bluesheep.entities.util.sport.Sport;

/**
 * Input reader, come suggerisce lo stesso nome, Ë in grado di leggere l'ingresso, sottoforma di lista di stringhe, e di restituire una lista di oggetti
 * Event più facilmente trattabili durante il resto dell'elaborazione. 
 * @author Fabio
 *
 */
public class InputReader {
	public List<Event> convert(List<String> inputRecords) {
	    List<Event> events = new ArrayList<Event>();
	    String extractionTime = null;
	    
	    if (inputRecords.size() > 0) {
		    extractionTime = inputRecords.get(0);	    	
	    }
	    
		for (int i = 1; i < inputRecords.size(); i++) {
    		String separator1 = "\\|";
    		
    		String[] splittedKeyPart = inputRecords.get(i).split(ArbsConstants.KEY_SEPARATOR);
    		
    		String[] parts = splittedKeyPart[0].replaceAll("&", "and").split(ArbsConstants.VALUE_SEPARATOR);
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
    		String bookmaker2 = parts[8];
    		String oddsType2 = parts[9];	
    		String odd2 = parts[10];
    		String money2 = null;
    		if (splittedKeyPart.length == 4) {
	    		money2 = splittedKeyPart[2];	    			
    		}
    		String linkBook1 = null;
    		String linkBook2 = null;
    		String[] linksSplitted = splittedKeyPart[splittedKeyPart.length - 1].split(ArbsConstants.VALUE_SEPARATOR);
			if(!"null".equals(linksSplitted[0])) {
				linkBook1 = TinyUrlShortener.getShortenedURLFromLongURL(linksSplitted[0]);
			}
			if(!"null".equals(linksSplitted[1])) {
				linkBook2 = TinyUrlShortener.getShortenedURLFromLongURL(linksSplitted[1]);
			}
    		
    		Event tempEvent = new Event(participant1, participant2, date, sport, country, championship, extractionTime);
    		
    		int index = -1;
    		for (int j = 0; j < events.size(); j++) {
    			Event iterationEvent = events.get(j);
    			List<String> bookmakerLinkList = iterationEvent.getLinkBook();
    			if (iterationEvent.isSameEvent(tempEvent)) {
    				if(!"null".equals(linkBook1) && !bookmakerLinkList.contains(bookmaker1 + ArbsConstants.KEY_SEPARATOR + linkBook1)) {
    					bookmakerLinkList.add(bookmaker1 + ArbsConstants.KEY_SEPARATOR + linkBook1);
    				}
    				if(!"null".equals(linkBook2) && !bookmakerLinkList.contains(bookmaker2 + ArbsConstants.KEY_SEPARATOR + linkBook2)) {
    					bookmakerLinkList.add(bookmaker2 + ArbsConstants.KEY_SEPARATOR + linkBook2);
    				}
    				index = j;
    			}
    		}
    		
    		if (index == -1) {
    			events.add(tempEvent);
				if(!"null".equals(linkBook1)) {
					tempEvent.getLinkBook().add(bookmaker1 + ArbsConstants.KEY_SEPARATOR + linkBook1);
				}
				if(!"null".equals(linkBook2)) {
					tempEvent.getLinkBook().add(bookmaker2 + ArbsConstants.KEY_SEPARATOR + linkBook2);
				}
    			index = events.size() - 1;
    		}

    		if (oddsType1.equals("U_2.5") || oddsType1.equals("O_2.5")) {
    			events.get(index).getBet_UO25().add(new Bet(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2));
    		} else if (oddsType1.equals("U_0.5") || oddsType1.equals("O_0.5")) {
    			events.get(index).getBet_UO05().add(new Bet(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2));
    		} else if (oddsType1.equals("U_1.5") || oddsType1.equals("O_1.5")) {
    			events.get(index).getBet_UO15().add(new Bet(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2));
    		} else if (oddsType1.equals("U_3.5") || oddsType1.equals("O_3.5")) {
    			events.get(index).getBet_UO35().add(new Bet(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2));
    		}else if (oddsType1.equals("U_4.5") || oddsType1.equals("O_4.5")) {
    			events.get(index).getBet_UO45().add(new Bet(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2));
    		}else if (oddsType1.equals("U_5.5") || oddsType1.equals("O_5.5")) {
    			events.get(index).getBet_UO55().add(new Bet(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2));
    		} else if (oddsType1.equals("U_6.5") || oddsType1.equals("O_6.5")) {
    			events.get(index).getBet_UO65().add(new Bet(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2));
    		} else if ((oddsType1.equals("1") || oddsType1.equals("2")) && !sport.equals(Sport.CALCIO.toString())) {
    			events.get(index).getBet_12().add(new Bet(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2));
    		} else if ((oddsType1.equals("1") || oddsType2.equals("2") || oddsType2.equals("X")) && sport.equals(Sport.CALCIO.toString())) {
    			events.get(index).getBet_1X2().add(new Bet(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2));
    		} else if (oddsType1.equals("GOAL") || oddsType1.equals("NOGOAL")) {
        		events.get(index).getBet_GGNG().add(new Bet(bookmaker1, oddsType1, odd1, null, bookmaker2, oddsType2, odd2, money2));
    		}	    	
		}
		
		for (int i = 0; i < events.size(); i++) {
			Collections.sort(events.get(i).getBet_12(), Collections.reverseOrder());
			Collections.sort(events.get(i).getBet_1X2(), Collections.reverseOrder());
			Collections.sort(events.get(i).getBet_UO65(), Collections.reverseOrder());
			Collections.sort(events.get(i).getBet_UO55(), Collections.reverseOrder());
			Collections.sort(events.get(i).getBet_UO45(), Collections.reverseOrder());
			Collections.sort(events.get(i).getBet_UO35(), Collections.reverseOrder());
			Collections.sort(events.get(i).getBet_UO25(), Collections.reverseOrder());
			Collections.sort(events.get(i).getBet_UO15(), Collections.reverseOrder());
			Collections.sort(events.get(i).getBet_UO05(), Collections.reverseOrder());
			Collections.sort(events.get(i).getBet_GGNG(), Collections.reverseOrder());
		}

		return events;
	}
}
