package it.bluesheep.servicehandler.servicemanager;

import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepLogger;

public class CSVServiceHandler extends AbstractBlueSheepServiceHandler{

	protected CSVServiceHandler() {
		super();
		logger = (new BlueSheepLogger(CSVServiceHandler.class)).getLogger();
		serviceName = Service.CSV_SERVICENAME;
	}
}
