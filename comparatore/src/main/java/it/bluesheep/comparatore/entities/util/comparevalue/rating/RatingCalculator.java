package it.bluesheep.comparatore.entities.util.comparevalue.rating;

import it.bluesheep.comparatore.entities.util.comparevalue.ICompareValue;

public abstract class RatingCalculator implements ICompareValue{
	
	public double getCompareValue(double odd1, double odd2) {
		return calculateRating(odd1, odd2);
	}
	
	/**
	 * GD - 16/04/18
	 * Calcola il rating1 legato a due quote in base al tipo a runtime utilizzato per il calcolo
	 * @param quotaBookmaker1 quota1
	 * @param quotaBookmaker2 quota2
	 * @return rating1 relativo alle 2 quote
	 */
	protected abstract double calculateRating(double quotaBookmaker1, double quotaBookmaker2);
	
	/**
	 * GD - 16/04/18
	 * Calcola il risk free rating1 legato a due quote in base al tipo a runtime utilizzato per il calcolo
	 * @param quotaBookmaker1 quota1
	 * @param quotaBookmaker2 quota2
	 * @return risk free rating1 legato alle due quote
	 */
	protected abstract double calculateRiskFreeRating(double quotaBookmaker1, double quotaBookmaker2);
	
}
