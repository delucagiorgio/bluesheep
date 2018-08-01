package it.bluesheep.io.datacompare.multithreadcompare;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.multithreadcompare.comparehelper.CompareThreadHelperFactory;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.util.BlueSheepLogger;

public class OddsComparisonSplitter {
	
	private static Logger logger;
	private static final int CONCURRENT_COMPARISON_THREAD = 2;
	private Map<String, List<RecordOutput>> threadComparisonResultMap;
	private ExecutorService executor;

	public OddsComparisonSplitter() {
		logger = (new BlueSheepLogger(OddsComparisonSplitter.class)).getLogger();
		threadComparisonResultMap = new ConcurrentHashMap<String, List<RecordOutput>>();
	}
	
	public List<RecordOutput> startComparisonOdds(ChiaveEventoScommessaInputRecordsMap map, Sport sport, String comparisonType) {
		
		List<RecordOutput> returnList = new ArrayList<RecordOutput>();
		
		Map<Date, Map<String, Map<Scommessa,List<AbstractInputRecord>>>> dataToBeSplitted = map.get(sport);
		List<Date> keysList = new ArrayList<Date>(dataToBeSplitted.keySet());
		
		logger.log(Level.CONFIG, "Events to be compared are " + keysList.size());
		
		int pageSize = keysList.size()/CONCURRENT_COMPARISON_THREAD;
		executor = Executors.newFixedThreadPool(CONCURRENT_COMPARISON_THREAD);
		
		for(int i = 0; i < CONCURRENT_COMPARISON_THREAD; i++) {
			
			int startIndex = i * pageSize;
			int endIndex = i == (CONCURRENT_COMPARISON_THREAD - 1) ? keysList.size() : startIndex + pageSize;
			startNewComparisonThread(keysList.subList(startIndex, endIndex), dataToBeSplitted, threadComparisonResultMap, comparisonType, sport);
		}
		int secondsToWait = 10;
		long startTime = System.currentTimeMillis();
		//Attende il tempo di timeout o la completa esecuzione corretta delle richieste
		while(threadComparisonResultMap.keySet().size() != CONCURRENT_COMPARISON_THREAD && !(System.currentTimeMillis() - startTime > secondsToWait * 1000L)) {
			
			logger.log(Level.CONFIG, "WAITING FOR ODDS COMPARISONS COMPLETION: threads have already processed data are " + threadComparisonResultMap.keySet().size());
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);				
			}
		}
		
		for(String idThread : threadComparisonResultMap.keySet()) {
			returnList.addAll(threadComparisonResultMap.get(idThread));
		}
		
		executor.shutdown();
		threadComparisonResultMap.clear();
		
		return returnList;
		
	}

	private void startNewComparisonThread(List<Date> subList,
			Map<Date, Map<String, Map<Scommessa, List<AbstractInputRecord>>>> dataToBeSplitted, 
			Map<String, List<RecordOutput>> threadComparisonResultMap,
			String comparisonType, Sport sport) {
		
		executor.submit(CompareThreadHelperFactory.
				getCorrectCompareThreadHelperByString(comparisonType, 
													  subList, 
													  dataToBeSplitted, 
													  threadComparisonResultMap, sport));
	}
	
}
