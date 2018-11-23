package it.bluesheep.servicehandler.servicemanager;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.record.TxOddsInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.util.BookmakerLinkGenerator;
import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.comparatore.serviceapi.impl.TxOddsApiImpl;
import it.bluesheep.comparatore.serviceapi.util.BoidStatesParser;
import it.bluesheep.util.BlueSheepSharedResources;

public final class TxOddsServiceHandler extends AbstractBlueSheepServiceHandler {
	
	protected TxOddsServiceHandler() {
		super();
		logger = Logger.getLogger(TxOddsServiceHandler.class);
		serviceName = Service.TXODDS_SERVICENAME;
	}

	/**
	 * Consideriamo TxOdds come il principale fornitore di quote, non è dunque necessario
	 * processare i dati relativi all'evento, ma saranno la base per gli altri servizi.
	 */
	@Override
	protected void startProcessingDataTransformation(List<AbstractInputRecord> inputRecordList) {
		
		if(Service.TXODDS_SERVICENAME.equals(serviceName) && 
				BlueSheepSharedResources.getUpdateCallCount() == 0 && 
				!inputRecordList.isEmpty() && 
				!BlueSheepSharedResources.getEventoScommessaRecordMap().isEmpty()) {

			logger.info("Removing all TxOdds events");
			resetAllTxOddsEvents();
			BookmakerLinkGenerator.initializeMap();
			logger.info("Starting GC...");
			long startGc = System.currentTimeMillis();
			long endGc = System.currentTimeMillis();
			System.gc();
			logger.info("GC completed execution: " + (endGc - startGc) + " ms.");
		}
		super.startProcessingDataTransformation(inputRecordList);
	}
	
	/**
	 * GD - 12/08/2018
	 * Rimuove tutti gli elementi nella mappa dei dati di input condivisa che hanno origine da TxOdds.
	 */
	private void resetAllTxOddsEvents() {
		
		ChiaveEventoScommessaInputRecordsMap dataMap = BlueSheepSharedResources.getEventoScommessaRecordMap();
		if(dataMap != null) {
			Set<Sport> sportSet = new HashSet<Sport>(dataMap.keySet());
			for(Sport sport : sportSet) {
				Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> sportMap = dataMap.get(sport);
				if(sportMap != null) {
					Set<Date> dateSet =  new HashSet<Date>(sportMap.keySet());
					for(Date date : dateSet) {
						Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>> dateMap = sportMap.get(date);
						if(dateMap != null) {
							Set<String> eventSet = new HashSet<String>(dateMap.keySet());
							for(String eventoKey : eventSet) {
								Map<Scommessa, Map<String, AbstractInputRecord>> eventoKeyMap = dateMap.get(eventoKey);
								if(eventoKeyMap != null) {
									Set<Scommessa> scommessaSet = new HashSet<Scommessa>(eventoKeyMap.keySet());
									for(Scommessa scommessa : scommessaSet) {
										Map<String, AbstractInputRecord> scommessaMap = eventoKeyMap.get(scommessa);
										if(scommessaMap != null) {
											Set<String> bookmakerList = new HashSet<String>(scommessaMap.keySet());
											for(String bookmaker : bookmakerList) {
												AbstractInputRecord recordOfBookmaker = scommessaMap.get(bookmaker);
											
												if(serviceName.equals(recordOfBookmaker.getSource())) {
													scommessaMap.remove(bookmaker);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected void startProcess() {
		//Set the smallest timestamp of the last requests before starting the process
		Long timestampLastRequest = new Long(BlueSheepSharedResources.getTxOddsNowMinimumUpdateTimestamp());
		BlueSheepSharedResources.setTxOddsUpdateTimestamp(timestampLastRequest);
		
		if(timestampLastRequest > 0L) {
			deleteNotAvailableMarkets(timestampLastRequest);
		}
		
		
		logger.info("Update count call for " + serviceName + " = " + BlueSheepSharedResources.getUpdateCallCount());
		
		boolean overMinutesOfValidityByCountCall = BlueSheepSharedResources.getUpdateCallCount() > 1500;
		
		//Reset the update call count and the timestamp to perform "an initial call"
		if(overMinutesOfValidityByCountCall) {
			BlueSheepSharedResources.setUpdateCallCount(0);
			BlueSheepSharedResources.setTxOddsUpdateTimestamp(-1L);
		}
		
		super.startProcess();
		
		//Set the number of updateCount already processed
		BlueSheepSharedResources.setUpdateCallCount(BlueSheepSharedResources.getUpdateCallCount() + 1);
	}

	private void deleteNotAvailableMarkets(Long timestampLastRequest) {
		TxOddsApiImpl txOddsApi = new TxOddsApiImpl();
		
		String result = txOddsApi.getInactiveMarketsFromTimestamp(timestampLastRequest);
		
		if(result != null && !StringUtils.isEmpty(result)) {
			List<String> notAvailableMarketsList = BoidStatesParser.getListToBeDropped(result);
			if(notAvailableMarketsList != null && !notAvailableMarketsList.isEmpty()) {
				BlueSheepSharedResources.setBoidOTBList(notAvailableMarketsList);
				findAndDeleteUnavailableMarkets();
			}
		}
	}

	private void findAndDeleteUnavailableMarkets() {
		ChiaveEventoScommessaInputRecordsMap map = BlueSheepSharedResources.getEventoScommessaRecordMap();
		
		Set<Sport> sportSet = new HashSet<Sport>(map.keySet());
		
		int count = 0;
		for(Sport sport : sportSet) {
			Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> dateMap = map.get(sport);
			
			Set<Date> dateSet = new HashSet<Date>(dateMap.keySet());
			for(Date date : dateSet) {
				Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>> eventoMap = dateMap.get(date);
				if(eventoMap != null) {
				Set<String> eventoSet = new HashSet<String>(eventoMap.keySet());
					for(String evento : eventoSet) {
						Map<Scommessa, Map<String, AbstractInputRecord>> scommessaMap = eventoMap.get(evento);
						if(scommessaMap != null) {
							Set<Scommessa> scommessaSet = new HashSet<Scommessa>(scommessaMap.keySet());
							for(Scommessa scommessa : scommessaSet) {
								Map<String, AbstractInputRecord> bookmakerMap = scommessaMap.get(scommessa);
								
								Set<String> bookmakerSet = new HashSet<String>(bookmakerMap.keySet());
								for(String bookmaker : bookmakerSet) {
									AbstractInputRecord record = bookmakerMap.get(bookmaker);
									if(record != null && record.getSource() == null) {
										record.toString();
									}
									if(record != null && record.getSource().equals(Service.TXODDS_SERVICENAME)) {
										TxOddsInputRecord txOddsRecord = (TxOddsInputRecord) record;
										int index = Collections.binarySearch(BlueSheepSharedResources.getBoidOTBList(), txOddsRecord.getBoid());
										if(index >= 0) {
											count++;
											bookmakerMap.remove(bookmaker);
											if(bookmakerMap.isEmpty()) {
												scommessaMap.remove(scommessa);
												if(eventoMap.isEmpty()) {
													eventoMap.remove(evento);
													if(dateMap.isEmpty()) {
														dateMap.remove(date);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		logger.info("" + count + " odds have been removed since not valid");
		
	}

}
