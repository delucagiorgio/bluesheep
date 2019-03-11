package it.bluesheep.servicehandler.servicemanager;

import java.util.List;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepSharedResources;

public class BetfairExchangeServiceHandler extends AbstractBlueSheepServiceHandler {

	protected BetfairExchangeServiceHandler() {
		super();
		logger = Logger.getLogger(BetfairExchangeServiceHandler.class);
		serviceName = Service.BETFAIR_EX_SERVICENAME;
	}
	
	@Override
	protected void startProcess() {
		
		BlueSheepSharedResources.checkAndDeleteOldRecords(startTime);
	
		List<AbstractInputRecord> inputRecordList = populateMapWithInputRecord();
		
		if(inputRecordList != null && !inputRecordList.isEmpty()) {
			super.deleteAllSourceEvents();
			startProcessingDataTransformation(inputRecordList);
			BlueSheepSharedResources.setExchangeRecordsList(inputRecordList, startTime);
		}
		
	}
	
}
