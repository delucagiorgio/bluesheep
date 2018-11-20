package it.bluesheep.comparatore.serviceapi;

public enum Service {
	
	TXODDS_SERVICENAME("TX_ODDS"),
	BETFAIR_SERVICENAME("BETFAIR"),
	BET365_SERVICENAME("BET365"),
	CSV_SERVICENAME("CSV"),
	EVERY_MATRIX("EVERY_MATRIX"),
	USERPREFNOTIFICATION_SERVICE("USERPREFNOTIFICATION");
	
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

}
