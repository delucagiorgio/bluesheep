package it.bluesheep.comparatore.entities.util.comparevalue.rating;

/**
 * Classe che definisce il calcolo dei rating1 per la categoria di confronto quote Bookmaker - Bookmaker
 * @author Giorgio De Luca
 *
 */
public class RatingCalculatorBookmakersOdds extends RatingCalculator {

	protected RatingCalculatorBookmakersOdds() {
		super();
	}
	
	@Override
	protected double calculateRating(double quotaBookmaker1, double quotaBookmaker2) {
		double y = (100 * quotaBookmaker1)/quotaBookmaker2;
		
		y = Math.round(y);
		
		double approxRating = (100 * quotaBookmaker1) - y;
		
		return approxRating / 100.00;		
	}

	@Override
	protected double calculateRiskFreeRating(double quotaBookmaker1, double quotaBookmaker2) {
		double returnValue = 0;
		
//		if(quotaBookmaker1 == 0 || quotaBookmaker2 == 0) {
//			//TODO gestire caso di errore
//		}
//		
//		//RFValue = (quotaBookmaker1 - 1) * (1 - 1 / quotaBookmaker2)
//		returnValue = (quotaBookmaker1 - 1) * (1 - 1 / quotaBookmaker2);
		
		return returnValue;
	}
	
	public static double calculateRatingApprox(double quotaBookmaker1, double quotaBookmaker2) {		
		double y = (100 * quotaBookmaker1)/quotaBookmaker2;
		
		y = Math.round(y);
		
		double approxRating = y * (quotaBookmaker2 - 1);
		
		return approxRating / 100.00;
	}

}
