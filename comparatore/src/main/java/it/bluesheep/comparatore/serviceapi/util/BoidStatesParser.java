package it.bluesheep.comparatore.serviceapi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class BoidStatesParser {

	private final static String DROPPED_MARKET = "0";
	private static Logger logger = Logger.getLogger(BoidStatesParser.class);
	
	private BoidStatesParser() {}

	
	public static List<String> getListToBeDropped(String inputResult) {
        List<String> droppedMarketsList = new ArrayList<String>();

		try {
	         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();   
	         // Funziona anche con una stringa come parametro in input
	         Document doc = dBuilder.parse(new InputSource(new java.io.StringReader(inputResult)));
	         doc.getDocumentElement().normalize();
	         NodeList nList = doc.getElementsByTagName("boid");
	         	         
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	            Node nNode = nList.item(temp);
	            
	            String id;
	            String flags;
	            
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	               Element eElement = (Element) nNode;
	               id = eElement.getAttribute("id");
	               flags = eElement.getAttribute("flags");
	               if (flags.equals(DROPPED_MARKET)) {
	            	   droppedMarketsList.add(id);
	               }
	            }	            
	         }
	         
	         Collections.sort(droppedMarketsList);
	      } catch (Exception e) {
	         logger.error(e.getMessage(), e);
	      }
		
		return droppedMarketsList;
	}
	
}
