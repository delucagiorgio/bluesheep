package it.bluesheep.comparatore.io.datacompare;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.impl.ProcessDataManagerFactory;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.AbstractBlueSheepService;
import it.bluesheep.servicehandler.ArbitraggiServiceHandler;
import it.bluesheep.servicehandler.JsonGeneratorServiceHandler;
import it.bluesheep.util.BlueSheepSharedResources;

public class CompareProcessFactory {
	
	private static Logger logger = Logger.getLogger(CompareProcessFactory.class);
	
	private CompareProcessFactory() {}
	
	/**
	 * GD - 11/07/18
	 * Avvia la comparazione delle quote del punta-punta e del punta banca per gli sport attivi. Salva i risultati in nel file JSON
	 * con filename uguale al timestamp dello start
	 */
	public static synchronized Map<Service, List<RecordOutput>> startComparisonOdds(AbstractBlueSheepService bluesheepService) {
		
		Map<Service, List<RecordOutput>> returnMap = new TreeMap<Service, List<RecordOutput>>();
		
		IProcessDataManager processDataManager;
		List<Service> serviceList = new ArrayList<Service>();
		serviceList.add(Service.TXODDS_SERVICENAME);
		if(bluesheepService instanceof JsonGeneratorServiceHandler || bluesheepService instanceof ArbitraggiServiceHandler) {
			serviceList.add(Service.BETFAIR_EX_SERVICENAME);
		}
		
		for(Service service : serviceList) {
			
			List<RecordOutput> tabellaOutputList = new ArrayList<RecordOutput>();
			processDataManager = ProcessDataManagerFactory.getProcessDataManagerByString(service);
			List<RecordOutput> outputRecord = new ArrayList<RecordOutput>();
			
			for(Sport sport : Sport.values()) {
				try {
					outputRecord = processDataManager.compareTwoWayOdds(BlueSheepSharedResources.getEventoScommessaRecordMap(), sport, bluesheepService);
					logger.info("" + service.getCode() + " :: Odds comparison result size for sport " + sport + " is " + outputRecord.size());
					tabellaOutputList.addAll(outputRecord);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			returnMap.put(service, tabellaOutputList);
		}
		return returnMap;
	}

}
