package it.bluesheep.entities.input.util;

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
import java.util.logging.Logger;

import org.json.JSONArray;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.util.BlueSheepLogger;

public class TranslatorUtil {
	
	private static Map<String, String> codeTranslationMap;
	private static Logger logger;
	private static final String ENGLISH = "en";
	private static final String ITALIAN = "it";
	
	static {
		InputStream csvFileStream = null;
		try {
			csvFileStream = new FileInputStream(BlueSheepComparatoreMain.getProperties().getProperty("PATH_NAZIONI_TRADUZIONE_CSV"));
		} catch (FileNotFoundException e1) {
			logger.severe("Error during initialization of codeTranslationMap : error is\n" + e1.getStackTrace());
		}
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        codeTranslationMap = new HashMap<String, String>();
        
        if(csvFileStream != null) {
	        try {

	            br = new BufferedReader(new InputStreamReader(csvFileStream));
	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	                String[] countryDefinition = line.split(cvsSplitBy);
	                codeTranslationMap.put(countryDefinition[1], countryDefinition[0]);
	            }

	        } catch (Exception e) {
	            logger.severe("Error during translation initialization map. Error is\n" + e.getStackTrace());
	        } finally {
	            if (br != null) {
	                try {
	                	csvFileStream.close();
	                    br.close();
	                } catch (IOException e) {
	    	            logger.severe("Error during translation initialization map. Error is\n" + e.getStackTrace());
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
	public static String getItalianTranslation(String toBeTranslatedString) {
		String translatedString = null;
		  
		try {
			String url = "https://translate.googleapis.com/translate_a/single?"+
				    "client=gtx&"+
				    "sl=" + ENGLISH + 
				    "&tl=" + ITALIAN + 
				    "&dt=t&q=" + URLEncoder.encode(toBeTranslatedString, "UTF-8");    
				  
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
		}catch(Exception e) {
            logger.severe("Error during translation initialization map. No translation is applied. Error is\n" + e.getStackTrace());
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
	public static String getNationTranslation(String toBeTranslatedNationCode) {
		return codeTranslationMap.get(toBeTranslatedNationCode);
	}

}
