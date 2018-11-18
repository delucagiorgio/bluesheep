package it.bluesheep.database.entities;

import java.sql.Timestamp;

public class Size extends AbstractBlueSheepFilterEntity {

	private String sizeText;
	private String sizeCode;
	private Double sizeValue;
	private boolean active;
	
	public boolean isSameRecord(Size size) {
		return super.sameRecord(size);
	}
	
	private Size(String sizeText, String sizeCode, Double sizeValue, boolean active, long id, Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, updateTime, "" + sizeValue);
		this.sizeText = sizeText;
		this.sizeCode = sizeCode;
		this.sizeValue = sizeValue;
		this.active = active;
	}
	
	public static Size getSizeFromDatabaseInfo(String sizeText, String sizeCode, Double sizeValue, boolean active, long id, Timestamp createTime, Timestamp updateTime) {
		return new Size(sizeText, sizeCode, sizeValue, active, id, createTime, updateTime);
	}

	@Override
	public String getTelegramButtonText() {
		return sizeText;
	}

	public String getSizeText() {
		return sizeText;
	}

	public String getSizeCode() {
		return sizeCode;
	}

	public Double getSizeValue() {
		return sizeValue;
	}

	public boolean isActive() {
		return active;
	}

}
