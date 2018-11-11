package it.bluesheep.database.entities;

public class RF extends AbstractBlueSheepEntity {

	private String rfText;
	private String rfCode;
	private boolean active;
	
	public boolean isSameRecord(RF rf) {
		return super.sameRecord(rf);
	}
	
	public RF(String rfText, String rfCode, boolean active, long id) {
		super(id);
		this.active = active;
		this.rfCode = rfCode;
		this.rfText = rfText;
	}

	public static RF getRFFromDatabaseInfo(String rfText, String rfCode, boolean active, long id) {
		return new RF(rfText, rfCode, active, id);
	}
	
	@Override
	public String getTelegramButtonText() {
		return rfText;
	}

	public String getRfText() {
		return rfText;
	}

	public String getRfCode() {
		return rfCode;
	}

	public boolean isActive() {
		return active;
	}

}
