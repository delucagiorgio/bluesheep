package it.bluesheep.servicehandler.servicemanager;

import java.util.List;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepSharedResources;

public class BetfairServiceHandler extends AbstractBlueSheepServiceHandler {

	protected BetfairServiceHandler() {
		super();
		logger = Logger.getLogger(BetfairServiceHandler.class);
		serviceName = Service.BETFAIR_SERVICENAME;
	}
	
	@Override
	protected void startProcess() {
		
		BlueSheepSharedResources.checkAndDeleteOldRecords(startTime);
	
		List<AbstractInputRecord> inputRecordList = populateMapWithInputRecord();
		
		startProcessingDataTransformation(inputRecordList);
		
		if(inputRecordList != null && !inputRecordList.isEmpty()) {
			BlueSheepSharedResources.setExchangeRecordsList(inputRecordList, startTime);
		}
		
	}
	
}
