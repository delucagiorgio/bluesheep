package it.bluesheep.comparatore.entities.util.comparevalue.netprofit;

public class NetProfitCalculatorExchangeVsBookmaker extends NetProfitCalculator{

	protected NetProfitCalculatorExchangeVsBookmaker() {
		super();
	}
	
	@Override
	protected double calculateNetProfit(double odd1, double odd2) {
		double realOdd2 = 1 / (1 - (1/odd2));
		double r1 = 1000 * odd1;
		//Considero la commissione di default dell'exchange
		double temp_r1 = r1 / 0.95;
		
		double x = temp_r1/realOdd2;
		return (((r1)/(1000 + x)) - 1) * 100;	
	}

}
