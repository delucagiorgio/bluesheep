package it.bluesheep;

import java.util.logging.LogManager;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;

public class BlueSheepComparatoreMain {
	
	public static void main(String[] args) throws Exception{
        LogManager.getLogManager().reset();
		
		initializeServiceHandler();
	}
	
	private static void initializeServiceHandler(){
		
		BlueSheepServiceHandlerManager serviceHandler = BlueSheepServiceHandlerManager.getBlueSheepServiceHandlerInstance();
		serviceHandler.start();
	}
}