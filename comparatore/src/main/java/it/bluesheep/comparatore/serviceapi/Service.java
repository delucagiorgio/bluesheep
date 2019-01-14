package it.bluesheep.comparatore.serviceapi;

import java.util.Arrays;

public enum Service {
	
	TXODDS_SERVICENAME("TX_ODDS"),
	BETFAIR_SERVICENAME("BETFAIR"),
	BET365_SERVICENAME("BET365"),
	STARVEGAS_SERVICENAME("STARVEGAS"),
	GOLDBET_SERVICENAME("GOLDBET"),
	PINTERBET_SERVICENAME("PINTERBET"),
	BETFLAG_SERVICENAME("BETFLAG"),
	CSV_SERVICENAME("CSV"),
	EVERY_MATRIX("EVERY_MATRIX"),
	USERPREFNOTIFICATION_SERVICE("USERPREFNOTIFICATION"),
	UPDATE_DB_BLUESHEEP_USER("UPDATE_DB_BLUESHEEP_USER"),
	UPDATE_DB_NOTIFICATION_USER("UPDATE_DB_NOTIFICATION_USER"),
	CUSTOM_FILETABLE_CREATOR_SERVICENAME("CUSTOM_FILETABLE_CREATOR");
	
	private String code;
	
	private Service(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static Service getServiceFromString(String string) {
		for(Service service : Service.values()) {
			if(string.equalsIgnoreCase(service.code)) {
				return service;
			}
		}
		return null;
	}

	public static boolean isScrapingService(Service serviceName) {
		return Arrays.asList(STARVEGAS_SERVICENAME, GOLDBET_SERVICENAME, PINTERBET_SERVICENAME, BETFLAG_SERVICENAME).contains(serviceName);
	}

}
