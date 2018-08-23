package it.bluesheep.comparatore.io.datacompare.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.output.subtype.RecordBookmakerVsBookmakerOdds;
import it.bluesheep.comparatore.entities.util.ScommessaUtilManager;
import it.bluesheep.comparatore.entities.util.TranslatorUtil;
import it.bluesheep.comparatore.entities.util.rating.impl.RatingCalculatorBookmakersOdds;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.AbstractProcessDataManager;
import it.bluesheep.comparatore.io.datacompare.util.BookmakerLinkGenerator;
import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.comparatore.io.datacompare.util.ICompareInformationEvents;
import it.bluesheep.comparatore.io.datacompare.util.ThresholdRatingFactory;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.AbstractBlueSheepService;
import it.bluesheep.servicehandler.ArbitraggiServiceHandler;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepSharedResources;

/**
 * Classe utilizzata per definire i metodi su cui si basa la comparazione di quote tra i vari
 * bookmaker di TxOdds. Il fine è quello di processare una determinata quota con la sua
 * opposta per poi valutarne la giustezza d'abbinamento tramite il calcolo del rating1 (> 70%) 
 * @author Giorgio De Luca
 *
 */
public class TxOddsProcessDataManager extends AbstractProcessDataManager implements ICompareInformationEvents{
	
	private double minThreshold;
	private double maxThreshold;
	private boolean controlValidityOdds;
	private long startComparisonTime;
	private long minutesOfOddValidity;
	
	protected TxOddsProcessDataManager() {
		super();
		controlValidityOdds = false;
		startComparisonTime = System.currentTimeMillis();
		minutesOfOddValidity = new Long(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.MINUTES_ODD_VALIDITY)) * 60 * 1000L;
	}
	
	@Override
	public List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap sportMap, Sport sport, AbstractBlueSheepService bluesheepServiceType) {
		Map<Service, Map<String, Double>> mapThresholdMap = ThresholdRatingFactory.getThresholdMapByAbstractBlueSheepService(bluesheepServiceType);
		this.minThreshold = mapThresholdMap.get(Service.TXODDS_SERVICENAME).get(BlueSheepConstants.PP_MIN);
		this.maxThreshold = mapThresholdMap.get(Service.TXODDS_SERVICENAME).get(BlueSheepConstants.PP_MAX);
		
		if(bluesheepServiceType instanceof ArbitraggiServiceHandler) {
			controlValidityOdds = true;
		}
		
		List<RecordOutput> mappedOutputRecord = new ArrayList<RecordOutput>();
		//per ogni evento in input
		ChiaveEventoScommessaInputRecordsMap recordInputMap = BlueSheepSharedResources.getEventoScommessaRecordMap();
		Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> sportDateMap = recordInputMap.get(sport);
		if(sportDateMap != null) {
			for(Date date : sportDateMap.keySet()) {
				for(String evento : sportDateMap.get(date).keySet()) {
					//per ogni tipo scommessa, cerco le scommesse opposte relative allo stesso evento e le comparo con 
					//quella in analisi
					Map<Scommessa, Map<String, AbstractInputRecord>> inputRecordEventoScommessaMap = sportDateMap.get(date).get(evento);
					Map<Scommessa,Scommessa> processedScommessaTypes = new HashMap<Scommessa, Scommessa>();
					Scommessa oppositeScommessa = null;			
					for(Scommessa scommessa : inputRecordEventoScommessaMap.keySet()) {
						Map<String, AbstractInputRecord> temp = inputRecordEventoScommessaMap.get(scommessa);
						if((Sport.CALCIO.equals(sport) && 
								!ScommessaUtilManager.getScommessaListCalcio3WayOdds().contains(scommessa)) ||
								(Sport.TENNIS.equals(sport) && 
										ScommessaUtilManager.getScommessaListTennis2WayOdds().contains(scommessa))) {
							
							oppositeScommessa = ScommessaUtilManager.getOppositeScommessaByScommessa(scommessa, sport);
							if(oppositeScommessa != null && !isAlreadyProcessedScommessaTypes(scommessa,oppositeScommessa,processedScommessaTypes)) {
								List<RecordOutput> outputRecordsList = verifyRequirementsAndMapOddsComparison(temp,inputRecordEventoScommessaMap.get(oppositeScommessa));
								mappedOutputRecord.addAll(outputRecordsList);
								processedScommessaTypes.put(scommessa, oppositeScommessa);
							}
						}
					}
				}
			}
		}
		return mappedOutputRecord;
	}
	
	/**
	 * GD - 19/04/18
	 * Verifica se la coppia di scommesse è stata già processata in passato
	 * @param scommessa scommessa1
	 * @param oppositeScommessa scommessa2
	 * @param processedScommessaTypes mappa di storico delle coppie di scommesse già processate
	 * @return true, se già processate, false altrimenti
	 */
	private boolean isAlreadyProcessedScommessaTypes(Scommessa scommessa, Scommessa oppositeScommessa, Map<Scommessa, Scommessa> processedScommessaTypes) {
		return processedScommessaTypes.get(scommessa) != null || processedScommessaTypes.get(oppositeScommessa) != null;
	}
	
	/**
	 * GD - 18/04/18
	 * Verifica che due liste di scommesse su stesso evento e tipo scommessa opposto siano comparabili secondo i criteri specificati
	 * dal business (rating1 maggiore del 70%) e se così valida la coppia di record e li mappa in un record di output aggiungendolo
	 * ad una lista
	 * @param bookmakerRecord1Map lista di quote sulla scommessa di iterazione principale
	 * @param bookmakerRecord2Map lista delle quote con scommessa opposta alla scommessa in analisi
	 * @return tutti i record mappati secondo il record di output che superano un rating1 del 70%
	 */
	private List<RecordOutput> verifyRequirementsAndMapOddsComparison(Map<String, AbstractInputRecord> bookmakerRecord1Map, Map<String, AbstractInputRecord> bookmakerRecord2Map) {
		
		List<RecordOutput> outputRecordList = new ArrayList<RecordOutput>();
		
		if(bookmakerRecord1Map != null && !bookmakerRecord1Map.isEmpty() && 
				bookmakerRecord2Map != null && !bookmakerRecord2Map.isEmpty()) {
			//per ogni quota disponibile sulla scommessa tipo 1
			List<String> bookmakerScommessaSet = new ArrayList<String>(bookmakerRecord1Map.keySet());
			List<String> bookmakerOppositeScommessaSet = new ArrayList<String>(bookmakerRecord2Map.keySet());

			for(String bookmakerScommessa : bookmakerScommessaSet) {
				//per ogni quota disponibile sulla scommessa tipo 2
				for(String bookmakerScommessaOpposite : bookmakerOppositeScommessaSet) {
					
					if(!bookmakerScommessa.equalsIgnoreCase(bookmakerScommessaOpposite) 
							&& !BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME.equalsIgnoreCase(bookmakerScommessaOpposite) 
							&& !BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME.equalsIgnoreCase(bookmakerScommessa)) { 
						
						AbstractInputRecord scommessaInputRecord = bookmakerRecord1Map.get(bookmakerScommessa);
						AbstractInputRecord oppositeScommessaInputRecord = bookmakerRecord2Map.get(bookmakerScommessaOpposite);

						List<AbstractInputRecord> orderedListByQuota = getOrderedQuotaList(scommessaInputRecord, oppositeScommessaInputRecord);
						
						double rating1 = (new RatingCalculatorBookmakersOdds()).calculateRating(orderedListByQuota.get(0).getQuota(), orderedListByQuota.get(1).getQuota());
						double rating2 = (new RatingCalculatorBookmakersOdds()).calculateRatingApprox(orderedListByQuota.get(0).getQuota(), orderedListByQuota.get(1).getQuota());

						//se le due quote in analisi raggiungono i termini di accettabilità, vengono mappate nel record di output
						if(rating1 >= minThreshold && 
						   rating2 >= minThreshold &&
						   rating1 <= maxThreshold && 
						   rating2 <= maxThreshold &&
						   (!controlValidityOdds || 
								   (!scommessaInputRecord.getSource().equals(Service.CSV_SERVICENAME) && 
										   !oppositeScommessaInputRecord.getSource().equals(Service.CSV_SERVICENAME) &&
										   hasBeenRecentlyUpdated(scommessaInputRecord) && 
										   hasBeenRecentlyUpdated(oppositeScommessaInputRecord)
									)
						   )) {
							RecordOutput outputRecord = mapRecordOutput(orderedListByQuota.get(0), orderedListByQuota.get(1), rating1);
							((RecordBookmakerVsBookmakerOdds) outputRecord).setRating2(rating2 * 100);
							outputRecordList.add(outputRecord);
						}
					}
				}
			}
		}
		
		return outputRecordList;
	}
	
	private boolean hasBeenRecentlyUpdated(AbstractInputRecord scommessaInputRecord) {
		return startComparisonTime - scommessaInputRecord.getTimeInsertionInSystem() <= minutesOfOddValidity;
	}

	/**
	 * GD - 29/05/18
	 * Ordina i record di input in base al valore di quota
	 * @param scommessaInputRecord input1
	 * @param oppositeScommessaInputRecord input2
	 * @return la lista in cui il primo elemento è quello con la quota superiore, il secondo quello con la quota inferiore.
	 */
	private List<AbstractInputRecord> getOrderedQuotaList(AbstractInputRecord scommessaInputRecord,
			AbstractInputRecord oppositeScommessaInputRecord) {
		List<AbstractInputRecord> returnList = new ArrayList<AbstractInputRecord>(2);
		
		if(scommessaInputRecord.getQuota() >= oppositeScommessaInputRecord.getQuota()) {
			returnList.add(0, scommessaInputRecord);
			returnList.add(1, oppositeScommessaInputRecord);
		}else {
			returnList.add(0, oppositeScommessaInputRecord);
			returnList.add(1, scommessaInputRecord);
		}
		
		return returnList;
	}
	
	protected RecordOutput mapRecordOutput(AbstractInputRecord scommessaInputRecord, AbstractInputRecord oppositeScommessaInputRecord, double rating1) {
		RecordBookmakerVsBookmakerOdds output = new RecordBookmakerVsBookmakerOdds();		
		output.setBookmakerName1(scommessaInputRecord.getBookmakerName());
		output.setBookmakerName2(oppositeScommessaInputRecord.getBookmakerName());
		output.setCampionato(scommessaInputRecord.getCampionato());
		output.setDataOraEvento(scommessaInputRecord.getDataOraEvento());
		output.setEvento(scommessaInputRecord.getPartecipante1() + BlueSheepConstants.REGEX_PIPE + scommessaInputRecord.getPartecipante2());
		output.setQuotaScommessaBookmaker1(scommessaInputRecord.getQuota());
		output.setQuotaScommessaBookmaker2(oppositeScommessaInputRecord.getQuota());
		output.setRating(rating1 * 100);
		output.setScommessaBookmaker1(scommessaInputRecord.getTipoScommessa().getCode());
		output.setScommessaBookmaker2(oppositeScommessaInputRecord.getTipoScommessa().getCode());
		output.setSport(scommessaInputRecord.getSport().toString());
		output = (RecordBookmakerVsBookmakerOdds)TranslatorUtil.translateFieldAboutCountry(output);
		output.setLinkBook1(BookmakerLinkGenerator.getBookmakerLinkEvent(scommessaInputRecord));
		output.setLinkBook2(BookmakerLinkGenerator.getBookmakerLinkEvent(oppositeScommessaInputRecord));
		
		return output;
	}

	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds(List<AbstractInputRecord> bookmakerList, ChiaveEventoScommessaInputRecordsMap eventiTxOddsMap) throws Exception {
		for(AbstractInputRecord txOddsRecord : bookmakerList) {
			AbstractInputRecord exchangeRecord = BlueSheepSharedResources.findExchangeRecord(txOddsRecord);
			if(exchangeRecord != null) {
				txOddsRecord.setDataOraEvento(exchangeRecord.getDataOraEvento());
			}
		}
		return null;
	}
}
