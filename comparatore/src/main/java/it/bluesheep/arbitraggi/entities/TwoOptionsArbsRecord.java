package it.bluesheep.arbitraggi.entities;

import java.text.DecimalFormat;
import java.util.Date;

import it.bluesheep.util.BlueSheepConstants;

public class TwoOptionsArbsRecord extends ArbsRecord {
	
	private double rating1;
	private double rating2;
	
	public TwoOptionsArbsRecord(String keyEvento, Date date, String sport, String championship, String country, String bookmaker1, String bookmaker2, double odd1, double odd2, String bet1, String bet2, double rating1, double rating2, String link1, String link2, double liquidita) {
		this.keyEvento = keyEvento;
		this.date = date;
		this.sport = sport;
		this.championship = championship;
		this.country = country;
		this.bet1 = bet1;
		this.bet2 = bet2;
		this.bookmaker1 = bookmaker1;
		this.bookmaker2 = bookmaker2;
		this.odd1 = odd1;
		this.odd2 = odd2;
		this.rating1 = rating1;
		this.rating2 = rating2;
		this.link1 = link1;
		this.link2 = link2;
		betterOdd = false;
		this.liquidita = liquidita;
		calculateNetProfit();
	}
	
	@Override
	protected void calculateNetProfit() {
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);		
		
		if (bet1.equals(bet2)) {
			double realOdd2 = 1 / (1 - (1/odd2));
			double r1 = 1000 * odd1;
			double x = r1/realOdd2;
			netProfit = (((r1)/(1000 + x)) - 1) * 100;			
		} else {
			double r1 = 1000 * odd1;
			double x = r1/odd2;
			netProfit = (((r1)/(1000 + x)) - 1) * 100;			
		}
	}
	
	public double getRating1() {
		return rating1;
	}

	public double getRating2() {
		return rating2;
	}

	@Override
	public String getKeyEventoBookmakerBet() {
		return keyEvento + BlueSheepConstants.REGEX_CSV + 
			   championship + BlueSheepConstants.REGEX_CSV + 
			   date + BlueSheepConstants.REGEX_CSV + 
			   bookmaker1 + BlueSheepConstants.REGEX_CSV + 
			   bookmaker2 + BlueSheepConstants.REGEX_CSV + 
			   odd1 + BlueSheepConstants.REGEX_CSV +
			   odd2 + BlueSheepConstants.REGEX_CSV;
	}

	@Override
	public String getBookmakerList() {
		return bookmaker1 + BlueSheepConstants.REGEX_CSV + bookmaker2;
	}
}
