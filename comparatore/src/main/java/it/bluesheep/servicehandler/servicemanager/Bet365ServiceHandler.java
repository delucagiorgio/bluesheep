package it.bluesheep.servicehandler.servicemanager;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.serviceapi.Service;

public class Bet365ServiceHandler extends AbstractBlueSheepServiceHandler {

	protected Bet365ServiceHandler() {
		super();
		logger = Logger.getLogger(Bet365ServiceHandler.class);
		serviceName = Service.BET365_SERVICENAME;
	}
	
}
