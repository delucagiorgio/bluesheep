package it.bluesheep.servicehandler.servicemanager;

import it.bluesheep.comparatore.serviceapi.Service;

public class BlueSheepServiceHandlerFactory {

	private BlueSheepServiceHandlerFactory() {}
	
	public static AbstractBlueSheepServiceHandler getCorrectServiceHandlerByService(Service service) {
		switch(service) {
		case BET365_SERVICENAME:
			return new Bet365ServiceHandler();
		case BETFAIR_SERVICENAME:
			return new BetfairServiceHandler();
		case TXODDS_SERVICENAME:
			return new TxOddsServiceHandler();
		case CSV_SERVICENAME:
			return new CSVServiceHandler();
		default:
			return null;
		}
	}
	
}
