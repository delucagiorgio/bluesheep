package it.bluesheep;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;

public class BlueSheepComparatoreMain {
	
	public static void main(String[] args) throws Exception{
		initializeServiceHandler();
	}
	
	private static void initializeServiceHandler(){
		BlueSheepServiceHandlerManager serviceHandler = BlueSheepServiceHandlerManager.getBlueSheepServiceHandlerInstance();
		serviceHandler.start();
	}
}