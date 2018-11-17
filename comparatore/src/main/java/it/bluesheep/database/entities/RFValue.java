package it.bluesheep.database.entities;

import java.sql.Timestamp;

public class RFValue extends AbstractBlueSheepFilterEntity {

	private String rfText;
	private String rfCode;
	private Double rfValue;
	private boolean active;
	
	public boolean isSameRecord(RFValue rf) {
		return super.sameRecord(rf);
	}
	
	private RFValue(String rfText, String rfCode, boolean active, long id, Double rfValue, Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, updateTime, "" + rfValue);
		this.active = active;
		this.rfCode = rfCode;
		this.rfText = rfText;
		this.rfValue = rfValue;
	}

	public static RFValue getRFFromDatabaseInfo(String rfText, String rfCode, boolean active, long id, Double rfValue, Timestamp createTime, Timestamp updateTime) {
		return new RFValue(rfText, rfCode, active, id, rfValue, createTime, updateTime);
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
