package it.bluesheep.servicehandler.servicemanager;

import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepLogger;

public class Bet365ServiceHandler extends AbstractBlueSheepServiceHandler {

	protected Bet365ServiceHandler() {
		super();
		logger = (new BlueSheepLogger(Bet365ServiceHandler.class)).getLogger();
		serviceName = Service.BET365_SERVICENAME;
	}
	
}
