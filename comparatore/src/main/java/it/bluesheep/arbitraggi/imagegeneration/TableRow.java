package it.bluesheep.arbitraggi.imagegeneration;

/*
 * Generica riga di una tabella generica. Genericamente parlando
 */
public abstract class TableRow implements Comparable<TableRow> {
	private String bookmaker;
	private String odd;
	private String money;
	private boolean betterOdd;
	private boolean removedOdd;

	public TableRow(String bookmaker, String odd, String money, boolean betterOdd, boolean removedOdd) {
		this.bookmaker = bookmaker;
		this.odd = odd;
		this.setMoney(money);
		this.setBetterOdd(betterOdd);
		this.setRemovedOdd(removedOdd);
	}
	
	@Override
	public int compareTo(TableRow arg0) {

		float odd1 = Float.parseFloat(this.getOdd().replace(",", "."));
		float odd2 = Float.parseFloat(arg0.getOdd().replace(",", "."));
		
		float q1 = this.getBookmaker().startsWith("Betfair Exchange") ? (odd1 - 1) * 0.95F + 1F : odd1;
		float q2 = arg0.getBookmaker().startsWith("Betfair Exchange") ? (odd2 - 1) * 0.95F + 1F : odd2;
		
		if (q1 > q2){
			return 1;
		} else if (q1 < q2) {
			return -1;
		} else {
			return 0;
		}
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

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}	
	
	public boolean isBetterOdd() {
		return betterOdd;
	}

	public void setBetterOdd(boolean betterOdd) {
		this.betterOdd = betterOdd;
	}

	public boolean isRemovedOdd() {
		return removedOdd;
	}

	public void setRemovedOdd(boolean removedOdd) {
		this.removedOdd = removedOdd;
	}	
}
