package it.bluesheep.entities.util.sport;

public enum Sport {

	CALCIO("calcio_1"),
	TENNIS("tennis_5");
	
	private String code;
	
	private Sport(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
}
