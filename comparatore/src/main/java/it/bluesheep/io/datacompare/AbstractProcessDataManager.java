package it.bluesheep.io.datacompare;

import java.util.List;

import it.bluesheep.entities.input.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.sport.Sport;

public abstract class AbstractProcessDataManager implements IProcessDataManager {

	@Override
	public abstract List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport);

}
