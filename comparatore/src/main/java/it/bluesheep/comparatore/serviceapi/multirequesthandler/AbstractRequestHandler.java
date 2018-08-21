package it.bluesheep.comparatore.serviceapi.multirequesthandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

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
	}
	
	public List<String> startMultithreadMarketRequests(List<String> ids){
		
		requestIdsList = ids;
		
		List<String> resultList = new ArrayList<String>();
		mapThreadResponse = new ConcurrentHashMap<String, String>();
		executor = Executors.newFixedThreadPool(maxThreadPoolSize);
		
		resultList = runThreadRequests(requestIdsList);
		
		logger.debug("Requests execution completed. Shutting down executor. Responses size = " + resultList.size());
		
		mapThreadResponse.clear();
		
		return resultList;
	}

	protected abstract List<String> runThreadRequests(List<String> requestIdsList);
	
}