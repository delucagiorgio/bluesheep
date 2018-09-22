package it.bluesheep.arbitraggi.entities;

public class ThreeOptionReference extends BetReference {
	
	private String bookmakerOdd3;
	private String odd3;
	private String betType3;
	
	public ThreeOptionReference(String bookmakerOdd1, String betType1, String odd1, String bookmakerOdd2, String betType2,  String odd2, String bookmakerOdd3, String betType3,  String odd3) {
		super(bookmakerOdd1, betType1, odd1, bookmakerOdd2, betType2, odd2);
		this.bookmakerOdd3 = bookmakerOdd3;
		this.odd3 = odd3;
		this.betType3 = betType3;
	}

	public String getOdd3() {
		return odd3;
	}

	public void setOdd3(String odd3) {
		this.odd3 = odd3;
	}

	public String getBetType3() {
		return betType3;
	}

	public void setBetType3(String betType3) {
		this.betType3 = betType3;
	}

	public String getBookmakerOdd3() {
		return bookmakerOdd3;
	}

	public void setBookmakerOdd3(String bookmakerOdd3) {
		this.bookmakerOdd3 = bookmakerOdd3;
	}
	
}
