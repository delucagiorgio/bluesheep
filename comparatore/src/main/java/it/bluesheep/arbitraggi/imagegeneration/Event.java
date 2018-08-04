package it.bluesheep.arbitraggi.imagegeneration;

import java.util.ArrayList;
import java.util.List;

import it.bluesheep.util.BlueSheepConstants;

/**
 * Event contiene le informazioni di un evento generico
 * @author Fabio
 *
 */
public class Event {

	private String extractionTime;
	private String participant1;
	private String participant2;
	private String date;
	private String sport;
	private String country;
	private String championship;
	private List<String> linkBook;

	private List<Bet> bet_UO05;
	private List<Bet> bet_UO15;
	private List<Bet> bet_UO25;
	private List<Bet> bet_UO35;
	private List<Bet> bet_UO45;
	private List<Bet> bet_UO55;
	private List<Bet> bet_UO65;
	private List<Bet> bet_GGNG;
	private List<Bet> bet_12;
	private List<Bet> bet_1X2;

	
	public Event(String participant1, String participant2, String date, String sport, String country,
			String championship, String extractionTime)  {
		this.participant1 = participant1;
		this.participant2 = participant2;
		this.date = date;
		this.sport = sport;
		this.country = country;
		this.championship = championship;
		this.setExtractionTime(extractionTime);
		this.linkBook = new ArrayList<String>();
		

		bet_12 = new ArrayList<Bet>();
		bet_1X2 = new ArrayList<Bet>();
		bet_UO05 = new ArrayList<Bet>();
		bet_UO15 = new ArrayList<Bet>();
		bet_UO25 = new ArrayList<Bet>();
		bet_UO35 = new ArrayList<Bet>();
		bet_UO45 = new ArrayList<Bet>();
		bet_UO55 = new ArrayList<Bet>();
		bet_UO65 = new ArrayList<Bet>();
		bet_GGNG = new ArrayList<Bet>();

	}
	
	@Override
	public String toString() {
		String result = "Event:\nPARTICIPANT 1 = " + this.participant1 + "\n" + 
								 "PARTICIPANT 2 = " + this.participant2 + "\n" + 
								 "DATE = " + this.date + "\n" + 
								 "SPORT = " + this.sport + "\n" + 
								 "COUNTRY = " + this.country + "\n" + 
								 "CHAMPIONSHIP = " + this.championship + "\n\n";
		result += "Bets:\n\n12\n";
		for (int i = 0; i< bet_12.size();i++) {
			result += "BOOKMAKER1: " + bet_12.get(i).getBookmaker1() + "\n";
			result += "ODDSTYPE1: " + bet_12.get(i).getBetType1() + "\n";
			result += "ODD1: " + bet_12.get(i).getOdd1() + "\n";
			result += "BOOKMAKER2: " + bet_12.get(i).getBookmaker2() + "\n";
			result += "ODDSTYPE2: " + bet_12.get(i).getBetType2() + "\n";
			result += "ODD2: " + bet_12.get(i).getOdd2() + "\n";
			result += "MONEY2: " + bet_12.get(i).getMoney2() + "\n";
			result += "INCOMING_PERCENTAGE: " + bet_12.get(i).getIncomePercentage() + "\n\n";

		}
		
		result += "\n";

		result += "1X2\n";
		for (int i = 0; i< bet_1X2.size();i++) {
			result += "BOOKMAKER1: " + bet_1X2.get(i).getBookmaker1() + "\n";
			result += "ODDSTYPE1: " + bet_1X2.get(i).getBetType1() + "\n";
			result += "ODD1: " + bet_1X2.get(i).getOdd1() + "\n";
			result += "BOOKMAKER2: " + bet_1X2.get(i).getBookmaker2() + "\n";
			result += "ODDSTYPE2: " + bet_1X2.get(i).getBetType2() + "\n";
			result += "ODD2: " + bet_1X2.get(i).getOdd2() + "\n";
			result += "MONEY2: " + bet_1X2.get(i).getMoney2() + "\n";
			result += "INCOMING_PERCENTAGE: " + bet_1X2.get(i).getIncomePercentage() + "\n\n";

		}
		
		result += "\n";

		
		result += "GG/NG\n";
		for (int i = 0; i< bet_GGNG.size();i++) {
			result += "BOOKMAKER1: " + bet_GGNG.get(i).getBookmaker1() + "\n";
			result += "ODDSTYPE1: " + bet_GGNG.get(i).getBetType1() + "\n";
			result += "ODD1: " + bet_GGNG.get(i).getOdd1() + "\n";
			result += "BOOKMAKER2: " + bet_GGNG.get(i).getBookmaker2() + "\n";
			result += "ODDSTYPE2: " + bet_GGNG.get(i).getBetType2() + "\n";
			result += "ODD2: " + bet_GGNG.get(i).getOdd2() + "\n";
			result += "MONEY2: " + bet_GGNG.get(i).getMoney2() + "\n";
			result += "INCOMING_PERCENTAGE: " + bet_GGNG.get(i).getIncomePercentage() + "\n\n";

		}
		
		result += "\n";

		result += "UNDER/OVER 0.5\n";
		for (int i = 0; i< bet_UO05.size();i++) {
			result += "BOOKMAKER1: " + bet_UO05.get(i).getBookmaker1() + "\n";
			result += "ODDSTYPE1: " + bet_UO05.get(i).getBetType1() + "\n";
			result += "ODD1: " + bet_UO05.get(i).getOdd1() + "\n";
			result += "BOOKMAKER2: " + bet_UO05.get(i).getBookmaker2() + "\n";
			result += "ODDSTYPE2: " + bet_UO05.get(i).getBetType2() + "\n";
			result += "ODD2: " + bet_UO05.get(i).getOdd2() + "\n";
			result += "MONEY2: " + bet_UO05.get(i).getMoney2() + "\n";
			result += "INCOMING_PERCENTAGE: " + bet_UO05.get(i).getIncomePercentage() + "\n\n";

		}
		
		result += "\n";

		result += "UNDER/OVER 1.5\n";
		for (int i = 0; i< bet_UO15.size();i++) {
			result += "BOOKMAKER1: " + bet_UO15.get(i).getBookmaker1() + "\n";
			result += "ODDSTYPE1: " + bet_UO15.get(i).getBetType1() + "\n";
			result += "ODD1: " + bet_UO15.get(i).getOdd1() + "\n";
			result += "BOOKMAKER2: " + bet_UO15.get(i).getBookmaker2() + "\n";
			result += "ODDSTYPE2: " + bet_UO15.get(i).getBetType2() + "\n";
			result += "ODD2: " + bet_UO15.get(i).getOdd2() + "\n";
			result += "MONEY2: " + bet_UO15.get(i).getMoney2() + "\n";
			result += "INCOMING_PERCENTAGE: " + bet_UO15.get(i).getIncomePercentage() + "\n\n";

		}
		
		result += "\n";

		result += "UNDER/OVER 2.5\n";
		for (int i = 0; i< bet_UO25.size();i++) {
			result += "BOOKMAKER1: " + bet_UO25.get(i).getBookmaker1() + "\n";
			result += "ODDSTYPE1: " + bet_UO25.get(i).getBetType1() + "\n";
			result += "ODD1: " + bet_UO25.get(i).getOdd1() + "\n";
			result += "BOOKMAKER2: " + bet_UO25.get(i).getBookmaker2() + "\n";
			result += "ODDSTYPE2: " + bet_UO25.get(i).getBetType2() + "\n";
			result += "ODD2: " + bet_UO25.get(i).getOdd2() + "\n";
			result += "MONEY2: " + bet_UO25.get(i).getMoney2() + "\n";
			result += "INCOMING_PERCENTAGE: " + bet_UO25.get(i).getIncomePercentage() + "\n\n";

		}
		
		result += "\n";

		result += "UNDER/OVER 3.5\n";
		for (int i = 0; i< bet_UO35.size();i++) {
			result += "BOOKMAKER1: " + bet_UO35.get(i).getBookmaker1() + "\n";
			result += "ODDSTYPE1: " + bet_UO35.get(i).getBetType1() + "\n";
			result += "ODD1: " + bet_UO35.get(i).getOdd1() + "\n";
			result += "BOOKMAKER2: " + bet_UO35.get(i).getBookmaker2() + "\n";
			result += "ODDSTYPE2: " + bet_UO35.get(i).getBetType2() + "\n";
			result += "ODD2: " + bet_UO35.get(i).getOdd2() + "\n";
			result += "MONEY2: " + bet_UO35.get(i).getMoney2() + "\n";
			result += "INCOMING_PERCENTAGE: " + bet_UO35.get(i).getIncomePercentage() + "\n\n";

		}
		
		result += "\n";

		result += "UNDER/OVER 4.5\n";
		for (int i = 0; i< bet_UO45.size();i++) {
			result += "BOOKMAKER1: " + bet_UO45.get(i).getBookmaker1() + "\n";
			result += "ODDSTYPE1: " + bet_UO45.get(i).getBetType1() + "\n";
			result += "ODD1: " + bet_UO45.get(i).getOdd1() + "\n";
			result += "BOOKMAKER2: " + bet_UO45.get(i).getBookmaker2() + "\n";
			result += "ODDSTYPE2: " + bet_UO45.get(i).getBetType2() + "\n";
			result += "ODD2: " + bet_UO45.get(i).getOdd2() + "\n";
			result += "MONEY2: " + bet_UO45.get(i).getMoney2() + "\n";
			result += "INCOMING_PERCENTAGE: " + bet_UO45.get(i).getIncomePercentage() + "\n\n";

		}
		
		result += "\n";
		
		result += "UNDER/OVER 5.5\n";
		for (int i = 0; i< bet_UO55.size();i++) {
			result += "BOOKMAKER1: " + bet_UO55.get(i).getBookmaker1() + "\n";
			result += "ODDSTYPE1: " + bet_UO55.get(i).getBetType1() + "\n";
			result += "ODD1: " + bet_UO55.get(i).getOdd1() + "\n";
			result += "BOOKMAKER2: " + bet_UO55.get(i).getBookmaker2() + "\n";
			result += "ODDSTYPE2: " + bet_UO55.get(i).getBetType2() + "\n";
			result += "ODD2: " + bet_UO55.get(i).getOdd2() + "\n";
			result += "MONEY2: " + bet_UO55.get(i).getMoney2() + "\n";
			result += "INCOMING_PERCENTAGE: " + bet_UO55.get(i).getIncomePercentage() + "\n\n";

		}
		
		result += "\n";
		
		result += "UNDER/OVER 6.5\n";
		for (int i = 0; i< bet_UO65.size();i++) {
			result += "BOOKMAKER1: " + bet_UO65.get(i).getBookmaker1() + "\n";
			result += "ODDSTYPE1: " + bet_UO65.get(i).getBetType1() + "\n";
			result += "ODD1: " + bet_UO65.get(i).getOdd1() + "\n";
			result += "BOOKMAKER2: " + bet_UO65.get(i).getBookmaker2() + "\n";
			result += "ODDSTYPE2: " + bet_UO65.get(i).getBetType2() + "\n";
			result += "ODD2: " + bet_UO65.get(i).getOdd2() + "\n";
			result += "MONEY2: " + bet_UO65.get(i).getMoney2() + "\n";
			result += "INCOMING_PERCENTAGE: " + bet_UO65.get(i).getIncomePercentage() + "\n\n";

		}
		
		result += "\n";

		
		return result;
		
	}
	
	public boolean isSameEvent(Event e) {
		
		return this.participant1.equals(e.participant1) &&
				this.participant2.equals(e.participant2) &&
				this.date.equals(e.date) &&
				this.sport.equals(e.sport) &&
				this.championship.equals(e.championship);
	}

	public String getParticipant1() {
		return participant1;
	}

	public void setParticipant1(String participant1) {
		this.participant1 = participant1;
	}

	public String getParticipant2() {
		return participant2;
	}

	public void setParticipant2(String participant2) {
		this.participant2 = participant2;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSport() {
		return sport;
	}

	public void setSport(String sport) {
		this.sport = sport;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getChampionship() {
		return championship;
	}

	public void setChampionship(String championship) {
		this.championship = championship;
	}

	public List<Bet> getBet_12() {
		return bet_12;
	}

	public void setBet_12(List<Bet> bet_12) {
		this.bet_12 = bet_12;
	}

	public List<Bet> getBet_UO05() {
		return bet_UO05;
	}

	public void setBet_UO05(List<Bet> bet_UO05) {
		this.bet_UO05 = bet_UO05;
	}

	public List<Bet> getBet_UO15() {
		return bet_UO15;
	}

	public void setBet_UO15(List<Bet> bet_UO15) {
		this.bet_UO15 = bet_UO15;
	}

	public List<Bet> getBet_UO25() {
		return bet_UO25;
	}

	public void setBet_UO25(List<Bet> bet_UO25) {
		this.bet_UO25 = bet_UO25;
	}

	public List<Bet> getBet_UO35() {
		return bet_UO35;
	}

	public void setBet_UO35(List<Bet> bet_UO35) {
		this.bet_UO35 = bet_UO35;
	}

	public List<Bet> getBet_UO45() {
		return bet_UO45;
	}

	public void setBet_UO45(List<Bet> bet_UO45) {
		this.bet_UO45 = bet_UO45;
	}

	public List<Bet> getBet_UO55() {
		return bet_UO55;
	}

	public void setBet_UO55(List<Bet> bet_UO55) {
		this.bet_UO55 = bet_UO55;
	}

	public List<Bet> getBet_UO65() {
		return bet_UO65;
	}

	public void setBet_UO65(List<Bet> bet_UO65) {
		this.bet_UO65 = bet_UO65;
	}

	public List<Bet> getBet_GGNG() {
		return bet_GGNG;
	}

	public void setBet_GGNG(List<Bet> bet_GGNG) {
		this.bet_GGNG = bet_GGNG;
	}

	public String getExtractionTime() {
		return extractionTime;
	}

	public void setExtractionTime(String extractionTime) {
		this.extractionTime = extractionTime;
	}

	public List<Bet> getBet_1X2() {
		return bet_1X2;
	}

	public void setBet_1X2(List<Bet> bet_1x2) {
		bet_1X2 = bet_1x2;
	}

	public String getUnifiedKeyAndLinks() {
		return participant1 + BlueSheepConstants.REGEX_CSV + 
				participant2 + BlueSheepConstants.REGEX_CSV + 
				date;
				
	}

	public List<String> getLinkBook() {
		return linkBook;
	}
}

