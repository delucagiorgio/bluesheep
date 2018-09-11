package it.bluesheep.comparatore.entities.util.comparevalue.netprofit;

import it.bluesheep.comparatore.entities.util.comparevalue.ICompareValue;

public abstract class NetProfitCalculator implements ICompareValue{
	
	public double getCompareValue(double odd1, double odd2) {
		return calculateNetProfit(odd1, odd2);
	}

	protected abstract double calculateNetProfit(double odd1, double odd2);

}
