package it.bluesheep.service.api;

import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.sport.Sport;

public interface IApiInterface {
	public String getData(String sport, String oddsType);	
}
