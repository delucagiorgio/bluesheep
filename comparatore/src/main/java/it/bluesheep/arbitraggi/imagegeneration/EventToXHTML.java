package it.bluesheep.arbitraggi.imagegeneration;

import java.util.ArrayList;
import java.util.List;

public class EventToXHTML {
	private final String bootstrapPath = "../xhtml/bootstrap.min.css";
	private final String mystylePath = "../xhtml/mystyle.css";
	private final String sheepPath = "../xhtml/img/bluesheep.png";
	private final String footballPath = "../xhtml/img/soccer-ball.png";
	private final String tennisballPath = "../xhtml/img/tennis-ball.png";
	private final String missilePath = "../xhtml/img/missile.png";
	
	private final float MISSILE_TRASHOLD = 25;

	public String convert (Event event, int index, int tot) {
				
		String result = 
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\" >" +
		"<html xmlns=\"http://www.bluesheep.it\">" +
		  "<head>" +
		    "<meta content=\"\" />" +
		    "<title>" +
		      "Bluesheep" +
		    "</title>" +
		    "<meta name=\"description\" content=\"Arbitraggi\" />" +
		      "<link rel=\"stylesheet\" href=\"" + bootstrapPath + "\" />" +
		      "<link rel=\"stylesheet\" href=\"" + mystylePath + "\" />" +
		  "</head>" +
		  "<body>" +
		    "<div class=\"container\">" +
		      "<div class=\"row header\">" +
		        "<div class=\"col goat-container header-side-col\">";
				
				if (event.getExtractionTime() != null) {
					result += "<h4 class=\"extraction-time\">" + 
					          	"Ora estrazione: <br />" + event.getExtractionTime() + 
					          "</h4>";
				}
				result += 
				"<span class=\"helper\"></span>" +
		          "<img class=\"big-goat\" src=\"" + sheepPath + "\" alt=\"Bluesheep\" />" +
		          "<img class=\"big-goat\" src=\"" + sheepPath + "\" alt=\"Bluesheep\" />" +
		          "<img class=\"big-goat\" src=\"" + sheepPath + "\" alt=\"Bluesheep\" />" +
		          "<br />" +
		          "<br />" +
		          "<br />" +
		          "<br />" +
		        "</div>" +
		        "<div class=\"col title-container header-main-col\">" +
		          "<h1>" +
		            event.getParticipant1() + " - " + event.getParticipant2() +
		          "</h1>" +
		          "<h2>" +
		            event.getChampionship() + 
		          "</h2>" +
		          "<h4>" +
		            event.getDate() +
		          "</h4>" +
		        "</div>" +
		        "<div class=\"col ball-container header-side-col\">" +
			        "<h4 class=\"extraction-time\">" + 
			        "            Segnalazione numero: <br />" + index + "/" + tot + 
			        "</h4>   " +
			        "<span class=\"helper\"></span>";
		
		if (event.getSport().equals("CALCIO")) {
			result += "<img class=\"soccer-ball\" src=\"" + footballPath + "\" alt=\"Soccer\" />";
			result += "<img class=\"soccer-ball\" src=\"" + footballPath + "\" alt=\"Soccer\" />";
			result += "<img class=\"soccer-ball\" src=\"" + footballPath + "\" alt=\"Soccer\" />";

		} else {
			result += "<img class=\"tennis-ball\" src=\"" + tennisballPath + "\" alt=\"Tennis\" />";
			result += "<img class=\"tennis-ball\" src=\"" + tennisballPath + "\" alt=\"Tennis\" />";
			result += "<img class=\"tennis-ball\" src=\"" + tennisballPath + "\" alt=\"Tennis\" />";
		}
		
		result += 
		        "</div>" +
		      "</div>" +
		    "</div>";
		
		String betType = "";
		List<String> leftBookmakers = new ArrayList<String>();
		List<String> rightBookmakers = new ArrayList<String>();
		List<String> leftBookmakersOdd = new ArrayList<String>();
		List<String> rightBookmakersOdd = new ArrayList<String>();
		List<String> money1 = new ArrayList<String>();
		List<String> money2 = new ArrayList<String>();
		List<String> percentages = new ArrayList<String>();
		List<String> punta1 = new ArrayList<String>();
		List<String> banca2 = new ArrayList<String>();
		List<String> punta2 = new ArrayList<String>();
		List<String> banca1 = new ArrayList<String>();
		List<String> punta1Odd = new ArrayList<String>();
		List<String>punta2Odd = new ArrayList<String>();
		List<String> banca1Odd = new ArrayList<String>();
		List<String> banca2Odd = new ArrayList<String>();
		List<String> percentages1 = new ArrayList<String>();
		List<String> percentages2 = new ArrayList<String>();
		List<String> punta3 = new ArrayList<String>();
		List<String> banca3 = new ArrayList<String>();
		List<String> money3 = new ArrayList<String>();
		List<String> percentages3 = new ArrayList<String>();
		List<String> banca3Odd = new ArrayList<String>();
		List<String> punta3Odd = new ArrayList<String>();

		if(event.getSport().equals("CALCIO")) {
	
			// 1 X 2 PUNTA BANCA
			betType = "1X2";
			for (int k = 0; k < event.getBet_1X2().size(); k++) {
				
				if (event.getBet_1X2().get(k).getBetType1().equals(event.getBet_1X2().get(k).getBetType2())) {
	
					// PUNTA BANCA
					if (event.getBet_1X2().get(k).getBetType1().equals("1")) {
						// PUNTA 2 BANCA 2
						if (!banca1.contains(event.getBet_1X2().get(k).getBookmaker2())) {
							banca1.add(event.getBet_1X2().get(k).getBookmaker2());
							banca1Odd.add(event.getBet_1X2().get(k).getOdd2());
							money1.add(event.getBet_1X2().get(k).getMoney2());
						}
						
						if (!punta1.contains(event.getBet_1X2().get(k).getBookmaker1())) {
							punta1.add(event.getBet_1X2().get(k).getBookmaker1());
							punta1Odd.add(event.getBet_1X2().get(k).getOdd1());
							percentages1.add(event.getBet_1X2().get(k).getIncomePercentage());
						}						
					} else if (event.getBet_1X2().get(k).getBetType1().equals("X")) {
						// PUNTA 2 BANCA 2
						if (!banca3.contains(event.getBet_1X2().get(k).getBookmaker2())) {
							banca3.add(event.getBet_1X2().get(k).getBookmaker2());
							banca3Odd.add(event.getBet_1X2().get(k).getOdd2());
							money3.add(event.getBet_1X2().get(k).getMoney2());
						}
						
						if (!punta3.contains(event.getBet_1X2().get(k).getBookmaker1())) {
							punta3.add(event.getBet_1X2().get(k).getBookmaker1());
							punta3Odd.add(event.getBet_1X2().get(k).getOdd1());
							percentages3.add(event.getBet_1X2().get(k).getIncomePercentage());
						}						
					} else {
						// PUNTA 1 BANCA 1
						if (!banca2.contains(event.getBet_1X2().get(k).getBookmaker2())) {
							banca2.add(event.getBet_1X2().get(k).getBookmaker2());
							banca2Odd.add(event.getBet_1X2().get(k).getOdd2());
							money2.add(event.getBet_1X2().get(k).getMoney2());
						}
						
						if (!punta2.contains(event.getBet_1X2().get(k).getBookmaker1())) {
							punta2.add(event.getBet_1X2().get(k).getBookmaker1());
							punta2Odd.add(event.getBet_1X2().get(k).getOdd1());
							percentages2.add(event.getBet_1X2().get(k).getIncomePercentage());
						}				
					}
				}
			}
			
			if (event.getBet_1X2().size() > 0) {
				result += addBetXhtml(betType, leftBookmakers, rightBookmakers, leftBookmakersOdd, rightBookmakersOdd, percentages, punta1, banca1, punta2, banca2, punta1Odd, banca1Odd, punta2Odd, banca2Odd, percentages1, percentages2, money1, money2,
						punta3, banca3, punta3Odd, banca3Odd, percentages3, money3);				
			}
			
			betType = "GOAL vs NO GOAL";
			leftBookmakers = new ArrayList<String>();
			rightBookmakers = new ArrayList<String>();
			leftBookmakersOdd = new ArrayList<String>();
			rightBookmakersOdd = new ArrayList<String>();
			punta1 = new ArrayList<String>();
			banca2 = new ArrayList<String>();
			punta2 = new ArrayList<String>();
			banca1 = new ArrayList<String>();
			punta1Odd = new ArrayList<String>();
			punta2Odd = new ArrayList<String>();
			banca1Odd = new ArrayList<String>();
			banca2Odd = new ArrayList<String>();
			percentages1 = new ArrayList<String>();
			percentages2 = new ArrayList<String>();
			money1 = new ArrayList<String>();
			money2 = new ArrayList<String>();
			percentages = new ArrayList<String>();
			punta3 = new ArrayList<String>();
			banca3 = new ArrayList<String>();
			money3 = new ArrayList<String>();
			percentages3 = new ArrayList<String>();
			banca3Odd = new ArrayList<String>();
			punta3Odd = new ArrayList<String>();

			for (int k = 0; k < event.getBet_GGNG().size(); k++) {
				
				if (event.getBet_GGNG().get(k).getBetType1().equals(event.getBet_GGNG().get(k).getBetType2())) {
								
					// PUNTA BANCA
					if (!event.getBet_GGNG().get(k).getBetType1().equals("GOAL")) {
						// PUNTA 2 BANCA 2
						if (!banca2.contains(event.getBet_GGNG().get(k).getBookmaker2())) {
							banca2.add(event.getBet_GGNG().get(k).getBookmaker2());
							banca2Odd.add(event.getBet_GGNG().get(k).getOdd2());
							money2.add(event.getBet_GGNG().get(k).getMoney2());
						}
						
						if (!punta2.contains(event.getBet_GGNG().get(k).getBookmaker1())) {
							punta2.add(event.getBet_GGNG().get(k).getBookmaker1());
							punta2Odd.add(event.getBet_GGNG().get(k).getOdd1());
							percentages2.add(event.getBet_GGNG().get(k).getIncomePercentage());
						}						
					} else {
						// PUNTA 1 BANCA 1
						if (!banca1.contains(event.getBet_GGNG().get(k).getBookmaker2())) {
							banca1.add(event.getBet_GGNG().get(k).getBookmaker2());
							banca1Odd.add(event.getBet_GGNG().get(k).getOdd2());
							money1.add(event.getBet_GGNG().get(k).getMoney2());
						}
						
						if (!punta1.contains(event.getBet_GGNG().get(k).getBookmaker1())) {
							punta1.add(event.getBet_GGNG().get(k).getBookmaker1());
							punta1Odd.add(event.getBet_GGNG().get(k).getOdd1());
							percentages1 .add(event.getBet_GGNG().get(k).getIncomePercentage());
						}				
					}
				} else {
									
					if (event.getBet_GGNG().get(k).getBetType1().equals("GOAL") && !leftBookmakers.contains(event.getBet_GGNG().get(k).getBookmaker1())) {
						leftBookmakers.add(event.getBet_GGNG().get(k).getBookmaker1());
						leftBookmakersOdd.add(event.getBet_GGNG().get(k).getOdd1());
					} else if (event.getBet_GGNG().get(k).getBetType2().equals("GOAL") && !leftBookmakers.contains(event.getBet_GGNG().get(k).getBookmaker2())) {
						leftBookmakers.add(event.getBet_GGNG().get(k).getBookmaker2());
						leftBookmakersOdd.add(event.getBet_GGNG().get(k).getOdd2());
					}
					
					if (event.getBet_GGNG().get(k).getBetType1().equals("GOAL") && !rightBookmakers.contains(event.getBet_GGNG().get(k).getBookmaker2())) {
						rightBookmakers.add(event.getBet_GGNG().get(k).getBookmaker2());
						rightBookmakersOdd.add(event.getBet_GGNG().get(k).getOdd2());
						
						float min = Float.parseFloat(event.getBet_GGNG().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_GGNG().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_GGNG().size(); m++) {
							if (!event.getBet_GGNG().get(m).getBetType1().equals(event.getBet_GGNG().get(m).getBetType2()) && k != m && 
									((event.getBet_GGNG().get(m).getBookmaker2().equals(event.getBet_GGNG().get(k).getBookmaker2()) && !event.getBet_GGNG().get(m).getBetType2().equals("GOAL")) ||
										(event.getBet_GGNG().get(m).getBookmaker1().equals(event.getBet_GGNG().get(k).getBookmaker2()) && !event.getBet_GGNG().get(m).getBetType1().equals("GOAL")))) {
									if (Float.parseFloat(event.getBet_GGNG().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_GGNG().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_GGNG().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_GGNG().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}	
						if (max != min) {
							percentages.add(max + " - " + min);		

						} else {
							percentages.add(max + "");
						}
					} else if (event.getBet_GGNG().get(k).getBetType2().equals("GOAL") && !rightBookmakers.contains(event.getBet_GGNG().get(k).getBookmaker1())) {
					
						rightBookmakers.add(event.getBet_GGNG().get(k).getBookmaker1());
						rightBookmakersOdd.add(event.getBet_GGNG().get(k).getOdd1());
						
						float min = Float.parseFloat(event.getBet_GGNG().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_GGNG().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_GGNG().size(); m++) {
							if (!event.getBet_GGNG().get(m).getBetType1().equals(event.getBet_GGNG().get(m).getBetType2()) && k != m && 
									((event.getBet_GGNG().get(m).getBookmaker2().equals(event.getBet_GGNG().get(k).getBookmaker1()) && !event.getBet_GGNG().get(m).getBetType2().equals("GOAL")) ||
										(event.getBet_GGNG().get(m).getBookmaker1().equals(event.getBet_GGNG().get(k).getBookmaker1()) && !event.getBet_GGNG().get(m).getBetType1().equals("GOAL")))) {
									if (Float.parseFloat(event.getBet_GGNG().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_GGNG().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_GGNG().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_GGNG().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						if (max != min) {
							percentages.add(max + " - " + min);
						} else {
							percentages.add(max + "");
						}
					}
				}
			}
			
			if (event.getBet_GGNG().size() > 0) {
				result += addBetXhtml(betType, leftBookmakers, rightBookmakers, leftBookmakersOdd, rightBookmakersOdd, percentages, punta1, banca1, punta2, banca2, punta1Odd, banca1Odd, punta2Odd, banca2Odd, percentages1, percentages2, money1, money2,
						punta3, banca3, punta3Odd, banca3Odd, percentages3, money3);				
			}
					
			betType = "UNDER/OVER 0.5";
			leftBookmakers = new ArrayList<String>();
			rightBookmakers = new ArrayList<String>();
			leftBookmakersOdd = new ArrayList<String>();
			rightBookmakersOdd = new ArrayList<String>();
			punta1 = new ArrayList<String>();
			banca2 = new ArrayList<String>();
			punta2 = new ArrayList<String>();
			banca1 = new ArrayList<String>();
			punta1Odd = new ArrayList<String>();
			punta2Odd = new ArrayList<String>();
			banca1Odd = new ArrayList<String>();
			banca2Odd = new ArrayList<String>();
			percentages1 = new ArrayList<String>();
			percentages2 = new ArrayList<String>();
			money1 = new ArrayList<String>();
			money2 = new ArrayList<String>();
			percentages = new ArrayList<String>();
	
			for (int k = 0; k < event.getBet_UO05().size(); k++) {	
				if (event.getBet_UO05().get(k).getBetType1().equals(event.getBet_UO05().get(k).getBetType2())) {
					// PUNTA BANCA
					if (event.getBet_UO05().get(k).getBetType1().equals("O_0.5")) {
						// PUNTA 2 BANCA 2
						if (!banca2.contains(event.getBet_UO05().get(k).getBookmaker2())) {
							banca2.add(event.getBet_UO05().get(k).getBookmaker2());
							banca2Odd.add(event.getBet_UO05().get(k).getOdd2());
							money2.add(event.getBet_UO05().get(k).getMoney2());
						}
						
						if (!punta2.contains(event.getBet_UO05().get(k).getBookmaker1())) {
							punta2.add(event.getBet_UO05().get(k).getBookmaker1());
							punta2Odd.add(event.getBet_UO05().get(k).getOdd1());
							percentages2.add(event.getBet_UO05().get(k).getIncomePercentage());
						}						
					} else {
						// PUNTA 1 BANCA 1
						if (!banca1.contains(event.getBet_UO05().get(k).getBookmaker2())) {
							banca1.add(event.getBet_UO05().get(k).getBookmaker2());
							banca1Odd.add(event.getBet_UO05().get(k).getOdd2());
							money1.add(event.getBet_UO05().get(k).getMoney2());
						}
						
						if (!punta1.contains(event.getBet_UO05().get(k).getBookmaker1())) {
							punta1.add(event.getBet_UO05().get(k).getBookmaker1());
							punta1Odd.add(event.getBet_UO05().get(k).getOdd1());
							percentages1 .add(event.getBet_UO05().get(k).getIncomePercentage());
						}				
					}
				} else {
					if (event.getBet_UO05().get(k).getBetType1().equals("O_0.5") && !leftBookmakers.contains(event.getBet_UO05().get(k).getBookmaker1())) {
						leftBookmakers.add(event.getBet_UO05().get(k).getBookmaker1());
						leftBookmakersOdd.add(event.getBet_UO05().get(k).getOdd1());
					} else if (event.getBet_UO05().get(k).getBetType2().equals("U_0.5") && !leftBookmakers.contains(event.getBet_UO05().get(k).getBookmaker2())) {
						leftBookmakers.add(event.getBet_UO05().get(k).getBookmaker2());
						leftBookmakersOdd.add(event.getBet_UO05().get(k).getOdd2());
					}
					
					if (event.getBet_UO05().get(k).getBetType1().equals("U_0.5") && !rightBookmakers.contains(event.getBet_UO05().get(k).getBookmaker2())) {
						rightBookmakers.add(event.getBet_UO05().get(k).getBookmaker2());
						rightBookmakersOdd.add(event.getBet_UO05().get(k).getOdd2());
						
						float min = Float.parseFloat(event.getBet_UO05().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO05().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO05().size(); m++) {
							if (!event.getBet_UO05().get(m).getBetType1().equals(event.getBet_UO05().get(m).getBetType2()) && k != m && 
									((event.getBet_UO05().get(m).getBookmaker2().equals(event.getBet_UO05().get(k).getBookmaker2()) && !event.getBet_UO05().get(m).getBetType2().equals("U_0.5")) ||
										(event.getBet_UO05().get(m).getBookmaker1().equals(event.getBet_UO05().get(k).getBookmaker2()) && !event.getBet_UO05().get(m).getBetType1().equals("U_0.5")))) {
									if (Float.parseFloat(event.getBet_UO05().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO05().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO05().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO05().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}	
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
					} else if (event.getBet_UO05().get(k).getBetType2().equals("U_0.5") && !rightBookmakers.contains(event.getBet_UO05().get(k).getBookmaker1())) {
						rightBookmakers.add(event.getBet_UO05().get(k).getBookmaker1());
						rightBookmakersOdd.add(event.getBet_UO05().get(k).getOdd1());

						float min = Float.parseFloat(event.getBet_UO05().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO05().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO05().size(); m++) {
							if (!event.getBet_UO05().get(m).getBetType1().equals(event.getBet_UO05().get(m).getBetType2()) && k != m && 
									((event.getBet_UO05().get(m).getBookmaker2().equals(event.getBet_UO05().get(k).getBookmaker1()) && !event.getBet_UO05().get(m).getBetType2().equals("U_0.5")) ||
										(event.getBet_UO05().get(m).getBookmaker1().equals(event.getBet_UO05().get(k).getBookmaker1()) && !event.getBet_UO05().get(m).getBetType1().equals("U_0.5")))) {
									if (Float.parseFloat(event.getBet_UO05().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO05().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO05().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO05().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}	
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
					}				
				}
			}
	
			if (event.getBet_UO05().size() > 0) {
				result += addBetXhtml(betType, leftBookmakers, rightBookmakers, leftBookmakersOdd, rightBookmakersOdd, percentages, punta1, banca1, punta2, banca2, punta1Odd, banca1Odd, punta2Odd, banca2Odd, percentages1, percentages2, money1, money2,
						punta3, banca3, punta3Odd, banca3Odd, percentages3, money3);				
			}			
			
			betType = "UNDER/OVER 1.5";
			leftBookmakers = new ArrayList<String>();
			rightBookmakers = new ArrayList<String>();
			leftBookmakersOdd = new ArrayList<String>();
			rightBookmakersOdd = new ArrayList<String>();
			punta1 = new ArrayList<String>();
			banca2 = new ArrayList<String>();
			punta2 = new ArrayList<String>();
			banca1 = new ArrayList<String>();
			punta1Odd = new ArrayList<String>();
			punta2Odd = new ArrayList<String>();
			banca1Odd = new ArrayList<String>();
			banca2Odd = new ArrayList<String>();
			percentages1 = new ArrayList<String>();
			percentages2 = new ArrayList<String>();
			money1 = new ArrayList<String>();
			money2 = new ArrayList<String>();
			percentages = new ArrayList<String>();

			for (int k = 0; k < event.getBet_UO15().size(); k++) {		
				if (event.getBet_UO15().get(k).getBetType1().equals(event.getBet_UO15().get(k).getBetType2())) {
					// PUNTA BANCA
					if (event.getBet_UO15().get(k).getBetType1().equals("O_1.5")) {
						// PUNTA 2 BANCA 2
						if (!banca2.contains(event.getBet_UO15().get(k).getBookmaker2())) {
							banca2.add(event.getBet_UO15().get(k).getBookmaker2());
							banca2Odd.add(event.getBet_UO15().get(k).getOdd2());
							money2.add(event.getBet_UO15().get(k).getMoney2());
						}
						
						if (!punta2.contains(event.getBet_UO15().get(k).getBookmaker1())) {
							punta2.add(event.getBet_UO15().get(k).getBookmaker1());
							punta2Odd.add(event.getBet_UO15().get(k).getOdd1());
							percentages2.add(event.getBet_UO15().get(k).getIncomePercentage());
						}						
					} else {
						// PUNTA 1 BANCA 1
						if (!banca1.contains(event.getBet_UO15().get(k).getBookmaker2())) {
							banca1.add(event.getBet_UO15().get(k).getBookmaker2());
							banca1Odd.add(event.getBet_UO15().get(k).getOdd2());
							money1.add(event.getBet_UO15().get(k).getMoney2());
						}
						
						if (!punta1.contains(event.getBet_UO15().get(k).getBookmaker1())) {
							punta1.add(event.getBet_UO15().get(k).getBookmaker1());
							punta1Odd.add(event.getBet_UO15().get(k).getOdd1());
							percentages1 .add(event.getBet_UO15().get(k).getIncomePercentage());
						}				
					}
				} else {
					if (event.getBet_UO15().get(k).getBetType1().equals("U_1.5") && !leftBookmakers.contains(event.getBet_UO15().get(k).getBookmaker1())) {
						leftBookmakers.add(event.getBet_UO15().get(k).getBookmaker1());
						leftBookmakersOdd.add(event.getBet_UO15().get(k).getOdd1());
					} else if (event.getBet_UO15().get(k).getBetType2().equals("U_1.5") && !leftBookmakers.contains(event.getBet_UO15().get(k).getBookmaker2())) {
						leftBookmakers.add(event.getBet_UO15().get(k).getBookmaker2());
						leftBookmakersOdd.add(event.getBet_UO15().get(k).getOdd2());
					}
					
					if (event.getBet_UO15().get(k).getBetType1().equals("U_1.5") && !rightBookmakers.contains(event.getBet_UO15().get(k).getBookmaker2())) {
						rightBookmakers.add(event.getBet_UO15().get(k).getBookmaker2());
						rightBookmakersOdd.add(event.getBet_UO15().get(k).getOdd2());
						
						float min = Float.parseFloat(event.getBet_UO15().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO15().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO15().size(); m++) {
							if (!event.getBet_UO15().get(m).getBetType1().equals(event.getBet_UO15().get(m).getBetType2()) && k != m && 
									((event.getBet_UO15().get(m).getBookmaker2().equals(event.getBet_UO15().get(k).getBookmaker2()) && !event.getBet_UO15().get(m).getBetType2().equals("U_1.5")) ||
										(event.getBet_UO15().get(m).getBookmaker1().equals(event.getBet_UO15().get(k).getBookmaker2()) && !event.getBet_UO15().get(m).getBetType1().equals("U_1.5")))) {
									if (Float.parseFloat(event.getBet_UO15().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO15().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO15().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO15().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
					} else if (event.getBet_UO15().get(k).getBetType2().equals("U_1.5") && !rightBookmakers.contains(event.getBet_UO15().get(k).getBookmaker1())) {
						rightBookmakers.add(event.getBet_UO15().get(k).getBookmaker1());
						rightBookmakersOdd.add(event.getBet_UO15().get(k).getOdd1());

						float min = Float.parseFloat(event.getBet_UO15().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO15().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO15().size(); m++) {
							if (!event.getBet_UO15().get(m).getBetType1().equals(event.getBet_UO15().get(m).getBetType2()) && k != m && 
									((event.getBet_UO15().get(m).getBookmaker2().equals(event.getBet_UO15().get(k).getBookmaker1()) && !event.getBet_UO15().get(m).getBetType2().equals("U_1.5")) ||
										(event.getBet_UO15().get(m).getBookmaker1().equals(event.getBet_UO15().get(k).getBookmaker1()) && !event.getBet_UO15().get(m).getBetType1().equals("U_1.5")))) {
									if (Float.parseFloat(event.getBet_UO15().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO15().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO15().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO15().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
					} 				
				}
			}
			
			if (event.getBet_UO15().size() > 0) {
				result += addBetXhtml(betType, leftBookmakers, rightBookmakers, leftBookmakersOdd, rightBookmakersOdd, percentages, punta1, banca1, punta2, banca2, punta1Odd, banca1Odd, punta2Odd, banca2Odd, percentages1, percentages2, money1, money2,
						punta3, banca3, punta3Odd, banca3Odd, percentages3, money3);				
			}
			
			betType = "UNDER/OVER 2.5";
			leftBookmakers = new ArrayList<String>();
			rightBookmakers = new ArrayList<String>();
			leftBookmakersOdd = new ArrayList<String>();
			rightBookmakersOdd = new ArrayList<String>();
			punta1 = new ArrayList<String>();
			banca2 = new ArrayList<String>();
			punta2 = new ArrayList<String>();
			banca1 = new ArrayList<String>();
			punta1Odd = new ArrayList<String>();
			punta2Odd = new ArrayList<String>();
			banca1Odd = new ArrayList<String>();
			banca2Odd = new ArrayList<String>();
			percentages1 = new ArrayList<String>();
			percentages2 = new ArrayList<String>();
			money1 = new ArrayList<String>();
			money2 = new ArrayList<String>();
			percentages = new ArrayList<String>();
			
			for (int k = 0; k < event.getBet_UO25().size(); k++) {
				if (event.getBet_UO25().get(k).getBetType1().equals(event.getBet_UO25().get(k).getBetType2())) {
					// PUNTA BANCA
					if (event.getBet_UO25().get(k).getBetType1().equals("O_2.5")) {
						// PUNTA 2 BANCA 2
						if (!banca2.contains(event.getBet_UO25().get(k).getBookmaker2())) {
							banca2.add(event.getBet_UO25().get(k).getBookmaker2());
							banca2Odd.add(event.getBet_UO25().get(k).getOdd2());
							money2.add(event.getBet_UO25().get(k).getMoney2());
						}
						
						if (!punta2.contains(event.getBet_UO25().get(k).getBookmaker1())) {
							punta2.add(event.getBet_UO25().get(k).getBookmaker1());
							punta2Odd.add(event.getBet_UO25().get(k).getOdd1());
							percentages2.add(event.getBet_UO25().get(k).getIncomePercentage());
						}						
					} else {
						// PUNTA 1 BANCA 1
						if (!banca1.contains(event.getBet_UO25().get(k).getBookmaker2())) {
							banca1.add(event.getBet_UO25().get(k).getBookmaker2());
							banca1Odd.add(event.getBet_UO25().get(k).getOdd2());
							money1.add(event.getBet_UO25().get(k).getMoney2());
						}
						
						if (!punta1.contains(event.getBet_UO25().get(k).getBookmaker1())) {
							punta1.add(event.getBet_UO25().get(k).getBookmaker1());
							punta1Odd.add(event.getBet_UO25().get(k).getOdd1());
							percentages1 .add(event.getBet_UO25().get(k).getIncomePercentage());
						}				
					}
				} else {
					if (event.getBet_UO25().get(k).getBetType1().equals("U_2.5") && !leftBookmakers.contains(event.getBet_UO25().get(k).getBookmaker1())) {
						leftBookmakers.add(event.getBet_UO25().get(k).getBookmaker1());
						leftBookmakersOdd.add(event.getBet_UO25().get(k).getOdd1());
					} else if (event.getBet_UO25().get(k).getBetType2().equals("U_2.5") && !leftBookmakers.contains(event.getBet_UO25().get(k).getBookmaker2())) {
						leftBookmakers.add(event.getBet_UO25().get(k).getBookmaker2());
						leftBookmakersOdd.add(event.getBet_UO25().get(k).getOdd2());
					}
					
					if (event.getBet_UO25().get(k).getBetType1().equals("U_2.5") && !rightBookmakers.contains(event.getBet_UO25().get(k).getBookmaker2())) {
						rightBookmakers.add(event.getBet_UO25().get(k).getBookmaker2());
						rightBookmakersOdd.add(event.getBet_UO25().get(k).getOdd2());
						
						float min = Float.parseFloat(event.getBet_UO25().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO25().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO25().size(); m++) {
							if (!event.getBet_UO25().get(m).getBetType1().equals(event.getBet_UO25().get(m).getBetType2()) && k != m && 
									((event.getBet_UO25().get(m).getBookmaker2().equals(event.getBet_UO25().get(k).getBookmaker2()) && !event.getBet_UO25().get(m).getBetType2().equals("U_2.5")) ||
										(event.getBet_UO25().get(m).getBookmaker1().equals(event.getBet_UO25().get(k).getBookmaker2()) && !event.getBet_UO25().get(m).getBetType1().equals("U_2.5")))) {
									if (Float.parseFloat(event.getBet_UO25().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO25().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO25().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO25().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
						
					} else if (event.getBet_UO25().get(k).getBetType2().equals("U_2.5") && !rightBookmakers.contains(event.getBet_UO25().get(k).getBookmaker1())) {
						rightBookmakers.add(event.getBet_UO25().get(k).getBookmaker1());
						rightBookmakersOdd.add(event.getBet_UO25().get(k).getOdd1());
						
						float min = Float.parseFloat(event.getBet_UO25().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO25().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO25().size(); m++) {
							if (!event.getBet_UO25().get(m).getBetType1().equals(event.getBet_UO25().get(m).getBetType2()) && k != m && 
									((event.getBet_UO25().get(m).getBookmaker2().equals(event.getBet_UO25().get(k).getBookmaker1()) && !event.getBet_UO25().get(m).getBetType2().equals("U_2.5")) ||
										(event.getBet_UO25().get(m).getBookmaker1().equals(event.getBet_UO25().get(k).getBookmaker1()) && !event.getBet_UO25().get(m).getBetType1().equals("U_2.5")))) {
									if (Float.parseFloat(event.getBet_UO25().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO25().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO25().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO25().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
					} 
				}
			}
			
			if (event.getBet_UO25().size() > 0) {
				result += addBetXhtml(betType, leftBookmakers, rightBookmakers, leftBookmakersOdd, rightBookmakersOdd, percentages, punta1, banca1, punta2, banca2, punta1Odd, banca1Odd, punta2Odd, banca2Odd, percentages1, percentages2, money1, money2,
						punta3, banca3, punta3Odd, banca3Odd, percentages3, money3);				
			}
			
			betType = "UNDER/OVER 3.5";
			leftBookmakers = new ArrayList<String>();
			rightBookmakers = new ArrayList<String>();
			leftBookmakersOdd = new ArrayList<String>();
			rightBookmakersOdd = new ArrayList<String>();
			punta1 = new ArrayList<String>();
			banca2 = new ArrayList<String>();
			punta2 = new ArrayList<String>();
			banca1 = new ArrayList<String>();
			punta1Odd = new ArrayList<String>();
			punta2Odd = new ArrayList<String>();
			banca1Odd = new ArrayList<String>();
			banca2Odd = new ArrayList<String>();
			percentages1 = new ArrayList<String>();
			percentages2 = new ArrayList<String>();
			money1 = new ArrayList<String>();
			money2 = new ArrayList<String>();
			percentages = new ArrayList<String>();
			
			for (int k = 0; k < event.getBet_UO35().size(); k++) {
				
				if (event.getBet_UO35().get(k).getBetType1().equals(event.getBet_UO35().get(k).getBetType2())) {
					// PUNTA BANCA
					if (event.getBet_UO35().get(k).getBetType1().equals("O_3.5")) {
						// PUNTA 2 BANCA 2
						if (!banca2.contains(event.getBet_UO35().get(k).getBookmaker2())) {
							banca2.add(event.getBet_UO35().get(k).getBookmaker2());
							banca2Odd.add(event.getBet_UO35().get(k).getOdd2());
							money2.add(event.getBet_UO35().get(k).getMoney2());
						}
						
						if (!punta2.contains(event.getBet_UO35().get(k).getBookmaker1())) {
							punta2.add(event.getBet_UO35().get(k).getBookmaker1());
							punta2Odd.add(event.getBet_UO35().get(k).getOdd1());
							percentages2.add(event.getBet_UO35().get(k).getIncomePercentage());
						}						
					} else {
						// PUNTA 1 BANCA 1
						if (!banca1.contains(event.getBet_UO35().get(k).getBookmaker2())) {
							banca1.add(event.getBet_UO35().get(k).getBookmaker2());
							banca1Odd.add(event.getBet_UO35().get(k).getOdd2());
							money1.add(event.getBet_UO35().get(k).getMoney2());
						}
						
						if (!punta1.contains(event.getBet_UO35().get(k).getBookmaker1())) {
							punta1.add(event.getBet_UO35().get(k).getBookmaker1());
							punta1Odd.add(event.getBet_UO35().get(k).getOdd1());
							percentages1 .add(event.getBet_UO35().get(k).getIncomePercentage());
						}				
					}
				} else {
					if (event.getBet_UO35().get(k).getBetType1().equals("U_3.5") && !leftBookmakers.contains(event.getBet_UO35().get(k).getBookmaker1())) {
						leftBookmakers.add(event.getBet_UO35().get(k).getBookmaker1());
						leftBookmakersOdd.add(event.getBet_UO35().get(k).getOdd1());
					} else if (event.getBet_UO35().get(k).getBetType2().equals("U_3.5") && !leftBookmakers.contains(event.getBet_UO35().get(k).getBookmaker2())) {
						leftBookmakers.add(event.getBet_UO35().get(k).getBookmaker2());
						leftBookmakersOdd.add(event.getBet_UO35().get(k).getOdd2());
					}
					
					if (event.getBet_UO35().get(k).getBetType1().equals("U_3.5") && !rightBookmakers.contains(event.getBet_UO35().get(k).getBookmaker2())) {
						rightBookmakers.add(event.getBet_UO35().get(k).getBookmaker2());
						rightBookmakersOdd.add(event.getBet_UO35().get(k).getOdd2());

						float min = Float.parseFloat(event.getBet_UO35().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO35().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO35().size(); m++) {
							if (!event.getBet_UO35().get(m).getBetType1().equals(event.getBet_UO35().get(m).getBetType2()) && k != m && 
									((event.getBet_UO35().get(m).getBookmaker2().equals(event.getBet_UO35().get(k).getBookmaker2()) && !event.getBet_UO35().get(m).getBetType2().equals("U_3.5")) ||
										(event.getBet_UO35().get(m).getBookmaker1().equals(event.getBet_UO35().get(k).getBookmaker2()) && !event.getBet_UO35().get(m).getBetType1().equals("U_3.5")))) {
									if (Float.parseFloat(event.getBet_UO35().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO35().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO35().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO35().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
						
					} else if (event.getBet_UO35().get(k).getBetType2().equals("U_3.5") && !rightBookmakers.contains(event.getBet_UO35().get(k).getBookmaker1())) {
						rightBookmakers.add(event.getBet_UO35().get(k).getBookmaker1());
						rightBookmakersOdd.add(event.getBet_UO35().get(k).getOdd1());

						float min = Float.parseFloat(event.getBet_UO35().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO35().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO35().size(); m++) {
							if (!event.getBet_UO35().get(m).getBetType1().equals(event.getBet_UO35().get(m).getBetType2()) && k != m && 
									((event.getBet_UO35().get(m).getBookmaker2().equals(event.getBet_UO35().get(k).getBookmaker1()) && !event.getBet_UO35().get(m).getBetType2().equals("U_3.5")) ||
										(event.getBet_UO35().get(m).getBookmaker1().equals(event.getBet_UO35().get(k).getBookmaker1()) && !event.getBet_UO35().get(m).getBetType1().equals("U_3.5")))) {
									if (Float.parseFloat(event.getBet_UO35().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO35().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO35().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO35().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
						
					} 
				}
			}
			
			if (event.getBet_UO35().size() > 0) {
				result += addBetXhtml(betType, leftBookmakers, rightBookmakers, leftBookmakersOdd, rightBookmakersOdd, percentages, punta1, banca1, punta2, banca2, punta1Odd, banca1Odd, punta2Odd, banca2Odd, percentages1, percentages2, money1, money2,
						punta3, banca3, punta3Odd, banca3Odd, percentages3, money3);				
			}
			
			betType = "UNDER/OVER 4.5";
			leftBookmakers = new ArrayList<String>();
			rightBookmakers = new ArrayList<String>();
			leftBookmakersOdd = new ArrayList<String>();
			rightBookmakersOdd = new ArrayList<String>();
			punta1 = new ArrayList<String>();
			banca2 = new ArrayList<String>();
			punta2 = new ArrayList<String>();
			banca1 = new ArrayList<String>();
			punta1Odd = new ArrayList<String>();
			punta2Odd = new ArrayList<String>();
			banca1Odd = new ArrayList<String>();
			banca2Odd = new ArrayList<String>();
			percentages1 = new ArrayList<String>();
			percentages2 = new ArrayList<String>();
			money1 = new ArrayList<String>();
			money2 = new ArrayList<String>();
			percentages = new ArrayList<String>();
			
			for (int k = 0; k < event.getBet_UO45().size(); k++) {
				if (event.getBet_UO45().get(k).getBetType1().equals(event.getBet_UO45().get(k).getBetType2())) {
					// PUNTA BANCA
					if (event.getBet_UO45().get(k).getBetType1().equals("O_4.5")) {
						// PUNTA 2 BANCA 2
						if (!banca2.contains(event.getBet_UO45().get(k).getBookmaker2())) {
							banca2.add(event.getBet_UO45().get(k).getBookmaker2());
							banca2Odd.add(event.getBet_UO45().get(k).getOdd2());
							money2.add(event.getBet_UO45().get(k).getMoney2());
						}
						
						if (!punta2.contains(event.getBet_UO45().get(k).getBookmaker1())) {
							punta2.add(event.getBet_UO45().get(k).getBookmaker1());
							punta2Odd.add(event.getBet_UO45().get(k).getOdd1());
							percentages2.add(event.getBet_UO45().get(k).getIncomePercentage());
						}						
					} else {
						// PUNTA 1 BANCA 1
						if (!banca1.contains(event.getBet_UO45().get(k).getBookmaker2())) {
							banca1.add(event.getBet_UO45().get(k).getBookmaker2());
							banca1Odd.add(event.getBet_UO45().get(k).getOdd2());
							money1.add(event.getBet_UO45().get(k).getMoney2());
						}
						
						if (!punta1.contains(event.getBet_UO45().get(k).getBookmaker1())) {
							punta1.add(event.getBet_UO45().get(k).getBookmaker1());
							punta1Odd.add(event.getBet_UO45().get(k).getOdd1());
							percentages1 .add(event.getBet_UO45().get(k).getIncomePercentage());
						}				
					}
				} else {
					if (event.getBet_UO45().get(k).getBetType1().equals("U_4.5") && !leftBookmakers.contains(event.getBet_UO45().get(k).getBookmaker1())) {
						leftBookmakers.add(event.getBet_UO45().get(k).getBookmaker1());
						leftBookmakersOdd.add(event.getBet_UO45().get(k).getOdd1());
					} else if (event.getBet_UO45().get(k).getBetType2().equals("U_4.5") && !leftBookmakers.contains(event.getBet_UO45().get(k).getBookmaker2())) {
						leftBookmakers.add(event.getBet_UO45().get(k).getBookmaker2());
						leftBookmakersOdd.add(event.getBet_UO45().get(k).getOdd2());
					}
					
					if (event.getBet_UO45().get(k).getBetType1().equals("U_4.5") && !rightBookmakers.contains(event.getBet_UO45().get(k).getBookmaker2())) {
						rightBookmakers.add(event.getBet_UO45().get(k).getBookmaker2());
						rightBookmakersOdd.add(event.getBet_UO45().get(k).getOdd2());

						float min = Float.parseFloat(event.getBet_UO45().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO45().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO45().size(); m++) {
							if (!event.getBet_UO45().get(m).getBetType1().equals(event.getBet_UO45().get(m).getBetType2()) && k != m && 
									((event.getBet_UO45().get(m).getBookmaker2().equals(event.getBet_UO45().get(k).getBookmaker2()) && !event.getBet_UO45().get(m).getBetType2().equals("U_4.5")) ||
										(event.getBet_UO45().get(m).getBookmaker1().equals(event.getBet_UO45().get(k).getBookmaker2()) && !event.getBet_UO45().get(m).getBetType1().equals("U_4.5")))) {
									if (Float.parseFloat(event.getBet_UO45().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO45().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO45().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO45().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
					
					} else if (event.getBet_UO45().get(k).getBetType2().equals("U_4.5") && !rightBookmakers.contains(event.getBet_UO45().get(k).getBookmaker1())) {
						rightBookmakers.add(event.getBet_UO45().get(k).getBookmaker1());
						rightBookmakersOdd.add(event.getBet_UO45().get(k).getOdd1());

						float min = Float.parseFloat(event.getBet_UO45().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO45().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO45().size(); m++) {
							if (!event.getBet_UO45().get(m).getBetType1().equals(event.getBet_UO45().get(m).getBetType2()) && k != m && 
									((event.getBet_UO45().get(m).getBookmaker2().equals(event.getBet_UO45().get(k).getBookmaker1()) && !event.getBet_UO45().get(m).getBetType2().equals("U_4.5")) ||
										(event.getBet_UO45().get(m).getBookmaker1().equals(event.getBet_UO45().get(k).getBookmaker1()) && !event.getBet_UO45().get(m).getBetType1().equals("U_4.5")))) {
									if (Float.parseFloat(event.getBet_UO45().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO45().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO45().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO45().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
					} 					
				}
			}
			
			if (event.getBet_UO45().size() > 0) {
				result += addBetXhtml(betType, leftBookmakers, rightBookmakers, leftBookmakersOdd, rightBookmakersOdd, percentages, punta1, banca1, punta2, banca2, punta1Odd, banca1Odd, punta2Odd, banca2Odd, percentages1, percentages2, money1, money2,
						punta3, banca3, punta3Odd, banca3Odd, percentages3, money3);				
			}
			
			betType = "UNDER/OVER 5.5";
			leftBookmakers = new ArrayList<String>();
			rightBookmakers = new ArrayList<String>();
			leftBookmakersOdd = new ArrayList<String>();
			rightBookmakersOdd = new ArrayList<String>();
			punta1 = new ArrayList<String>();
			banca2 = new ArrayList<String>();
			punta2 = new ArrayList<String>();
			banca1 = new ArrayList<String>();
			punta1Odd = new ArrayList<String>();
			punta2Odd = new ArrayList<String>();
			banca1Odd = new ArrayList<String>();
			banca2Odd = new ArrayList<String>();
			percentages1 = new ArrayList<String>();
			percentages2 = new ArrayList<String>();
			money1 = new ArrayList<String>();
			money2 = new ArrayList<String>();
			percentages = new ArrayList<String>();
			
			for (int k = 0; k < event.getBet_UO55().size(); k++) {
				
				if (event.getBet_UO55().get(k).getBetType1().equals(event.getBet_UO55().get(k).getBetType2())) {
					// PUNTA BANCA
					if (event.getBet_UO55().get(k).getBetType1().equals("O_5.5")) {
						// PUNTA 2 BANCA 2
						if (!banca2.contains(event.getBet_UO55().get(k).getBookmaker2())) {
							banca2.add(event.getBet_UO55().get(k).getBookmaker2());
							banca2Odd.add(event.getBet_UO55().get(k).getOdd2());
							money2.add(event.getBet_UO55().get(k).getMoney2());
						}
						
						if (!punta2.contains(event.getBet_UO55().get(k).getBookmaker1())) {
							punta2.add(event.getBet_UO55().get(k).getBookmaker1());
							punta2Odd.add(event.getBet_UO55().get(k).getOdd1());
							percentages2.add(event.getBet_UO55().get(k).getIncomePercentage());
						}						
					} else {
						// PUNTA 1 BANCA 1
						if (!banca1.contains(event.getBet_UO55().get(k).getBookmaker2())) {
							banca1.add(event.getBet_UO55().get(k).getBookmaker2());
							banca1Odd.add(event.getBet_UO55().get(k).getOdd2());
							money1.add(event.getBet_UO55().get(k).getMoney2());
						}
						
						if (!punta1.contains(event.getBet_UO55().get(k).getBookmaker1())) {
							punta1.add(event.getBet_UO55().get(k).getBookmaker1());
							punta1Odd.add(event.getBet_UO55().get(k).getOdd1());
							percentages1 .add(event.getBet_UO55().get(k).getIncomePercentage());
						}				
					}
				} else {
					if (event.getBet_UO55().get(k).getBetType1().equals("U_5.5") && !leftBookmakers.contains(event.getBet_UO55().get(k).getBookmaker1())) {
						leftBookmakers.add(event.getBet_UO55().get(k).getBookmaker1());
						leftBookmakersOdd.add(event.getBet_UO55().get(k).getOdd1());
					} else if (event.getBet_UO55().get(k).getBetType2().equals("U_5.5") && !leftBookmakers.contains(event.getBet_UO55().get(k).getBookmaker2())) {
						leftBookmakers.add(event.getBet_UO55().get(k).getBookmaker2());
						leftBookmakersOdd.add(event.getBet_UO55().get(k).getOdd2());
					}
					
					if (event.getBet_UO55().get(k).getBetType1().equals("U_5.5") && !rightBookmakers.contains(event.getBet_UO55().get(k).getBookmaker2())) {
						rightBookmakers.add(event.getBet_UO55().get(k).getBookmaker2());
						rightBookmakersOdd.add(event.getBet_UO55().get(k).getOdd2());

						float min = Float.parseFloat(event.getBet_UO55().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO55().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO55().size(); m++) {
							if (!event.getBet_UO55().get(m).getBetType1().equals(event.getBet_UO55().get(m).getBetType2()) && k != m && 
									((event.getBet_UO55().get(m).getBookmaker2().equals(event.getBet_UO55().get(k).getBookmaker2()) && !event.getBet_UO55().get(m).getBetType2().equals("U_5.5")) ||
										(event.getBet_UO55().get(m).getBookmaker1().equals(event.getBet_UO55().get(k).getBookmaker2()) && !event.getBet_UO55().get(m).getBetType1().equals("U_5.5")))) {
									if (Float.parseFloat(event.getBet_UO55().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO55().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO55().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO55().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
					} else if (event.getBet_UO55().get(k).getBetType2().equals("U_5.5") && !rightBookmakers.contains(event.getBet_UO55().get(k).getBookmaker1())) {
						rightBookmakers.add(event.getBet_UO55().get(k).getBookmaker1());
						rightBookmakersOdd.add(event.getBet_UO55().get(k).getOdd1());
						
						float min = Float.parseFloat(event.getBet_UO55().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO55().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO55().size(); m++) {
							if (!event.getBet_UO55().get(m).getBetType1().equals(event.getBet_UO55().get(m).getBetType2()) && k != m && 
									((event.getBet_UO55().get(m).getBookmaker2().equals(event.getBet_UO55().get(k).getBookmaker1()) && !event.getBet_UO55().get(m).getBetType2().equals("U_5.5")) ||
										(event.getBet_UO55().get(m).getBookmaker1().equals(event.getBet_UO55().get(k).getBookmaker1()) && !event.getBet_UO55().get(m).getBetType1().equals("U_5.5")))) {
									if (Float.parseFloat(event.getBet_UO55().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO55().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO55().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO55().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
					}
				}
			}
			
			if (event.getBet_UO55().size() > 0) {
				result += addBetXhtml(betType, leftBookmakers, rightBookmakers, leftBookmakersOdd, rightBookmakersOdd, percentages, punta1, banca1, punta2, banca2, punta1Odd, banca1Odd, punta2Odd, banca2Odd, percentages1, percentages2, money1, money2,
						punta3, banca3, punta3Odd, banca3Odd, percentages3, money3);				
			}
			
			betType = "UNDER/OVER 6.5";
			leftBookmakers = new ArrayList<String>();
			rightBookmakers = new ArrayList<String>();
			leftBookmakersOdd = new ArrayList<String>();
			rightBookmakersOdd = new ArrayList<String>();
			punta1 = new ArrayList<String>();
			banca2 = new ArrayList<String>();
			punta2 = new ArrayList<String>();
			banca1 = new ArrayList<String>();
			punta1Odd = new ArrayList<String>();
			punta2Odd = new ArrayList<String>();
			banca1Odd = new ArrayList<String>();
			banca2Odd = new ArrayList<String>();
			percentages1 = new ArrayList<String>();
			percentages2 = new ArrayList<String>();
			money1 = new ArrayList<String>();
			money2 = new ArrayList<String>();
			percentages = new ArrayList<String>();
			
			for (int k = 0; k < event.getBet_UO65().size(); k++) {
				
				if (event.getBet_UO65().get(k).getBetType1().equals(event.getBet_UO65().get(k).getBetType2())) {
					// PUNTA BANCA
					if (event.getBet_UO65().get(k).getBetType1().equals("O_6.5")) {
						// PUNTA 2 BANCA 2
						if (!banca2.contains(event.getBet_UO65().get(k).getBookmaker2())) {
							banca2.add(event.getBet_UO65().get(k).getBookmaker2());
							banca2Odd.add(event.getBet_UO65().get(k).getOdd2());
							money2.add(event.getBet_UO65().get(k).getMoney2());
						}
						
						if (!punta2.contains(event.getBet_UO65().get(k).getBookmaker1())) {
							punta2.add(event.getBet_UO65().get(k).getBookmaker1());
							punta2Odd.add(event.getBet_UO65().get(k).getOdd1());
							percentages2.add(event.getBet_UO65().get(k).getIncomePercentage());
						}						
					} else {
						// PUNTA 1 BANCA 1
						if (!banca1.contains(event.getBet_UO65().get(k).getBookmaker2())) {
							banca1.add(event.getBet_UO65().get(k).getBookmaker2());
							banca1Odd.add(event.getBet_UO65().get(k).getOdd2());
							money1.add(event.getBet_UO65().get(k).getMoney2());
						}
						
						if (!punta1.contains(event.getBet_UO65().get(k).getBookmaker1())) {
							punta1.add(event.getBet_UO65().get(k).getBookmaker1());
							punta1Odd.add(event.getBet_UO65().get(k).getOdd1());
							percentages1 .add(event.getBet_UO65().get(k).getIncomePercentage());
						}				
					}
				} else {
					if (event.getBet_UO65().get(k).getBetType1().equals("U_6.5") && !leftBookmakers.contains(event.getBet_UO65().get(k).getBookmaker1())) {
						leftBookmakers.add(event.getBet_UO65().get(k).getBookmaker1());
						leftBookmakersOdd.add(event.getBet_UO65().get(k).getOdd1());
					} else if (event.getBet_UO65().get(k).getBetType2().equals("U_6.5") && !leftBookmakers.contains(event.getBet_UO65().get(k).getBookmaker2())) {
						leftBookmakers.add(event.getBet_UO65().get(k).getBookmaker2());
						leftBookmakersOdd.add(event.getBet_UO65().get(k).getOdd2());
					}
					
					if (event.getBet_UO65().get(k).getBetType1().equals("U_6.5") && !rightBookmakers.contains(event.getBet_UO65().get(k).getBookmaker2())) {
						rightBookmakers.add(event.getBet_UO65().get(k).getBookmaker2());
						rightBookmakersOdd.add(event.getBet_UO65().get(k).getOdd2());
						
						float min = Float.parseFloat(event.getBet_UO65().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO65().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO65().size(); m++) {
							if (!event.getBet_UO65().get(m).getBetType1().equals(event.getBet_UO65().get(m).getBetType2()) && k != m && 
									((event.getBet_UO65().get(m).getBookmaker2().equals(event.getBet_UO65().get(k).getBookmaker2()) && !event.getBet_UO65().get(m).getBetType2().equals("U_6.5")) ||
										(event.getBet_UO65().get(m).getBookmaker1().equals(event.getBet_UO65().get(k).getBookmaker2()) && !event.getBet_UO65().get(m).getBetType1().equals("U_6.5")))) {
									if (Float.parseFloat(event.getBet_UO65().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO65().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO65().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO65().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
						
					} else if (event.getBet_UO65().get(k).getBetType2().equals("U_6.5") && !rightBookmakers.contains(event.getBet_UO65().get(k).getBookmaker1())) {
						rightBookmakers.add(event.getBet_UO65().get(k).getBookmaker1());
						rightBookmakersOdd.add(event.getBet_UO65().get(k).getOdd1());
						
						float min = Float.parseFloat(event.getBet_UO65().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_UO65().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_UO65().size(); m++) {
							if (!event.getBet_UO65().get(m).getBetType1().equals(event.getBet_UO65().get(m).getBetType2()) && k != m && 
									((event.getBet_UO65().get(m).getBookmaker2().equals(event.getBet_UO65().get(k).getBookmaker1()) && !event.getBet_UO65().get(m).getBetType2().equals("U_6.5")) ||
										(event.getBet_UO65().get(m).getBookmaker1().equals(event.getBet_UO65().get(k).getBookmaker1()) && !event.getBet_UO65().get(m).getBetType1().equals("U_6.5")))) {
									if (Float.parseFloat(event.getBet_UO65().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_UO65().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_UO65().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_UO65().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
					}					
				}
			}
			
			if (event.getBet_UO65().size() > 0) {
				result += addBetXhtml(betType, leftBookmakers, rightBookmakers, leftBookmakersOdd, rightBookmakersOdd, percentages, punta1, banca1, punta2, banca2, punta1Odd, banca1Odd, punta2Odd, banca2Odd, percentages1, percentages2, money1, money2,
						punta3, banca3, punta3Odd, banca3Odd, percentages3, money3);				
			}
			
		} else {
			betType = "1 vs 2";
			leftBookmakers = new ArrayList<String>();
			rightBookmakers = new ArrayList<String>();
			leftBookmakersOdd = new ArrayList<String>();
			rightBookmakersOdd = new ArrayList<String>();
			punta1 = new ArrayList<String>();
			banca2 = new ArrayList<String>();
			punta2 = new ArrayList<String>();
			banca1 = new ArrayList<String>();
			punta1Odd = new ArrayList<String>();
			punta2Odd = new ArrayList<String>();
			banca1Odd = new ArrayList<String>();
			banca2Odd = new ArrayList<String>();
			percentages1 = new ArrayList<String>();
			percentages2 = new ArrayList<String>();
			money1 = new ArrayList<String>();
			money2 = new ArrayList<String>();
			percentages = new ArrayList<String>();
			
			for (int k = 0; k < event.getBet_12().size(); k++) {
				
				if (event.getBet_12().get(k).getBetType1().equals(event.getBet_12().get(k).getBetType2())) {
					// PUNTA BANCA
					if (event.getBet_12().get(k).getBetType1().equals("2")) {
						// PUNTA 2 BANCA 2
						if (!banca2.contains(event.getBet_12().get(k).getBookmaker2())) {
							banca2.add(event.getBet_12().get(k).getBookmaker2());
							banca2Odd.add(event.getBet_12().get(k).getOdd2());
							money2.add(event.getBet_12().get(k).getMoney2());
						}
						
						if (!punta2.contains(event.getBet_12().get(k).getBookmaker1())) {
							punta2.add(event.getBet_12().get(k).getBookmaker1());
							punta2Odd.add(event.getBet_12().get(k).getOdd1());
							percentages2.add(event.getBet_12().get(k).getIncomePercentage());
						}						
					} else {
						// PUNTA 1 BANCA 1
						if (!banca1.contains(event.getBet_12().get(k).getBookmaker2())) {
							banca1.add(event.getBet_12().get(k).getBookmaker2());
							banca1Odd.add(event.getBet_12().get(k).getOdd2());
							money1.add(event.getBet_12().get(k).getMoney2());
						}
						
						if (!punta1.contains(event.getBet_12().get(k).getBookmaker1())) {
							punta1.add(event.getBet_12().get(k).getBookmaker1());
							punta1Odd.add(event.getBet_12().get(k).getOdd1());
							percentages1 .add(event.getBet_12().get(k).getIncomePercentage());
						}				
					}
				} else {
					if (event.getBet_12().get(k).getBetType1().equals("1") && !leftBookmakers.contains(event.getBet_12().get(k).getBookmaker1())) {
						leftBookmakers.add(event.getBet_12().get(k).getBookmaker1());
						leftBookmakersOdd.add(event.getBet_12().get(k).getOdd1());
					} else if (event.getBet_12().get(k).getBetType2().equals("1") && !leftBookmakers.contains(event.getBet_12().get(k).getBookmaker2())) {
						leftBookmakers.add(event.getBet_12().get(k).getBookmaker2());
						leftBookmakersOdd.add(event.getBet_12().get(k).getOdd2());
					}
					
					if (event.getBet_12().get(k).getBetType1().equals("1") && !rightBookmakers.contains(event.getBet_12().get(k).getBookmaker2())) {
						rightBookmakers.add(event.getBet_12().get(k).getBookmaker2());
						rightBookmakersOdd.add(event.getBet_12().get(k).getOdd2());

						float min = Float.parseFloat(event.getBet_12().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_12().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_12().size(); m++) {
							if (!event.getBet_12().get(m).getBetType1().equals(event.getBet_12().get(m).getBetType2()) && k != m && 
									((event.getBet_12().get(m).getBookmaker2().equals(event.getBet_12().get(k).getBookmaker2()) && !event.getBet_12().get(m).getBetType2().equals("1")) ||
										(event.getBet_12().get(m).getBookmaker1().equals(event.getBet_12().get(k).getBookmaker2()) && !event.getBet_12().get(m).getBetType1().equals("1")))) {
									if (Float.parseFloat(event.getBet_12().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_12().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_12().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_12().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
						
					} else if (event.getBet_12().get(k).getBetType2().equals("1") && !rightBookmakers.contains(event.getBet_12().get(k).getBookmaker1())) {
						rightBookmakers.add(event.getBet_12().get(k).getBookmaker1());
						rightBookmakersOdd.add(event.getBet_12().get(k).getOdd1());

						float min = Float.parseFloat(event.getBet_12().get(k).getIncomePercentage().replace(",", "."));
						float max = Float.parseFloat(event.getBet_12().get(k).getIncomePercentage().replace(",", "."));

						for (int m = 1; m < event.getBet_12().size(); m++) {
							if (!event.getBet_12().get(m).getBetType1().equals(event.getBet_12().get(m).getBetType2()) && k != m && 
									((event.getBet_12().get(m).getBookmaker2().equals(event.getBet_12().get(k).getBookmaker1()) && !event.getBet_12().get(m).getBetType2().equals("1")) ||
										(event.getBet_12().get(m).getBookmaker1().equals(event.getBet_12().get(k).getBookmaker1()) && !event.getBet_12().get(m).getBetType1().equals("1")))) {
									if (Float.parseFloat(event.getBet_12().get(m).getIncomePercentage().replace(",", ".")) > max) {
										max = Float.parseFloat(event.getBet_12().get(m).getIncomePercentage().replace(",", "."));
									} else if (Float.parseFloat(event.getBet_12().get(m).getIncomePercentage().replace(",", ".")) < min){
										min = Float.parseFloat(event.getBet_12().get(m).getIncomePercentage().replace(",", "."));
									}
							}
						}
						
						if (max != min) {
							percentages.add(max + " - " + min);							
						} else {
							percentages.add(max + "");
						}
					}				
				}
			}
			
			if (event.getBet_12().size() > 0) {
				result += addBetXhtml(betType, leftBookmakers, rightBookmakers, leftBookmakersOdd, rightBookmakersOdd, percentages, punta1, banca1, punta2, banca2, punta1Odd, banca1Odd, punta2Odd, banca2Odd, percentages1, percentages2, money1, money2,
						punta3, banca3, punta3Odd, banca3Odd, percentages3, money3);				
			}
		}	

		result += 
				"</body>" +
				"</html>";
		
		return result;
		
	}

	private String addBetXhtml(String betType, List<String> leftBookmakers, List<String> rightBookmakers,
			List<String> leftBookmakersOdd, List<String> rightBookmakersOdd, List<String> percentages, 
			List<String> punta1, List<String> banca1, List<String> punta2, List<String> banca2, List<String> punta1Odd, List<String> banca1Odd,
			List<String> punta2Odd, List<String> banca2Odd, List<String> percentages1, List<String> percentages2, List<String> money1, 
			List<String> money2, List<String> punta3, List<String> banca3, List<String> punta3Odd, List<String> banca3Odd, List<String> percentages3, List<String> money3) {
		
		String linkBet = "";
		String rightBet = "";
		
		if (betType.equals("1 vs 2")) {
			linkBet = "1";
			rightBet = "2";
		} else if (betType.equals("1X2")) { 
			linkBet = "1";
			rightBet = "2";
		} else if (betType.equals("UNDER/OVER 6.5")) {
			linkBet = "Under 6.5";
			rightBet = "Over 6.5";
		}  else if (betType.equals("UNDER/OVER 5.5")) {
			linkBet = "Under 5.5";
			rightBet = "Over 5.5";
		}  else if (betType.equals("UNDER/OVER 4.5")) {
			linkBet = "Under 4.5";
			rightBet = "Over 4.5";
		}  else if (betType.equals("UNDER/OVER 3.5")) {
			linkBet = "Under 3.5";
			rightBet = "Over 3.5";
		}  else if (betType.equals("UNDER/OVER 2.5")) {
			linkBet = "Under 2.5";
			rightBet = "Over 2.5";
		}  else if (betType.equals("UNDER/OVER 1.5")) {
			linkBet = "Under 1.5";
			rightBet = "Over 1.5";
		}  else if (betType.equals("UNDER/OVER 0.5")) {
			linkBet = "Under 0.5";
			rightBet = "Over 0.5";
		} else if (betType.equals("GOAL vs NO GOAL")) {
			linkBet = "GOAL";
			rightBet = "NO GOAL";
		} 
		
		String result = "";
		result += 
			    "<div class=\"container\">" +
			      "<div class=\"row\">" +
			        "<div class=\"col\">" +
			          "<h2 class=\"tipo-di-scommessa\">" +
			          	betType +
			          "</h2>" +
			        "</div>" +
			      "</div>";
		
		if (leftBookmakers.size() > 0) {

			result += 
			      "<div class=\"row\">" +
			        "<div class=\"col left-col\">" +
			        	"<h3 class=\"tipo-di-scommessa\">" +
			        		"Punta " + linkBet +
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
				
				for (int i = 0; i < leftBookmakers.size(); i++) {					
					result += 
						"<tr>" +
			                "<th scope=\"row\">" +
			                	leftBookmakers.get(i) + 
			                "</th>" +
			                "<td>" +
			                	leftBookmakersOdd.get(i) +
			                "</td>" +
			              "</tr>";
				}
				
				int t = 0;
				while (t + leftBookmakers.size() < rightBookmakers.size()) {
					result += "<br />";
					t++;
				}
				
		  		result += 			
			  		"</tbody>" +
			        "</table>" +
			        "</div>";
				
		  		result += 
			        "<div class=\"col right-col\">" +
		        	"<h3 class=\"tipo-di-scommessa\">" +
		        		"Punta " + rightBet +
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
		  						
		  		for (int i = 0; i < rightBookmakers.size(); i++) {		
		  			result += 
		  					"<tr>" +
				                "<th scope=\"row\">" +
				                	rightBookmakers.get(i) + 
				                "</th>" +
				                "<td>" +
				                	rightBookmakersOdd.get(i) +
				                "</td>" +
				                "<td>";
		  			
		  			if (Float.parseFloat(percentages.get(i).replace(",", ".").split(" - ")[0]) > MISSILE_TRASHOLD) {
		  				result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
		  			}
				                    
		  			result += "&nbsp; " + percentages.get(i) + " &nbsp;";
		  			
		  			if (Float.parseFloat(percentages.get(i).replace(",", ".").split(" - ")[0]) > MISSILE_TRASHOLD) {
		  				result += " <img class=\"goat\" src=\"" + missilePath + "\" alt=\"Missile\" />";
		  			}
		  			
				    result += 		"</td>" +
				              "</tr>";
				}
		  		
		  		t = 0;
				while (t + rightBookmakers.size() < leftBookmakers.size()) {
					result += "<br />";
					t++;
				}
		            
				result +=
						          "</tbody>" +
						        "</table>" +
						      "</div>" +
						    "</div>" +
						    "<br />" ;
		}
		
			if (banca1.size() > 0) {
				
				result += 			
				"<div class=\"row\">" +
		        "<div class=\"col left-col\">" +
		          "<h4 class=\"tipo-di-scommessa\">" +
		            "Banca " + linkBet +
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
		                  "Liquidit‡" +
		                "</th>" +
		              "</tr>" +
		            "</thead>" +
		            "<tbody>";
				
				for (int i = 0; i < banca1.size(); i++) {
						result += 
							"<tr>" +
				                "<th scope=\"row\">" +
				                	banca1.get(i) + 
				                "</th>" +
				                "<td>" +
				                	banca1Odd.get(i) +
				                "</td>" +
				                "<td>" +
				                	money1.get(i) +
				                "</td>" +
				              "</tr>";
				}
				
				int t = 0;
				while (t + banca1.size() < punta1.size()) {
					result += "<br />";
					t++;
				}
				
				result += 
				  				"</tbody>" +
				  			"</table>" +
				        "</div>";
						
				result += 
		        "<div class=\"col right-col\">" +
		          "<h4 class=\"tipo-di-scommessa\">" +
		            "Punta " + linkBet +
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
				
				for (int i = 0; i < punta1.size(); i++) {
						result += 
							"<tr>" +
				                "<th scope=\"row\">" +
				                	punta1.get(i) + 
				                "</th>" +
				                "<td>" +
				                	punta1Odd.get(i) +
				                "</td>" +
				                "<td>" +
				                	percentages1.get(i) +
				                "</td>" +
				              "</tr>";
				}
				
				t = 0;
				while (t + punta1.size() < banca1.size()) {
					result += "<br />";
					t++;
				}
				
				result += 
				          "</tbody>" +
				        "</table>" +
				      "</div>" +
				    "</div>" +
				    "<br />" ;
			}
			
			if (banca2.size() > 0) {	
				result += 
						"<div class=\"row\">" +
				        "<div class=\"col left-col\">" +
				          "<h4 class=\"tipo-di-scommessa\">" +
				            "Banca " + rightBet +
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
				                  "Liquidit‡" +
				                "</th>" +
				              "</tr>" +
				            "</thead>" +
				            "<tbody>";
						
						for (int i = 0; i < banca2.size(); i++) {
								result += 
									"<tr>" +
						                "<th scope=\"row\">" +
						                	banca2.get(i) + 
						                "</th>" +
						                "<td>" +
						                	banca2Odd.get(i) +
						                "</td>" +
						                "<td>" +
						                	money2.get(i) +
						                "</td>" +
						              "</tr>";
						}
						
						int t = 0;
						while (t + banca2.size() < punta2.size()) {
							result += "<br />";
							t++;
						}
						
						result += 
						  		"</tbody>" +
						        "</table>" +
						        "</div>";
								
						result += 
				        "<div class=\"col right-col\">" +
				          "<h4 class=\"tipo-di-scommessa\">" +
				            "Punta " + rightBet +
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
						
						for (int i = 0; i < punta2.size(); i++) {
								result += 
									"<tr>" +
						                "<th scope=\"row\">" +
						                	punta2.get(i) + 
						                "</th>" +
						                "<td>" +
						                	punta2Odd.get(i) +
						                "</td>" +
						                "<td>" +
						                	percentages2.get(i) +
						                "</td>" +
						              "</tr>";
						}
						
						t = 0;
						while (t + punta2.size() < banca2.size()) {
							result += "<br />";
							t++;
						}
						
						result += 
						          "</tbody>" +
						        "</table>" +
						      "</div>" +
						    "</div>" +
						    "<br />" ;
			}
			
			if (banca3.size() > 0) {
				result += 
						"<div class=\"row\">" +
				        "<div class=\"col left-col\">" +
				          "<h4 class=\"tipo-di-scommessa\">" +
				            "Banca X" +
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
				                  "Liquidit‡" +
				                "</th>" +
				              "</tr>" +
				            "</thead>" +
				            "<tbody>";
						
						for (int i = 0; i < banca3.size(); i++) {
								result += 
									"<tr>" +
						                "<th scope=\"row\">" +
						                	banca3.get(i) + 
						                "</th>" +
						                "<td>" +
						                	banca3Odd.get(i) +
						                "</td>" +
						                "<td>" +
						                	money3.get(i) +
						                "</td>" +
						              "</tr>";
						}
						
						int t = 0;
						while (t + banca3.size() < punta3.size()) {
							result += "<br />";
							t++;
						}
						
						result += 
						  		"</tbody>" +
						        "</table>" +
						        "</div>";
								
						result += 
				        "<div class=\"col right-col\">" +
				          "<h4 class=\"tipo-di-scommessa\">" +
				            "Punta X" +
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
						
						for (int i = 0; i < punta3.size(); i++) {
								result +=
									"<tr>" +
						                "<th scope=\"row\">" +
						                	punta3.get(i) + 
						                "</th>" +
						                "<td>" +
						                	punta3Odd.get(i) +
						                "</td>" +
						                "<td>" +
						                	percentages3.get(i) +
						                "</td>" +
						              "</tr>";
						}
						
						t = 0;
						while (t + punta3.size() < banca3.size()) {
							result += "<br />";
							t++;
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

			return result;
	}
}
