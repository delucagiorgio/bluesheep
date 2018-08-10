package it.bluesheep.servicehandler.servicemanager;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.serviceapi.Service;

public class BetfairServiceHandler extends AbstractBlueSheepServiceHandler {

	protected BetfairServiceHandler() {
		super();
		logger = Logger.getLogger(BetfairServiceHandler.class);
		serviceName = Service.BETFAIR_SERVICENAME;
	}
	
}
