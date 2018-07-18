package it.bluesheep.io.datacompare.impl;

import it.bluesheep.io.datacompare.IProcessDataManager;
import it.bluesheep.io.datacompare.util.ICompareInformationEvents;
import it.bluesheep.serviceapi.Service;

public class ProcessDataManagerFactory {
	
	private ProcessDataManagerFactory() {}
	
	public static synchronized IProcessDataManager getProcessDataManagerByString(Service service) {
		switch(service) {
		case TXODDS_SERVICENAME:
			return new TxOddsProcessDataManager();
		case BETFAIR_SERVICENAME:
			return new BetfairExchangeProcessDataManager();
		case BET365_SERVICENAME:
			return new Bet365ProcessDataManager();
		case CSV_SERVICENAME:
			return new CSVProcessDataManager();
		default:
			return null;
		}
	}
	
	public static synchronized ICompareInformationEvents getICompareInformationEventsByString(Service service) {
		switch(service) {
		case BETFAIR_SERVICENAME:
			return new BetfairExchangeProcessDataManager();
		case BET365_SERVICENAME:
			return new Bet365ProcessDataManager();
		case CSV_SERVICENAME:
			return new CSVProcessDataManager();
		default:
			return null;
		}
	}

}
