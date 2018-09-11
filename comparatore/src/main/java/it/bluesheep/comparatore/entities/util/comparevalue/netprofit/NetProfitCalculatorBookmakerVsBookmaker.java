package it.bluesheep.comparatore.entities.util.comparevalue.netprofit;

public class NetProfitCalculatorBookmakerVsBookmaker extends NetProfitCalculator {

	protected NetProfitCalculatorBookmakerVsBookmaker() {
		super();
	}
	
	@Override
	protected double calculateNetProfit(double odd1, double odd2) {
		double r1 = 1000 * odd1;
		double x = r1/odd2;		
		return (((r1)/(1000 + x)) - 1) * 100;
	}

}
