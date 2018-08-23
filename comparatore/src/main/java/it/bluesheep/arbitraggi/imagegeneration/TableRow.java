package it.bluesheep.arbitraggi.imagegeneration;

/*
 * Generica riga di una tabella generica. Genericamente parlando
 */
public abstract class TableRow implements Comparable<TableRow> {
	private String bookmaker;
	private String odd;
	
	public TableRow(String bookmaker, String odd) {
		this.bookmaker = bookmaker;
		this.odd = odd;
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

	@Override
	public abstract int compareTo(TableRow arg0);	
}
