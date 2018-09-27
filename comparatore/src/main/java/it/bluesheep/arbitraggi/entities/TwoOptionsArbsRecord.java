package it.bluesheep.arbitraggi.entities;

import java.text.DecimalFormat;

import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.util.BlueSheepConstants;

public class TwoOptionsArbsRecord extends ArbsRecord {
	
	public TwoOptionsArbsRecord(String status,
			String bookmaker1, String bookmaker2, double odd1,
			double odd2, String bet1, String bet2, String date, String keyEvento,
			String championship, String sport, String link1,
			String link2, String country, double liquidita1, double liquidita2,
			boolean betterOdd1, boolean betterOdd2, boolean removedOdd1, boolean removedOdd2,
			BetReference ref, BetReference average) {
		super(status, bookmaker1, bookmaker2, odd1,
				odd2, bet1, bet2, date, keyEvento,
				championship, sport, link1,
				link2, country, liquidita1, liquidita2, betterOdd1, betterOdd2, removedOdd1, removedOdd2, 
				ref, average);
		this.type = ArbsType.TWO_WAY;
	}
	
	public TwoOptionsArbsRecord(String status, RecordOutput recordOutput, BetReference ref, BetReference average) {
		super(status, 
				recordOutput.getBookmakerName1(), 
				recordOutput.getBookmakerName2(), 
				recordOutput.getQuotaScommessaBookmaker1(),
				recordOutput.getQuotaScommessaBookmaker2(),
				recordOutput.getScommessaBookmaker1(),
				recordOutput.getScommessaBookmaker2(),
				recordOutput.getDataOraEvento().toString(),
				recordOutput.getEvento(),
				recordOutput.getCampionato(), 
				recordOutput.getSport(), 
				recordOutput.getLinkBook1(),
				recordOutput.getLinkBook2(),
				recordOutput.getNazione(),
				recordOutput.getLiquidita1(),
				recordOutput.getLiquidita2(),
				false, false, false, false, 
				ref, average);
		this.type = ArbsType.TWO_WAY;
	}
	
	@Override
	protected void calculateNetProfit() {
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);		
		
		if (getBet1().equals(getBet2())) {
			double realOdd2 = 1 / (1 - (1/getOdd2()));
			double r1 = 1000 * getOdd1();
			double x = r1/realOdd2;
			netProfit = ((((r1)/(1000 + x)) - 1) * 100);			
		} else {
			double r1 = 1000 * getOdd1();
			double x = r1/getOdd2();
			netProfit = ((((r1)/(1000 + x)) - 1) * 100);			
		}
	}
	
	@Override
	public String getKeyEventoBookmakerBet() {
		return keyEvento + BlueSheepConstants.REGEX_CSV + 
				   date + BlueSheepConstants.REGEX_CSV + 
				   sport + BlueSheepConstants.REGEX_CSV +
				   country + BlueSheepConstants.REGEX_CSV + 
				   championship + BlueSheepConstants.REGEX_CSV + 
				   bookmaker1 + BlueSheepConstants.REGEX_CSV + 
				   bet1 + BlueSheepConstants.REGEX_CSV + 
				   bookmaker2 + BlueSheepConstants.REGEX_CSV +
				   bet2 + BlueSheepConstants.KEY_SEPARATOR +
				   odd1 + BlueSheepConstants.REGEX_CSV +
				   odd2;
	}

	@Override
	public String getBookmakerList() {
		return bookmaker1 + BlueSheepConstants.REGEX_CSV + bookmaker2;
	}

	@Override
	public String getKeyEventoBet() {
		return keyEvento + BlueSheepConstants.REGEX_CSV + 
				   date + BlueSheepConstants.REGEX_CSV + 
				   sport + BlueSheepConstants.REGEX_CSV +
				   country + BlueSheepConstants.REGEX_CSV + 
				   championship + BlueSheepConstants.REGEX_CSV;
	}

	@Override
	public String getStoredDataFormat() {
		return getKeyEventoBookmakerBet() + BlueSheepConstants.KEY_SEPARATOR + getStatus()
				+ BlueSheepConstants.KEY_SEPARATOR + getLink1() + BlueSheepConstants.REGEX_CSV
				+ getLink2() + BlueSheepConstants.KEY_SEPARATOR + getLiquidita1()
				+ BlueSheepConstants.REGEX_CSV + getLiquidita2();
	}
}
