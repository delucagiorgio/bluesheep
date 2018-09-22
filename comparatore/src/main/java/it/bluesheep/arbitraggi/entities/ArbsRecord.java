package it.bluesheep.arbitraggi.entities;

import it.bluesheep.util.BlueSheepConstants;

public abstract class ArbsRecord{
	
	protected String bookmaker1;
	protected String bookmaker2;
	protected double odd1;
	protected double odd2;
	protected String bet1;
	protected String bet2;
	protected String date;
	protected String keyEvento;
	protected String championship;
	protected String sport;
	protected double netProfit;
	protected String link1;
	protected String link2;
	protected String country;
	protected double liquidita1;
	protected double liquidita2;
	protected String status;
	protected boolean betterOdd1;
	protected boolean betterOdd2;
	protected boolean removedOdd1;
	protected boolean removedOdd2;
	protected BetReference ref;
	protected BetReference average;
	protected String participant1;
	protected String participant2;
	protected ArbsType type;
	
	protected abstract void calculateNetProfit();
	
	public abstract String getKeyEventoBookmakerBet();
	
	public abstract String getKeyEventoBet();
	
	public abstract String getBookmakerList();

	public static boolean isSameEventBookmakerBet(ArbsRecord record1, ArbsRecord record2) {
		
		if(record1.type.equals(record2.type)) {
			String uniqueKeyRecord1 = record1.getKeyEventoBet();
			String uniqueKeyRecord2 = record2.getKeyEventoBet();
			if(ArbsType.THREE_WAY.equals(record1.type) && record1.bookmaker1.equalsIgnoreCase(record2.bookmaker1)) {
				return false;
			}
			return uniqueKeyRecord1 != null && uniqueKeyRecord2 != null && uniqueKeyRecord2.equals(uniqueKeyRecord1);
		}
		
		return false;
	}
	
	public ArbsRecord(String status, String bookmaker1, String bookmaker2, double odd1,
			double odd2, String bet1, String bet2, String date, String keyEvento,
			String championship, String sport, String link1,
			String link2, String country, double liquidita1, double liquidita2,
			boolean betterOdd1, boolean betterOdd2, boolean removedOdd1, boolean removedOdd2, 
			BetReference ref, BetReference average) {
		this.bookmaker1 = bookmaker1;
		this.bookmaker2 = bookmaker2;
		this.odd1 = odd1;
		this.odd2 = odd2;
		this.bet1 = bet1;
		this.bet2 = bet2;
		this.date = date;
		this.keyEvento = keyEvento;
		this.championship = championship;
		this.sport = sport;
		this.link1 = link1;
		this.link2 = link2;
		this.country = country;
		this.liquidita1 = liquidita1;
		this.liquidita2 = liquidita2;
		this.participant1 = keyEvento.split(BlueSheepConstants.REGEX_VERSUS)[0];
		this.participant2 = keyEvento.split(BlueSheepConstants.REGEX_VERSUS)[1];
		this.betterOdd1 = betterOdd1;
		this.betterOdd2 = betterOdd2;
		this.removedOdd1 = removedOdd1;
		this.removedOdd2 = removedOdd2;
		this.ref = ref;
		this.average = average;
		this.status = status;
		calculateNetProfit();
	}
	
	public boolean isSameEventBookmakerBet(String recordKey) {
		String uniqueKeyRecord2 = getKeyEventoBookmakerBet().split(BlueSheepConstants.KEY_SEPARATOR)[0];
		return recordKey != null && uniqueKeyRecord2 != null && uniqueKeyRecord2.equals(recordKey);
	}

	public String getKeyEvento() {
		return keyEvento;
	}

	public String getDate() {
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

	public double getLiquidita1() {
		return liquidita1;
	}
	
	public double getLiquidita2() {
		return liquidita2;
	}

	public void changeStatus() {
		switch(status) {
		case BlueSheepConstants.STATUSINVALID_ARBS_RECORD:
			status = BlueSheepConstants.STATUS1_ARBS_RECORD;
			break;
		case BlueSheepConstants.STATUS0_ARBS_RECORD:
		case BlueSheepConstants.STATUS1_ARBS_RECORD:
			status = BlueSheepConstants.STATUS1_ARBS_RECORD;
			break;
		default :
			status = BlueSheepConstants.STATUSINVALID_ARBS_RECORD;
		}
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public boolean isBetterOdd1() {
		return betterOdd1;
	}

	public boolean isBetterOdd2() {
		return betterOdd2;
	}

	public boolean isRemovedOdd1() {
		return removedOdd1;
	}

	public boolean isRemovedOdd2() {
		return removedOdd2;
	}
	
	public BetReference getRef() {
		return ref;
	}

	public BetReference getAverage() {
		return average;
	}

	public void setRef(BetReference ref) {
		this.ref = ref;
	}

	public void setAverage(BetReference average) {
		this.average = average;
	}
	
	public String getParticipant1() {
		return participant1;
	}
	
	public String getParticipant2() {
		return participant2;
	}

	public abstract String getStoredDataFormat();

	public boolean isSameEventBookmakerBet(ArbsRecord record) {
		String uniqueKeyRecord1 = record.getKeyEventoBookmakerBet().split(BlueSheepConstants.KEY_SEPARATOR)[0];
		String uniqueKeyRecord2 = getKeyEventoBookmakerBet().split(BlueSheepConstants.KEY_SEPARATOR)[0];

		return uniqueKeyRecord1.equalsIgnoreCase(uniqueKeyRecord2);
	}
	
	public ArbsType getArbType() {
		return type;
	}
}
