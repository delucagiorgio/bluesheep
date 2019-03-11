package it.bluesheep.comparatore.entities.util.comparevalue.rating;

import it.bluesheep.comparatore.serviceapi.Service;

public class RatingCalculatorFactory {

	private RatingCalculatorFactory() {}
	
	public static RatingCalculator getRatingCalculator(Service comparisonType) {
		if(Service.TXODDS_SERVICENAME.equals(comparisonType)) {
			return new RatingCalculatorBookmakersOdds();
		}else if(Service.BETFAIR_EX_SERVICENAME.equals(comparisonType)) {
			return new RatingCalculatorBookMakerExchangeOdds();
		}
		return null;
	}
	
}
