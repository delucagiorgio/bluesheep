package it.bluesheep.comparatore.serviceapi.impl;

import org.apache.log4j.Logger;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

public class BetFairSportbookApiImpl extends AbstractBetfairApi {

	
	public BetFairSportbookApiImpl() {
		super(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_APPKEY_SB), 
				BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_BASE_URL_SB),
				BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_RESCRIPT_SUFFIX_SB),
				BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_SB_ENDPOINT), true);
		logger = Logger.getLogger(BetFairSportbookApiImpl.class);
	}
}
