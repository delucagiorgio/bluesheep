package it.bluesheep.arbitraggi.entities;

public abstract class BetReference {
	private String bookmakerOdd1;
	private String bookmakerOdd2;
	private String betType1;
	private String betType2;
	private String odd1;
	private String odd2;
	
	public BetReference(String bookmakerOdd1, String betType1, String odd1, String bookmakerOdd2, String betType2,  String odd2) {
		this.bookmakerOdd1 = bookmakerOdd1;
		this.bookmakerOdd2 = bookmakerOdd2;
		this.betType1 = betType1;
		this.betType2 = betType2;
		this.odd1 = odd1;
		this.odd2 = odd2;
	}
	public String getBookmakerOdd1() {
		return bookmakerOdd1;
	}
	public String getOdd1() {
		return odd1;
	}
	public String getOdd2() {
		return odd2;
	}
	public void setBookmakersName(String bookmakersName) {
		this.bookmakerOdd1 = bookmakersName;
	}
	public void setOdd1(String odd1) {
		this.odd1 = odd1;
	}
	public void setOdd2(String odd2) {
		this.odd2 = odd2;
	}
	public String getBetType1() {
		return betType1;
	}
	public void setBetType1(String betType1) {
		this.betType1 = betType1;
	}
	public String getBetType2() {
		return betType2;
	}
	public void setBetType2(String betType2) {
		this.betType2 = betType2;
	}
	public String getBookmakerOdd2() {
		return bookmakerOdd2;
	}
	public void setBookmakerOdd2(String bookmakerOdd2) {
		this.bookmakerOdd2 = bookmakerOdd2;
	}
}

