package it.bluesheep.comparatore.entities.util.comparevalue.rating;

/**
 * Classe che definisce il calcolo dei rating1 per la categoria di confronto quote Bookmaker - Exchange
 * @author Giorgio De Luca
 *
 */
public class RatingCalculatorBookMakerExchangeOdds extends RatingCalculator {

	protected RatingCalculatorBookMakerExchangeOdds() {
		super();
	}
	
	@Override
	protected double calculateRating(double quotaBookmaker1, double quotaBookmaker2) {
		double returnValue = 0;
		
		if(quotaBookmaker1 == 0 || quotaBookmaker2 == 0) {
			//TODO gestire caso di errore
		}
		
		//Rating = quotaBookmaker * 0,95 / (quotaExchange - 0,05)
		returnValue = quotaBookmaker1 * 0.95 / (quotaBookmaker2 - 0.05);
		
		return returnValue;
	}

	@Override
	protected double calculateRiskFreeRating(double quotaBookmaker1, double quotaBookmaker2) {
		return 0;
//		double returnValue = 0;
//		
//		if(quotaBookmaker1 == 0 || quotaBookmaker2 == 0) {
//			//TODO gestire caso di errore
//		}
//		
//		//RF = (quotaBookmaker - 1 ) * (1 - (quotaExchange - 1)) / (quotaExchange - 0,05)
//		returnValue = (quotaBookmaker1 - 1) * (1 - (quotaBookmaker2 - 1)) / (quotaBookmaker2 - 0.05);
//		
//		return returnValue;
	}

}
