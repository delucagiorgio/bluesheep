package it.bluesheep.comparatore.io.datacompare.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.AbstractProcessDataManager;
import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.comparatore.io.datacompare.util.ICompareInformationEvents;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.AbstractBlueSheepService;

public class Bet365ProcessDataManager extends AbstractProcessDataManager implements ICompareInformationEvents {
	
	protected Bet365ProcessDataManager() {
		super();
		this.logger = Logger.getLogger(Bet365ProcessDataManager.class);
		service = Service.BET365_SERVICENAME;
	}
	
	@Override
	public List<RecordOutput> compareTwoWayOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport, AbstractBlueSheepService bluesheepServiceType) throws Exception {
		throw new Exception("Incorrect implementation of compareTwoWayOdds");
		//Viene lasciato fare al processManager di TxOdds : tratterà gli eventi di Bet365 come un normale Bookmaker in più
	}

	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds(List<AbstractInputRecord> bookmakerList) throws Exception {
		
		int threadNumber = bookmakerList != null ? (bookmakerList.size() / 2000) + 1 : 0;
		ExecutorService executor = Executors.newFixedThreadPool(threadNumber);
		List<AbstractInputRecord> returnList = new ArrayList<AbstractInputRecord>();
		if(bookmakerList != null && !bookmakerList.isEmpty()) {
			
			Map<Long, List<AbstractInputRecord>> resultMap = new ConcurrentHashMap<Long, List<AbstractInputRecord>>();
			int bookmakerListSize = bookmakerList.size();
			int page = bookmakerListSize / threadNumber + bookmakerListSize % threadNumber;

			for(int i = 0; i < threadNumber; i++) {
				
				if(i * page < bookmakerListSize) {
					List<AbstractInputRecord> splittedList = bookmakerList.subList(i * page, Math.min((i + 1) * page, bookmakerListSize));
					
					Bet365CompareThreadHelper thread = new Bet365CompareThreadHelper(splittedList, resultMap);
					executor.submit(thread);
				}
			}
			try {
				executor.shutdown();
				if(!executor.awaitTermination(2, TimeUnit.MINUTES)) {
					executor.shutdownNow();
				}
			}catch(Exception e) {
				logger.error(e.getMessage(), e);
			}
			
			for(Long threadId : resultMap.keySet()) {
				if(resultMap.get(threadId) != null && !resultMap.get(threadId).isEmpty()) {
					returnList.addAll(resultMap.get(threadId));
				}
			}
			
		}
		
		logger.info("Matching process completed. Matched events are " + returnList.size() + ": events Bet365 = " + bookmakerList.size());
		
		return returnList;
	}

	@Override
	public List<ArbsRecord> compareThreeWayOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport, AbstractBlueSheepService bluesheepServiceType) throws Exception {
		throw new Exception("Incorrect implementation of compareTwoWayOdds");
	}

}
