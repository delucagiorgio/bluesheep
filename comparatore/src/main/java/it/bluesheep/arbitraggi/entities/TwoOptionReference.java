package it.bluesheep.arbitraggi.entities;

public class TwoOptionReference extends BetReference {
	public TwoOptionReference(String bookmakerOdd1, String betType1, String odd1, String bookmakerOdd2, String betType2, String odd2) {
		super(bookmakerOdd1, betType1, odd1, bookmakerOdd2, betType2, odd2);
	}
}
