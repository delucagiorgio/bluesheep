package it.bluesheep.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.gtranslate.Language;
import com.gtranslate.Translator;

public class TranslatorUtil {
	
	private static Map<String, String> codeTranslationMap;
	
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
		Translator translate = Translator.getInstance(); 
		String translatedString = translate.translate(toBeTranslatedString, Language.ENGLISH, Language.ITALIAN);
		return translatedString;
	}
	
	public static String getNationTranslation(String toBeTranslatedNationCode) {
		return codeTranslationMap.get(toBeTranslatedNationCode);
	}

}
