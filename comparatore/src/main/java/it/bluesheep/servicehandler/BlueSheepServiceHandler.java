package it.bluesheep.servicehandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.bluesheep.util.BlueSheepLogger;

public final class BlueSheepServiceHandler {

	private static BlueSheepServiceHandler instance;
	private static Logger logger;
	
	private static ScheduledExecutorService executor;
	
	private BlueSheepServiceHandler() {
		logger = (new BlueSheepLogger(BlueSheepServiceHandler.class)).getLogger();
	}
	
	public static synchronized BlueSheepServiceHandler getBlueSheepServiceHandlerInstance() {
		if(instance == null) {
			instance = new BlueSheepServiceHandler();
		}
		return instance;
	}
	
	
	public void start() throws InterruptedException {
		
		//1. avvio tutti i servizi come thread indipendenti e lascio la gestione all'executor
		logger.log(Level.INFO, "Starting all active services");
		executor = Executors.newScheduledThreadPool(3);

		executor.scheduleWithFixedDelay(ComparatoreServiceHandler.getComparisonOperationManagerFactory(), 0, 30, TimeUnit.SECONDS);
		
		executor.scheduleWithFixedDelay(ArbitraggiServiceHandler.getArbitraggiServiceHandlerInstance(), 4, 1, TimeUnit.MINUTES);
		
		executor.scheduleAtFixedRate(JsonGeneratorServiceHandler.getJsonGeneratorServiceHandlerInstance(), 2, 2, TimeUnit.MINUTES);
		
		while(true) {
			Thread.sleep(5 * 60 * 1000L);
		}
		//2. monitoro la situazione delle proprietà: nel caso in cui cambiano, modifico la risorsa condivisa
		//	 in modo che tutti i servizi possano poi procedere al corretto utilizzo delle risorse secondo le proprietà
		
		//3. controlla lo stato dell'applicazione generale: se da stoppare, controlla che tutti i thread siano terminati
		//	 e chiude il pool di thread relativo ai servizi
		
		
	}
	
}
