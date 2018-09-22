package it.bluesheep.arbitraggi.entities;

import it.bluesheep.util.BlueSheepConstants;

public class ThreeOptionsArbsRecord extends ArbsRecord{
	
	private String bookmaker3;
	private double odd3;
	private String bet3;
	private String link3;
	private double liquidita3;
	private boolean betterOdd3;
	private boolean removedOdd3;
	
	public ThreeOptionsArbsRecord(String status,
			String bookmaker1, String bookmaker2, String bookmaker3, double odd1,
			double odd2, double odd3, String bet1, String bet2, String bet3, String date, String keyEvento,
			String championship, String sport, String link1,
			String link2, String link3, String country, double liquidita1, double liquidita2,
			double liquidita3, boolean betterOdd1, boolean betterOdd2, boolean betterOdd3, boolean removedOdd1, boolean removedOdd2,
			boolean removedOdd3, BetReference ref, BetReference average) {
		super(status, bookmaker1, bookmaker2, odd1,
				odd2, bet1, bet2, date, keyEvento,
				championship, sport, link1,
				link2, country, liquidita1, liquidita2, betterOdd1, betterOdd2, removedOdd1, removedOdd2, 
				ref, average);
		
		this.bookmaker3 = bookmaker3;
		this.odd3 = odd3;
		this.bet3 = bet3;
		this.link3 = link3;
		this.liquidita3 = liquidita3;
		this.betterOdd3 = betterOdd3;
		this.removedOdd3 = removedOdd3;
		this.type = ArbsType.THREE_WAY;
		calculateNetProfit();
	}
	
	@Override
	protected void calculateNetProfit() {
		netProfit = ((1 / ((1 / odd1) + (1 / odd2) + (1 / odd3))) - 1) * 100;
	}
	
	@Override
	public String getKeyEventoBookmakerBet() {
		return keyEvento + BlueSheepConstants.REGEX_CSV + 
				   date + BlueSheepConstants.REGEX_CSV + 
				   sport + BlueSheepConstants.REGEX_CSV +
				   country + BlueSheepConstants.REGEX_CSV + 
				   championship + BlueSheepConstants.REGEX_CSV + 
				   bookmaker1 + BlueSheepConstants.REGEX_CSV + 
				   bet1 + BlueSheepConstants.REGEX_CSV + 
				   bookmaker2 + BlueSheepConstants.REGEX_CSV +
				   bet2 + BlueSheepConstants.REGEX_CSV +
				   bookmaker3 + BlueSheepConstants.REGEX_CSV + 
				   bet3 + BlueSheepConstants.KEY_SEPARATOR + 
				   odd1 + BlueSheepConstants.REGEX_CSV +
				   odd2 + BlueSheepConstants.REGEX_CSV +
				   odd3;
	}

	@Override
	public String getBookmakerList() {
		return bookmaker1 + BlueSheepConstants.REGEX_CSV + bookmaker2;
	}

	public String getBookmaker3() {
		return bookmaker3;
	}

	public double getOdd3() {
		return odd3;
	}

	public String getBet3() {
		return bet3;
	}

	public String getLink3() {
		return link3;
	}

	public double getLiquidita3() {
		return liquidita3;
	}

	public boolean isBetterOdd3() {
		return betterOdd3;
	}

	public boolean isRemovedOdd3() {
		return removedOdd3;
	}

	public void setBookmaker3(String bookmaker3) {
		this.bookmaker3 = bookmaker3;
	}

	public void setOdd3(double odd3) {
		this.odd3 = odd3;
	}

	public void setBet3(String bet3) {
		this.bet3 = bet3;
	}

	public void setLink3(String link3) {
		this.link3 = link3;
	}

	public void setLiquidita3(double liquidita3) {
		this.liquidita3 = liquidita3;
	}

	public void setBetterOdd3(boolean betterOdd3) {
		this.betterOdd3 = betterOdd3;
	}

	public void setRemovedOdd3(boolean removedOdd3) {
		this.removedOdd3 = removedOdd3;
	}

	@Override
	public String getKeyEventoBet() {
		return keyEvento + BlueSheepConstants.REGEX_CSV + 
				   date + BlueSheepConstants.REGEX_CSV + 
				   sport + BlueSheepConstants.REGEX_CSV +
				   country + BlueSheepConstants.REGEX_CSV + 
				   championship + BlueSheepConstants.REGEX_CSV;
	}

	@Override
	public String getStoredDataFormat() {
		return getKeyEventoBookmakerBet() + BlueSheepConstants.KEY_SEPARATOR + getStatus()
				+ BlueSheepConstants.KEY_SEPARATOR + getLink1()
				+ BlueSheepConstants.REGEX_CSV + getLink2()
				+ BlueSheepConstants.REGEX_CSV + getLink3();
	}
}

