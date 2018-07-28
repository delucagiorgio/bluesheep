package it.bluesheep.io.datacompare;

import java.util.List;
import java.util.logging.Logger;

import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.util.BlueSheepLogger;

public abstract class AbstractProcessDataManager implements IProcessDataManager {

	protected static Logger logger;
	
	protected AbstractProcessDataManager() {
		logger = (new BlueSheepLogger(AbstractProcessDataManager.class)).getLogger();	
	}
	
	@Override
	public abstract List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport) throws Exception;	
}
