package it.bluesheep.entities.util.sport;

public enum Sport {

	CALCIO("calcio"),
	TENNIS("tennis");
	
	private String code;
	
	private Sport(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
}
