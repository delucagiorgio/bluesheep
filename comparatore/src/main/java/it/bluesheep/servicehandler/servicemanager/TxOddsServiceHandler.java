package it.bluesheep.servicehandler.servicemanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;
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
			int minutesOfValidity = new Integer(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.MINUTES_ODD_VALIDITY));

			logger.info("Removing all TxOdds events: " + minutesOfValidity + " minute(s) are passed since last initial call");
			resetAllTxOddsEvents();
		}
		super.addToChiaveEventoScommessaMap(inputRecordList);
	}
	
	/**
	 * GD - 12/08/2018
	 * Rimuove tutti gli elementi nella mappa dei dati di input condivisa che hanno origine da TxOdds.
	 */
	private void resetAllTxOddsEvents() {
		
		ChiaveEventoScommessaInputRecordsMap dataMap = BlueSheepSharedResources.getEventoScommessaRecordMap();
		
		for(Sport sport : dataMap.keySet()) {
			Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> sportMap = dataMap.get(sport);
			
			for(Date date : sportMap.keySet()) {
				Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>> dateMap = sportMap.get(date);
			
				for(String eventoKey : dateMap.keySet()) {
					Map<Scommessa, Map<String, AbstractInputRecord>> eventoKeyMap = dateMap.get(eventoKey);
				
					for(Scommessa scommessa : eventoKeyMap.keySet()) {
						Map<String, AbstractInputRecord> scommessaMap = eventoKeyMap.get(scommessa);
						List<String> bookmakerList = new ArrayList<String>(scommessaMap.keySet());
						
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

	@Override
	protected void startProcess() {
		//Set the smallest timestamp of the last requests before starting the process
		BlueSheepSharedResources.setTxOddsUpdateTimestamp(BlueSheepSharedResources.getTxOddsNowMinimumUpdateTimestamp());
		
		int secondFrequencyTxOdds = new Integer(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.FREQ_TXODDS_SEC));
		int minutesOfValidity = new Integer(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.MINUTES_ODD_VALIDITY)) * 60;
		
		boolean overMinutesOfValidityByCountCall = BlueSheepSharedResources.getUpdateCallCount() > (minutesOfValidity / secondFrequencyTxOdds);
		
		//Reset the update call count and the timestamp to perform "an initial call"
		if(overMinutesOfValidityByCountCall) {
			BlueSheepSharedResources.setUpdateCallCount(0);
			BlueSheepSharedResources.setTxOddsUpdateTimestamp(-1L);
		}
		
		super.startProcess();
		//Set the number of updateCount already processed
		BlueSheepSharedResources.setUpdateCallCount(BlueSheepSharedResources.getUpdateCallCount() + 1);
	}

}
