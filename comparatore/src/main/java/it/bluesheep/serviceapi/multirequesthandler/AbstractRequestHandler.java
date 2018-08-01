package it.bluesheep.serviceapi.multirequesthandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.bluesheep.util.BlueSheepLogger;

public abstract class AbstractRequestHandler {
	
	protected Logger logger;
	protected ExecutorService executor;
	protected Map<String, String> mapThreadResponse;
	protected int maxThreadPoolSize;
	protected List<String> requestIdsList;
	protected String token;
	
	protected AbstractRequestHandler(int maxThreadPoolSize, String token) {
		this.maxThreadPoolSize = maxThreadPoolSize;
		this.token = token;
		this.logger = (new BlueSheepLogger(AbstractRequestHandler.class)).getLogger();
	}
	
	public List<String> startMultithreadMarketRequests(List<String> ids){
		
		requestIdsList = ids;
		
		List<String> resultList = new ArrayList<String>();
		mapThreadResponse = new ConcurrentHashMap<String, String>();
		executor = Executors.newFixedThreadPool(maxThreadPoolSize);
		
		logger.log(Level.INFO, "Size requests to handle: " + ids.size());
		
		int sizeWait = ids.size() * 2;
		resultList = runThreadRequests(requestIdsList, sizeWait);
		
		logger.log(Level.INFO, "Requests execution completed. Shutting down executor. Responses size = " + resultList.size());
		
		executor.shutdown();
		mapThreadResponse.clear();
		
		return resultList;
	}

	protected abstract List<String> runThreadRequests(List<String> requestIdsList, int sizeWait);
	
}
