package it.bluesheep.serviceapi.multirequesthandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import it.bluesheep.entities.util.ComparatoreConstants;
import it.bluesheep.serviceapi.util.Bet365RequestThreadHelper;

public class Bet365RequestHandler extends AbstractRequestHandler {
	
	public Bet365RequestHandler(int maxThreadPoolSize, String token) {
		super(maxThreadPoolSize, token);
	}
	
	@Override
	protected List<String> runThreadRequests(List<String> ids, int sizeWait) {
	
		List<String> resultList = new ArrayList<String>();
		
		for(int j = 0; j < ids.size() ; j++) {
			
			
			String https_url = "https://api.betsapi.com/v1/bet365/start_sp?token="+token+"&event_id="+ids.get(j)+"&charset="+ComparatoreConstants.ENCODING_UTF_8.toLowerCase();
			executor.submit(new Bet365RequestThreadHelper(https_url, token, mapThreadResponse));
			
			boolean isLastQueueRequest = (j + 1) == ids.size();
			if((j + 1) % maxThreadPoolSize == 0 || isLastQueueRequest) {
			
				boolean allFinished = false;
				long startTime = System.currentTimeMillis();
				int sizeMapParameter = maxThreadPoolSize;
				
				if(isLastQueueRequest && (j + 1) % maxThreadPoolSize != 0) {
					sizeMapParameter = (j + 1) % maxThreadPoolSize;
				}
				
				do{

					logger.log(Level.CONFIG, "WAITING FOR REQUESTS COMPLETION: Actual size of completed request list is " + mapThreadResponse.keySet().size() + "/" + sizeMapParameter);
					logger.log(Level.CONFIG, "Remains " + (sizeWait - (System.currentTimeMillis() - startTime ) / 1000) + " seconds to close request pool"); 
					
					if(System.currentTimeMillis() - startTime >= sizeWait * 1000L || mapThreadResponse.keySet().size() == sizeMapParameter) {
						allFinished = true;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.severe(e.getMessage());				
					}
				}while(!allFinished);
				
				for(String idJSON : mapThreadResponse.keySet()) {
					resultList.add(mapThreadResponse.get(idJSON));
				}
				
				mapThreadResponse.clear();
			}
		}
		return resultList;
	}

}
