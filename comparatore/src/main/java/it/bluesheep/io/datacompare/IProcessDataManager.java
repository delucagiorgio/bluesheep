package it.bluesheep.io.datacompare;

import java.util.List;

import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;

public interface IProcessDataManager {
	
	public List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport) throws Exception;

}
