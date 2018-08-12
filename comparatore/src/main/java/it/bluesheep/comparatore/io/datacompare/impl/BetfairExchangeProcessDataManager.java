package it.bluesheep.comparatore.io.datacompare.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.record.BetfairExchangeInputRecord;
import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.output.subtype.RecordBookmakerVsExchangeOdds;
import it.bluesheep.comparatore.entities.util.TranslatorUtil;
import it.bluesheep.comparatore.entities.util.rating.impl.RatingCalculatorBookMakerExchangeOdds;
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
	}
	
	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds
					(List<AbstractInputRecord> exchangeList, ChiaveEventoScommessaInputRecordsMap eventiTxOddsMap) throws Exception{
		for(AbstractInputRecord record : exchangeList) {
			String[] splittedEventoKeyRecord = record.getKeyEvento().split("\\|");
			String key = splittedEventoKeyRecord[1];
			Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> dateMap = eventiTxOddsMap.get(Sport.valueOf(key));
			for(Date date : dateMap.keySet()) {
				if((Sport.TENNIS.equals(record.getSport()) && record.compareDate(date, record.getDataOraEvento())) || 
						(Sport.CALCIO.equals(record.getSport()) && date.equals(record.getDataOraEvento()))) {
					for(String eventoTxOdds : dateMap.get(date).keySet()) {
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
							AbstractInputRecord bookmakerRecord = bookmakerRecordMap.get(bookmakerSet.get(0)); 
							exchangeRecord.setCampionato(bookmakerRecord.getCampionato());
							exchangeRecord.setDataOraEvento(bookmakerRecord.getDataOraEvento());
							exchangeRecord.setKeyEvento(bookmakerRecord.getKeyEvento());
							exchangeRecord.setPartecipante1(bookmakerRecord.getPartecipante1());
							exchangeRecord.setPartecipante2(bookmakerRecord.getPartecipante2());
							break;
						}
					}
				}
			}
		}
		return exchangeList;
	}

	@Override
	public List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap sportMap, Sport sport, AbstractBlueSheepService bluesheepServiceType) {

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
			for(Date date : sportDateMap.keySet()) {
				//per ogni evento in input
				for(String evento : sportDateMap.get(date).keySet()) {
					
					//per ogni scommessa, cerco il record relativo alle quote dell'Exchange
					Map<Scommessa,Map<String, AbstractInputRecord>> inputRecordEventoScommessaMap = sportDateMap.get(date).get(evento);
					for(Scommessa scommessa : inputRecordEventoScommessaMap.keySet()) {
						AbstractInputRecord exchangeRecord = findExchangeRecord(inputRecordEventoScommessaMap.get(scommessa));
						//Se trovato
						if(exchangeRecord != null) {
							List<RecordOutput> outputRecordsList = 
									verifyRequirementsAndMapOddsComparison(inputRecordEventoScommessaMap.get(scommessa), exchangeRecord);
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
		recordOutput.setEvento(scommessaInputRecord1.getPartecipante1() + "|" + scommessaInputRecord1.getPartecipante2());
		recordOutput.setQuotaScommessaBookmaker1(scommessaInputRecord1.getQuota());
		recordOutput.setQuotaScommessaBookmaker2(scommessaInputRecord2.getQuota());
		recordOutput.setRating(rating * 100);
		recordOutput.setScommessaBookmaker1(scommessaInputRecord1.getTipoScommessa().getCode());
		recordOutput.setScommessaBookmaker2(scommessaInputRecord2.getTipoScommessa().getCode());
		recordOutput.setSport(scommessaInputRecord1.getSport().toString());
		BetfairExchangeInputRecord exchangeRecord = (BetfairExchangeInputRecord) scommessaInputRecord2;
		recordOutput.setLiquidita(exchangeRecord.getLiquidita());
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
	private AbstractInputRecord findExchangeRecord(Map<String, AbstractInputRecord> scommessaRecordList) {
		return scommessaRecordList.get(BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME);
	}
	
	/**
	 * GD - 30/04/18
	 * Metodo che verifica i requisiti minimi affinchè la comparazione tra la lista di eventi-bookmaker e l'evento-Betfair 
	 * siano mappati in OUTPUT
	 * @param eventoScommessaRecordList la lista di quote offerte dai bookmaker relativi ad uno specifico evento-scommessa
	 * @param exchangeRecord la quota offerta da Betfair Exchange relativa all'evento-scommessa in analisi 
	 * @return la lista di comparazioni mappate in base ai requisiti minimi
	 */
	private List<RecordOutput> verifyRequirementsAndMapOddsComparison(Map<String, AbstractInputRecord> eventoScommessaRecordList, AbstractInputRecord exchangeRecord) {
		
		List<RecordOutput> outputRecordList = new ArrayList<RecordOutput>();
		
		for(String bookmaker : eventoScommessaRecordList.keySet()) {
			AbstractInputRecord record = eventoScommessaRecordList.get(bookmaker);
			//Confronto solo il record exchange con tutti quelli dei bookmaker
			if(record != exchangeRecord && record.getQuota() >= minimumOddValue) {
				double rating1 = getRatingByScommessaPair(record, exchangeRecord);
				//se il rating1 è sufficientemente alto
				if(rating1 >= minThreshold && 
				   rating1 <= maxThreshold && 
					   (!controlValidityOdds || 
							   (
									   !record.getSource().equals(Service.CSV_SERVICENAME) &&
									   hasBeenRecentlyUpdated(record) && 
									   hasBeenRecentlyUpdated(exchangeRecord)
							   )
						)
					) {
					RecordOutput recordOutput = mapRecordOutput(record, exchangeRecord, rating1);
					outputRecordList.add(recordOutput);
				}
			}
		}
		return outputRecordList;
	}
	
	private double getRatingByScommessaPair(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2){
		return (new RatingCalculatorBookMakerExchangeOdds()).calculateRating(scommessaInputRecord1.getQuota(), scommessaInputRecord2.getQuota());
	}
	
	private boolean hasBeenRecentlyUpdated(AbstractInputRecord scommessaInputRecord) {
		return startComparisonTime - scommessaInputRecord.getTimeInsertionInSystem() <= minutesOfOddValidity;
	}

}
