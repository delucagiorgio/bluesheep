package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.database.dao.IFilterDAO;
import it.bluesheep.database.entities.Event;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.util.BlueSheepConstants;

public class EventDAO extends AbstractDAO<Event> implements IFilterDAO<Event> {

	private static EventDAO instance;
	public static final String tableName = "EVENT_FILTER";
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
			Timestamp dateEvent = getTimestampFromResultSet(returnSelect, DATEEVENT);
			boolean active = returnSelect.getBoolean(ACTIVE);
			long id = returnSelect.getLong(ID);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			eventList.add(Event.getEventFromDatabaseInfo(eventName, partecipant1, partecipant2, dateEvent, id, active, createTime, updateTime));
		}
		
		return eventList;
	}

	@Override
	protected String getAllColumnValuesFromEntity(Event entity) {
		return "("
				+ EVENTNAME + BlueSheepConstants.REGEX_COMMA
				+ PARTECIPANT1 + BlueSheepConstants.REGEX_COMMA
				+ PARTECIPANT2 + BlueSheepConstants.REGEX_COMMA
				+ ACTIVE + BlueSheepConstants.REGEX_COMMA +
				"?" + BlueSheepConstants.REGEX_COMMA +
				"?" +")";
	}

	@Override
	public List<Event> getAllRowFromButtonText(String textButton) {
		
		List<Event> events = null;
		String query = getBasicSelectQuery() + WHERE + EVENTNAME + " = ? " + AND + ACTIVE + IS + true;
		
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, textButton);
			events = getMappedObjectBySelect(ps);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}

		return events;
	}

	@Override
	public Event getSingleRowFromButtonText(String textButton) throws MoreThanOneResultException {
		return getSingleResult(getAllRowFromButtonText(textButton));
	}

}
