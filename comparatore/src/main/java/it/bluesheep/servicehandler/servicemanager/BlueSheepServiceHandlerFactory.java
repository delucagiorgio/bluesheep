package it.bluesheep.servicehandler.servicemanager;

import it.bluesheep.comparatore.serviceapi.Service;

public class BlueSheepServiceHandlerFactory {

	private BlueSheepServiceHandlerFactory() {}
	
	public static AbstractBlueSheepServiceHandler getCorrectServiceHandlerByService(Service service) {
		switch(service) {
		case BET365_SERVICENAME:
			return new Bet365ServiceHandler();
		case BETFAIR_EX_SERVICENAME:
			return new BetfairExchangeServiceHandler();
		case TXODDS_SERVICENAME:
			return new TxOddsServiceHandler();
		case CSV_SERVICENAME:
			return new CSVServiceHandler();
		case STARVEGAS_SERVICENAME:
			return new StarVegasServiceHandler();
		case BETFLAG_SERVICENAME:
			return new BetflagServiceHandler();
		case GOLDBET_SERVICENAME:
			return new GoldBetServiceHandler();
		case PINTERBET_SERVICENAME:
			return new PinterBetServiceHandler();
		case STANLEYBET_SERVICENAME:
			return new StanleyBetServiceHandler();
		case BETFAIR_SB_SERVICENAME:
			return new BetfairSportbookServiceHandler();
		default:
			return null;
		}
	}
	
}
