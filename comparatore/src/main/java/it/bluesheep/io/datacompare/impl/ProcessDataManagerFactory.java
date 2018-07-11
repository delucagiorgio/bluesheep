package it.bluesheep.io.datacompare.impl;

import it.bluesheep.io.datacompare.IProcessDataManager;
import it.bluesheep.io.datacompare.util.ICompareInformationEvents;

public class ProcessDataManagerFactory {
	
	private ProcessDataManagerFactory() {}
	
	public static IProcessDataManager getProcessDataManagerByString(String serviceName) {
		switch(serviceName) {
		case "TX_ODDS":
			return new TxOddsProcessDataManager();
		case "BETFAIR":
			return new BetfairExchangeProcessDataManager();
		case "BET365":
			return new Bet365ProcessDataManager();
		case "CSV":
			return new CSVProcessDataManager();
		default:
			return null;
		}
	}
	
	public static ICompareInformationEvents getICompareInformationEventsByString(String serviceName) {
		switch(serviceName) {
		case "BETFAIR":
			return new BetfairExchangeProcessDataManager();
		case "BET365":
			return new Bet365ProcessDataManager();
		case "CSV":
			return new CSVProcessDataManager();
		default:
			return null;
		}
	}

}
