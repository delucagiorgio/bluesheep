package arbs.imagegeneration;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Bet represents a general bet and provides a compare method to get the best betting choice
 * @author Fabio
 *
 */
public class Bet implements Comparable<Bet>{
	private String bookmaker1;
	private String betType1;
	private String odd1;
	private String money1;
	
	private String bookmaker2;
	private String betType2;
	private String odd2;
	private String money2;
	
	private String incomePercentage;
	
	public Bet(String bookmaker1, String betType1, String odd1, String money1, String bookmaker2, String betType2,
			String odd2, String money2) {
		super();
		this.bookmaker1 = bookmaker1;
		this.betType1 = betType1;
		this.odd1 = odd1;
		this.money1 = money1;
		this.bookmaker2 = bookmaker2;
		this.betType2 = betType2;
		this.odd2 = odd2;
		this.money2 = money2;
		
		if (money1 == null) {
			this.money1 = "-";
		}
		if (money2 == null) {
			this.money2 = "-";
		}
		
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.DOWN);
		
		Float betAmount1 = new Float(1000.00);
		Float betAmount2 = null;
		Float odd1sNumber = Float.parseFloat(odd1.replace(",", "."));
		Float odd2sNumber = Float.parseFloat(odd2.replace(",", "."));
		
		if (bookmaker2.equalsIgnoreCase("Betfair Exchange")) {
			Float probabilityOfLay = (1 - (1 / odd2sNumber));
			betAmount2 = new Float(betAmount1 * odd1sNumber * probabilityOfLay);
		} else {
			betAmount2 = new Float(betAmount1 * odd1sNumber / odd2sNumber);
		}
		
		Float percentageOfIncomeFloat = (betAmount1 * (odd1sNumber) / (betAmount1 + betAmount2)) - 1;
		this.incomePercentage = df.format(percentageOfIncomeFloat * 100);
	}
	
	public String getBookmaker1() {
		return bookmaker1;
	}
	public void setBookmaker1(String bookmaker1) {
		this.bookmaker1 = bookmaker1;
	}
	public String getBetType1() {
		return betType1;
	}
	public void setBetType1(String betType1) {
		this.betType1 = betType1;
	}
	public String getOdd1() {
		return odd1;
	}
	public void setOdd1(String odd1) {
		this.odd1 = odd1;
	}
	public String getMoney1() {
		return money1;
	}
	public void setMoney1(String money1) {
		this.money1 = money1;
	}
	public String getBookmaker2() {
		return bookmaker2;
	}
	public void setBookmaker2(String bookmaker2) {
		this.bookmaker2 = bookmaker2;
	}
	public String getBetType2() {
		return betType2;
	}
	public void setBetType2(String betType2) {
		this.betType2 = betType2;
	}
	public String getOdd2() {
		return odd2;
	}
	public void setOdd2(String odd2) {
		this.odd2 = odd2;
	}
	public String getMoney2() {
		return money2;
	}
	public void setMoney2(String money2) {
		this.money2 = money2;
	}

	public String getIncomePercentage() {
		return incomePercentage;
	}

	public void setIncomePercentage(String string) {
		this.incomePercentage = string;
	}
	

	@Override
	public int compareTo(Bet arg0) {
				
		Float income1 = Float.parseFloat(this.incomePercentage.replace(",", "."));
		Float income2 = Float.parseFloat(arg0.getIncomePercentage().replace(",", "."));
		
		return income1.compareTo(income2);
	}

}
