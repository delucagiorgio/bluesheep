package it.bluesheep.database.entities;

import java.sql.Timestamp;

public class RFType extends AbstractBlueSheepFilterEntity {
	
	private String refundText;
	private Double refundPercentage;
	private boolean active;
	
	public boolean isSameRecord(RFType rf) {
		return super.sameRecord(rf);
	}
	
	private RFType(String refundText, boolean active, long id, Double refundPercentage, Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, updateTime, "" + refundPercentage);
		this.active = active;
		this.refundText = refundText;
		this.refundPercentage = refundPercentage;
	}
	
	@Override
	public String getTelegramButtonText() {
		return refundText;
	}

	public String getRefundText() {
		return refundText;
	}

	public Double getRefundPercentage() {
		return refundPercentage;
	}

	public boolean isActive() {
		return active;
	}

	public static RFType getRFTypeFromDatabaseInfo(Double refundPercentage, String refundText, boolean active, Timestamp createTime, Timestamp updateTime, long id) {
		return new RFType(refundText, active, id, refundPercentage, createTime, updateTime);
	}

}
