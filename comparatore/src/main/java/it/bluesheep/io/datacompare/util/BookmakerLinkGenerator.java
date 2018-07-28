package it.bluesheep.io.datacompare.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import it.bluesheep.entities.input.AbstractInputRecord;

public class BookmakerLinkGenerator {
	
	private BookmakerLinkGenerator() {}
	
	public static String getBookmakerLinkEvent(AbstractInputRecord record) throws UnsupportedEncodingException {
		String link = null;
		String sport = record.getSport().toString().toLowerCase();
		String partecipante1 = record.getPartecipante1().toLowerCase();
		
		if(partecipante1.split(" ").length > 1) {
			String temp = null;
			String[] splittedPartecipante = partecipante1.split(" ");
			int i = 0;
			for(String pieceOfPartecipante : splittedPartecipante) {
				if(i < 3 && //per evitare che il nome più lungo sia in fondo e che non venga considerata la parte principale
						(temp == null || temp.length() < pieceOfPartecipante.length())) {
					temp = pieceOfPartecipante;
				}
				i++;
			}
			partecipante1 = temp;
		}
		
		partecipante1 = partecipante1.replaceAll("\\p{Punct}", "");
		
		switch(record.getBookmakerName()) {
		case "Betfair Exchange":
			if("calcio".equals(sport)) {
				sport = "football";
			}
			link = "https://www.betfair.it/exchange/plus/" + sport + "/market/" + URLEncoder.encode(record.getFiller(), "UTF-8");
			break;
		case "Sisal Matchpoint":
			link = "https://www.sisal.it/scommesse-matchpoint/palinsesto?cerca=" + URLEncoder.encode(partecipante1, "UTF-8");
			break;
		case "888sport":
			if("calcio".equals(sport)) {
				sport = "football";
			}			
			link = "https://www.888sport.it/scommesse-sportive/#/filter/" + sport + "/all/all/";
			break;
		case "Betaland":
			link = "https://www.betaland.it/Sport/OddsSearchResults.aspx?q=" + URLEncoder.encode(partecipante1, "UTF-8") + "&antepost=1";
			break;
		case "Bwin.it":
			link = "https://sports.m.bwin.it/it/sports?popup=betfinder";
			break;
		case "EnjoyBet.it":
			link = "https://www.enjoybet.it/Sport/OddsSearchResults.aspx?q=" + URLEncoder.encode(partecipante1, "UTF-8") + "&antepost=1";
			break;
		case "Eurobet.it":
			link = "https://www.eurobet.it/it/scommesse/#!/cerca/" + URLEncoder.encode(partecipante1, "UTF-8");
			break;
		case "Gioco Digitale":
			link = "https://sports.m.giocodigitale.it/it/sports?popup=betfinder";
			break;
		case "LeoVegas.it":
			link = "https://www.leovegas.it/it-it/scommesse-sportive#filter/football,all,all," + URLEncoder.encode(record.getPartecipante1().toLowerCase().replaceAll(" ", "_"), "UTF-8");
			break;
		case "Lottomatica":
			link = "https://www.lottomatica.it/scommesse/avvenimenti";
			break;
		case "PlanetWin365":
			link = "https://www.planetwin365.it/Sport/OddsSearchResults.aspx?q=" + URLEncoder.encode(partecipante1, "UTF-8") + "&antepost=1";
			break;
		case "SNAI":
			link = "https://www.snai.it/sport/cerca/" + URLEncoder.encode(partecipante1, "UTF-8");
			break;
		case "Betflag":
			link = "https://www.betflag.it/sport";
			break;
		case "Domusbet.it":
			link = "https://www.domusbet.it/scommesse-sportive";
			break;
		case "Intralot":
			link = "https://www.intralot.it/scommesse";
			break;
		case "Merkur-win.it":
			link = "https://www.merkur-win.it/scommesse.html";
			break;	
		case "Pokerstars":
			if("calcio".equals(sport)) {
				sport = "soccer";
			}
			link = "https://www.betstars.it/#/" + sport + "/competitions";
			break;
		case "NetBet.it":
			link = "https://scommesse.netbet.it/" + sport + "/";
			break;
		case "Scommettendo":
			link = "https://scommettendo.it/sports/";
			break;
		case "SkyBet":
			link = "https://www.skybet.it/" + sport;
			break;
		case "SportPesa":
			link = "https://www.sportpesa.it/scommesse";
			break;
		case "SportYes.it":
			link = "https://www.sportyes.it/scommesse";
			break;
		case "Unibet.it":
			if("calcio".equalsIgnoreCase(sport)) {
				sport = "football";
			}
			link = "https://www.unibet.it/betting#filter/" + sport;
			break;
		case "Bet365":
			if("calcio".equalsIgnoreCase(sport)) {
				sport = "B1";
			}else if ("tennis".equalsIgnoreCase(sport)) {
				sport = "B13";
			}
			link = "https://www.bet365.it/#/AS/" + sport + "/";
			break;
		case "Williamhill.it":
			if("tennis".equalsIgnoreCase(sport)) {
				link = "http://sports.williamhill.it/bet_ita/it/betting/y/17/Tennis.html";
			}else if("calcio".equalsIgnoreCase(sport)) {
				link = "http://sports.williamhill.it/bet_ita/it/betting/y/5/Calcio.html";
			}
			break;
		case "Betfair SB":
			if("calcio".equalsIgnoreCase(sport)) {
				sport = "football";
			}
			link = "https://www.betfair.it/sport/" + sport;
			break;
		case "Marathonbet":
			if("calcio".equalsIgnoreCase(sport)) {
				sport = "Football";
			}else if("tennis".equalsIgnoreCase(sport)) {
				sport = "Tennis";
			}
			link = "https://www.marathonbet.it/it/popular/"+sport;
			break;
		case "BetClic.it":
			link = record.getFiller();
			break;
		case "Betway":
			if("calcio".equalsIgnoreCase(sport)) {
				sport = "soccer";
			}
			link = "https://sports.betway.it/it/sports/cat/" + sport;
			break;
		default:
			break;
		}
		return link;
	}

}
