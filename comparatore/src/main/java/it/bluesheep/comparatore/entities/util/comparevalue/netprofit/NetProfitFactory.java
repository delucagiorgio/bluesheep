package it.bluesheep.comparatore.entities.util.comparevalue.netprofit;

import it.bluesheep.comparatore.serviceapi.Service;

public class NetProfitFactory {
	
	private NetProfitFactory() {}
	
	public static NetProfitCalculator getNetProfitCalculator(Service comparisonType) {
		if(Service.TXODDS_SERVICENAME.equals(comparisonType)) {
			return new NetProfitCalculatorBookmakerVsBookmaker();
		}else if(Service.BETFAIR_SERVICENAME.equals(comparisonType)) {
			return new NetProfitCalculatorExchangeVsBookmaker();
		}
		return null;
	}

}
