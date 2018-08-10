package it.bluesheep.serviceapi.multirequesthandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import it.bluesheep.entities.util.ComparatoreConstants;
import it.bluesheep.serviceapi.util.Bet365RequestThreadHelper;

public class Bet365RequestHandler extends AbstractRequestHandler {
	
	public Bet365RequestHandler(int maxThreadPoolSize, String token) {
		super(maxThreadPoolSize, token);
	}
	
	@Override
	protected List<String> runThreadRequests(List<String> ids) {
	
		List<String> resultList = new ArrayList<String>();

		for(int j = 0; j < ids.size() ; j++) {
			
			String https_url = "https://api.betsapi.com/v1/bet365/start_sp?token="+token+"&event_id="+ids.get(j)+"&charset="+ComparatoreConstants.ENCODING_UTF_8.toLowerCase();
			executor.submit(new Bet365RequestThreadHelper(https_url, token, mapThreadResponse));
			
			boolean isLastQueueRequest = (j + 1) == ids.size();
			if((j + 1) % maxThreadPoolSize == 0 || isLastQueueRequest) {
				boolean timeoutReached = true;
				try {
					executor.shutdown();

					timeoutReached = !executor.awaitTermination(120, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					logger.log(Level.WARNING, e.getMessage(), e);
				}
				if(!isLastQueueRequest) {
					executor = Executors.newFixedThreadPool(maxThreadPoolSize);
				}
				
				if(timeoutReached) {
					logger.log(Level.WARNING, "" + this.getClass().getSimpleName() + " timeout reached = " + timeoutReached);
				}				
			}
		}
		
		for(String idJSON : mapThreadResponse.keySet()) {
			resultList.add(mapThreadResponse.get(idJSON));
		}

		return resultList;
	}

}
