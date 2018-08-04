package it.bluesheep.comparatore.serviceapi.util;

import java.util.Map;

public abstract class AbstractRequestThreadHelper extends Thread {
	
	protected String token;
	protected Map<String, String> resultThreadRequest;

	@Override
	public abstract void run();

}
