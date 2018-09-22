package it.bluesheep.arbitraggi.imagegeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.bluesheep.arbitraggi.entities.ArbsRecord;

/**
 * Format and draw a table describing a 2 options bet
 * @author Fabio
 *
 */
public class TwoOptionsBetSumUp extends BetSumUp {
	
	String betType1;
	String betType2;
	String betType1Code;
	String betType2Code;
	
	List<TableRow> punta1;
	List<TableRow> punta2;
	
	List<TableRow> banca1stOption;
	List<TableRow> punta1stOption;

	List<TableRow> banca2ndOption;
	List<TableRow> punta2ndOption;
	
	public TwoOptionsBetSumUp(String betType, String betType1, String betType2, String betType1Code, String betType2Code) {
		super(betType);
		this.betType1 = betType1;
		this.betType2 = betType2;
		this.betType1Code = betType1Code;
		this.betType2Code = betType2Code;
	
		punta1 = new ArrayList<TableRow>();
		punta2 = new ArrayList<TableRow>();

		banca1stOption = new ArrayList<TableRow>();
		punta1stOption = new ArrayList<TableRow>();

		banca2ndOption = new ArrayList<TableRow>();
		punta2ndOption = new ArrayList<TableRow>();
	}
	
	public String getBetType1() {
		return betType1;
	}
	public void setBetType1(String betType1) {
		this.betType1 = betType1;
	}
	public String getBetType2() {
		return betType2;
	}
	public void setBetType2(String betType2) {
		this.betType2 = betType2;
	}
	public List<TableRow> getPunta1() {
		return punta1;
	}
	public void setPunta1(List<TableRow> punta1) {
		this.punta1 = punta1;
	}
	public List<TableRow> getPunta2() {
		return punta2;
	}
	public void setPunta2(List<TableRow> punta2) {
		this.punta2 = punta2;
	}
	public List<TableRow> getBanca1stOption() {
		return banca1stOption;
	}
	public void setBanca1stOption(List<TableRow> banca1stOption) {
		this.banca1stOption = banca1stOption;
	}
	public List<TableRow> getPunta1stOption() {
		return punta1stOption;
	}
	public void setPunta1stOption(List<TableRow> punta1stOption) {
		this.punta1stOption = punta1stOption;
	}
	public List<TableRow> getBanca2ndOption() {
		return banca2ndOption;
	}
	public void setBanca2ndOption(List<TableRow> banca2ndOption) {
		this.banca2ndOption = banca2ndOption;
	}
	public List<TableRow> getPunta2ndOption() {
		return punta2ndOption;
	}
	public void setPunta2ndOption(List<TableRow> punta2ndOption) {
		this.punta2ndOption = punta2ndOption;
	}

	@Override
	public void addRecord(ArbsRecord arbsRecord, boolean b) {
		
		String bookmaker1 = arbsRecord.getBookmaker1();
		String oddsType1 = arbsRecord.getBet1();
		String odd1 = String.valueOf(arbsRecord.getOdd1());
		String money1 = String.valueOf(arbsRecord.getLiquidita1());
		String bookmaker2 = arbsRecord.getBookmaker2();
		String oddsType2 = arbsRecord.getBet2();
		String odd2 = String.valueOf(arbsRecord.getOdd2());
		String money2 = String.valueOf(arbsRecord.getLiquidita2());
		boolean betterOdd1 = arbsRecord.isBetterOdd1();
		boolean betterOdd2 = arbsRecord.isBetterOdd2();
		boolean removedOdd1 = arbsRecord.isRemovedOdd1();
		boolean removedOdd2 = arbsRecord.isRemovedOdd2();	
		String netProfit = String.valueOf(arbsRecord.getNetProfit());
		
		if (oddsType1.equals(betType1Code) && oddsType2.equals(betType2Code)) {
			// punta 1 vs punta 2

			TableRow temp1 = new LeftTableRow(bookmaker1, odd1, money1, betterOdd1, removedOdd1);
			int alreadyExists = -1;
			for (int i = 0; i < punta1.size(); i++) {
				if (punta1.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				punta1.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker2, odd2, netProfit, money2, betterOdd2, removedOdd2);
			alreadyExists = -1;
			for (int i = 0; i < punta2.size(); i++) {
				if (punta2.get(i).getBookmaker().equals(temp2.getBookmaker())) {
					alreadyExists = i;
				}
			}
			if (alreadyExists < 0) {
				punta2.add(temp2);
			} else {
				((RightTableRow) punta2.get(alreadyExists)).updatePercentages(((RightTableRow) temp2).getMaxPercentage());
			}
			
		} else if (oddsType2.equals(betType1Code) && oddsType1.equals(betType2Code)) {
			// punta 2 vs punta 1
			
			TableRow temp1 = new LeftTableRow(bookmaker2, odd2, money2, betterOdd2, removedOdd2);
			int alreadyExists = -1;
			for (int i = 0; i < punta1.size(); i++) {
				if (punta1.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				punta1.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker1, odd1, netProfit, money1, betterOdd1, removedOdd1);
			alreadyExists = -1;
			for (int i = 0; i < punta2.size(); i++) {
				if (punta2.get(i).getBookmaker().equals(temp2.getBookmaker())) {
					alreadyExists = i;
				}
			}
			if (alreadyExists < 0) {
				punta2.add(temp2);
			} else {
				((RightTableRow) punta2.get(alreadyExists)).updatePercentages(((RightTableRow) temp2).getMaxPercentage());
			}

		} else if (oddsType1.equals(betType1Code) && oddsType2.equals(betType1Code)) {
			// punta 1 e banca 1
			
			TableRow temp1 = new LeftTableRow(bookmaker2, odd2, money2, betterOdd2, removedOdd2);
			int alreadyExists = -1;
			for (int i = 0; i < banca1stOption.size(); i++) {
				if (banca1stOption.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				banca1stOption.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker1, odd1, netProfit, money1, betterOdd1, removedOdd1);
			alreadyExists = -1;
			for (int i = 0; i < punta1stOption.size(); i++) {
				if (punta1stOption.get(i).getBookmaker().equals(temp2.getBookmaker())) {
					alreadyExists = i;
				}
			}
			if (alreadyExists < 0) {
				punta1stOption.add(temp2);
			} else {
				((RightTableRow) punta1stOption.get(alreadyExists)).updatePercentages(((RightTableRow) temp2).getMaxPercentage());
			}
					
		} else if (oddsType1.equals(betType2Code) && oddsType2.equals(betType2Code)) {
			// punta 2 e banca 2
			
			TableRow temp1 = new LeftTableRow(bookmaker2, odd2, money2, betterOdd2, removedOdd2);
			int alreadyExists = -1;
			for (int i = 0; i < banca2ndOption.size(); i++) {
				if (banca2ndOption.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				banca2ndOption.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker1, odd1, netProfit, money1, betterOdd1, removedOdd1);
			alreadyExists = -1;
			for (int i = 0; i < punta2ndOption.size(); i++) {
				if (punta2ndOption.get(i).getBookmaker().equals(temp2.getBookmaker())) {
					alreadyExists = i;
				}
			}
			if (alreadyExists < 0) {
				punta2ndOption.add(temp2);
			} else {
				((RightTableRow) punta2ndOption.get(alreadyExists)).updatePercentages(((RightTableRow) temp2).getMaxPercentage());
			}
			
		} else {
			// ERRORE DI FORMATTAZIONE
			//System.out.println("Errore di formattazione dei dati");
		}
	
		if (getAverage() == null && arbsRecord.getAverage() != null) {
			setAverage(arbsRecord.getAverage());
		}
		if (getRef() == null && arbsRecord.getRef() != null) {
			setRef(arbsRecord.getRef());
		}
	}

	public String drawTable() {
				
		Collections.sort(punta1, Collections.reverseOrder());
		Collections.sort(punta2, Collections.reverseOrder());
		Collections.sort(banca1stOption, Collections.reverseOrder());
		Collections.sort(punta1stOption, Collections.reverseOrder());
		Collections.sort(banca2ndOption, Collections.reverseOrder());
		Collections.sort(punta2ndOption, Collections.reverseOrder());
		
		final String sheepPath = "./img/bluesheep.png";
		String result = "";
		
		if (punta1.size() > 0 || banca1stOption.size() > 0 || banca2ndOption.size() > 0) {
			result += 
				    "<div class=\"container\">" +
				      "<div class=\"row\">" +
				        "<div class=\"col\">" +
				          "<h2 class=\"tipo-di-scommessa\">" +
				          	this.getBetType() +
				          "</h2>" +
				        "</div>" +
				      "</div>";
			
			if (punta1.size() > 0) {
				result += drawSingleTable("Punta", betType1, "Punta", betType2, punta1, punta2);
			}
			
			if (banca1stOption.size() > 0) {
				result += drawSingleTable("Banca", betType1, "Punta", betType1, banca1stOption, punta1stOption);
			}
				
			if (banca2ndOption.size() > 0) {	
				result += drawSingleTable("Banca", betType2, "Punta", betType2, banca2ndOption, punta2ndOption);
			}
						
			
			result += drawReferences(betType1, betType2);		
			
			result += 
			"<div class=\"row\">" +
	        "<div class=\"col goat-container\">";
			
			for (int i = 0; i < 25; i++) {
				result += 
						"<img class=\"goat\" src=\"" + sheepPath + "\" alt=\"Bluesheep\" />";
			}
					
		result += 
	        "</div>" +
	        "</div>" +
		    "</div>" +
			"<br />";
		}
		return result;
	}

	
	private String drawReferences(String betType1, String betType2) {

		String result = "";
		
		String oddCol1 = null;
		String oddCol2 = null;
		
		if (getRef() != null || getAverage() != null) {
			
			result += "<div class=\"div-riferimento\">" +
					"<h3 class=\"tipo-di-scommessa\">" +
						"Riferimenti " + getBetType() +  
					"</h3>" +
					"<div class=\"container\">" +
						"<div class=\"row\">" +
							"<table class=\"table\">" +
								"<thead>" +
									"<tr class=\"prima-riga-senza-bordo\">" +
										"<th scope=\"col-4\"></th>" +
										"<th scope=\"col-4\">" +
										betType1 +
										"</th>" +
										"<th scope=\"col-4\">" +
										betType2 +
										"</th>" +
									"</tr>" +
								"</thead>" +
								"<tbody>";

			if (getRef() != null) {
				
				if(getRef().getBetType1().equalsIgnoreCase(betType1Code) && getRef().getBetType2().equalsIgnoreCase(betType2Code)) {
					oddCol1 = getRef().getOdd1();
					oddCol2 = getRef().getOdd2();
				}else {
					oddCol1 = getRef().getOdd2();
					oddCol2 = getRef().getOdd1();
				}
				result += "<tr>" +
                        	"<th scope=\"col-4\">" +
                            	getRef().getBookmakerOdd1() +
                            "</th>" +
                            "<td>" +
                            oddCol1 +
                            "</td>" +
                            "<td>" +
                            oddCol2 +
                            "</td>" +
                           "</tr>";				
			}
			
			if (getAverage() != null) {
				String book1 = "";
				String book2 = "";
				if(getAverage().getBetType1().equalsIgnoreCase(betType1Code) && getAverage().getBetType2().equalsIgnoreCase(betType2Code)) {
					oddCol1 = getAverage().getOdd1();
					oddCol2 = getAverage().getOdd2();
					book1 = getAverage().getBookmakerOdd1();
					book2 = getAverage().getBookmakerOdd2();
				}else {
					oddCol1 = getAverage().getOdd2();
					oddCol2 = getAverage().getOdd1();
					book2 = getAverage().getBookmakerOdd1();
					book1 = getAverage().getBookmakerOdd2();
				}
				
				result += "<tr>" +
                        	"<th scope=\"col-4\">" +
                        		"Mediana " +
                            "</th>" +
                            "<td>" +
                            oddCol1 +
                            "</br>" +
                            	"(" + book1 + ")" +
                            "</td>" +
                            "<td>" +
                            oddCol2 +
                            	"</br>" +
                            	"(" + book2  + ")" +
                            "</td>" +
                           "</tr>";				
			}

		}
                
		result += "</tbody>" +
				  "</table>" +
				  "</div>" +
				  "</div>" +
				  "</div>" +
				  "</br>";

		
		return result;
	}

}