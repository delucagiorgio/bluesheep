package it.bluesheep.arbitraggi.imagegeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

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
	}

	@Override
	public void addRecord(String bookmaker1, String oddsType1, String odd1,
			String money1, String bookmaker2, String oddsType2, String odd2, String money2) {

		if (oddsType1.equals(betType1Code) && oddsType2.equals(betType23Code)) {
			// punta 1 vs punta 23

			TableRow temp1 = new LeftTableRow(bookmaker1, odd1, money1);
			int alreadyExists = -1;
			for (int i = 0; i < punta1.size(); i++) {
				if (punta1.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				punta1.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker2, odd2, this.calcutateIncome(odd1, odd2, oddsType1, oddsType2));
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

			TableRow temp1 = new LeftTableRow(bookmaker2, odd2, money2);
			int alreadyExists = -1;
			for (int i = 0; i < punta1.size(); i++) {
				if (punta1.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				punta1.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker1, odd1, this.calcutateIncome(odd1, odd2, oddsType1, oddsType2));
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

			TableRow temp1 = new LeftTableRow(bookmaker1, odd1, money1);
			int alreadyExists = -1;
			for (int i = 0; i < punta2.size(); i++) {
				if (punta2.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				punta2.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker2, odd2, this.calcutateIncome(odd1, odd2, oddsType1, oddsType2));
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

			TableRow temp1 = new LeftTableRow(bookmaker2, odd2, money2);
			int alreadyExists = -1;
			for (int i = 0; i < punta2.size(); i++) {
				if (punta2.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				punta2.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker1, odd1, this.calcutateIncome(odd1, odd2, oddsType1, oddsType2));
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

			TableRow temp1 = new LeftTableRow(bookmaker1, odd1, money1);
			int alreadyExists = -1;
			for (int i = 0; i < punta3.size(); i++) {
				if (punta3.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				punta3.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker2, odd2, this.calcutateIncome(odd1, odd2, oddsType1, oddsType2));
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

			TableRow temp1 = new LeftTableRow(bookmaker2, odd2, money2);
			int alreadyExists = -1;
			for (int i = 0; i < punta3.size(); i++) {
				if (punta3.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				punta3.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker1, odd1, this.calcutateIncome(odd1, odd2, oddsType1, oddsType2));
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
			
			TableRow temp1 = new LeftTableRow(bookmaker2, odd2, money2);
			int alreadyExists = -1;
			for (int i = 0; i < banca1stOption.size(); i++) {
				if (banca1stOption.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				banca1stOption.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker1, odd1, this.calcutateIncome(odd1, odd2, oddsType1, oddsType2));
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
			
			TableRow temp1 = new LeftTableRow(bookmaker2, odd2, money2);
			int alreadyExists = -1;
			for (int i = 0; i < banca2ndOption.size(); i++) {
				if (banca2ndOption.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				banca2ndOption.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker1, odd1, this.calcutateIncome(odd1, odd2, oddsType1, oddsType2));
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
			
			TableRow temp1 = new LeftTableRow(bookmaker2, odd2, money2);
			int alreadyExists = -1;
			for (int i = 0; i < banca3rdOption.size(); i++) {
				if (banca3rdOption.get(i).getBookmaker().equals(temp1.getBookmaker())) {
					alreadyExists = i;
				}
			}			
			if (alreadyExists < 0) {
				banca3rdOption.add(temp1);
			}
			
			TableRow temp2 = new RightTableRow(bookmaker1, odd1, this.calcutateIncome(odd1, odd2, oddsType1, oddsType2));
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
	}

		public String drawTable() {
					
		final String sheepPath = "./img/bluesheep.png";
		final String missilePath = "./img/missile.png";
		int MISSILE_TRASHOLD = Integer.parseInt(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.MISSILE_TRASHOLD));
		
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
				banca2ndOption.size() > 0 || banca3rdOption.size() > 0) {
		
			result += 
				    "<div class=\"container\">" +
				      "<div class=\"row\">" +
				        "<div class=\"col\">" +
				          "<h2 class=\"tipo-di-scommessa\">" +
				          	betType +
				          "</h2>" +
				        "</div>" +
				      "</div>";
			
			if (punta1.size() > 0) {
				result += 
				      "<div class=\"row\">" +
				        "<div class=\"col left-col\">" +
				        	"<h3 class=\"tipo-di-scommessa\">" +
				        		"Punta " + betType1 +
				        	"</h3>" +
				          "<table class=\"table\">" +
				            "<thead>" +
				              "<tr>" +
				                "<th scope=\"col\">" +
				                  "Bookmakers" +
				                "</th>" +
				                "<th scope=\"col\">" +
				                  "Quota" +
				                "</th>" +
				              "</tr>" +
				            "</thead>" +
				            "<tbody>";
					
					for (int i = 0; i < punta1.size(); i++) {					
						result += 
							"<tr>" +
				                "<th scope=\"row\">" +
				                punta1.get(i).getBookmaker() + 
				                "</th>" +
				                "<td>" +
				                ((LeftTableRow) punta1.get(i)).getOdd() +
				                "</td>" +
				              "</tr>";
					}
					
			  		result += 			
				  		"</tbody>" +
				        "</table>" +
				        "</div>";					
			  		result += 
				        "<div class=\"col right-col\">" +
			        	"<h3 class=\"tipo-di-scommessa\">" +
			        		"Punta " + betType23 +
			        	"</h3>" +
				        "<table class=\"table\">" +
				          "<thead>" +
				            "<tr>" +
				              "<th scope=\"col\">" +
				                "Bookmakers" +
				              "</th>" +
				              "<th scope=\"col\">" +
				                "Quota" +
				              "</th>" +
				              "<th scope=\"col\">" +
				                "% di guadagno" +
				              "</th>" +
				            "</tr>" +
				          "</thead>" +
				          "<tbody>";
			  						
			  		for (int i = 0; i < punta23.size(); i++) {		
			  			result += 
			  					"<tr>" +
					                "<th scope=\"row\">" +
					                	punta23.get(i).getBookmaker() + 
					                "</th>" +
					                "<td>" +
					                	((RightTableRow) punta23.get(i)).getOdd() +
					                "</td>" +
					                "<td>";
			  			
			  			if (Float.parseFloat(((RightTableRow) punta23.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
			  				result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
			  			}
			  			if (Float.parseFloat(((RightTableRow) punta23.get(i)).getMaxPercentage().replace(",", ".")) > Float.parseFloat(((RightTableRow) punta23.get(i)).getMinPercentage().replace(",", "."))) {
			  				result += "&nbsp; " + ((RightTableRow) punta23.get(i)).getMaxPercentage() + " - " + ((RightTableRow) punta23.get(i)).getMinPercentage() + " &nbsp;";
			  			}	else {
			  				result += "&nbsp; " + ((RightTableRow) punta23.get(i)).getMaxPercentage() + " &nbsp;";
			  			}
			  			if (Float.parseFloat(((RightTableRow) punta23.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
			  				result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
			  			}
			  			
					    result += "</td>" +
					              "</tr>";
					}
			  		
					result +=
							          "</tbody>" +
							        "</table>" +
							      "</div>" +
							    "</div>" +
							    "<br />" ;
			}
			
			if (punta2.size() > 0) {	
				result += 
				      "<div class=\"row\">" +
				        "<div class=\"col left-col\">" +
				        	"<h3 class=\"tipo-di-scommessa\">" +
				        		"Punta " + betType2 +
				        	"</h3>" +
				          "<table class=\"table\">" +
				            "<thead>" +
				              "<tr>" +
				                "<th scope=\"col\">" +
				                  "Bookmakers" +
				                "</th>" +
				                "<th scope=\"col\">" +
				                  "Quota" +
				                "</th>" +
				              "</tr>" +
				            "</thead>" +
				            "<tbody>";
					
					for (int i = 0; i < punta2.size(); i++) {					
						result += 
							"<tr>" +
				                "<th scope=\"row\">" +
				                punta2.get(i).getBookmaker() + 
				                "</th>" +
				                "<td>" +
				                ((LeftTableRow) punta2.get(i)).getOdd() +
				                "</td>" +
				              "</tr>";
					}
					
			  		result += 			
				  		"</tbody>" +
				        "</table>" +
				        "</div>";
			  		result += 
				        "<div class=\"col right-col\">" +
			        	"<h3 class=\"tipo-di-scommessa\">" +
			        		"Punta " + betType13 +
			        	"</h3>" +
				        "<table class=\"table\">" +
				          "<thead>" +
				            "<tr>" +
				              "<th scope=\"col\">" +
				                "Bookmakers" +
				              "</th>" +
				              "<th scope=\"col\">" +
				                "Quota" +
				              "</th>" +
				              "<th scope=\"col\">" +
				                "% di guadagno" +
				              "</th>" +
				            "</tr>" +
				          "</thead>" +
				          "<tbody>";
			  						
			  		for (int i = 0; i < punta13.size(); i++) {		
			  			result += 
			  					"<tr>" +
					                "<th scope=\"row\">" +
					                	punta13.get(i).getBookmaker() + 
					                "</th>" +
					                "<td>" +
					                	((RightTableRow) punta13.get(i)).getOdd() +
					                "</td>" +
					                "<td>";
			  			
			  			if (Float.parseFloat(((RightTableRow) punta13.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
			  				result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
			  			}
			  			if (Float.parseFloat(((RightTableRow) punta13.get(i)).getMaxPercentage().replace(",", ".")) > Float.parseFloat(((RightTableRow) punta13.get(i)).getMinPercentage().replace(",", "."))) {
			  				result += "&nbsp; " + ((RightTableRow) punta13.get(i)).getMaxPercentage() + " - " + ((RightTableRow) punta13.get(i)).getMinPercentage() + " &nbsp;";
			  			}	else {
			  				result += "&nbsp; " + ((RightTableRow) punta13.get(i)).getMaxPercentage() + " &nbsp;";
			  			}
			  			if (Float.parseFloat(((RightTableRow) punta13.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
			  				result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
			  			}
					    result += "</td>" +
					              "</tr>";
					}
			  					  		
					result +=
							          "</tbody>" +
							        "</table>" +
							      "</div>" +
							    "</div>" +
							    "<br />" ;
			}
			
			if (punta3.size() > 0) {
				result += 
				      "<div class=\"row\">" +
				        "<div class=\"col left-col\">" +
				        	"<h3 class=\"tipo-di-scommessa\">" +
				        		"Punta " + betType3 +
				        	"</h3>" +
				          "<table class=\"table\">" +
				            "<thead>" +
				              "<tr>" +
				                "<th scope=\"col\">" +
				                  "Bookmakers" +
				                "</th>" +
				                "<th scope=\"col\">" +
				                  "Quota" +
				                "</th>" +
				              "</tr>" +
				            "</thead>" +
				            "<tbody>";
					
					for (int i = 0; i < punta3.size(); i++) {					
						result += 
							"<tr>" +
				                "<th scope=\"row\">" +
				                punta3.get(i).getBookmaker() + 
				                "</th>" +
				                "<td>" +
				                ((LeftTableRow) punta3.get(i)).getOdd() +
				                "</td>" +
				              "</tr>";
					}
					
			  		result += 			
				  		"</tbody>" +
				        "</table>" +
				        "</div>";
			  		result += 
				        "<div class=\"col right-col\">" +
			        	"<h3 class=\"tipo-di-scommessa\">" +
			        		"Punta " + betType12 +
			        	"</h3>" +
				        "<table class=\"table\">" +
				          "<thead>" +
				            "<tr>" +
				              "<th scope=\"col\">" +
				                "Bookmakers" +
				              "</th>" +
				              "<th scope=\"col\">" +
				                "Quota" +
				              "</th>" +
				              "<th scope=\"col\">" +
				                "% di guadagno" +
				              "</th>" +
				            "</tr>" +
				          "</thead>" +
				          "<tbody>";
			  						
			  		for (int i = 0; i < punta12.size(); i++) {		
			  			result += 
			  					"<tr>" +
					                "<th scope=\"row\">" +
					                punta12.get(i).getBookmaker() + 
					                "</th>" +
					                "<td>" +
					                	((RightTableRow) punta12.get(i)).getOdd() +
					                "</td>" +
					                "<td>";
			  			
			  			if (Float.parseFloat(((RightTableRow) punta12.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
			  				result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
			  			}
			  			if (Float.parseFloat(((RightTableRow) punta12.get(i)).getMaxPercentage().replace(",", ".")) > Float.parseFloat(((RightTableRow) punta12.get(i)).getMinPercentage().replace(",", "."))) {
			  				result += "&nbsp; " + ((RightTableRow) punta12.get(i)).getMaxPercentage() + " - " + ((RightTableRow) punta12.get(i)).getMinPercentage() + " &nbsp;";
			  			}	else {
			  				result += "&nbsp; " + ((RightTableRow) punta12.get(i)).getMaxPercentage() + " &nbsp;";
			  			}
			  			if (Float.parseFloat(((RightTableRow) punta12.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
			  				result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
			  			}
					    result += "</td>" +
					              "</tr>";
					}
			  		
					result +=
							          "</tbody>" +
							        "</table>" +
							      "</div>" +
							    "</div>" +
							    "<br />" ;
			}
			
				if (banca1stOption.size() > 0) {
					result += 			
					"<div class=\"row\">" +
			        "<div class=\"col left-col\">" +
			          "<h4 class=\"tipo-di-scommessa\">" +
			            "Banca " + betType1 +
			          "</h4>" +
			          "<table class=\"table\">" +
			            "<thead>" +
			              "<tr>" +
			                "<th scope=\"col\">" +
			                  "Bookmakers" + 
			                "</th>" +
			                "<th scope=\"col\">" +
			                  "Quota" +
			                "</th>" +
			                "<th scope=\"col\">" +
			                  "Liquidita'" +
			                "</th>" +
			              "</tr>" +
			            "</thead>" +
			            "<tbody>";
					
					for (int i = 0; i < banca1stOption.size(); i++) {
							result += 
								"<tr>" +
					                "<th scope=\"row\">" +
					                	banca1stOption.get(i).getBookmaker() + 
					                "</th>" +
					                "<td>" +
					                	((LeftTableRow) banca1stOption.get(i)).getOdd() +
					                "</td>" +
					                "<td>" +
				                		((LeftTableRow) banca1stOption.get(i)).getMoney() +
					                "</td>" +
					              "</tr>";
					}
					
					result += 
					  				"</tbody>" +
					  			"</table>" +
					        "</div>";
							
					result += 
			        "<div class=\"col right-col\">" +
			          "<h4 class=\"tipo-di-scommessa\">" +
			            "Punta " + betType1 +
			          "</h4>" +
			          "<table class=\"table\">" +
			            "<thead>" +
			              "<tr>" +
			                "<th scope=\"col\">" +
			                  "Bookmakers" +
			                "</th>" +
			                "<th scope=\"col\">" +
			                  "Quota" +
			                "</th>" +
			                "<th scope=\"col\">" +
			                	"% di guadagno" +
			                "</th>" +
			              "</tr>" +
			            "</thead>" +
			            "<tbody>";
					
					for (int i = 0; i < punta1stOption.size(); i++) {
							result += 
								"<tr>" +
					                "<th scope=\"row\">" +
					                	punta1stOption.get(i).getBookmaker() + 
					                "</th>" +
					                "<td>" +
					                	((RightTableRow) punta1stOption.get(i)).getOdd() +
					                "</td>" +
					                "<td>";
	
					    if (Float.parseFloat(((RightTableRow) punta1stOption.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
							result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
						}
			  			if (Float.parseFloat(((RightTableRow) punta1stOption.get(i)).getMaxPercentage().replace(",", ".")) > Float.parseFloat(((RightTableRow) punta1stOption.get(i)).getMinPercentage().replace(",", "."))) {
			  				result += "&nbsp; " + ((RightTableRow) punta1stOption.get(i)).getMaxPercentage() + " - " + ((RightTableRow) punta1stOption.get(i)).getMinPercentage() + " &nbsp;";
			  			}	else {
			  				result += "&nbsp; " + ((RightTableRow) punta1stOption.get(i)).getMaxPercentage() + " &nbsp;";
			  			}
						if (Float.parseFloat(((RightTableRow) punta1stOption.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
							result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
						}
					                
							result += "</td>" +
									  "</tr>";
					}

					result += 
					          "</tbody>" +
					        "</table>" +
					      "</div>" +
					    "</div>" +
					    "<br />" ;
				}
				
				if (banca2ndOption.size() > 0) {	
					result += 
							"<div class=\"row\">" +
					        "<div class=\"col left-col\">" +
					          "<h4 class=\"tipo-di-scommessa\">" +
					            "Banca " + betType2 +
					          "</h4>" +
					          "<table class=\"table\">" +
					            "<thead>" +
					              "<tr>" +
					                "<th scope=\"col\">" +
					                  "Bookmakers" + 
					                "</th>" +
					                "<th scope=\"col\">" +
					                  "Quota" +
					                "</th>" +
					                "<th scope=\"col\">" +
					                  "Liquidita'" +
					                "</th>" +
					              "</tr>" +
					            "</thead>" +
					            "<tbody>";
							
							for (int i = 0; i < banca2ndOption.size(); i++) {
									result += 
										"<tr>" +
							                "<th scope=\"row\">" +
							                	banca2ndOption.get(i).getBookmaker() + 
							                "</th>" +
							                "<td>" +
							                	((LeftTableRow) banca2ndOption.get(i)).getOdd() +
							                "</td>" +
							                "<td>" +
						                		((LeftTableRow) banca2ndOption.get(i)).getMoney() +
							                "</td>" +
							              "</tr>";
							}
							
							result += 
							  		"</tbody>" +
							        "</table>" +
							        "</div>";
									
							result += 
					        "<div class=\"col right-col\">" +
					          "<h4 class=\"tipo-di-scommessa\">" +
					            "Punta " + betType2 +
					          "</h4>" +
					          "<table class=\"table\">" +
					            "<thead>" +
					              "<tr>" +
					                "<th scope=\"col\">" +
					                  "Bookmakers" +
					                "</th>" +
					                "<th scope=\"col\">" +
					                  "Quota" +
					                "</th>" +
					                "<th scope=\"col\">" +
					                	"% di guadagno" +
					                "</th>" +
					              "</tr>" +
					            "</thead>" +
					            "<tbody>";
							
							for (int i = 0; i < punta2ndOption.size(); i++) {
									result += 
										"<tr>" +
							                "<th scope=\"row\">" +
							                	punta2ndOption.get(i).getBookmaker() + 
							                "</th>" +
							                "<td>" +
							                	((RightTableRow) punta2ndOption.get(i)).getOdd() +
							                "</td>" +
							                "<td>";
									
							    if (Float.parseFloat(((RightTableRow) punta2ndOption.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
									result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
								}
							             
					  			if (Float.parseFloat(((RightTableRow) punta2ndOption.get(i)).getMaxPercentage().replace(",", ".")) > Float.parseFloat(((RightTableRow) punta2ndOption.get(i)).getMinPercentage().replace(",", "."))) {
					  				result += "&nbsp; " + ((RightTableRow) punta2ndOption.get(i)).getMaxPercentage() + " - " + ((RightTableRow) punta2ndOption.get(i)).getMinPercentage() + " &nbsp;";
					  			}	else {
					  				result += "&nbsp; " + ((RightTableRow) punta2ndOption.get(i)).getMaxPercentage() + " &nbsp;";
					  			}
								
								if (Float.parseFloat(((RightTableRow) punta2ndOption.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
									result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
								}
	
						                		
							               result +=  "</td>" +
							              "</tr>";
							}
							
							result += 
							          "</tbody>" +
							        "</table>" +
							      "</div>" +
							    "</div>" +
							    "<br />" ;
				}
				
				if (banca3rdOption.size() > 0) {
										
					result += 
							"<div class=\"row\">" +
					        "<div class=\"col left-col\">" +
					          "<h4 class=\"tipo-di-scommessa\">" +
					            "Banca " + betType3 + 
					          "</h4>" +
					          "<table class=\"table\">" +
					            "<thead>" +
					              "<tr>" +
					                "<th scope=\"col\">" +
					                  "Bookmakers" + 
					                "</th>" +
					                "<th scope=\"col\">" +
					                  "Quota" +
					                "</th>" +
					                "<th scope=\"col\">" +
					                  "Liquidita'" +
					                "</th>" +
					              "</tr>" +
					            "</thead>" +
					            "<tbody>";
							
							for (int i = 0; i < banca3rdOption.size(); i++) {
									result += 
										"<tr>" +
							                "<th scope=\"row\">" +
							                banca3rdOption.get(i).getBookmaker() + 
							                "</th>" +
							                "<td>" +
							                ((LeftTableRow) banca3rdOption.get(i)).getOdd() +
							                "</td>" +
							                "<td>" +
							                ((LeftTableRow) banca3rdOption.get(i)).getMoney() +
							                "</td>" +
							              "</tr>";
							}
							
							result += 
							  		"</tbody>" +
							        "</table>" +
							        "</div>";									
							result += 
					        "<div class=\"col right-col\">" +
					          "<h4 class=\"tipo-di-scommessa\">" +
					            "Punta " + betType3 +
					          "</h4>" +
					          "<table class=\"table\">" +
					            "<thead>" +
					              "<tr>" +
					                "<th scope=\"col\">" +
					                  "Bookmakers" +
					                "</th>" +
					                "<th scope=\"col\">" +
					                  "Quota" +
					                "</th>" +
					                "<th scope=\"col\">" +
					                	"% di guadagno" +
					                "</th>" +
					              "</tr>" +
					            "</thead>" +
					            "<tbody>";
							
							for (int i = 0; i < punta3rdOption.size(); i++) {
									result +=
										"<tr>" +
							                "<th scope=\"row\">" +
							                	punta3rdOption.get(i).getBookmaker() + 
							                "</th>" +
							                "<td>" +
							                	((RightTableRow) punta3rdOption.get(i)).getOdd() +
							                "</td>" +
							                "<td>";
									
									if (Float.parseFloat(((RightTableRow) punta3rdOption.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
										result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
									}
						  			if (Float.parseFloat(((RightTableRow) punta3rdOption.get(i)).getMaxPercentage().replace(",", ".")) > Float.parseFloat(((RightTableRow) punta3rdOption.get(i)).getMinPercentage().replace(",", "."))) {
						  				result += "&nbsp; " + ((RightTableRow) punta3rdOption.get(i)).getMaxPercentage() + " - " + ((RightTableRow) punta3rdOption.get(i)).getMinPercentage() + " &nbsp;";
						  			}	else {
						  				result += "&nbsp; " + ((RightTableRow) punta3rdOption.get(i)).getMaxPercentage() + " &nbsp;";
						  			}
									if (Float.parseFloat(((RightTableRow) punta3rdOption.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
										result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
									}
									
									result += "</td>" +
							              "</tr>";
							}

							result += 
							          "</tbody>" +
							        "</table>" +
							      "</div>" +
							    "</div>" +
							    "<br />" ;
				}
				
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
}