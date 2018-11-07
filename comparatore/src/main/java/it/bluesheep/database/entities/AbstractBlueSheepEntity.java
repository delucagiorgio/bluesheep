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
	
	protected boolean sameRecord(AbstractBlueSheepEntity entity) {
		return this.id.equals(entity.getId());
	}
	
}
