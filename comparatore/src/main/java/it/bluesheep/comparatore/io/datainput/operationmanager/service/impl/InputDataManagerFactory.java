package it.bluesheep.comparatore.io.datainput.operationmanager.service.impl;

import java.util.List;
import java.util.Map;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datainput.IInputDataManager;
import it.bluesheep.comparatore.io.datainput.operationmanager.csv.CSVInputDataManagerImpl;
import it.bluesheep.comparatore.serviceapi.Service;

public class InputDataManagerFactory {
	
	private InputDataManagerFactory() {}
	
	public static synchronized IInputDataManager getInputDataManagerByString(Sport sport, Service serviceApiName, Map<Service, Map<Sport,List<AbstractInputRecord>>> allServiceApiMapResult) {
		switch(serviceApiName){
			case TXODDS_SERVICENAME:
				return new TxOddsInputDataManagerImpl(sport, allServiceApiMapResult);
			case BETFAIR_SERVICENAME:
				return new BetfairExchangeInputDataManagerImpl(sport, allServiceApiMapResult);
			case BET365_SERVICENAME:
				return new Bet365InputDataManagerImpl(sport, allServiceApiMapResult);
			case CSV_SERVICENAME:
				return new CSVInputDataManagerImpl(sport, allServiceApiMapResult);
			default:
				return null;
		}
	}

}
