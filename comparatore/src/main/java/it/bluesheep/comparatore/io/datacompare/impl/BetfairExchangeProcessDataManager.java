package it.bluesheep.comparatore.io.datacompare.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.record.BetfairExchangeInputRecord;
import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.output.subtype.RecordBookmakerVsExchangeOdds;
import it.bluesheep.comparatore.entities.util.TranslatorUtil;
import it.bluesheep.comparatore.entities.util.comparevalue.CompareValueFactory;
import it.bluesheep.comparatore.entities.util.comparevalue.rating.RatingCalculatorFactory;
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

public class BetfairExchangeProcessDataManager extends AbstractProcessDataManager implements ICompareInformationEvents{

	private double minThreshold;
	private double maxThreshold;
	private boolean controlValidityOdds;
	private long startComparisonTime;
	private double minimumOddValue;
	private long minutesOfOddValidity;
	
	protected BetfairExchangeProcessDataManager() {
		super();
		controlValidityOdds = false;
		startComparisonTime = System.currentTimeMillis();
		minimumOddValue = new Double(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.MINIMUM_ODD_VALUE));
		minutesOfOddValidity = new Long(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.MINUTES_ODD_VALIDITY)) * 60 * 1000L;
		service = Service.BETFAIR_SERVICENAME;
	}
	
	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds(List<AbstractInputRecord> exchangeList) throws Exception{
		ChiaveEventoScommessaInputRecordsMap eventiTxOddsMap = BlueSheepSharedResources.getEventoScommessaRecordMap();
		
		for(AbstractInputRecord record : exchangeList) {
			String[] splittedEventoKeyRecord = record.getKeyEvento().split("\\|");
			String key = splittedEventoKeyRecord[1];
			Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> dateMap = eventiTxOddsMap.get(Sport.valueOf(key));
			if(dateMap != null) {
				List<Date> dateList = new ArrayList<Date>(dateMap.keySet());
				for(Date date : dateList) {
					if((Sport.TENNIS.equals(record.getSport()) && AbstractInputRecord.compareDate(date, record.getDataOraEvento())) || 
							(Sport.CALCIO.equals(record.getSport()) && date.equals(record.getDataOraEvento()))) {
						List<String> keyEventoList = new ArrayList<String>(dateMap.get(date).keySet());
						for(String eventoTxOdds : keyEventoList) {
							String[] splittedEventoKey = eventoTxOdds.split("\\|");
							String sport = splittedEventoKey[1];
							String[] partecipantiSplitted = splittedEventoKey[2].split(BlueSheepConstants.REGEX_VERSUS);
							String partecipante1 = partecipantiSplitted[0];
							String partecipante2 = partecipantiSplitted[1];
							
							BetfairExchangeInputRecord exchangeRecord = (BetfairExchangeInputRecord) record;
							
							if(exchangeRecord.isSameEventAbstractInputRecord(date, sport, partecipante1, partecipante2) || 
														exchangeRecord.isSameEventSecondaryMatch(date, sport, partecipante1, partecipante2)) {
								Map<Scommessa, Map<String, AbstractInputRecord>> mapScommessaRecord = dateMap.get(date).get(eventoTxOdds);
								List<Scommessa> scommessaSet = new ArrayList<Scommessa>(mapScommessaRecord.keySet());
								Map<String, AbstractInputRecord> bookmakerRecordMap = mapScommessaRecord.get(scommessaSet.get(0));
								List<String> bookmakerSet = new ArrayList<String>(bookmakerRecordMap.keySet());
								if(!bookmakerSet.isEmpty()) {
									AbstractInputRecord txOddsReference = findTxOddsRecord(bookmakerSet, bookmakerRecordMap);
									if(txOddsReference != null) {
										exchangeRecord.setCampionato(txOddsReference.getCampionato());
										exchangeRecord.setPartecipante1(txOddsReference.getPartecipante1());
										exchangeRecord.setPartecipante2(txOddsReference.getPartecipante2());
										exchangeRecord.setKeyEvento("" + exchangeRecord.getDataOraEvento() + BlueSheepConstants.REGEX_PIPE + 
													exchangeRecord.getSport() + BlueSheepConstants.REGEX_PIPE + 
													exchangeRecord.getPartecipante1() + BlueSheepConstants.REGEX_VERSUS +
													exchangeRecord.getPartecipante2());
									}
								}
								break;
							}
						}
					}
				}
			}
		}
		return exchangeList;
	}

	private AbstractInputRecord findTxOddsRecord(List<String> bookmakerSet, Map<String, AbstractInputRecord> bookmakerRecordMap) {
		
		AbstractInputRecord txOddsRecord = null;
		
		for(String bookmaker : bookmakerSet) {
			AbstractInputRecord record = bookmakerRecordMap.get(bookmaker);
			if(record.getSource().equals(Service.TXODDS_SERVICENAME)) {
				txOddsRecord = record;
				break;
			}
		}
		
		return txOddsRecord;
	}

	@Override
	public List<RecordOutput> compareTwoWayOdds(ChiaveEventoScommessaInputRecordsMap sportMap, Sport sport, AbstractBlueSheepService bluesheepServiceType) {

		Map<Service, Map<String, Double>> mapThresholdMap = ThresholdRatingFactory.getThresholdMapByAbstractBlueSheepService(bluesheepServiceType);
		this.minThreshold = mapThresholdMap.get(Service.BETFAIR_SERVICENAME).get(BlueSheepConstants.PB_MIN);
		this.maxThreshold = mapThresholdMap.get(Service.BETFAIR_SERVICENAME).get(BlueSheepConstants.PB_MAX);
		
		if(bluesheepServiceType instanceof ArbitraggiServiceHandler) {
			controlValidityOdds = true;
		}
		
		List<RecordOutput> mappedOutputRecord = new ArrayList<RecordOutput>();
		ChiaveEventoScommessaInputRecordsMap recordInputMap = BlueSheepSharedResources.getEventoScommessaRecordMap();
		Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> sportDateMap = recordInputMap.get(sport);
		if(sportDateMap != null) {
			Set<Date> dateSet = new HashSet<Date>(sportDateMap.keySet());
			for(Date date : dateSet) {
				Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>> dateMap = sportDateMap.get(date);
				Set<String> eventoSet = new HashSet<String>(dateMap.keySet());
				//per ogni evento in input
				for(String evento : eventoSet) {
					//per ogni scommessa, cerco il record relativo alle quote dell'Exchange
					Map<Scommessa,Map<String, AbstractInputRecord>> inputRecordEventoScommessaMap = dateMap.get(evento);
					Set<Scommessa> scommessaSet = new HashSet<Scommessa>(inputRecordEventoScommessaMap.keySet());
					for(Scommessa scommessa : scommessaSet) {
						AbstractInputRecord exchangeRecord = findExchangeRecord(inputRecordEventoScommessaMap.get(scommessa));
						//Se trovato
						if(exchangeRecord != null) {
							List<RecordOutput> outputRecordsList = 
									verifyRequirementsAndMapOddsComparison(inputRecordEventoScommessaMap.get(scommessa), exchangeRecord, bluesheepServiceType);
							mappedOutputRecord.addAll(outputRecordsList);	
						}
					}
				}
			}
		}
		return mappedOutputRecord;
	}

	private RecordOutput mapRecordOutput(AbstractInputRecord scommessaInputRecord1,
		AbstractInputRecord scommessaInputRecord2, double rating) {
		RecordBookmakerVsExchangeOdds recordOutput = new RecordBookmakerVsExchangeOdds();
		recordOutput.setBookmakerName1(scommessaInputRecord1.getBookmakerName());
		recordOutput.setBookmakerName2(scommessaInputRecord2.getBookmakerName());
		recordOutput.setCampionato(scommessaInputRecord1.getCampionato());
		recordOutput.setDataOraEvento(scommessaInputRecord1.getDataOraEvento());
		recordOutput.setEvento(scommessaInputRecord1.getPartecipante1() + BlueSheepConstants.REGEX_VERSUS + scommessaInputRecord1.getPartecipante2());
		recordOutput.setQuotaScommessaBookmaker1(scommessaInputRecord1.getQuota());
		recordOutput.setQuotaScommessaBookmaker2(scommessaInputRecord2.getQuota());
		recordOutput.setRating(rating * 100);
		recordOutput.setScommessaBookmaker1(scommessaInputRecord1.getTipoScommessa().getCode());
		recordOutput.setScommessaBookmaker2(scommessaInputRecord2.getTipoScommessa().getCode());
		recordOutput.setSport(scommessaInputRecord1.getSport().toString());
		recordOutput.setLiquidita1(scommessaInputRecord1.getLiquidita());
		recordOutput.setLiquidita2(scommessaInputRecord2.getLiquidita());
		recordOutput = (RecordBookmakerVsExchangeOdds) TranslatorUtil.translateFieldAboutCountry(recordOutput);
		recordOutput.setLinkBook1(BookmakerLinkGenerator.getBookmakerLinkEvent(scommessaInputRecord1));
		recordOutput.setLinkBook2(BookmakerLinkGenerator.getBookmakerLinkEvent(scommessaInputRecord2));
		return recordOutput;
	}
	
	/**
	 * GD - 30/04/2018	
	 * Metodo che cerca e ritorna (se trovato) il record relativo alle quote offerte da Betfair Exchange
	 * @param scommessaRecordList lista di offerte quote
	 * @return il record relativo alle quote offerte da Betfair Exchange, null se non trovato
	 */
	private BetfairExchangeInputRecord findExchangeRecord(Map<String, AbstractInputRecord>  scommessaRecordList) {
		return (BetfairExchangeInputRecord) scommessaRecordList.get(BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME_LAY);
	}
	
	/**
	 * GD - 30/04/18
	 * Metodo che verifica i requisiti minimi affinchè la comparazione tra la lista di eventi-bookmaker e l'evento-Betfair 
	 * siano mappati in OUTPUT
	 * @param eventoScommessaRecordList la lista di quote offerte dai bookmaker relativi ad uno specifico evento-scommessa
	 * @param exchangeRecord la quota offerta da Betfair Exchange relativa all'evento-scommessa in analisi 
	 * @return la lista di comparazioni mappate in base ai requisiti minimi
	 */
	private List<RecordOutput> verifyRequirementsAndMapOddsComparison(Map<String, AbstractInputRecord> eventoScommessaRecordList, AbstractInputRecord exchangeRecord, AbstractBlueSheepService bluesheepServiceType) {
		
		List<RecordOutput> outputRecordList = new ArrayList<RecordOutput>();
		Set<String> bookmakerSet = new HashSet<String>(eventoScommessaRecordList.keySet());
		for(String bookmaker : bookmakerSet) {
			AbstractInputRecord record = eventoScommessaRecordList.get(bookmaker);
			//Confronto solo il record exchange con tutti quelli dei bookmaker
			if(record != exchangeRecord && !BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME_BACK.equalsIgnoreCase(record.getBookmakerName()) && record.getQuota() >= minimumOddValue) {
				double compareValue = CompareValueFactory.getCompareValueInterfaceByComparisonTypeAndService(service, bluesheepServiceType).getCompareValue(record.getQuota(), exchangeRecord.getQuota());
				//se il rating1 è sufficientemente alto
				if(compareValue >= minThreshold && (
						(controlValidityOdds && ArbsUtil.validOddsRatio(record.getQuota(), exchangeRecord.getQuota(), service))
						||
						(!controlValidityOdds && compareValue <= maxThreshold)) 
						&& 
					   (!controlValidityOdds || 
							   (
									   !record.getSource().equals(Service.CSV_SERVICENAME) &&
									   hasBeenRecentlyUpdated(record) && 
									   hasBeenRecentlyUpdated(exchangeRecord)
							   )
						)
					) {
					RecordOutput recordOutput = mapRecordOutput(record, exchangeRecord, getRatingByScommessaPair(record, exchangeRecord));
					outputRecordList.add(recordOutput);
				}
			}
		}
		return outputRecordList;
	}
	
	private double getRatingByScommessaPair(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2){
		return RatingCalculatorFactory.getRatingCalculator(service).getCompareValue(scommessaInputRecord1.getQuota(), scommessaInputRecord2.getQuota());
	}
	
	private boolean hasBeenRecentlyUpdated(AbstractInputRecord scommessaInputRecord) {
		return startComparisonTime - scommessaInputRecord.getTimeInsertionInSystem() <= minutesOfOddValidity;
	}

	@Override
	public List<ArbsRecord> compareThreeWayOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport, AbstractBlueSheepService bluesheepServiceType) throws Exception {
		throw new Exception("Incorrect implementation of compareThreeWayOdds");
	}

}
