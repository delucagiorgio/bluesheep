package it.bluesheep.database.entities;

import java.sql.Timestamp;

public class Event extends AbstractBlueSheepEntity {

	private String event;
	private String partecipant1;
	private String partecipant2;
	private Timestamp dateEvent;
	private boolean active;
	
	public Event(String event, String partecipant1, String partecipant2, Timestamp dateEvent, long id, boolean active, Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, updateTime);
		this.dateEvent = dateEvent;
		this.event = event;
		this.partecipant1 = partecipant1;
		this.partecipant2 = partecipant2;
	}

	@Override
	public String getTelegramButtonText() {
		return event;
	}
	
	public static Event getEventFromDatabaseInfo(String event, String partecipant1, String partecipant2, Timestamp dateEvent, long id, boolean active, Timestamp createTime, Timestamp updateTime) {
		return new Event(event, partecipant1, partecipant2, dateEvent, id, active, createTime, updateTime);
	}
	
	public boolean isSameRecord(Event event) {
		return super.sameRecord(event);
	}

	public String getEvent() {
		return event;
	}

	public String getPartecipant1() {
		return partecipant1;
	}

	public String getPartecipant2() {
		return partecipant2;
	}

	public Timestamp getDateEvent() {
		return dateEvent;
	}

	public boolean isActive() {
		return active;
	}

}
