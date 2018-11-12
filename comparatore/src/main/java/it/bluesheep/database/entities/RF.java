package it.bluesheep.database.entities;

import java.sql.Timestamp;

public class RF extends AbstractBlueSheepEntity {

	private String rfText;
	private String rfCode;
	private Double rfValue;
	private boolean active;
	
	public boolean isSameRecord(RF rf) {
		return super.sameRecord(rf);
	}
	
	public RF(String rfText, String rfCode, boolean active, long id, Double rfValue, Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, updateTime);
		this.active = active;
		this.rfCode = rfCode;
		this.rfText = rfText;
		this.rfValue = rfValue;
	}

	public static RF getRFFromDatabaseInfo(String rfText, String rfCode, boolean active, long id, Double rfValue, Timestamp createTime, Timestamp updateTime) {
		return new RF(rfText, rfCode, active, id, rfValue, createTime, updateTime);
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

	public Double getRfValue() {
		return rfValue;
	}

}
