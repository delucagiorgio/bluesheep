package it.bluesheep.io.datacompare.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.ComparatoreConstants;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.util.BlueSheepLogger;

/**
 * 30/07/18
 * Classe per la generazione dei link relativi ai bookmaker.
 * @author GD
 *
 */
public class BookmakerLinkGenerator {
	
	private static Logger logger = (new BlueSheepLogger(BookmakerLinkGenerator.class)).getLogger();
	private static Map<String, Map<Sport, Map<String, String>>> linkBookmakerSportMap;
	
	private BookmakerLinkGenerator() {}
	
	static {
		if(linkBookmakerSportMap == null) {
			initializeMap();
		}
	}
	
	/**
	 * GD - 30/07/18	
	 * Ritorna il link dell'evento relativo al record passato come input
	 * @param record il record su cui calcolare il link
	 * @return il link all'evento
	 */
	public static String getBookmakerLinkEvent(AbstractInputRecord record){

		String sourceOfURL = "URL";
		if("betclic.it".equalsIgnoreCase(record.getBookmakerName())) {
			sourceOfURL = "Tx-Odds";
		}else if ("betfair exchange".equalsIgnoreCase(record.getBookmakerName())) {
			sourceOfURL = "BetfairAPI";
		}
		
		String urlToBeReturned = getURLFromMapBySourceAndRecord(record, sourceOfURL);
		
		return urlToBeReturned;
	}

	/**
	 * GD - 30/07/18
	 * Ritorna il link dalla mappa dei bookmaker-sport-link
	 * @param record il record dove sono presenti le informazioni con cui visitare la mappa
	 * @param sourceOfURL la sorgente del link
	 * @return il link dell'evento
	 */
	private static String getURLFromMapBySourceAndRecord(AbstractInputRecord record, String sourceOfURL) {
		String returnString = null;
		if("Tx-Odds".equals(sourceOfURL)) {
			returnString = record.getFiller();
		}else {
			Map<Sport, Map<String, String>> sportBookmakerMap = linkBookmakerSportMap.get(sourceOfURL);
			if(sportBookmakerMap != null) {
				Map<String, String> bookmakerURLMap = sportBookmakerMap.get(record.getSport());
				if(bookmakerURLMap != null) {
					String specificOrGeneralURL = bookmakerURLMap.get(record.getBookmakerName());
					if(specificOrGeneralURL != null) {
						String[] splittedSpecificOrGeneralURL = specificOrGeneralURL.split("BSKEY");
						returnString = splittedSpecificOrGeneralURL[1];
						if("si".equalsIgnoreCase(splittedSpecificOrGeneralURL[0])) {
							String specificField = record.getFiller();
							if(!sourceOfURL.equals("BetfairAPI")) {
								specificField = getPartecipanteStringForSearch(record);
							}	
							returnString += specificField;
						}
					}else {
						logger.log(Level.WARNING, "Bookmaker " + record.getBookmakerName() + " is not present in linkMap");
					}
				}
			}
		}

		return returnString;
	}

	/**
	 * GD - 30/07/18
	 * Ritorna il la stringa da inserire nella ricerca per la ricerca puntuale
	 * @param record il record da cui prendere l'informazione base
	 * @return parte dell'informazione utile alla ricerca puntuale
	 */
	private static String getPartecipanteStringForSearch(AbstractInputRecord record) {
		
		String partecipanteStringForSearch = record.getPartecipante1().toLowerCase();
		
		//Considero solo una parte del nome partecipante, in modo da rendere più generica la ricerca sul nome
		if(partecipanteStringForSearch.split(ComparatoreConstants.REGEX_SPACE).length > 1) {
			String temp = null;
			String[] splittedPartecipante = partecipanteStringForSearch.split(ComparatoreConstants.REGEX_SPACE);
			int i = 0;
			for(String pieceOfPartecipante : splittedPartecipante) {
				if(i < 3 && //per evitare che il nome più lungo sia in fondo e che non venga considerata la parte principale
						(temp == null || temp.length() < pieceOfPartecipante.length())) {
					temp = pieceOfPartecipante;
				}
				i++;
			}
			partecipanteStringForSearch = temp;
		}
		
		//Rimuove tutta la punteggiatura
		partecipanteStringForSearch = partecipanteStringForSearch.replaceAll("\\p{Punct}", "");
		
		return partecipanteStringForSearch;
	}

	/**
	 * GD - 30/07/18
	 * Inizializza la mappa con i valori in input da file di testo
	 */
	private synchronized static void initializeMap() {
		
		logger.log(Level.INFO, "Starting initialization of URL map for bookmakers");
		
		InputStream csvFileStream = null;
		try {
			csvFileStream = new FileInputStream(BlueSheepComparatoreMain.getProperties().getProperty("LINK_BOOKMAKER_FILENAME"));
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

        BufferedReader br = null;
        String line = null;
        String cvsSplitBy = ";";
        /**
         * Il primo livello indica la sorgente (servizio o URL statico)
         * Il secondo livello indica lo sport a cui il link fa riferimento
         * Il terzo livello indica se il link è di tipo puntuale o generale
         * Il quarto e ultimo livello contiene il mapping tra bookmaker e URL
         */
        linkBookmakerSportMap = new HashMap<String, Map<Sport, Map<String, String>>>();
        
        if(csvFileStream != null) {
	        try {
	            br = new BufferedReader(new InputStreamReader(csvFileStream));
	            while ((line = br.readLine()) != null) {
	                // use comma as separator
	                String[] splittedLinkInformation = line.split(cvsSplitBy);
	                Map<Sport, Map<String, String>> availabilityType = linkBookmakerSportMap.get(splittedLinkInformation[3]);
	                if(availabilityType == null) {
	                	availabilityType = new HashMap<Sport, Map<String, String>>();
	                	linkBookmakerSportMap.put(splittedLinkInformation[3], availabilityType);
	                }
	                
	                String sportType = splittedLinkInformation[1];
	                List<Sport> sportTypeList = new ArrayList<Sport>();
	                
	                if("all".equalsIgnoreCase(sportType)) {
	                	sportTypeList.add(Sport.CALCIO);
	                	sportTypeList.add(Sport.TENNIS);
	                }else if("calcio".equalsIgnoreCase(sportType)) {
	                	sportTypeList.add(Sport.CALCIO);
	                }else if("tennis".equalsIgnoreCase(sportType)) {
	                	sportTypeList.add(Sport.TENNIS);
	                }
	                
	                for(Sport sport : sportTypeList) {
		                Map<String, String> bookmakerURLMap = availabilityType.get(sport);
		                if(bookmakerURLMap == null) {
		                	bookmakerURLMap = new HashMap<String, String>();
		                	availabilityType.put(sport, bookmakerURLMap);
			            }
		                if(splittedLinkInformation.length == 5) {
		                	String generalOrSpecificURLKey = splittedLinkInformation[2] + "BSKEY" + splittedLinkInformation[4];
		                	bookmakerURLMap.put(splittedLinkInformation[0], generalOrSpecificURLKey);
		                }
	                
	                }
	            }
	        } catch (Exception e) {
	            logger.log(Level.SEVERE, "Error during initialization map. Error is in line : " + line, e);
	        } finally {
	            if (br != null) {
	                try {
	                	csvFileStream.close();
	                    br.close();
	                } catch (IOException e) {
	    	            logger.log(Level.SEVERE, "Error during initialization map. Error is " + e.getMessage(), e);
	                }
	            }
	        }
        }
	}
}
