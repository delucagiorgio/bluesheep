package it.bluesheep.arbitraggi.entities;


public enum RecordOutputType {
	
	TWO_WAY("2WAY"),
	THREE_WAY("3WAY");
	
	private String code;
	
	private RecordOutputType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static RecordOutputType getServiceFromString(String string) {
		for(RecordOutputType recordType : RecordOutputType.values()) {
			if(string.equalsIgnoreCase(recordType.code)) {
				return recordType;
			}
		}
		return null;
	}


}
