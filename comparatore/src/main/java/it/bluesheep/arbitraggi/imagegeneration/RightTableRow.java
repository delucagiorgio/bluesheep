package it.bluesheep.arbitraggi.imagegeneration;

import java.text.DecimalFormat;

/*
 * Tabella di destra con bookmaker, quota e percentuale di guadagno netto
 */
public class RightTableRow extends TableRow {
	
	private String minPercentage;
	private String maxPercentage;

	public RightTableRow(String bookmaker, String odd, String percentage, String money, boolean betterOdd, boolean removedOdd) {
		super(bookmaker, odd, money, betterOdd, removedOdd);
		
		float decimalNumber = Float.parseFloat(percentage.replace(",", "."));
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);
			
		this.setMinPercentage(df.format(decimalNumber).toString());
		this.setMaxPercentage(df.format(decimalNumber).toString());
	}

	public String getMinPercentage() {
		return minPercentage.replace(",", ".");
	}

	public void setMinPercentage(String minPercentage) {
		this.minPercentage = minPercentage;
	}

	public String getMaxPercentage() {
		return maxPercentage.replace(",", ".");
	}

	public void setMaxPercentage(String maxPercentage) {
		this.maxPercentage = maxPercentage;
	}
	
	public void updatePercentages(String p) {
		float decimalNumber = Float.parseFloat(p.replace(",", "."));
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);
		
		if (Float.parseFloat(this.getMaxPercentage()) < decimalNumber) {
			maxPercentage = df.format(decimalNumber);
		} else if (Float.parseFloat(this.getMinPercentage()) > decimalNumber){
			minPercentage = df.format(decimalNumber);
		}		
	}
}