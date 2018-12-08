package it.bluesheep;

import java.io.File;
import java.util.logging.LogManager;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

public class BlueSheepComparatoreMain {
	
	public static void main(String[] args) throws Exception{
        LogManager.getLogManager().reset();
		
        File bluesheepStatusOnFile = new File(BlueSheepConstants.PATH_TO_RESOURCES + "bluesheepStatusOn.txt");
        if(!bluesheepStatusOnFile.exists()) {
        	bluesheepStatusOnFile.createNewFile();
        	initializeServiceHandler();
		}else {
			System.out.println("Delete bluesheepStatusOn.txt");
		}
	}
	
	private static void initializeServiceHandler(){
		BlueSheepServiceHandlerManager serviceHandler = BlueSheepServiceHandlerManager.getBlueSheepServiceHandlerInstance();
		serviceHandler.start();
	}
}