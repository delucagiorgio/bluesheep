package it.bluesheep.database;

public enum ProcessStatus {
	
	RUNNING("In corso"),
	COMPLETED("Completata"),
	ERROR("In errore");
	
	private String code;
	
	ProcessStatus(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public static ProcessStatus getProcessStatusFromCode(String status) {
		for(ProcessStatus ps : ProcessStatus.values()) {
			if(ps.getCode().equals(status)) {
				return ps;
			}
		}
		return null;
	}

}
