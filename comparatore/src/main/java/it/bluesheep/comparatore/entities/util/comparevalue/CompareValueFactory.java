package it.bluesheep.comparatore.entities.util.comparevalue;

import it.bluesheep.comparatore.entities.util.comparevalue.netprofit.NetProfitFactory;
import it.bluesheep.comparatore.entities.util.comparevalue.rating.RatingCalculatorFactory;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.AbstractBlueSheepService;
import it.bluesheep.servicehandler.ArbitraggiServiceHandler;
import it.bluesheep.servicehandler.JsonGeneratorServiceHandler;

public class CompareValueFactory {
	
	private CompareValueFactory() {}
	
	public static ICompareValue getCompareValueInterfaceByComparisonTypeAndService(Service comparisonType, AbstractBlueSheepService service) {
		if(service instanceof ArbitraggiServiceHandler) {
			return NetProfitFactory.getNetProfitCalculator(comparisonType);
		}else if(service instanceof JsonGeneratorServiceHandler) {
			return RatingCalculatorFactory.getRatingCalculator(comparisonType);
		}
		return null;
	}

}
