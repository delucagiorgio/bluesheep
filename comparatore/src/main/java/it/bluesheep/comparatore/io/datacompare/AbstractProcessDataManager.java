package it.bluesheep.comparatore.io.datacompare;

import java.util.List;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.AbstractBlueSheepService;

public abstract class AbstractProcessDataManager implements IProcessDataManager {

	protected Logger logger;
	protected Service service;
	
	protected AbstractProcessDataManager() {}
	
	@Override
	public abstract List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport, AbstractBlueSheepService bluesheepServiceType) throws Exception;	
}
