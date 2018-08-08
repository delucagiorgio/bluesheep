package it.bluesheep.comparatore.io.datainput.operationmanager.service.impl;

import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datainput.IInputDataManager;
import it.bluesheep.comparatore.io.datainput.operationmanager.csv.CSVInputDataManagerImpl;
import it.bluesheep.comparatore.serviceapi.Service;

public class InputDataManagerFactory {
	
	private InputDataManagerFactory() {}
	
	public static synchronized IInputDataManager getInputDataManagerByString(Sport sport, Service serviceApiName) {
		switch(serviceApiName){
			case TXODDS_SERVICENAME:
				return new TxOddsInputDataManagerImpl(sport);
			case BETFAIR_SERVICENAME:
				return new BetfairExchangeInputDataManagerImpl(sport);
			case BET365_SERVICENAME:
				return new Bet365InputDataManagerImpl(sport);
			case CSV_SERVICENAME:
				return new CSVInputDataManagerImpl(sport);
			default:
				return null;
		}
	}

}
