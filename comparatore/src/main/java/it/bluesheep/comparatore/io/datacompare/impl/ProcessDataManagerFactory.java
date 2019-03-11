package it.bluesheep.comparatore.io.datacompare.impl;

import it.bluesheep.comparatore.io.datacompare.IProcessDataManager;
import it.bluesheep.comparatore.io.datacompare.util.ICompareInformationEvents;
import it.bluesheep.comparatore.serviceapi.Service;

public class ProcessDataManagerFactory {
	
	private ProcessDataManagerFactory() {}
	
	public static synchronized IProcessDataManager getProcessDataManagerByString(Service service) {
		switch(service) {
		case TXODDS_SERVICENAME:
			return new TxOddsProcessDataManager();
		case BETFAIR_EX_SERVICENAME:
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
		case BETFAIR_EX_SERVICENAME:
			return new BetfairExchangeProcessDataManager();
		case BET365_SERVICENAME:
			return new Bet365ProcessDataManager();
		case CSV_SERVICENAME:
			return new CSVProcessDataManager();
		case BETFLAG_SERVICENAME:
			return new BetflagProcessDataManager();
		case GOLDBET_SERVICENAME:
		case STARVEGAS_SERVICENAME:
		case PINTERBET_SERVICENAME:
		case STANLEYBET_SERVICENAME:
		case TXODDS_SERVICENAME:
			return new TxOddsProcessDataManager();
		default:
			return null;
		}
	}

}
