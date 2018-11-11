package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.entities.Event;
import it.bluesheep.util.BlueSheepConstants;

public class EventDAO extends AbstractDAO<Event> {

	private static EventDAO instance;
	public static final String tableName = "EVENT";
	private static final String EVENTNAME = "event";
	private static final String PARTECIPANT1 = "partecipant1";
	private static final String PARTECIPANT2 = "partecipant2";
	private static final String DATEEVENT = "dateEvent";
	private static final String ACTIVE = "active";
	
	protected EventDAO(Connection connection) {
		super(tableName, connection);
	}
	
	public static synchronized EventDAO getEventDAOInstance(Connection connection) {
		if(instance == null) {
			instance = new EventDAO(connection);
		}
		return instance;
	}

	@Override
	protected List<Event> mapDataIntoObject(ResultSet returnSelect) throws SQLException {
		
		List<Event> eventList = new ArrayList<Event>();
		
		while(returnSelect.next()) {
			String eventName = returnSelect.getString(EVENTNAME);
			String partecipant1 = returnSelect.getString(PARTECIPANT1);
			String partecipant2 = returnSelect.getString(PARTECIPANT2);
			Timestamp dateEvent = returnSelect.getTimestamp(DATEEVENT);
			boolean active = returnSelect.getBoolean(ACTIVE);
			long id = returnSelect.getLong(ID);
			
			eventList.add(Event.getEventFromDatabaseInfo(eventName, partecipant1, partecipant2, dateEvent, id, active));
		}
		
		return eventList;
	}

	@Override
	protected String getAllColumnValuesFromEntity(Event entity) {
		return "("
				+ EVENTNAME + BlueSheepConstants.REGEX_COMMA
				+ PARTECIPANT1 + BlueSheepConstants.REGEX_COMMA
				+ PARTECIPANT2 + BlueSheepConstants.REGEX_COMMA
				+ ACTIVE + ")";
	}

}
