package it.bluesheep.entities.util.rating;

public abstract class RatingCalculator {
	
	/**
	 * GD - 16/04/18
	 * Calcola il rating legato a due quote in base al tipo a runtime utilizzato per il calcolo
	 * @param quotaBookmaker1 quota1
	 * @param quotaBookmaker2 quota2
	 * @return rating relativo alle 2 quote
	 */
	public abstract double calculateRating(double quotaBookmaker1, double quotaBookmaker2);
	
	/**
	 * GD - 16/04/18
	 * Calcola il risk free rating legato a due quote in base al tipo a runtime utilizzato per il calcolo
	 * @param quotaBookmaker1 quota1
	 * @param quotaBookmaker2 quota2
	 * @return risk free rating legato alle due quote
	 */
	public abstract double calculateRiskFreeRating(double quotaBookmaker1, double quotaBookmaker2);
	
}
