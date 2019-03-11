package it.bluesheep.comparatore.io.datacompare.util;

import java.util.HashMap;
import java.util.Map;

import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.AbstractBlueSheepService;
import it.bluesheep.servicehandler.ArbitraggiServiceHandler;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.servicehandler.CustomFileTableServiceHandler;
import it.bluesheep.servicehandler.JsonGeneratorServiceHandler;
import it.bluesheep.util.BlueSheepConstants;

public class ThresholdRatingFactory {

	private ThresholdRatingFactory() {}
	
	public static Map<Service, Map<String, Double>> getThresholdMapByAbstractBlueSheepService(AbstractBlueSheepService bluesheepServiceType){
		
		Map<Service, Map<String, Double>> returnMap = new HashMap<Service, Map<String, Double>>();
		String puntaBancaMinRating = null;
		String puntaBancaMaxRating = null;
		String puntaPuntaMinRating = null;
		String puntaPuntaMaxRating = null;
		if(bluesheepServiceType instanceof ArbitraggiServiceHandler) {
			puntaBancaMinRating = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.ARBS_PB_MIN_THRESHOLD);
			puntaBancaMaxRating = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.ARBS_PB_MAX_THRESHOLD);
			puntaPuntaMinRating = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.ARBS_PP_MIN_THRESHOLD);
			puntaPuntaMaxRating = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.ARBS_PP_MAX_THRESHOLD);
		
		}else if(bluesheepServiceType instanceof JsonGeneratorServiceHandler) {
			puntaBancaMinRating = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BONUS_ABUSING_PB_MIN_THRESHOLD);
			puntaBancaMaxRating = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BONUS_ABUSING_PB_MAX_THRESHOLD);
			puntaPuntaMinRating = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BONUS_ABUSING_PP_MIN_THRESHOLD);
			puntaPuntaMaxRating = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BONUS_ABUSING_PP_MAX_THRESHOLD);
		
		}else if(bluesheepServiceType instanceof CustomFileTableServiceHandler) {
			puntaPuntaMinRating = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.CUSTOM_PP_MIN_THRESHOLD);
			puntaPuntaMaxRating = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.CUSTOM_PP_MAX_THRESHOLD);
			
		}
		Map<String, Double> puntaPuntaMap = new HashMap<String, Double>();
		Map<String, Double> puntaBancaMap = new HashMap<String, Double>();
		if(puntaPuntaMinRating != null && puntaPuntaMaxRating != null) {
			puntaPuntaMap.put(BlueSheepConstants.PP_MIN, new Double(puntaPuntaMinRating));
			puntaPuntaMap.put(BlueSheepConstants.PP_MAX, new Double(puntaPuntaMaxRating));
		}
		
		if(puntaBancaMinRating != null && puntaBancaMaxRating != null) {
			puntaBancaMap.put(BlueSheepConstants.PB_MIN, new Double(puntaBancaMinRating));
			puntaBancaMap.put(BlueSheepConstants.PB_MAX, new Double(puntaBancaMaxRating));
		}
		
		returnMap.put(Service.BETFAIR_EX_SERVICENAME, puntaBancaMap);
		returnMap.put(Service.TXODDS_SERVICENAME, puntaPuntaMap);
		
		return returnMap;
	}
	
}
