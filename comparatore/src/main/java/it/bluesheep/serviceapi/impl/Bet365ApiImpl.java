package it.bluesheep.serviceapi.impl;

import java.util.List;
import java.util.logging.Logger;

import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.serviceapi.IApiInterface;
import it.bluesheep.util.BlueSheepLogger;

public class Bet365ApiImpl implements IApiInterface {

	private static Logger logger;
	private static final String SOCCER = "1";
	private static final String TENNIS = "13";
	private static final String THREE_WAY = "1_1";
//	private static final String 
	
	public Bet365ApiImpl() {
		this.logger = (new BlueSheepLogger(Bet365ApiImpl.class)).getLogger();
	}
	
	@Override
	public List<String> getData(Sport sport, Scommessa scommessa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String identifyCorrectBetCode(Scommessa scommessa, Sport sport) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String identifyCorrectGameCode(Sport sport) {
		// TODO Auto-generated method stub
		return null;
	}

}
