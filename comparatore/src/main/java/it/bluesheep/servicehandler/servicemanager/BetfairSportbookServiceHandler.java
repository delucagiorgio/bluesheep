package it.bluesheep.servicehandler.servicemanager;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.serviceapi.Service;

public class BetfairSportbookServiceHandler extends AbstractBlueSheepServiceHandler {

	protected BetfairSportbookServiceHandler(){
		super();
		this.logger = Logger.getLogger(BetfairSportbookServiceHandler.class);
		this.serviceName = Service.BETFAIR_SB_SERVICENAME;
	}
	
}
