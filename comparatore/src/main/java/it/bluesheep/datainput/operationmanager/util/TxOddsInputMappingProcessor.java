package it.bluesheep.datainput.operationmanager.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.TxOddsInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.util.TxOddsBluesheepJsonConverter;

/**
 * La classe si occupa di mappare i dati in input sotto forma di JSON in oggetti della stessa morfologia (AbstractInputRecord)
 * @author Giorgio De Luca
 */
public class TxOddsInputMappingProcessor extends AbstractInputMappingProcessor{

	public static List<AbstractInputRecord> mapInputRecordIntoAbstractInputRecord(String jsonString, Scommessa scommessaTipo) {
		
		TxOddsBluesheepJsonConverter jsonConverter = TxOddsBluesheepJsonConverter.getTxOddsBluesheepJsonConverter();
		
		JSONObject jsonRootObject = new JSONObject(jsonString);
		
		/**
		 * 1. per ogni "match"
		 * 		1.1	definisco quali sono le variabili che identificano il match e le salvo in una variabile comune
		 * 		1.2 per ogni "offer" 
		 * 			1.2.1	per ogni "bookmaker"
		 * 				1.2.1.1		definisco le variabili relative al singolo bookmaker
		 * 				1.2.1.2		scorro le quote relative a quel bookmaker
		 * 					1.2.1.2.1	la tipologia di quota sarà unica--> salvo la tipologia di quota e mi salvo i valori relativi a quel
		 * 								bookmaker
		 */
		
		//tutti i match
		JSONArray matchesArrays = jsonConverter.getAllMatchesFromJSONObjectRoot(jsonRootObject);
		
		List<AbstractInputRecord> recordsToBeReturned = new ArrayList<AbstractInputRecord>();
		
		for(int i = 0; i < matchesArrays.length(); i++) {
			try {
				//System.out.println("Processing record id = " + i);
				JSONObject matchJsonObject = matchesArrays.getJSONObject(i);
				
				//le informazioni del match vengono mappate nell'entità
				TxOddsInputRecord recordToBeMapped = mapInfoMatchIntoAbstractInputRecord(matchJsonObject);
				
				//Ora il match è definito e devo prendere le relative quote per la scommessa in analisi (esempio UNDER 2,5)
				List<AbstractInputRecord> mappedRecordsWithOdds = mapInfoBookmakersIntoAbstractInputRecordList(recordToBeMapped, matchJsonObject, scommessaTipo);
				
				recordsToBeReturned.addAll(mappedRecordsWithOdds);
			}catch(Exception e) {
				System.out.println("Error : cause is " + e.getMessage());
			}
		}
		
		return recordsToBeReturned;
	}

	/**
	 * GD - 17/04/18
	 * Metodo che per ogni evento va a creare un record relativo ad un bookmaker e alla sua quota per la data scommessa
	 * @param recordToBeMapped il record generico del match
	 * @param matchJsonObject l'oggetto JSON del match
	 * @param scommessaTipo la tipologia di scommessa
	 * @return i record omologati relativi al match passato come parametro sulla determinata scommessa
	 */
	private static List<AbstractInputRecord> mapInfoBookmakersIntoAbstractInputRecordList(TxOddsInputRecord recordToBeMapped, JSONObject matchJsonObject, Scommessa scommessaTipo) {
		
		TxOddsBluesheepJsonConverter jsonConverter = TxOddsBluesheepJsonConverter.getTxOddsBluesheepJsonConverter();
		
		//Insieme di tutti i bookmaker che offrono quote sul match e scommessa in analisi 
		JSONArray bookmakersArrayJSONObject = jsonConverter.getChildNodeArrayByKey(matchJsonObject, "bookmaker");
		
		List<AbstractInputRecord> recordToBeReturned = new ArrayList<AbstractInputRecord>();
		
		for(int i = 0; i < bookmakersArrayJSONObject.length(); i++) {
			//per ogni bookmaker, accedo all'offer e prendo le quote relative alla mia scommessa
			JSONObject bookmakerJSONObject = bookmakersArrayJSONObject.getJSONObject(i);
			JSONObject offerJSONObject = jsonConverter.getChildNodeByKey(bookmakerJSONObject, "offer");
			
			//in base al tipo di offerta, devo prendere il primo, il secondo o il terzo campo
			String campoQuotaScommessa = getCorrectFieldByScommessa(scommessaTipo);
			if(campoQuotaScommessa != null) {
				
				//l'oggetto quota sarà sempre unico per bookmaker (le quote offerte per una determinata scommessa sono uniche e le ultime più aggiornate)
				//prendo l'oggetto JSON delle quote e prendo il dato richiesto
				JSONObject oddsJSONObject = jsonConverter.getChildNodeByKey(offerJSONObject, "odds");
				
				//JSONObject oddsJSONObject = oddsJSONArray.getJSONObject(0);
				
				double quotaScommessa = oddsJSONObject.getDouble(campoQuotaScommessa);
			
				//mappo le informazioni rimanenti nel nuovo oggetto
				TxOddsInputRecord newRecord = new TxOddsInputRecord(recordToBeMapped);	
				
				newRecord.setBookmakerName(jsonConverter.getAttributesNodeFromJSONObject(bookmakerJSONObject).getString("name"));
				newRecord.setTipoScommessa(scommessaTipo);
				newRecord.setQuota(quotaScommessa);
				
				System.out.println("Bookmaker = " + newRecord.getBookmakerName() + " | " +
								   "Campionato = " + newRecord.getCampionato() + " | " +
								   "Partecipante1 = " + newRecord.getPartecipante1() + " | " +
								   "Partecipante2 = " +  newRecord.getPartecipante2() +" | " +
								   "Quota = " +  newRecord.getQuota() + " | " +
								   "DataOraEvento = " +  newRecord.getDataOraEvento() + " | " +
								   "TipoScommessa = " + newRecord.getTipoScommessa().toString());
				
				recordToBeReturned.add(newRecord);
			}
		}
		
		return recordToBeReturned;
	}

	/**
	 * GD - 17/04/18
	 * Ritorna il nome del campo relativo all'oggetto JSON "odds" sul quale occorre leggere il dato per la scommessa
	 * passata come parametro
	 * @param scommessaTipo la scommessa
	 * @return il nome del campo su cui leggere la quota
	 */
	private static String getCorrectFieldByScommessa(Scommessa scommessaTipo) {
		
		String fieldByScommessa = null;
		
		switch(scommessaTipo) {
		case SFIDANTE1VINCENTE_1:
		case ALMENO1GOAL_O0X5:
		case ALMENO2GOAL_O1X5:
		case ALMENO3GOAL_O2X5:
		case ALMENO4G0AL_O3X5:
		case ALMENO5GOAL_O4X5:
		case ENTRAMBISEGNANO_GOAL:
			fieldByScommessa = "o1";
			break;
		case PAREGGIO_X:
		case NESSUNGOAL_U0X5:
		case ALPIU1GOAL_U1X5: 
		case ALPIU2GOAL_U2X5:
		case ALPIU3GOAL_U3X5:
		case ALPIU4GOAL_U4X5:
			fieldByScommessa = "o2";
			break;
		case SFIDANTE2VINCENTE_2:
		case NESSUNOSEGNA_NOGOAL:
			fieldByScommessa = "o3";
			break;
		default:
			break;
			
		}
		return fieldByScommessa;
	}

	/**
	 * GD - 17/04/18
	 * Mappa le informazioni principali di un match nel record corretto
	 * @param matchJsonObject l'oggetto JSON del match
	 * @return il record di input generico
	 * @throws ParseException
	 */
	private static TxOddsInputRecord mapInfoMatchIntoAbstractInputRecord(JSONObject matchJsonObject) throws ParseException {		
		//Mapping della data e dell'ora dell'evento
		//"time": "2018-04-17T16:30:00+00:00"
		String timeDateMatch = matchJsonObject.getString("time");
		DateFormat df = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss");
		Date date = df.parse(timeDateMatch);
		
	    //Mapping del campionato e della lega
	    //"group": "FBDUT Eredivisie > Regular Season-17"
	    String group = matchJsonObject.getString("group");
		
	    //Mapping partecipante 1
	    //"hteam": "Twente"
	    String homeTeam = matchJsonObject.getString("hteam");
	    	
	    //Mapping partecipante 2
	    //"ateam": "Zwolle"
	    String awayTeam = matchJsonObject.getString("ateam");
	    
	    TxOddsInputRecord record = new TxOddsInputRecord(date, group, homeTeam, awayTeam);
	    
		return record;
	}
	
}
