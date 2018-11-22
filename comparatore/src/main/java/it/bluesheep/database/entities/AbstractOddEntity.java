package it.bluesheep.database.entities;

import java.sql.Timestamp;

public abstract class AbstractOddEntity extends AbstractBlueSheepEntity {

	protected AbstractOddEntity(long id, Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, updateTime);
	}

	@Override
	public String getTelegramButtonText() {
		return null;
	}

}
