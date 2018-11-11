package it.bluesheep.database.entities;

public class Championship extends AbstractBlueSheepEntity {

	private String championshipName;
	private boolean active;
	
	private Championship(String championshipName, long id, boolean active) {
		super(id);
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
	
	public static Championship getChampionshipFromDatabaseInfo(String championshipName, long id, boolean active) {
		return new Championship(championshipName, id, active);
	}

	public String getChampionshipName() {
		return championshipName;
	}

	public boolean isActive() {
		return active;
	}
	
}
