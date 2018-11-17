package it.bluesheep.database.entities;

import java.sql.Timestamp;

public class MinOddValue extends AbstractBlueSheepFilterEntity {

	private Double minOddValue;
	private String minOddText;
	private String minOddCode;
	private boolean active;
	
	private MinOddValue(Double minOddValue, String minOddText, String minOddCode, boolean active, long id, Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, updateTime, "" + minOddValue);
		this.active = active;
		this.minOddCode = minOddCode;
		this.minOddText = minOddText;
		this.minOddValue = minOddValue;
	}
	
	public static MinOddValue getMinOddValueFromDatabaseInfo(Double minOddValue, String minOddText, String minOddCode, boolean active, long id, Timestamp createTime, Timestamp updateTime) {
		return new MinOddValue(minOddValue, minOddText, minOddCode, active, id, createTime, updateTime);
	}
	
	@Override
	public String getTelegramButtonText() {
		return minOddText;
	}

	public Double getMinOddValue() {
		return minOddValue;
	}

	public String getMinOddText() {
		return minOddText;
	}

	public String getMinOddCode() {
		return minOddCode;
	}

	public boolean isActive() {
		return active;
	}

}
