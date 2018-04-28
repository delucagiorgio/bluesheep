package it.bluesheep.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

public class TranslatorUtil {
	
	private static Map<String, String> codeTranslationMap;
	private static final String ENGLISH = "en";
	private static final String ITALIAN = "it";
	
	static {
			InputStream csvFileStream = TranslatorUtil.class.getResourceAsStream("/Country-Nazione_Code.csv");
	        BufferedReader br = null;
	        String line = "";
	        String cvsSplitBy = ";";
	        codeTranslationMap = new HashMap<String, String>();

	        try {

	            br = new BufferedReader(new InputStreamReader(csvFileStream));
	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	                String[] countryDefinition = line.split(cvsSplitBy);
	                codeTranslationMap.put(countryDefinition[1], countryDefinition[0]);
	            }

	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	}
	
	private TranslatorUtil() {}
	
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
			System.out.println("Error during translation. No change is applied to string " + toBeTranslatedString);
		}
		 
		return translatedString;
	}
	
	private static String parseResult(String string) {
		JSONArray jsonArray = new JSONArray(string);
		JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
		JSONArray jsonArray3 = (JSONArray) jsonArray2.get(0);
		return jsonArray3.get(0).toString();
	}

	public static String getNationTranslation(String toBeTranslatedNationCode) {
		return codeTranslationMap.get(toBeTranslatedNationCode);
	}

}
