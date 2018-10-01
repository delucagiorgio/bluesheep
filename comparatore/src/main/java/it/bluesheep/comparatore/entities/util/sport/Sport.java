package it.bluesheep.comparatore.entities.util.sport;

public enum Sport {

	CALCIO("CALCIO"),
	TENNIS("TENNIS");
	
	private String code;
	
	private Sport(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static Sport getSportByCode(String sportBookmaker) {
		for(Sport sport : Sport.values()) {
			if(sport.getCode().equalsIgnoreCase(sportBookmaker)) {
				return sport;
			}
		}
		return null;
	}
	
}
