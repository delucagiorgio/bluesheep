package it.bluesheep.arbitraggi.imagegeneration;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Generica classe riepilogativa di una scommessa
 * @author Fabio
 *
 */
public abstract class BetSumUp {
	String betType;
	
	public BetSumUp(String betType) {
		this.betType = betType;
	}

	public String getBetType() {
		return betType;
	}

	public void setBetType(String betType) {
		this.betType = betType;
	}
	
	public abstract void addRecord(String bookmaker1, String oddsType1, String odd1, String money1, String bookmaker2, String oddsType2, String odd2, String money2);
	
	protected String calcutateIncome(String odd1, String odd2, String betType1, String betType2) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.DOWN);
		
		if (betType1.equals(betType2)) {
			float odd1sNumber = Float.parseFloat(odd1.replace(",", "."));
			float odd2sNumber = Float.parseFloat(odd2.replace(",", "."));
			
			float realOdd2 = 1 / (1 - (1/odd2sNumber));
			float r1 = 1000 * odd1sNumber;
			float x = r1/realOdd2;
			float p = (((r1)/(1000 + x)) - 1) * 100;			
			//System.out.println(p);
			return df.format(p);
			
		} else {
			float odd1sNumber = Float.parseFloat(odd1.replace(",", "."));
			float odd2sNumber = Float.parseFloat(odd2.replace(",", "."));
			
			float r1 = 1000 * odd1sNumber;
			float x = r1/odd2sNumber;
			float p = (((r1)/(1000 + x)) - 1) * 100;			
			//System.out.println(p);
			return df.format(p);
		}
	}
}