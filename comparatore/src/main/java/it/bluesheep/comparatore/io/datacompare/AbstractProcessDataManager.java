package it.bluesheep.comparatore.io.datacompare;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
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
	public abstract List<RecordOutput> compareTwoWayOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport, AbstractBlueSheepService bluesheepServiceType) throws Exception;
	
	protected AbstractInputRecord findTxOddsRecord(List<String> bookmakerSet, Map<String, AbstractInputRecord> bookmakerRecordMap) {
		
		AbstractInputRecord txOddsRecord = null;
		
		for(String bookmaker : bookmakerSet) {
			AbstractInputRecord record = bookmakerRecordMap.get(bookmaker);
			if(record.getSource().equals(Service.TXODDS_SERVICENAME)) {
				txOddsRecord = record;
				break;
			}
		}
		
		return txOddsRecord;
	}
}
