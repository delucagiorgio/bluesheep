package it.bluesheep.arbitraggi.imagegeneration;

import java.util.ArrayList;
import java.util.List;

import it.bluesheep.arbitraggi.entities.ArbsRecord;

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
	
	public List<Event> convert(List<ArbsRecord> inputRecords, String extractionTime) {
	    List<Event> events = new ArrayList<Event>();
	   	    
		for (int i = 0; i < inputRecords.size(); i++) {		    
			Event tempEvent = null;
    		if (inputRecords.get(i).getSport().equals(SOCCER)) {
        		tempEvent = new SoccerEvent(inputRecords.get(i), extractionTime);
    		} else if (inputRecords.get(i).getSport().equals(TENNIS)) {
        		tempEvent = new TennisEvent(inputRecords.get(i), extractionTime);    			
    		} else if (inputRecords.get(i).getSport().equals(BASKET)) {
        		tempEvent = new BasketballEvent(inputRecords.get(i), extractionTime);    			
    		} else if (inputRecords.get(i).getSport().equals(VOLLEY)) {
        		tempEvent = new VolleyballEvent(inputRecords.get(i), extractionTime);    			
    		}
    		if(tempEvent != null) {
    				
	    		int index = -1;
	    		for (int j = 0; j < events.size(); j++) {
	    			if (events.get(j).isSameEvent(tempEvent)) {
	    				index = j;
	    				break;
	    			}
	    		}
	    		
	    		if (index == -1) {
	    			events.add(tempEvent);
	    			index = events.size() -1;
	    		}		
	    		
	    		events.get(index).addRecord(inputRecords.get(i));		    	
			}
		}
		
		return events;
	}
}