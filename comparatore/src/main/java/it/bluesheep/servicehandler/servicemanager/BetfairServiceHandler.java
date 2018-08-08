package it.bluesheep.servicehandler.servicemanager;

import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepLogger;

public class BetfairServiceHandler extends AbstractBlueSheepServiceHandler {

	protected BetfairServiceHandler() {
		super();
		logger = (new BlueSheepLogger(BetfairServiceHandler.class)).getLogger();
		serviceName = Service.BETFAIR_SERVICENAME;
	}
	
}
