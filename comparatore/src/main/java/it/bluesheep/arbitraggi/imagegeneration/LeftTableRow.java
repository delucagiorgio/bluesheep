package it.bluesheep.arbitraggi.imagegeneration;

/*
 * Tabella di sinistra, con bookmaker, quota e disponibilit‡ di giocata
 */
public class LeftTableRow extends TableRow {
		
	public LeftTableRow(String bookmaker, String odd, String money, boolean betterOdd, boolean removedOdd) {
		super(bookmaker, odd, money, betterOdd, removedOdd);
	}
}