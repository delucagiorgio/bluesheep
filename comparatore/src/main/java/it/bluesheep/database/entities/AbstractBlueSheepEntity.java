package it.bluesheep.database.entities;

public abstract class AbstractBlueSheepEntity {

	private Long id;
	
	protected AbstractBlueSheepEntity(long id) {
		this.id = id;
	}
	
	protected AbstractBlueSheepEntity() {}

	public Long getId() {
		return id;
	}
	
	public boolean sameRecord(AbstractBlueSheepEntity entity) {
		return this.id.equals(entity.getId());
	}
	
	public abstract String getTelegramButtonText();	
}
