package it.bluesheep.servicehandler.servicemanager;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.serviceapi.Service;

public class CSVServiceHandler extends AbstractBlueSheepServiceHandler{

	protected CSVServiceHandler() {
		super();
		logger = Logger.getLogger(CSVServiceHandler.class);
		serviceName = Service.CSV_SERVICENAME;
	}
}
