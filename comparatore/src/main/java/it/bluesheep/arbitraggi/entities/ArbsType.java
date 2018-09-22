package it.bluesheep.arbitraggi.entities;

public enum ArbsType {
	
	TWO_WAY("2"),
	THREE_WAY("3");

	private String code;
	
	ArbsType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
