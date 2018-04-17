package it.bluesheep.entities.util.rating.impl;

import it.bluesheep.entities.util.rating.RatingCalculator;

/**
 * Classe che definisce il calcolo dei rating per la categoria di confronto quote Bookmaker - Bookmaker
 * @author Giorgio De Luca
 *
 */
public class RatingCalculatorBookmakersOdds extends RatingCalculator {

	@Override
	public double calculateRating(double quotaBookmaker1, double quotaBookmaker2) {
		double returnValue = 0;
		
		if(quotaBookmaker1 == 0 || quotaBookmaker2 == 0) {
			//TODO gestire caso di errore
		}
		
		//Rating = quotaBookmaker1 - (quotaBookmaker1 / quotaBookmaker2)
		returnValue = quotaBookmaker1 - (quotaBookmaker1 / quotaBookmaker2);
		
		return returnValue;
	}

	@Override
	public double calculateRiskFreeRating(double quotaBookmaker1, double quotaBookmaker2) {
		double returnValue = 0;
		
		if(quotaBookmaker1 == 0 || quotaBookmaker2 == 0) {
			//TODO gestire caso di errore
		}
		
		//RF = (quotaBookmaker1 - 1) * (1 - 1 / quotaBookmaker2)
		returnValue = (quotaBookmaker1 - 1) * (1 - 1 / quotaBookmaker2);
		
		return returnValue;
	}

}
