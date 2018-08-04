package it.bluesheep;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import it.bluesheep.servicehandler.BlueSheepServiceHandler;

public class BlueSheepComparatoreMain {
	
	private static Properties properties = new Properties(); 

	public static void main(String[] args) throws Exception{
		
		String pathToProperties = "";
//		String pathToProperties = args[0];
		
		intializePropertiesFromFile(pathToProperties);
		
		initializeServiceHandler();
		
		
		
//        try {
//        	InputStream in = new FileInputStream(arg s[0]);
//        	InputStream in = new FileInputStream("../RISORSE_BLUESHEEP/bluesheepComparatore.properties");
//            properties.load(in);
//        	// va stabilito un path per il file delle proprieta'    	
//            in.close();
//        } catch (IOException exception) {
//        	System.out.println("Error retrieving properties\n" + exception.getMessage());
//            System.exit(-1);
//        }
//
//        ComparatoreServiceHandler om = ComparatoreServiceHandler.getComparisonOperationManagerFactory();
//        
//        om.startProcess();
//	        
	}
	
	private static void initializeServiceHandler() throws InterruptedException {
		BlueSheepServiceHandler serviceHandler = BlueSheepServiceHandler.getBlueSheepServiceHandlerInstance();
		serviceHandler.start();
	}

	private static void intializePropertiesFromFile(String pathToPropertiesFile) {
		try {
//			InputStream in = new FileInputStream(pathToPropertiesFile);
			InputStream in = new FileInputStream("../RISORSE_BLUESHEEP/bluesheepComparatore.properties");
			properties.load(in);
			// va stabilito un path per il file delle proprieta'
			in.close();
		} catch (IOException exception) {
			System.out.println("Error retrieving properties\n" + exception.getMessage());
			System.exit(-1);
		}
	}

	public static Properties getProperties() {
		return properties;
	}
}