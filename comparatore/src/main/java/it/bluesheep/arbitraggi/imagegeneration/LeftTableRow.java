package it.bluesheep.arbitraggi.imagegeneration;

/*
 * Tabella di sinistra, con bookmaker, quota e disponibilit� di giocata
 */
public class LeftTableRow extends TableRow {
	
	private String money;
	
	public LeftTableRow(String bookmaker, String odd, String money) {
		super(bookmaker, odd);
		this.money = money;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	@Override
	public int compareTo(TableRow arg0) {

		float income1 = Float.parseFloat(this.getOdd().replace(",", "."));
		float income2 = Float.parseFloat(arg0.getOdd().replace(",", "."));
		
		if (income1 > income2){
			return 1;
		} else if (income1 < income2) {
			return -1;
		} else {
			return 0;
		}
	}
	
}
