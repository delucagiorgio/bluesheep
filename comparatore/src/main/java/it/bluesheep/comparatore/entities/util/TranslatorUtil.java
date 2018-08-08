package it.bluesheep.comparatore.entities.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;

import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepLogger;

public class TranslatorUtil {
	
	private static Map<String, String> codeTranslationMap;
	private static Map<String, String> apiTranslationMap;
	private static Logger logger;
	private static final String ENGLISH = "en";
	private static final String ITALIAN = "it";
	
	public static void initializeMapFromFile(){
		InputStream csvFileStream = null;
		try {
			csvFileStream = new FileInputStream(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.NATION_PATH_INPUTFILE));
		} catch (FileNotFoundException e1) {
			logger.log(Level.SEVERE, "Error during initialization of codeTranslationMap : error is " + e1.getMessage());
		}
        BufferedReader br = null;
        String line = "";
        codeTranslationMap = new HashMap<String, String>();
        apiTranslationMap = new HashMap<String, String>();
        
        if(csvFileStream != null) {
	        try {

	            br = new BufferedReader(new InputStreamReader(csvFileStream, BlueSheepConstants.ENCODING_UTF_8));
	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	                String[] countryDefinition = line.split(BlueSheepConstants.REGEX_CSV);
	                codeTranslationMap.put(countryDefinition[1], countryDefinition[0]);
	            }

	        } catch (Exception e) {
	        	logger.log(Level.SEVERE, "Error during translation initialization map. Error is " + e.getMessage());
	        } finally {
	            if (br != null) {
	                try {
	                	csvFileStream.close();
	                    br.close();
	                } catch (IOException e) {
	                	logger.log(Level.SEVERE, "Error during translation initialization map. Error is " + e.getMessage());
	                }
	            }
	        }
        }
	}
	
	private TranslatorUtil() {
		logger = (new BlueSheepLogger(TranslatorUtil.class)).getLogger();
	}
	
	/**
	 * GD - 30/04/2018
	 * Traduce dall'inglese all'italiano una parola passata come parametro
	 * @param toBeTranslatedString parola inglese da tradurre
	 * @return parola tradotta in italiano, la parola inglese se non riesce ad effettuare la traduzione
	 */
	private static String getItalianTranslation(String toBeTranslatedString) {
		String translatedString = null;
			try {
				String url = "https://translate.googleapis.com/translate_a/single?"+
					    "client=gtx&"+
					    "sl=" + ENGLISH + 
					    "&tl=" + ITALIAN + 
					    "&dt=t&q=" + URLEncoder.encode(toBeTranslatedString, BlueSheepConstants.ENCODING_UTF_8);    
					  
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection(); 
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
				 
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				 
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
					  
				translatedString = parseResult(response.toString());
				apiTranslationMap.put(toBeTranslatedString, translatedString);
			}catch(Exception e) {
				logger.log(Level.SEVERE, "Error during translation initialization map. No translation is applied. Error is " + e.getMessage());
	            translatedString = toBeTranslatedString;
		}
		 
		return translatedString;
	}
	
	private static String parseResult(String string) {
		JSONArray jsonArray = new JSONArray(string);
		JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
		JSONArray jsonArray3 = (JSONArray) jsonArray2.get(0);
		return jsonArray3.get(0).toString();
	}

	/**
	 * GD - 30/04/2018
	 * Assegna in base al codice trovato nella definizione della lega il nome della nazione
	 * @param toBeTranslatedNationCode il codice della lega
	 * @return la nazione relativa al codice passato come parametro, null se non trovato
	 */
	private static String getNationTranslation(String toBeTranslatedNationCode) {
		return codeTranslationMap.get(toBeTranslatedNationCode);
	}
	
	/**
	 * GD - 30/04/2018
	 * Metodo che si occupa di fornire le traduzioni relative alle nazioni:
	 * in caso di partite di campionato nazionale, viene popolato il campo "Nazione"
	 * con il relativo nome italiano del paese; nel caso di competizione internazionale di calcio,
	 * vengono tradotti i nomi dei partecipanti (nazionali) con il loro nome italiano
	 * @param recordOutput il record su cui effettuare le traduzioni
	 * @return il record con i campi tradotti
	 */
	public static RecordOutput translateFieldAboutCountry(RecordOutput recordOutput) {
		String campionato = recordOutput.getCampionato();
		if(campionato.startsWith("FB") || campionato.startsWith("WFB")) {
			String[] splittedCampionato = campionato.split(" ");
			if(splittedCampionato != null) {
				int startIndex = 2;
				if(campionato.startsWith("WFB")) {
					startIndex = 3;
				}
				String countryCodeFootball = splittedCampionato[0].substring(startIndex, splittedCampionato[0].length());
				String nation = TranslatorUtil.getNationTranslation(countryCodeFootball);
				if(nation == null) {
					nation = "";
				}
				recordOutput.setNazione(nation);

				if("INT".equalsIgnoreCase(countryCodeFootball)) {
					String[] eventoSplitted = recordOutput.getEvento().split("\\|");
					String partecipante1 = getTraduzioneItaliana(eventoSplitted[0]);
					String partecipante2 = getTraduzioneItaliana(eventoSplitted[1]);
					recordOutput.setEvento(partecipante1 + BlueSheepConstants.REGEX_PIPE + partecipante2); 
				}
			}
		}
		return recordOutput;
	}

	private static String getTraduzioneItaliana(String toBeTranslatedString) {
		String translatedString = apiTranslationMap.get(toBeTranslatedString);
		if(translatedString == null) {
			if("Turkey".equalsIgnoreCase(toBeTranslatedString)) {
				translatedString = "Turchia";
				apiTranslationMap.put(toBeTranslatedString, translatedString);
				return translatedString;
			}
			if("Iran".equalsIgnoreCase(toBeTranslatedString)) {
				apiTranslationMap.put(toBeTranslatedString, toBeTranslatedString);
				return toBeTranslatedString;
			}
			
			translatedString = TranslatorUtil.getItalianTranslation(toBeTranslatedString);
		}
		return translatedString;
	}


}
