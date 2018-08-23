package it.bluesheep.arbitraggi.imagegeneration;

/*
 * Tabella di destra con bookmaker, quota e percentuale di guadagno netto
 */
public class RightTableRow extends TableRow {
	
	private String minPercentage;
	private String maxPercentage;

	public RightTableRow(String bookmaker, String odd, String percentage) {
		super(bookmaker, odd);
		this.setMinPercentage(percentage);
		this.setMaxPercentage(percentage);
	}

	@Override
	public int compareTo(TableRow arg0) {
		float income1 = Float.parseFloat(this.maxPercentage.replace(",", "."));
		float income2 = Float.parseFloat(((RightTableRow) arg0).getMaxPercentage().replace(",", "."));
		
		if (income1 > income2){
			return 1;
		} else if (income1 < income2) {
			return -1;
		} else {
			return 0;
		}
	}

	public String getMinPercentage() {
		return minPercentage;
	}

	public void setMinPercentage(String minPercentage) {
		this.minPercentage = minPercentage;
	}

	public String getMaxPercentage() {
		return maxPercentage;
	}

	public void setMaxPercentage(String maxPercentage) {
		this.maxPercentage = maxPercentage;
	}
	
	public void updatePercentages(String p) {
		if (Float.parseFloat(this.getMaxPercentage().replace(",", ".")) < Float.parseFloat(p.replace(",", "."))) {
			maxPercentage = p;
		} else if (Float.parseFloat(this.getMinPercentage().replace(",",  ".")) > Float.parseFloat(p.replace(",", "."))){
			minPercentage = p;
		}
	}
}