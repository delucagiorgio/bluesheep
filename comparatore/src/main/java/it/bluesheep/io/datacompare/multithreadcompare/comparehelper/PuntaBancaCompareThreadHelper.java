package it.bluesheep.io.datacompare.multithreadcompare.comparehelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.record.BetfairExchangeInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.output.subtype.RecordBookmakerVsExchangeOdds;
import it.bluesheep.entities.util.ComparatoreConstants;
import it.bluesheep.entities.util.TranslatorUtil;
import it.bluesheep.entities.util.rating.impl.RatingCalculatorBookMakerExchangeOdds;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.util.BookmakerLinkGenerator;

public class PuntaBancaCompareThreadHelper extends CompareThreadHelper {

	private double minThreshold;
	private double maxThreshold;
	private double minimumOddValue;
	
	protected PuntaBancaCompareThreadHelper(Map<String, List<RecordOutput>> oddsComparisonThreadMap,
			List<Date> keyList,
			Map<Date, Map<String, Map<Scommessa, List<AbstractInputRecord>>>> dataMap,
			Sport sport) {
		super(oddsComparisonThreadMap, keyList, dataMap, sport);
		this.minThreshold = new Double(BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.PB_MIN_THRESHOLD)).doubleValue();
		this.maxThreshold = new Double(BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.PB_MAX_THRESHOLD)).doubleValue();
		this.minimumOddValue = new Double(BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.MINIMUM_ODD_VALUE)).doubleValue();
	}

	@Override
	public void run() {
		List<RecordOutput> mappedOutputRecord = new ArrayList<RecordOutput>();
		for(Date date : keyList) {
			//per ogni evento in input
			for(String evento : dateMap.get(date).keySet()) {
				
				//per ogni scommessa, cerco il record relativo alle quote dell'Exchange
				Map<Scommessa,List<AbstractInputRecord>> inputRecordEventoScommessaMap = dateMap.get(date).get(evento);
				for(Scommessa scommessa : inputRecordEventoScommessaMap.keySet()) {
					List<AbstractInputRecord> eventoScommessaRecordList = inputRecordEventoScommessaMap.get(scommessa);
					BetfairExchangeInputRecord exchangeRecord = findExchangeRecord(inputRecordEventoScommessaMap.get(scommessa));
					//Se trovato
					if(exchangeRecord != null) {
						List<RecordOutput> outputRecordsList = verifyRequirementsAndMapOddsComparison(eventoScommessaRecordList,exchangeRecord);
						mappedOutputRecord.addAll(outputRecordsList);	
					}
				}
			}
		}
		oddsComparisonThreadMap.put("" + this.getId(), mappedOutputRecord);
		logger.log(Level.INFO, "Thread " + this.getId() + " completed execution. Mapped records are " + mappedOutputRecord.size());
	}

	@Override
	protected RecordOutput mapRecordOutput(AbstractInputRecord scommessaInputRecord1,
			AbstractInputRecord scommessaInputRecord2, double rating){
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
		BetfairExchangeInputRecord exchangeRecord = (BetfairExchangeInputRecord)scommessaInputRecord2;
		recordOutput.setLiquidita(exchangeRecord.getLiquidita());
		recordOutput = (RecordBookmakerVsExchangeOdds)TranslatorUtil.translateFieldAboutCountry(recordOutput);
		recordOutput.setLinkBook1(BookmakerLinkGenerator.getBookmakerLinkEvent(scommessaInputRecord1));
		recordOutput.setLinkBook2(BookmakerLinkGenerator.getBookmakerLinkEvent(scommessaInputRecord2));
		return recordOutput;
	}

	@Override
	protected double getRatingByScommessaPair(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2){
		return (new RatingCalculatorBookMakerExchangeOdds()).calculateRating(scommessaInputRecord1.getQuota(), scommessaInputRecord2.getQuota());
	}
	
	/**
	 * GD - 30/04/18
	 * Metodo che verifica i requisiti minimi affinchè la comparazione tra la lista di eventi-bookmaker e l'evento-Betfair 
	 * siano mappati in OUTPUT
	 * @param eventoScommessaRecordList la lista di quote offerte dai bookmaker relativi ad uno specifico evento-scommessa
	 * @param exchangeRecord la quota offerta da Betfair Exchange relativa all'evento-scommessa in analisi 
	 * @return la lista di comparazioni mappate in base ai requisiti minimi
	 */
	private List<RecordOutput> verifyRequirementsAndMapOddsComparison(List<AbstractInputRecord> eventoScommessaRecordList, AbstractInputRecord exchangeRecord) {
		
		List<RecordOutput> outputRecordList = new ArrayList<RecordOutput>();
		
		for(AbstractInputRecord record : eventoScommessaRecordList) {
			//Confronto solo il record exchange con tutti quelli dei bookmaker
			if(record != exchangeRecord && record.getQuota() >= minimumOddValue) {
				double rating1 = getRatingByScommessaPair(record, exchangeRecord);
				//se il rating1 è sufficientemente alto
				if(rating1 >= minThreshold && 
				   rating1 <= maxThreshold) {
					RecordOutput recordOutput = mapRecordOutput(record, exchangeRecord, rating1);
					outputRecordList.add(recordOutput);
				}
			}
		}
		return outputRecordList;
	}
	
	/**
	 * GD - 30/04/2018	
	 * Metodo che cerca e ritorna (se trovato) il record relativo alle quote offerte da Betfair Exchange
	 * @param scommessaRecordList lista di offerte quote
	 * @return il record relativo alle quote offerte da Betfair Exchange, null se non trovato
	 */
	private BetfairExchangeInputRecord findExchangeRecord(List<AbstractInputRecord> scommessaRecordList) {
		BetfairExchangeInputRecord exchangeRecord = null;
		for(AbstractInputRecord record : scommessaRecordList) {
			if(ComparatoreConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME.equals(record.getBookmakerName()) && ((BetfairExchangeInputRecord) record).isLayRecord()) {
				exchangeRecord = (BetfairExchangeInputRecord) record;
				break;
			}
		}
		return exchangeRecord;
	}

}
