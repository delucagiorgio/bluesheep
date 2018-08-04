package it.bluesheep.comparatore.io.datacompare.util;

import java.util.HashMap;
import java.util.Map;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.AbstractBlueSheepService;
import it.bluesheep.servicehandler.ArbitraggiServiceHandler;
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
			//TODO - da aggiungere i nuovi codici per gli arbitraggi
			puntaBancaMinRating = BlueSheepComparatoreMain.getProperties().getProperty(BlueSheepConstants.ARBS_PB_MIN_THRESHOLD);
			puntaBancaMaxRating = BlueSheepComparatoreMain.getProperties().getProperty(BlueSheepConstants.ARBS_PB_MAX_THRESHOLD);
			puntaPuntaMinRating = BlueSheepComparatoreMain.getProperties().getProperty(BlueSheepConstants.ARBS_PP_MIN_THRESHOLD);
			puntaPuntaMaxRating = BlueSheepComparatoreMain.getProperties().getProperty(BlueSheepConstants.ARBS_PP_MAX_THRESHOLD);
		}else if(bluesheepServiceType instanceof JsonGeneratorServiceHandler) {
			puntaBancaMinRating = BlueSheepComparatoreMain.getProperties().getProperty(BlueSheepConstants.BONUS_ABUSING_PB_MIN_THRESHOLD);
			puntaBancaMaxRating = BlueSheepComparatoreMain.getProperties().getProperty(BlueSheepConstants.BONUS_ABUSING_PB_MAX_THRESHOLD);
			puntaPuntaMinRating = BlueSheepComparatoreMain.getProperties().getProperty(BlueSheepConstants.BONUS_ABUSING_PP_MIN_THRESHOLD);
			puntaPuntaMaxRating = BlueSheepComparatoreMain.getProperties().getProperty(BlueSheepConstants.BONUS_ABUSING_PP_MAX_THRESHOLD);
		}
		Map<String, Double> puntaPuntaMap = new HashMap<String, Double>();
		Map<String, Double> puntaBancaMap = new HashMap<String, Double>();
		
		puntaPuntaMap.put(BlueSheepConstants.PP_MIN, new Double(puntaPuntaMinRating));
		puntaPuntaMap.put(BlueSheepConstants.PP_MAX, new Double(puntaPuntaMaxRating));
		puntaBancaMap.put(BlueSheepConstants.PB_MIN, new Double(puntaBancaMinRating));
		puntaBancaMap.put(BlueSheepConstants.PB_MAX, new Double(puntaBancaMaxRating));
		
		returnMap.put(Service.BETFAIR_SERVICENAME, puntaBancaMap);
		returnMap.put(Service.TXODDS_SERVICENAME, puntaPuntaMap);
		
		return returnMap;
	}
	
}
