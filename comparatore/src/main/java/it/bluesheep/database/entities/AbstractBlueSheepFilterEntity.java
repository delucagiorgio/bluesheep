package it.bluesheep.database.entities;

import java.sql.Timestamp;

public abstract class AbstractBlueSheepFilterEntity extends AbstractBlueSheepEntity {

	protected String codeDB;
	
	public AbstractBlueSheepFilterEntity(long id, Timestamp createTime, Timestamp updateTime, String code) {
		super(id, createTime, updateTime);
		this.codeDB = code;
	}

	@Override
	public abstract String getTelegramButtonText();

	public String getCodeDB() {
		return codeDB;
	}

}
