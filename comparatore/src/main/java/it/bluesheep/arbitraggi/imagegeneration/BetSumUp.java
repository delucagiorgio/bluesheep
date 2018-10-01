package it.bluesheep.arbitraggi.imagegeneration;

import java.util.List;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.arbitraggi.entities.BetReference;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

/**
 * Generica classe riepilogativa di una scommessa
 * @author Fabio
 *
 */
public abstract class BetSumUp {
	private String betType;
    private BetReference ref;
    private BetReference average;
	
	public BetSumUp(String betType) {
		this.betType = betType;
	}

	public String getBetType() {
		return betType;
	}

	public void setBetType(String betType) {
		this.betType = betType;
	}
	
    public abstract void addRecord(ArbsRecord arbsRecord, boolean b);
    
    public BetReference getRef() {
        return ref;
    }
 
    public BetReference getAverage() {
        return average;
    }
 
    public void setRef(BetReference ref) {
        this.ref = ref;
    }
 
    public void setAverage(BetReference average) {
        this.average = average;
    }
    
	protected String drawSingleTable(String action1, String betType1, String action2, String betType2, List<TableRow> p1, List<TableRow> p2) {
        final String missilePath = "./img/missile.png";
        final String betterOddPath ="./img/up_arrow.png";
        int MISSILE_TRASHOLD = Integer.parseInt(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.MISSILE_TRASHOLD));
        
        String result = 
                  "<div class=\"row\">" +
                    "<div class=\"col left-col\">" +
                        "<h3 class=\"tipo-di-scommessa\">" +
                            action1 + " " + betType1 +
                        "</h3>" +
                      "<table class=\"table\">" +
                        "<thead>" +
                          "<tr>" +
                            "<th scope=\"col\">" +
                              "Bookmakers" +
                            "</th>" +
                            "<th scope=\"col\">" +
                              "Quota" +
                            "</th>";
        
        if (needLiquidita(p1)){
            result +="<th scope=\"col\">" +
                        "Liquidita'" +
                      "</th>";
        }             
                          
        result +=       "</tr>" +
                        "</thead>" +
                        "<tbody>";
                
                for (int i = 0; i < p1.size(); i++) {                    
 
                    if (p1.get(i).isRemovedOdd()) {
                        result += 
                                "<tr class=\"removedOdd\">";
                    } else {
                        result += 
                                "<tr>";
                    }
                    
                    result += 
                            "<th scope=\"row\"> &nbsp; &nbsp; " +
                            p1.get(i).getBookmaker() +
                            " &nbsp; &nbsp; </th>" +
                            "<td>";
                    
                    if (p1.get(i).isBetterOdd()) {
                        result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
                    }
                    
                    result+= " &nbsp; &nbsp; " +  ((LeftTableRow) p1.get(i)).getOdd() + "  &nbsp; &nbsp; ";
                    
                    if (p1.get(i).isBetterOdd()) {
                        result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";  
                    }
 
                            
                    result+="</td>";
                    
                    if (needLiquidita(p1)) {
                        if (Float.parseFloat(p1.get(i).getMoney()) < 0) {
                            result += "<td> - </td>";                     
                        } else {
                            result += "<td>" +
                                    " &nbsp; &nbsp; " + p1.get(i).getMoney() + " &nbsp; &nbsp; " +
                                  "</td>";                          
                        }
                    }
                    
                    result+= "</tr>";
 
                }
                
                result +=           
                    "</tbody>" +
                    "</table>" +
                    "</div>";                   
                result += 
                    "<div class=\"col right-col\">" +
                    "<h3 class=\"tipo-di-scommessa\">" +
                        action2 + " " + betType2 +
                    "</h3>" +
                    "<table class=\"table\">" +
                      "<thead>" +
                        "<tr>" +
                          "<th scope=\"col\">" +
                            "Bookmakers" +
                          "</th>" +
                          "<th scope=\"col\">" +
                            "Quota" +
                          "</th>";
                
                if (needLiquidita(p2)){
                    result +="<th scope=\"col\">" +
                                "Liquidita'" +
                              "</th>";
                }   
                
                result +=   "<th scope=\"col\">" +
                            "% di guadagno" +
                          "</th>" +
                        "</tr>" +
                      "</thead>" +
                      "<tbody>";
                                
                for (int i = 0; i < p2.size(); i++) {        
                    
                    if (p2.get(i).isRemovedOdd()) {
                        result += 
                                "<tr class=\"removedOdd\">";
 
                    } else {
                        result += 
                                "<tr>";
                    }
                    
                    result += 
                                "<th scope=\"row\">" + " &nbsp; " + 
                                    p2.get(i).getBookmaker() + " &nbsp; " + 
                                "</th>" + 
                                "<td>";
                    
                    if (p2.get(i).isBetterOdd()) {
                        result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
                    }
                    
                    result+= " &nbsp; " +  ((RightTableRow) p2.get(i)).getOdd() + " &nbsp; ";
                                
                    if (p2.get(i).isBetterOdd()) {
                        result += " <img class=\"betterodd\" src=\"" + betterOddPath + "\" alt=\"Better odd\" />";
                    }
                                
                        
                    result += "</td>";
                    
                    if (needLiquidita(p2)) {
                        if (Float.parseFloat(p2.get(i).getMoney()) < 0) {
                            result += "<td> - </td>";                     
                        } else {
                            result += "<td>" +
                                    " &nbsp; " + p2.get(i).getMoney() + " &nbsp; " +
                                  "</td>";                          
                        }
                    }
                    
                    result += "<td>";
                
                    if (Float.parseFloat(((RightTableRow) p2.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
                        result += " <img class=\"missile\" src=\"" + missilePath + "\" alt=\"Missile\" />";
                    }
                    
                    if (Float.parseFloat(((RightTableRow) p2.get(i)).getMaxPercentage().replace(",", ".")) > Float.parseFloat(((RightTableRow) p2.get(i)).getMinPercentage().replace(",", "."))) {
                        result += " &nbsp; " + ((RightTableRow) p2.get(i)).getMaxPercentage() + " - " + ((RightTableRow) p2.get(i)).getMinPercentage() + " &nbsp; ";
                    }   else {
                        result += " &nbsp; " + ((RightTableRow) p2.get(i)).getMaxPercentage() + " &nbsp; ";
                    }
                    
                    if (Float.parseFloat(((RightTableRow) p2.get(i)).getMaxPercentage().replace(",", ".")) > MISSILE_TRASHOLD) {
                        result += " <img class=\"missile\" src=\"" + missilePath + "\" alt=\"Missile\" />";
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
        return result;
 
    }
 
    private boolean needLiquidita(List<TableRow> p1) {
        for (int i = 0; i< p1.size(); i++){
            if (Float.parseFloat(p1.get(i).getMoney()) > 0) {
                return true;
            }
        }
        return false;
    }   
}