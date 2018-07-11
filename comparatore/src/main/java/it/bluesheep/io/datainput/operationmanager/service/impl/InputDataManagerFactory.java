package it.bluesheep.io.datainput.operationmanager.service.impl;

import java.util.List;
import java.util.Map;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.IInputDataManager;
import it.bluesheep.io.datainput.operationmanager.csv.CSVInputDataManagerImpl;

public class InputDataManagerFactory {
	
	private InputDataManagerFactory() {}
	
	public static IInputDataManager getInputDataManagerByString(Sport sport, String serviceApiName, Map<String, Map<Sport,List<AbstractInputRecord>>> allServiceApiMapResult) {
		switch(serviceApiName){
			case "TX_ODDS":
				return new TxOddsInputDataManagerImpl(sport, allServiceApiMapResult);
			case "BETFAIR":
				return new BetfairExchangeInputDataManagerImpl(sport, allServiceApiMapResult);
			case "BET365":
				return new Bet365InputDataManagerImpl(sport, allServiceApiMapResult);
			case "CSV":
				return new CSVInputDataManagerImpl(sport, allServiceApiMapResult);
			default:
				return null;
		}
	}

}
