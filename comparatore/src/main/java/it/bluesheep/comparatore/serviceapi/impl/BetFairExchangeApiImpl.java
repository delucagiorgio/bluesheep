package it.bluesheep.comparatore.serviceapi.impl;

import org.apache.log4j.Logger;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

/**
  	//ottenere gli eventi relativi alla scommessa da voler analizzare e del determinato sport
	//parsare i dati JSON ritornati per mappare i dati nelle classi EventoBetfair
	//aggiungere gli eventi alla mappa mercatoEventoBetfairMap
	//per ogni evento si vada a prendere il relativo marketId e lo si inserisca nella mappa, tramite la funzione 
	//mercatoEventoBetfairMap.addEventoBetfairMercatoByTipoScommessa(evento, scommessaTipo, marketId);
	//ottenere tutti i dati relativi ai marketId collezionati e ritornare il JSON contenente i dati delle quote
 * @author 
 *
 */
public class BetFairExchangeApiImpl extends AbstractBetfairApi {

	public BetFairExchangeApiImpl() {
		super(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_APPKEY_EX), 
				BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_BASE_URL_EX),
				BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_RESCRIPT_SUFFIX_EX),
				BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_EX_ENDPOINT), false);
		logger = Logger.getLogger(BetFairExchangeApiImpl.class);
	}
}