package it.bluesheep.arbitraggi.imagegeneration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.arbitraggi.entities.ThreeOptionReference;
import it.bluesheep.arbitraggi.entities.ThreeOptionsArbsRecord;

/**
 * Format and draw a table describing a 3 options bet
 * @author Fabio
 *
 */
public class ThreeOptionsBetSumUp extends BetSumUp {
	
	String betType1;
	String betType2;
	String betType3;

	String betType12;
	String betType13;
	String betType23;
	
	String betType1Code;
	String betType2Code;
	String betType3Code;

	String betType12Code;
	String betType13Code;
	String betType23Code;
	
	List<TableRow> punta1;
	List<TableRow> punta23;

	List<TableRow> punta2;
	List<TableRow> punta13;

	List<TableRow> punta3;
	List<TableRow> punta12;

	List<TableRow> banca1stOption;
	List<TableRow> punta1stOption;

	List<TableRow> banca2ndOption;
	List<TableRow> punta2ndOption;

	List<TableRow> banca3rdOption;
	List<TableRow> punta3rdOption;

	List<TableRow> books1;
	List<TableRow> books2;
	List<TableRow> books3;

	
	public ThreeOptionsBetSumUp(String betType, String betType1, String betType2, String betType3, String betType12, String betType13, String betType23, String betType1Code, String betType2Code, String betType3Code, String betType13Code, String betType12Code, String betType23Code) {
		super(betType);
		this.betType1 = betType1;
		this.betType2 = betType2;
		this.betType3 = betType3;

		this.betType12 = betType12;
		this.betType13 = betType13;
		this.betType23 = betType23;

		this.betType1Code = betType1Code;
		this.betType2Code = betType2Code;
		this.betType3Code = betType3Code;

		this.betType12Code = betType12Code;
		this.betType13Code = betType13Code;
		this.betType23Code = betType23Code;

		
		punta1 = new ArrayList<TableRow>();
		punta23 = new ArrayList<TableRow>();

		punta2 = new ArrayList<TableRow>();
		punta13 = new ArrayList<TableRow>();

		punta3 = new ArrayList<TableRow>();
		punta12 = new ArrayList<TableRow>();
		
		banca1stOption = new ArrayList<TableRow>();
		punta1stOption = new ArrayList<TableRow>();

		banca2ndOption = new ArrayList<TableRow>();
		punta2ndOption = new ArrayList<TableRow>();
		
		banca3rdOption = new ArrayList<TableRow>();
		punta3rdOption = new ArrayList<TableRow>();
		
		books1 = new ArrayList<TableRow>();
		books2 = new ArrayList<TableRow>();
		books3 = new ArrayList<TableRow>();
		
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

		if (!b){			
			if (oddsType1.equals(betType1Code) && oddsType2.equals(betType23Code)) {
				// punta 1 vs punta 23

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
				for (int i = 0; i < punta23.size(); i++) {
					if (punta23.get(i).getBookmaker().equals(temp2.getBookmaker())) {
						alreadyExists = i;
					}
				}
				if (alreadyExists < 0) {
					punta23.add(temp2);
				} else {
					((RightTableRow) punta23.get(alreadyExists)).updatePercentages(((RightTableRow) temp2).getMaxPercentage());
				}
				
			} else if (oddsType2.equals(betType1Code) && oddsType1.equals(betType23Code)) {
				//  punta 23 vs punta 1

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
				for (int i = 0; i < punta23.size(); i++) {
					if (punta23.get(i).getBookmaker().equals(temp2.getBookmaker())) {
						alreadyExists = i;
					}
				}
				if (alreadyExists < 0) {
					punta23.add(temp2);
				} else {
					((RightTableRow) punta23.get(alreadyExists)).updatePercentages(((RightTableRow) temp2).getMaxPercentage());
				}
				
			} else if (oddsType1.equals(betType2Code) && oddsType2.equals(betType13Code)) {
				// punta 2 vs punta 13

				TableRow temp1 = new LeftTableRow(bookmaker1, odd1, money1, betterOdd1, removedOdd1);
				int alreadyExists = -1;
				for (int i = 0; i < punta2.size(); i++) {
					if (punta2.get(i).getBookmaker().equals(temp1.getBookmaker())) {
						alreadyExists = i;
					}
				}			
				if (alreadyExists < 0) {
					punta2.add(temp1);
				}
				
				TableRow temp2 = new RightTableRow(bookmaker2, odd2, netProfit, money2, betterOdd2, removedOdd2);
				alreadyExists = -1;
				for (int i = 0; i < punta13.size(); i++) {
					if (punta13.get(i).getBookmaker().equals(temp2.getBookmaker())) {
						alreadyExists = i;
					}
				}
				if (alreadyExists < 0) {
					punta13.add(temp2);
				} else {
					((RightTableRow) punta13.get(alreadyExists)).updatePercentages(((RightTableRow) temp2).getMaxPercentage());
				}
				
			} else if (oddsType2.equals(betType2Code) && oddsType1.equals(betType13Code)) {
				//  punta 13 vs punta 2

				TableRow temp1 = new LeftTableRow(bookmaker2, odd2, money2, betterOdd2, removedOdd2);
				int alreadyExists = -1;
				for (int i = 0; i < punta2.size(); i++) {
					if (punta2.get(i).getBookmaker().equals(temp1.getBookmaker())) {
						alreadyExists = i;
					}
				}			
				if (alreadyExists < 0) {
					punta2.add(temp1);
				}
				
				TableRow temp2 = new RightTableRow(bookmaker1, odd1, netProfit, money1, betterOdd1, removedOdd1);
				alreadyExists = -1;
				for (int i = 0; i < punta13.size(); i++) {
					if (punta13.get(i).getBookmaker().equals(temp2.getBookmaker())) {
						alreadyExists = i;
					}
				}
				if (alreadyExists < 0) {
					punta13.add(temp2);
				} else {
					((RightTableRow) punta13.get(alreadyExists)).updatePercentages(((RightTableRow) temp2).getMaxPercentage());
				}
				
			} if (oddsType1.equals(betType3Code) && oddsType2.equals(betType12Code)) {
				// punta 3 vs punta 12

				TableRow temp1 = new LeftTableRow(bookmaker1, odd1, money1, betterOdd1, removedOdd1);
				int alreadyExists = -1;
				for (int i = 0; i < punta3.size(); i++) {
					if (punta3.get(i).getBookmaker().equals(temp1.getBookmaker())) {
						alreadyExists = i;
					}
				}			
				if (alreadyExists < 0) {
					punta3.add(temp1);
				}
				
				TableRow temp2 = new RightTableRow(bookmaker2, odd2, netProfit, money2, betterOdd2, removedOdd2);
				alreadyExists = -1;
				for (int i = 0; i < punta12.size(); i++) {
					if (punta12.get(i).getBookmaker().equals(temp2.getBookmaker())) {
						alreadyExists = i;
					}
				}
				if (alreadyExists < 0) {
					punta12.add(temp2);
				} else {
					((RightTableRow) punta12.get(alreadyExists)).updatePercentages(((RightTableRow) temp2).getMaxPercentage());
				}
				
			} else if (oddsType2.equals(betType3Code) && oddsType1.equals(betType12Code)) {
				//  punta 12 vs punta 3

				TableRow temp1 = new LeftTableRow(bookmaker2, odd2, money2, betterOdd2, removedOdd2);
				int alreadyExists = -1;
				for (int i = 0; i < punta3.size(); i++) {
					if (punta3.get(i).getBookmaker().equals(temp1.getBookmaker())) {
						alreadyExists = i;
					}
				}			
				if (alreadyExists < 0) {
					punta3.add(temp1);
				}
				
				TableRow temp2 = new RightTableRow(bookmaker1, odd1, netProfit, money1, betterOdd1, removedOdd1);
				alreadyExists = -1;
				for (int i = 0; i < punta12.size(); i++) {
					if (punta12.get(i).getBookmaker().equals(temp2.getBookmaker())) {
						alreadyExists = i;
					}
				}
				if (alreadyExists < 0) {
					punta12.add(temp2);
				} else {
					((RightTableRow) punta12.get(alreadyExists)).updatePercentages(((RightTableRow) temp2).getMaxPercentage());
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
				
			} else if (oddsType1.equals(betType3Code) && oddsType2.equals(betType3Code)) {
				// punta 3 e banca 3
				
				TableRow temp1 = new LeftTableRow(bookmaker2, odd2, money2, betterOdd2, removedOdd2);
				int alreadyExists = -1;
				for (int i = 0; i < banca3rdOption.size(); i++) {
					if (banca3rdOption.get(i).getBookmaker().equals(temp1.getBookmaker())) {
						alreadyExists = i;
					}
				}			
				if (alreadyExists < 0) {
					banca3rdOption.add(temp1);
				}
				
				TableRow temp2 = new RightTableRow(bookmaker1, odd1, netProfit, money1, betterOdd1, removedOdd1);
				alreadyExists = -1;
				for (int i = 0; i < punta3rdOption.size(); i++) {
					if (punta3rdOption.get(i).getBookmaker().equals(temp2.getBookmaker())) {
						alreadyExists = i;
					}
				}
				if (alreadyExists < 0) {
					punta3rdOption.add(temp2);
				} else {
					((RightTableRow) punta3rdOption.get(alreadyExists)).updatePercentages(((RightTableRow) temp2).getMaxPercentage());
				}
				
			} else {
				// ERRORE DI FORMATTAZIONE
				//System.out.println("Errore di formattazione dei dati");
			}
		} else {
			String bookmaker3 = ((ThreeOptionsArbsRecord) arbsRecord).getBookmaker3();
			String oddsType3 = ((ThreeOptionsArbsRecord) arbsRecord).getBet3();
			String odd3 = String.valueOf(((ThreeOptionsArbsRecord) arbsRecord).getOdd3());
			String money3 = String.valueOf(((ThreeOptionsArbsRecord) arbsRecord).getLiquidita3());
			boolean betterOdd3 = ((ThreeOptionsArbsRecord) arbsRecord).isBetterOdd3();
			boolean removedOdd3 = ((ThreeOptionsArbsRecord) arbsRecord).isRemovedOdd3();

			TableRow temp1 = null;
			TableRow temp2 = null;
			TableRow temp3 = null;
			
			if (oddsType1.equals(betType1Code)) {
				if (oddsType2.equals(betType2Code) && oddsType3.equals(betType3Code)) {
					temp1 = new LeftTableRow(bookmaker1, odd1, money1, betterOdd1, removedOdd1);
					temp2 = new LeftTableRow(bookmaker2, odd2, money2, betterOdd2, removedOdd2);
					temp3 = new RightTableRow(bookmaker3, odd3, netProfit, money3, betterOdd3, removedOdd3);					
				} else if (oddsType2.equals(betType3Code) && oddsType3.equals(betType2Code)) {
					temp1 = new LeftTableRow(bookmaker1, odd1, money1, betterOdd1, removedOdd1);
					temp2 = new LeftTableRow(bookmaker3, odd3, money3, betterOdd3, removedOdd3);
					temp3 = new RightTableRow(bookmaker2, odd2, netProfit, money2, betterOdd2, removedOdd2);					
				}
			} else if (oddsType1.equals(betType2Code)) {
				if (oddsType2.equals(betType1Code) && oddsType3.equals(betType3Code)) {
					temp1 = new LeftTableRow(bookmaker2, odd2, money2, betterOdd2, removedOdd2);
					temp2 = new LeftTableRow(bookmaker1, odd1, money1, betterOdd1, removedOdd1);
					temp3 = new RightTableRow(bookmaker3, odd3, netProfit, money3, betterOdd3, removedOdd3);					
				} else if (oddsType2.equals(betType3Code) && oddsType3.equals(betType1Code)) {
					temp1 = new LeftTableRow(bookmaker3, odd3, money3, betterOdd3, removedOdd3);
					temp2 = new LeftTableRow(bookmaker1, odd1, money1, betterOdd1, removedOdd1);
					temp3 = new RightTableRow(bookmaker2, odd2, netProfit, money2, betterOdd2, removedOdd2);
				}
			} else if (oddsType1.equals(betType3Code)) {
				if (oddsType2.equals(betType1Code) && oddsType3.equals(betType2Code)) {
					temp1 = new LeftTableRow(bookmaker2, odd2, money2, betterOdd2, removedOdd2);
					temp2 = new LeftTableRow(bookmaker3, odd3, money3, betterOdd3, removedOdd3);	
					temp3 = new RightTableRow(bookmaker1, odd1, netProfit, money1, betterOdd1, removedOdd1); 				
				} else if (oddsType2.equals(betType2Code) && oddsType3.equals(betType1Code)) {
					temp1 = new LeftTableRow(bookmaker3, odd3, money3, betterOdd3, removedOdd3);
					temp2 = new LeftTableRow(bookmaker2, odd2, money2, betterOdd2, removedOdd2);
					temp3 = new RightTableRow(bookmaker1, odd1, netProfit, money1, betterOdd1, removedOdd1); 				
				}
			}
			
			int index = 0;
						
			// Inserimento ad ordine crescente
			for (int i = 0; i < books3.size();i++){
				if (Float.parseFloat(((RightTableRow) temp3).getMaxPercentage()) < Float.parseFloat(((RightTableRow) books3.get(i)).getMaxPercentage())) {					
					index = i + 1;
				}
			}
						
			books1.add(index, temp1);
			books2.add(index, temp2);
			books3.add(index, temp3);
		}
		
		
		if (getAverage() == null && arbsRecord.getAverage() != null) {
			setAverage(arbsRecord.getAverage());
		}
		if (getRef() == null && arbsRecord.getRef() != null) {
			setRef(arbsRecord.getRef());
		}
	}

		public String drawTable() {
					
		final String sheepPath = "./img/bluesheep.png";
		
		Collections.sort(punta1, Collections.reverseOrder());
		Collections.sort(punta23, Collections.reverseOrder());
		Collections.sort(punta2, Collections.reverseOrder());
		Collections.sort(punta13, Collections.reverseOrder());
		Collections.sort(punta3, Collections.reverseOrder());
		Collections.sort(punta12, Collections.reverseOrder());
		Collections.sort(banca1stOption, Collections.reverseOrder());
		Collections.sort(punta1stOption, Collections.reverseOrder());
		Collections.sort(banca2ndOption, Collections.reverseOrder());
		Collections.sort(punta2ndOption, Collections.reverseOrder());
		Collections.sort(banca3rdOption, Collections.reverseOrder());
		Collections.sort(punta3rdOption, Collections.reverseOrder());

		String result = "";
		
		if (punta1.size() > 0 || punta2.size() > 0 || punta3.size() > 0 || banca1stOption.size() > 0 ||
				banca2ndOption.size() > 0 || banca3rdOption.size() > 0 || books1.size() > 0) {
		
			result += 
				    "<div class=\"container\">" +
				      "<div class=\"row\">" +
				        "<div class=\"col\">" +
				          "<h2 class=\"tipo-di-scommessa\"><u><b>" +
				          	getBetType() +
				          "</b></u></h2>" +
				        "</div>" +
				      "</div>";
			
			if (punta1.size() > 0) {
				result += drawSingleTable("Punta", betType1, "Punta", betType23, punta1, punta23);
			}

			if (punta2.size() > 0) {
				result += drawSingleTable("Punta", betType2, "Punta", betType13, punta2, punta13);
			}

			if (punta3.size() > 0) {
				result += drawSingleTable("Punta", betType3, "Punta", betType12, punta3, punta12);
			}
			
			if (banca1stOption.size() > 0) {
				result += drawSingleTable("Banca", betType1, "Punta", betType1, banca1stOption, punta1stOption);
			}

			if (banca2ndOption.size() > 0) {
				result += drawSingleTable("Banca", betType2, "Punta", betType2, banca2ndOption, punta2ndOption);
			}

			if (banca3rdOption.size() > 0) {
				result += drawSingleTable("Banca", betType3, "Punta", betType3, banca3rdOption, punta3rdOption);
			}
			
			if (books1.size() > 0) {
				result += drawMultipleTable(betType1, betType2, betType3, books1, books2, books3);
			}
			
			result += drawReferences(betType1, betType2, betType3);		
			
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
		
	private String drawMultipleTable(String betType1,String betType2, String betType3, List<TableRow> books1,
				List<TableRow> books2, List<TableRow> books3) {
			final String betterOddPath ="./img/up_arrow.png";

			String b1 = null;
			String b2 = null;
			String b3 = null;
			float q1;
			float q2;
			float q3;
			float l1 = -1;
			float l2 = -1;
			float l3 = -1;
			float bestP;
			
			List<TableRow> books1Removed = new ArrayList<TableRow>();
			List<TableRow> books2Removed = new ArrayList<TableRow>();
			List<TableRow> books3Removed = new ArrayList<TableRow>();
			
			float temp = 0;
			for (int i = 0; i < books1.size(); i++) {
				if (books1.get(i).isRemovedOdd()) {
					books1Removed.add(books1.get(i));
					books1.remove(i);
					i--;
				} else if (Float.parseFloat(books1.get(i).getOdd()) > temp) {
					temp = Float.parseFloat(books1.get(i).getOdd());
					b1 = books1.get(i).getBookmaker();
					l1 = Float.parseFloat(books1.get(i).getMoney());
				}
			}
			q1 = temp;
			
			temp = 0;
			for (int i = 0; i < books2.size(); i++) {
				if (books2.get(i).isRemovedOdd()) {
					books2Removed.add(books2.get(i));
					books2.remove(i);
					i--;
				} else if (Float.parseFloat(books2.get(i).getOdd()) > temp) {
					temp = Float.parseFloat(books2.get(i).getOdd());
					b2 = books2.get(i).getBookmaker();
					l2 = Float.parseFloat(books2.get(i).getMoney());
				}
			}
			q2 = temp;
			
			temp = 0;
			for (int i = 0; i < books3.size(); i++) {
				if (books3.get(i).isRemovedOdd()) {
					books3Removed.add(books3.get(i));
					books3.remove(i);
					i--;
				} else if (!books3.get(i).isRemovedOdd() && Float.parseFloat(books3.get(i).getOdd()) > temp) {
					temp = Float.parseFloat(books3.get(i).getOdd());
					b3 = books3.get(i).getBookmaker();
					l3 = Float.parseFloat(books3.get(i).getMoney());
				}
			}
			q3 = temp;
			
			// QUI DOVREI CALCOLARE IL BEST INCOME
			bestP = (( 1 / ( (1 / q1) + (1 / q2) + (1 / q3) )) - 1) * 100;
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
			df.setMinimumFractionDigits(2);
			
			String result = "";
			result += 
			"<h3 class=\"tipo-di-scommessa\">" +
					"Mercati a tre valori" +
			"</h3>" +
		    "<table class=\"table\">" +
		      "<thead>" +
		        "<tr>" +
		          "<th scope=\"col\">Bookmakers</th>" +
		          "<th scope=\"col\">" + betType1 + "</th>" +
		          "<th scope=\"col\">" + betType2 + "</th>" +
		          "<th scope=\"col\">" + betType3 + "</th>" +
		          "<th scope=\"col\">% di guadagno</th>" +
		        "</tr>" +
		      "</thead>" +
		      "<tbody>";
			
			for (int i = 0; i < books1.size(); i++){
				
				if (books1.get(i).isRemovedOdd()) {
					result += 
							"<tr class=\"removedOdd\">";
				} else {
					result += 
							"<tr>";
				}
				
				result += 
				        "<th scope=\"row\">" + books1.get(i).getBookmaker() + "</th>";
						
						if (!books1.get(i).isRemovedOdd() && Float.parseFloat(books1.get(i).getOdd()) == q1) {
							result += "<td class=\"best-odd\">";
						} else {
							result += "<td>";	
						}
						
						if (books1.get(i).isBetterOdd()) {
							result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
						}
						
						result += " &nbsp; " + books1.get(i).getOdd() + " &nbsp; ";							
						
						if (books1.get(i).isBetterOdd()) {
							result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
						}
						
						if (Float.parseFloat(books1.get(i).getMoney()) > 0) {
							result += "<br />(liquidita': " + books1.get(i).getMoney() + ")";
						}
						
						result += "</td>";	
						
						
						if (!books2.get(i).isRemovedOdd() && Float.parseFloat(books2.get(i).getOdd()) == q2) {
							result += "<td class=\"best-odd\">";
						} else {
							result += "<td>";	
						}
						
						if (books2.get(i).isBetterOdd()) {
							result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
						}
							
						result += " &nbsp; " + books2.get(i).getOdd() + " &nbsp; ";							
						
						if (books2.get(i).isBetterOdd()) {
							result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
						}
						
						if (Float.parseFloat(books2.get(i).getMoney()) > 0) {
							result += "<br />(liquidita': " + books2.get(i).getMoney() + ")";
						}
						
						result += "</td>";	

						if (!books3.get(i).isRemovedOdd() && Float.parseFloat(books3.get(i).getOdd()) == q3) {
							result += "<td class=\"best-odd\">";
						} else {
							result += "<td>";	
						}
						
						if (books3.get(i).isBetterOdd()) {
							result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
						}
						
						result += " &nbsp; " + books3.get(i).getOdd() + " &nbsp; ";							
						
						if (books3.get(i).isBetterOdd()) {
							result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
						}

						if (Float.parseFloat(books3.get(i).getMoney()) > 0) {
							result += "<br />(liquidita': " + books3.get(i).getMoney() + ")";
						}
						
						result += "</td>";	

						result += "<td>" + ((RightTableRow)books3.get(i)).getMaxPercentage() + "</td>" +
				       "</tr>";			
			}

			for (int i = 0; i < books1Removed.size(); i++){
				
				if (books1Removed.get(i).isRemovedOdd()) {
					result += 
							"<tr class=\"removedOdd\">";
				} else {
					result += 
							"<tr>";
				}
				
				result += 
				        "<th scope=\"row\">" + books1Removed.get(i).getBookmaker() + "</th>";
						
						if (!books1Removed.get(i).isRemovedOdd() && Float.parseFloat(books1Removed.get(i).getOdd()) == q1) {
							result += "<td class=\"best-odd\">";
						} else {
							result += "<td>";	
						}
						
						if (books1Removed.get(i).isBetterOdd()) {
							result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
						}
						
						result += " &nbsp; " + books1Removed.get(i).getOdd() + " &nbsp; ";							
						
						if (books1Removed.get(i).isBetterOdd()) {
							result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
						}
						
						if (Float.parseFloat(books1Removed.get(i).getMoney()) > 0) {
							result += "<br />(liquidita': " + books1Removed.get(i).getMoney() + ")";
						}
						
						result += "</td>";	
						
						
						if (!books2Removed.get(i).isRemovedOdd() && Float.parseFloat(books2Removed.get(i).getOdd()) == q2) {
							result += "<td class=\"best-odd\">";
						} else {
							result += "<td>";	
						}
						
						if (books2Removed.get(i).isBetterOdd()) {
							result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
						}
							
						result += " &nbsp; " + books2Removed.get(i).getOdd() + " &nbsp; ";							
						
						if (books2Removed.get(i).isBetterOdd()) {
							result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
						}
						
						if (Float.parseFloat(books2Removed.get(i).getMoney()) > 0) {
							result += "<br />(liquidita': " + books2Removed.get(i).getMoney() + ")";
						}
						
						result += "</td>";	

						if (!books3Removed.get(i).isRemovedOdd() && Float.parseFloat(books3Removed.get(i).getOdd()) == q3) {
							result += "<td class=\"best-odd\">";
						} else {
							result += "<td>";	
						}
						
						if (books3Removed.get(i).isBetterOdd()) {
							result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
						}
						
						result += " &nbsp; " + books3Removed.get(i).getOdd() + " &nbsp; ";							
						
						if (books3Removed.get(i).isBetterOdd()) {
							result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
						}

						if (Float.parseFloat(books3Removed.get(i).getMoney()) > 0) {
							result += "<br />(liquidita': " + books3Removed.get(i).getMoney() + ")";
						}
						
						result += "</td>";	

						result += "<td>" + ((RightTableRow)books3Removed.get(i)).getMaxPercentage() + "</td>" +
				       "</tr>";			
			}
			
				result += "<tr><td colspan=\"5\"></td></tr>";
				result += 
				        "<tr class=\"best-combination\">" +
				          "<th scope=\"row\">Miglior combinazione</th>" +
				          "<td>" + b1 + " - " + q1;
				if (l1 > 0) {
					result += "<br />(liquidita': " + l1 + ")";
				}
				result += "</td>" +
				          "<td>" + b2 + " - " + q2;
				
				if (l2 > 0) {
					result += "<br />(liquidita': " + l2 + ")";
				}
				result += "</td>" +
				          "<td>" + b3 + " - " + q3;
				
				if (l3 > 0) {
					result += "<br />(liquidita': " + l3 + ")";
				}
				result += "</td>" +
				          "<td>" + df.format(bestP) + "</td>" +
				        "</tr>";
				result += "<tr><td class=\"fake-row\" colspan=\"5\"></td></tr>" +
				       "</tbody>" +
				      "</table>";
				
			return result;
	}

	private String drawReferences(String betType1, String betType2, String betType3) {

		String result = "";
		String col1 = null;
		String col2 = null;
		String col3 = null;

		
		if (getRef() != null || getAverage() != null) {
			
			result += "<div class=\"div-riferimento\">" +
					"<h4 class=\"tipo-di-scommessa\">" +
						"Riferimenti" +  
					"</h4>" +
					"<div class=\"container\">" +
						"<div class=\"row\">" +
							"<table class=\"table\">" +
								"<thead>" +
									"<tr class=\"prima-riga-senza-bordo\">" +
										"<th scope=\"col-3\"></th>" +
										"<th scope=\"col-3\">" +
										betType1 +
										"</th>" +
										"<th scope=\"col-3\">" +
										betType2 +
										"</th>" +
										"<th scope=\"col-3\">" +
										betType3 +
										"</th>" +
									"</tr>" +
								"</thead>" +
								"<tbody>";

			if (getRef() != null) {
				ThreeOptionReference ref = (ThreeOptionReference) getRef();
				if(betType1Code.equals(ref.getBetType1())) {
					col1 = ref.getOdd1();
				}else if(betType1Code.equals(ref.getBetType2())) {
					col1 = ref.getOdd2();
				}else if(betType1Code.equals(ref.getBetType3())) {
					col1 = ref.getOdd3();
				}
				
				if(betType2Code.equals(ref.getBetType1())) {
					col2 = ref.getOdd1();
				}else if(betType2Code.equals(ref.getBetType2())) {
					col2 = ref.getOdd2();
				}else if(betType2Code.equals(ref.getBetType3())) {
					col2 = ref.getOdd3();
				}
				
				if(betType3Code.equals(ref.getBetType1())) {
					col3 = ref.getOdd1();
				}else if(betType3Code.equals(ref.getBetType2())) {
					col3 = ref.getOdd2();
				}else if(betType3Code.equals(ref.getBetType3())) {
					col3 = ref.getOdd3();
				}
				
				result += "<tr>" +
                        	"<th scope=\"col-3\">" +
                            	getRef().getBookmakerOdd1() +
                            "</th>" +
                            "<td>" +
                            	col1 +
                            "</td>" +
                            "<td>" +
                            	col2 +
                            "</td>" +
	                        "<td>" +
	                        	col3 +
	                        "</td>" +
                           "</tr>";				
			}
			
			if (getAverage() != null) {
				ThreeOptionReference average = (ThreeOptionReference) getAverage();
				String book1 = "";
				String book2 = "";
				String book3 = "";
				if(betType1Code.equals(average.getBetType1())) {
					col1 = average.getOdd1();
					book1 = average.getBookmakerOdd1();
				}else if(betType1Code.equals(average.getBetType2())) {
					col1 = average.getOdd2();
					book1 = average.getBookmakerOdd2();
				}else if(betType1Code.equals(average.getBetType3())) {
					col1 = average.getOdd3();
					book1 = average.getBookmakerOdd3();
				}
				
				if(betType2Code.equals(average.getBetType1())) {
					col2 = average.getOdd1();
					book2 = average.getBookmakerOdd1();
				}else if(betType2Code.equals(average.getBetType2())) {
					col2 = average.getOdd2();
					book2 = average.getBookmakerOdd2();
				}else if(betType2Code.equals(average.getBetType3())) {
					col2 = average.getOdd3();
					book3 = average.getBookmakerOdd3();
				}
				
				if(betType3Code.equals(average.getBetType1())) {
					col3 = average.getOdd1();
					book3 = average.getBookmakerOdd1();
				}else if(betType3Code.equals(average.getBetType2())) {
					col3 = average.getOdd2();
					book3 = average.getBookmakerOdd2();
				}else if(betType3Code.equals(average.getBetType3())) {
					col3 = average.getOdd3();
					book3 = average.getBookmakerOdd3();

				}
				
				result += "<tr>" +
                        	"<th scope=\"col-3\">" +
                        		"Mediana" +
                            "</th>" +
                            "<td>" +
                            	col1 +
                            	"</br>" +
                            	"(" + book1 + ")" +
                            "</td>" +
                            "<td>" +
                            	col2 +
                            	"</br>" +
                            	"(" + book2 + ")"  +
                            "</td>" +
	                        "<td>" +
	                        	col3 +
	                        	"</br>" +
	                        	"(" + book3 + ")"  +
	                        "</td>" +
                           "</tr>";				
			}

		}
                
		result += "</tbody>" +
				  "</table>" +
				  "</div>" +
				  "</div>" +
				  "</div>" +
				  "</br>" +
				  "</br>";

		
		return result;
	}

}