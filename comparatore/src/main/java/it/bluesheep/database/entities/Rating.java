package it.bluesheep.database.entities;

import java.sql.Timestamp;

public class Rating extends AbstractBlueSheepFilterEntity {

	private Double ratingValue;
	private String ratingText;
	private String ratingCode;
	private boolean active;
	
	protected Rating(Double ratingValue, String ratingText, String ratingCode, boolean active, long id, Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, updateTime, "" + ratingValue);
		this.ratingCode = ratingCode;
		this.ratingText = ratingText;
		this.active = active;
		this.ratingValue = ratingValue;
	}
	
	public static Rating getRatingFromDatabaseInfo(Double ratingValue, String ratingText, String ratingCode, boolean active, long id, Timestamp createTime, Timestamp updateTime) {
		return new Rating(ratingValue, ratingText, ratingCode, active, id, createTime, updateTime);
	}

	
	@Override
	public String getTelegramButtonText() {
		return ratingText;
	}


	public Double getRatingValue() {
		return ratingValue;
	}


	public String getRatingText() {
		return ratingText;
	}


	public String getRatingCode() {
		return ratingCode;
	}


	public boolean isActive() {
		return active;
	}

}
