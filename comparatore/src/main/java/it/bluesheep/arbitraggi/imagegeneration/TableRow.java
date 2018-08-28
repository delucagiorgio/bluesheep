package it.bluesheep.arbitraggi.imagegeneration;

/*
 * Generica riga di una tabella generica. Genericamente parlando
 */
public abstract class TableRow implements Comparable<TableRow> {
	private String bookmaker;
	private String odd;
	boolean betterOdd;

	public TableRow(String bookmaker, String odd, boolean betterOdd) {
		this.bookmaker = bookmaker;
		this.odd = odd;
		this.betterOdd = betterOdd;
	}
	
	public String getBookmaker() {
		return bookmaker;
	}
	public void setBookmaker(String bookmaker) {
		this.bookmaker = bookmaker;
	}
	public String getOdd() {
		return odd;
	}
	public void setOdd(String odd) {
		this.odd = odd;
	}

	public boolean isBetterOdd() {
		return betterOdd;
	}

	public void setBetterOdd(boolean betterOdd) {
		this.betterOdd = betterOdd;
	}
	
	@Override
	public abstract int compareTo(TableRow arg0);	
}
