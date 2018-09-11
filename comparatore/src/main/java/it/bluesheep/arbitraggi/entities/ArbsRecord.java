package it.bluesheep.arbitraggi.entities;

import java.util.Date;

public abstract class ArbsRecord{
	
	protected String bookmaker1;
	protected String bookmaker2;
	protected double odd1;
	protected double odd2;
	protected String bet1;
	protected String bet2;
	protected boolean betterOdd;
	protected Date date;
	protected String keyEvento;
	protected String championship;
	protected String sport;
	protected double netProfit;
	protected String link1;
	protected String link2;
	protected String country;
	protected double liquidita;
	
	protected abstract void calculateNetProfit();
	
	public abstract String getKeyEventoBookmakerBet();
	
	public abstract String getBookmakerList();

	public static boolean isSameEventBookmakerBet(ArbsRecord record1, ArbsRecord record2) {
		String uniqueKeyRecord1 = record1.getKeyEventoBookmakerBet();
		String uniqueKeyRecord2 = record2.getKeyEventoBookmakerBet();
		return uniqueKeyRecord1 != null && uniqueKeyRecord2 != null && uniqueKeyRecord2.equals(uniqueKeyRecord1);
	}
	
	public boolean isBetterOdd() {
		return betterOdd;
	}

	public void setBetterOdd(boolean betterOdd) {
		this.betterOdd = betterOdd;
	}

	public String getKeyEvento() {
		return keyEvento;
	}

	public Date getDate() {
		return date;
	}

	public String getChampionship() {
		return championship;
	}

	public String getSport() {
		return sport;
	}
	
	public String getBookmaker1() {
		return bookmaker1;
	}

	public String getBookmaker2() {
		return bookmaker2;
	}
	
	public double getOdd1() {
		return odd1;
	}

	public double getOdd2() {
		return odd2;
	}
	
	public String getBet1() {
		return bet1;
	}

	public String getBet2() {
		return bet2;
	}
	
	public double getNetProfit() {
		return netProfit;
	}

	public String getLink1() {
		return link1;
	}

	public String getLink2() {
		return link2;
	}

	public String getCountry() {
		return country;
	}

	public double getLiquidita() {
		return liquidita;
	}
}
