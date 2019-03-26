package it.bluesheep.comparatore.io.datacompare.impl;

import it.bluesheep.comparatore.entities.input.record.StanleyBetInputRecord;
import it.bluesheep.comparatore.serviceapi.Service;

public class StanleyBetProcessDataManager extends AbstractScrapingProcessDataManager<StanleyBetInputRecord> {
	
	protected StanleyBetProcessDataManager() {
		super();
		service = Service.STANLEYBET_SERVICENAME;
	}

}