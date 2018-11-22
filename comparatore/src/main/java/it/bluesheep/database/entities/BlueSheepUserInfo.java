package it.bluesheep.database.entities;

import java.sql.Timestamp;

public class BlueSheepUserInfo extends AbstractBlueSheepEntity {

	private String telegramUsername;
	private String bluesheepGroup;
	private String bluesheepUsername;
	
	public BlueSheepUserInfo(String telegramUsername, String group, String bluesheepUsername, long id, Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, updateTime);
		this.telegramUsername = telegramUsername;
		this.bluesheepGroup = group;
		this.bluesheepUsername = bluesheepUsername;
	}
	
	@Override
	public String getTelegramButtonText() {
		return null;
	}

	public String getTelegramUsername() {
		return telegramUsername;
	}

	public String getGroup() {
		return bluesheepGroup;
	}

	public String getBluesheepUsername() {
		return bluesheepUsername;
	}

}
