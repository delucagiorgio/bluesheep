package it.bluesheep.comparatore.io.datacompare.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepSharedResources;

public class TxOddsCompareThreadHelper implements Runnable {
	
	private static Logger logger = Logger.getLogger(TxOddsCompareThreadHelper.class);
	private List<AbstractInputRecord> subList;
	private List<AbstractInputRecord> exchangeRecordList;
	private Map<Long, List<AbstractInputRecord>> comparedRecords;
	
	public TxOddsCompareThreadHelper(List<AbstractInputRecord> subList, List<AbstractInputRecord> exchangeRecordList, Map<Long, List<AbstractInputRecord>> comparedRecords) {
		this.subList = subList;
		this.exchangeRecordList = exchangeRecordList;
		this.comparedRecords = comparedRecords;
	}
	
	@Override
	public void run() {
		logger.info("Start compare thread");
		
		try {
			List<AbstractInputRecord> record = new ArrayList<AbstractInputRecord>();
			comparedRecords.put(Thread.currentThread().getId(), record);
			
			int i = 0;
			for(AbstractInputRecord oddsRecord : subList) {
				i++;
				
				if(i % 10000 == 0) {
					logger.info("Comparing odd number " + i);
				}
				
				AbstractInputRecord exchangeRecord = BlueSheepSharedResources.findExchangeRecord(oddsRecord, exchangeRecordList);
				if(exchangeRecord != null) {
					oddsRecord.setDataOraEvento(exchangeRecord.getDataOraEvento());
					oddsRecord.setKeyEvento("" + oddsRecord.getDataOraEvento() + BlueSheepConstants.REGEX_PIPE + 
							oddsRecord.getSport() + BlueSheepConstants.REGEX_PIPE + 
							oddsRecord.getPartecipante1()+ BlueSheepConstants.REGEX_VERSUS + 
							oddsRecord.getPartecipante2());
					
					record.add(oddsRecord);
				}
			}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		logger.info("Exiting compare thread");
	}

}
