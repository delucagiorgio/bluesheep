package it.bluesheep.database.entities;

import java.sql.Timestamp;

public class Bookmaker extends AbstractBlueSheepEntity {

	private String bookmakerName;
	private boolean active;
	
	private Bookmaker(String name) {
		super();
		bookmakerName = name;
		setActive(true);
	}
	
	private Bookmaker(String name, long id, boolean active, Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, createTime);
		bookmakerName = name;
		setActive(active);
	}

	public String getBookmakerName() {
		return bookmakerName;
	}
	
	public static Bookmaker getBlueSheepBookmakerFromDatabaseInfo(String bookmakerName, long id, boolean active, Timestamp createTime, Timestamp updateTime) {
		return new Bookmaker(bookmakerName, id, active, createTime, updateTime);
	}
	
	public static Bookmaker getBlueSheepBookmakerFromBookmakerNameInfo(String bookmakerName) {
		return new Bookmaker(bookmakerName);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String getTelegramButtonText() {
		return bookmakerName;
	}
	
}
