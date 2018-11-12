package it.bluesheep.database.entities;

import java.sql.Timestamp;

public abstract class AbstractBlueSheepEntity {

	private Long id;
	private Timestamp createTime;
	private Timestamp updateTime;
	
	protected AbstractBlueSheepEntity(long id, Timestamp createTime, Timestamp updateTime) {
		this.id = id;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}
	
	protected AbstractBlueSheepEntity() {}

	public Long getId() {
		return id;
	}
	
	public boolean sameRecord(AbstractBlueSheepEntity entity) {
		return this.id.equals(entity.getId());
	}
	
	public abstract String getTelegramButtonText();

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}	
}
