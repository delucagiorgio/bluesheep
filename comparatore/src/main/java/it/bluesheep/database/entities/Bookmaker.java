package it.bluesheep.database.entities;

public class Bookmaker extends AbstractBlueSheepEntity {

	private String bookmakerName;
	private boolean active;
	
	private Bookmaker(String name) {
		super();
		bookmakerName = name;
		setActive(true);
	}
	
	private Bookmaker(String name, long id, boolean active) {
		super(id);
		bookmakerName = name;
		setActive(active);
	}

	public String getBookmakerName() {
		return bookmakerName;
	}
	
	public static Bookmaker getBlueSheepBookmakerFromDatabaseInfo(String bookmakerName, long id, boolean active) {
		return new Bookmaker(bookmakerName, id, active);
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
