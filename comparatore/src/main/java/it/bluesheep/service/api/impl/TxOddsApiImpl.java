package it.bluesheep.service.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import it.bluesheep.service.api.IApiInterface;

/**
 * In questa classe si stabilisce la connessione con il servizio txodds e si ottiene il file json contenente le
 * quote relative allo sport e al tipo di scommessa richiesti
 * 
 * @author random
 *
 */
public class TxOddsApiImpl implements IApiInterface {

	public String getData(String sport, String oddsType) {
		
		
		String u = "fabiodisante";
		// random 
		String p = "1282mdm38cjnbdcjcnddfdsa19932933ncd".substring(20, 28);
		String days = "0,7";

		String active = "1";
		String json = "1";
		String allOdds = "2";
		String odds_format = "0";
		
		String https_url = "https://xml2.txodds.com/feed/odds/xml.php?ident="+u+"&passwd="+p+"&active="+active+"&spid="+sport+"&ot="+oddsType+"&days="+days+"&json="+json+"&all_odds="+ allOdds + "&odds_format=" + odds_format;
		String result = null;
		
		URL url;
		try {
		
		   url = new URL(https_url);
		   HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		     
		   //dump all the content
		   result = get_result(con);
				
		} catch (MalformedURLException e) {
		   e.printStackTrace();
		} catch (IOException e) {
		   e.printStackTrace();
		}
		
		return result;	
	}
	
	private String get_result(HttpsURLConnection con){
		String result = "";
		if(con!=null){	
			try {
			   BufferedReader br = 
				new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			
			   String input;
			   while ((input = br.readLine()) != null){
				   result += input;
			   }
			   br.close();
			   
			  // Print in console just to test 
		      //System.out.println(result);		   
			} catch (IOException e) {
			   e.printStackTrace();
			}
				
		}
	
		return result;
	}	
}