package it.bluesheep;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import it.bluesheep.operationmanager.ComparisonOperationManager;
import it.bluesheep.util.zip.ZipUtil;

public class BlueSheepComparatoreMain {
	
	private static Properties properties = new Properties(); 

	public static void main(String[] args) throws Exception{
				
		
        try {
        	InputStream in = new FileInputStream(args[0]);
//        	InputStream in = new FileInputStream("../RISORSE_BLUESHEEP/bluesheepComparatore.properties");
            properties.load(in);
        	// va stabilito un path per il file delle proprieta'    	
            in.close();
        } catch (IOException exception) {
        	System.out.println("Error retrieving properties\n" + exception.getMessage());
            System.exit(-1);
        }
        
        ComparisonOperationManager om = ComparisonOperationManager.getComparisonOperationManagerFactory();
        
        om.startProcess();
        
		ZipUtil zipUtil = new ZipUtil();
		zipUtil.zipLastRunLogFiles();
	}
	
	public static Properties getProperties() {
		return properties;
	}
}