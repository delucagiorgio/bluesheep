package it.bluesheep.entities.util.rating.impl;

import it.bluesheep.entities.util.rating.RatingCalculator;

/**
 * Classe che definisce il calcolo dei rating1 per la categoria di confronto quote Bookmaker - Bookmaker
 * @author Giorgio De Luca
 *
 */
public class RatingCalculatorBookmakersOdds extends RatingCalculator {

	@Override
	public double calculateRating(double quotaBookmaker1, double quotaBookmaker2) {
		double y = (100 * quotaBookmaker1)/quotaBookmaker2;
		
		y = Math.round(y);
		
		double approxRating = (100 * quotaBookmaker1) - y;
		
		return approxRating / 100.00;		
	}

	@Override
	public double calculateRiskFreeRating(double quotaBookmaker1, double quotaBookmaker2) {
		double returnValue = 0;
		
//		if(quotaBookmaker1 == 0 || quotaBookmaker2 == 0) {
//			//TODO gestire caso di errore
//		}
//		
//		//RF = (quotaBookmaker1 - 1) * (1 - 1 / quotaBookmaker2)
//		returnValue = (quotaBookmaker1 - 1) * (1 - 1 / quotaBookmaker2);
		
		return returnValue;
	}
	
	public double calculateRatingApprox(double quotaBookmaker1, double quotaBookmaker2) {		
		double y = (100 * quotaBookmaker1)/quotaBookmaker2;
		
		y = Math.round(y);
		
		double approxRating = y * (quotaBookmaker2 - 1);
		
		return approxRating / 100.00;
	}
	
	public double calculateRatingBackSideExchange(double backOdd, double bookOdd) {
		double y = (100 * backOdd) + (100 * (backOdd - 1) * 0.05) / bookOdd;
		
		y = Math.round(y);
		
		double approxRating = (100 * backOdd) - y;
		
		return approxRating / 100.00;	
		
	}

	public double calculateRatingApproxBackSiceExchange(double backOdd, double bookOdd) {
		double y = (100 * backOdd) + (100 * (backOdd - 1) * 0.05) / bookOdd;
		
		y = Math.round(y);
		
		double approxRating = y * (bookOdd - 1);
		
		return approxRating / 100.00;
	}

}
