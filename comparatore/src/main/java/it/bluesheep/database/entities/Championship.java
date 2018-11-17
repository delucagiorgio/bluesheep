package it.bluesheep.database.entities;

import java.sql.Timestamp;

public class Championship extends AbstractBlueSheepFilterEntity {

	private String championshipName;
	private boolean active;
	
	private Championship(String championshipName, long id, boolean active, Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, updateTime, championshipName);
		this.championshipName = championshipName;
		this.active = active;
	}

	@Override
	public String getTelegramButtonText() {
		return championshipName;
	}

	public boolean sameRecord(Championship championship) {
		return super.sameRecord(championship);
	}
	
	public static Championship getChampionshipFromDatabaseInfo(String championshipName, long id, boolean active, Timestamp createTime, Timestamp updateTime) {
		return new Championship(championshipName, id, active, createTime, updateTime);
	}

	public String getChampionshipName() {
		return championshipName;
	}

	public boolean isActive() {
		return active;
	}
	
}
