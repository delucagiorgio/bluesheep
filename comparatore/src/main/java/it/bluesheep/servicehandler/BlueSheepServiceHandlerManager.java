package it.bluesheep.servicehandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.util.TranslatorUtil;
import it.bluesheep.comparatore.io.datacompare.util.BookmakerLinkGenerator;
import it.bluesheep.comparatore.io.datainput.operationmanager.service.util.InputDataHelper;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.servicemanager.BlueSheepServiceHandlerFactory;
import it.bluesheep.telegrambot.TelegramBotHandler;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepSharedResources;

/**
 * 
 * @author GD 
 * Classe che gestisce tutti i servizi attivi nell'applicazione e garantisce la corretta applicazione
 * dei cambiamenti a livello di proprietà. La classe gestisce anche lo spegnimento e la disattivazione 
 * dei servizi.
 *
 */
public final class BlueSheepServiceHandlerManager {

	private static final long SINGLE_EXECUTION = -129;
	private static BlueSheepServiceHandlerManager instance;
	private static Logger logger;
	private static Properties properties = new Properties(); 
	private static ScheduledExecutorService executor;
	
	/**
	 * Avvia le fasi iniziali e inizializza le informazioni provenienti dai file di proprietà
	 */
	private BlueSheepServiceHandlerManager() {
		updateInformationFromProperties();	
		TranslatorUtil.initializeMapFromFile();
		BookmakerLinkGenerator.initializeMap();
		logger = Logger.getLogger(BlueSheepServiceHandlerManager.class);
		logger.info(properties.toString());
	}
	
	public static synchronized BlueSheepServiceHandlerManager getBlueSheepServiceHandlerInstance() {
		if(instance == null) {
			instance = new BlueSheepServiceHandlerManager();
		}
		return instance;
	}
	
	/**
	 * GD - 05/08/18
	 * Avvia i servizi e monitora i cambiamenti delle proprietà: in base al tipo di file che viene modificato
	 * viene riconfigurata in maniera corretta l'applicazione, affinchè possa essere coerente con le proprietà 
	 * specificate
	 */
	public void start() {
		
		//1. avvio tutti i servizi come thread indipendenti e lascio la gestione all'executor
		logger.info("Starting all active services");
		boolean stopApplication = false;
		boolean propertiesConfigurationChanged = false;
		
		//Avvio il bot handler
		TelegramBotServiceHandler.getTelegramBotServiceHandlerInstance().run();
		
		boolean firstStartExecuted = false;
		
		do {
			BlueSheepSharedResources.initializeDataStructures();
			
			executor = Executors.newScheduledThreadPool(BlueSheepSharedResources.getActiveServices().size() + 2);
			long initialDelay = firstStartExecuted ? 0 : 120;

			for(Service activeService : BlueSheepSharedResources.getActiveServices().keySet()) {
				if(Service.BETFAIR_SERVICENAME.equals(activeService)) {
					initialDelay = 0;
				}else if(Service.TXODDS_SERVICENAME.equals(activeService)) {
					initialDelay = 60;
				}else {
					initialDelay = 120;
				}
				if(BlueSheepSharedResources.getActiveServices().get(activeService) > 0) {
					executor.scheduleWithFixedDelay(BlueSheepServiceHandlerFactory.getCorrectServiceHandlerByService(activeService), initialDelay, BlueSheepSharedResources.getActiveServices().get(activeService), TimeUnit.SECONDS);
				}else if(BlueSheepSharedResources.getActiveServices().get(activeService) == SINGLE_EXECUTION) {
					executor.submit(BlueSheepServiceHandlerFactory.getCorrectServiceHandlerByService(activeService));
				}
				initialDelay = firstStartExecuted ? 0 : 120;
			}
			
			long arbsFrequencySeconds = new Long(properties.getProperty(BlueSheepConstants.FREQ_ARBS_SEC));
			if(arbsFrequencySeconds > 0) {
				executor.scheduleWithFixedDelay(ArbitraggiServiceHandler.getArbitraggiServiceHandlerInstance(), initialDelay, arbsFrequencySeconds, TimeUnit.SECONDS);
			}else if(arbsFrequencySeconds == SINGLE_EXECUTION){
				executor.submit(ArbitraggiServiceHandler.getArbitraggiServiceHandlerInstance());
			}
			
			long jsonFrequencySeconds = new Long(properties.getProperty(BlueSheepConstants.FREQ_JSON_SEC));
			if(jsonFrequencySeconds > 0) {
				executor.scheduleAtFixedRate(JsonGeneratorServiceHandler.getJsonGeneratorServiceHandlerInstance(), initialDelay, jsonFrequencySeconds, TimeUnit.SECONDS);
			}else if(arbsFrequencySeconds == SINGLE_EXECUTION){
				executor.submit(ArbitraggiServiceHandler.getArbitraggiServiceHandlerInstance());
			}
			
			
			WatchService ws = null;
			
			firstStartExecuted = true;
			
			try {
				ws = FileSystems.getDefault().newWatchService();
				
				Path pathToResources = Paths.get(BlueSheepConstants.PATH_TO_RESOURCES);
				
				pathToResources.register(ws, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
				WatchKey key;
				propertiesConfigurationChanged = false;
				while(!stopApplication && !propertiesConfigurationChanged && (key = ws.take()) != null) {
					for (WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();
		                logger.info("Event kind:" + kind + " - File affected: " + event.context());
		                
		                if(kind == StandardWatchEventKinds.ENTRY_MODIFY) {
		                	String fileName = event.context().toString();
			                switch(fileName) {
			                	case BlueSheepConstants.TRADUZIONI_NAZIONI_FILENAME:
			                	case BlueSheepConstants.TRADUZIONI_API_FILENAME:
			                		logger.info("File " + fileName + " has changed. Map of data is going to be updated.");
			                		TranslatorUtil.initializeMapFromFile();
			                		break;
			                	case BlueSheepConstants.CSV_FILENAME:
			                		logger.info("File " + BlueSheepConstants.CSV_FILENAME + " has changed. CSV is going to be recalculated");
			                		BlueSheepSharedResources.setCsvToBeProcessed(Boolean.TRUE);
			                		break;
			                	case BlueSheepConstants.BOOKMAKER_LINK_FILENAME:
			                		logger.info("File " + BlueSheepConstants.BOOKMAKER_LINK_FILENAME + " has changed. BookmakerLinkMap is going to be updated");
			                		BookmakerLinkGenerator.initializeMap();
			                		break;
			                	case BlueSheepConstants.PROPERTIES_FILENAME:
			                		logger.info("File " + BlueSheepConstants.PROPERTIES_FILENAME + " has changed. Properties are going to be updated");
			                		updateInformationFromProperties();
			                		InputDataHelper.getInputDataHelperInstance().forceUpdateMapBlockedBookmakers();
			                		propertiesConfigurationChanged = true;
			                		logger.info(getProperties().toString());
			                		break;
			                	default:
			                		logger.info("No particular actions are required for the changed file");
			                }
			            }else if(StandardWatchEventKinds.ENTRY_DELETE == kind){
			            	if(BlueSheepConstants.BLUESHEEP_APP_STATUS.equalsIgnoreCase(event.context().toString())) {
			            		logger.info("File " + BlueSheepConstants.BLUESHEEP_APP_STATUS + " has been deleted: application is going to be stopped");
			            		stopApplication = true;
			            	}
			            }
		                key.reset();
					}
				}
				ws.close();
				
				boolean terminatedCorrectly = true;
				if(stopApplication) {
					long timeout = 30;
					TimeUnit timeUnitTimeout = TimeUnit.MINUTES;
					String message = "all executors";
					
					if(stopApplication) {
						message = "Start shutting down " + message;
					}else if(propertiesConfigurationChanged) {
						message = "Restarting " + message;
					}
					
					logger.info(message + ". Timeout to force shutdown is " + timeout + " " + timeUnitTimeout);
					
					executor.shutdown();
					terminatedCorrectly = executor.awaitTermination(timeout, timeUnitTimeout);
					if(!terminatedCorrectly) {
						executor.shutdownNow();
					}
				}
				
				logger.info("Application completes the executions correctly = " + terminatedCorrectly);
				
			} catch (IOException | InterruptedException e) {
				logger.error(e.getMessage(), e);
				if(!executor.isShutdown()) {
					executor.shutdownNow();
				}
			}
		}while(!stopApplication || propertiesConfigurationChanged);
		
		TelegramBotHandler.getTelegramBotHandlerInstance().stopExecution();

	}

	public static Properties getProperties() {
		return properties;
	}
	
	/**
	 * GD - 05/08/18
	 * Carica le informazioni del file "bluesheepComparatore.properties" nell'oggetto properties della classe.
	 * 
	 * Il metodo viene chiamato all'avvio dell'applicazione e ad ogni modifica che il file subisce
	 */
	private static void updateInformationFromProperties() {
		try {
			InputStream in = new FileInputStream(BlueSheepConstants.PATH_TO_RESOURCES + BlueSheepConstants.PROPERTIES_FILENAME);

			synchronized (properties) {
				properties = new Properties();
				properties.load(in);
			}
			
			in.close();
		} catch (IOException e) {
			if(logger != null) {
				logger.error(e.getMessage(), e);
			}else {
				System.out.println("Error retrieving properties\n" + e.getMessage());
			}
		}
	}
}
