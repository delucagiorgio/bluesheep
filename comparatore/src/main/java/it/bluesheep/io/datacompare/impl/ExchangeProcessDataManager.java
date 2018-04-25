package it.bluesheep.io.datacompare.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.record.BetfairExchangeInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.output.subtype.RecordBookmakerVsExchangeOdds;
import it.bluesheep.entities.util.rating.impl.RatingCalculatorBookMakerExchangeOdds;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.AbstractProcessDataManager;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;

public class ExchangeProcessDataManager extends AbstractProcessDataManager{
	
	private static final String BETFAIR_EXCHANGE_MINIMUM_RATING_AMOUNT = "0.7";
	
	public List<AbstractInputRecord> compareAndCollectSameEventsFromExchangeAndBookmakers
					(List<AbstractInputRecord> exchangeList, List<AbstractInputRecord> bookmakerList) {
		for(AbstractInputRecord exchangeRecord : exchangeList) {
			for(AbstractInputRecord bookmakerRecord : bookmakerList) {
				if(exchangeRecord.isSameEventAbstractInputRecord(bookmakerRecord)) {
					exchangeRecord.setCampionato(bookmakerRecord.getCampionato());
					exchangeRecord.setDataOraEvento(bookmakerRecord.getDataOraEvento());
					exchangeRecord.setKeyEvento(bookmakerRecord.getKeyEvento());
					exchangeRecord.setPartecipante1(bookmakerRecord.getPartecipante1());
					exchangeRecord.setPartecipante2(bookmakerRecord.getPartecipante2());
				}
			}
		}
		return exchangeList;
	}

	@Override
	public List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport) {
		
		List<RecordOutput> mappedOutputRecord = new ArrayList<RecordOutput>();	

		//per ogni evento in input
		for(String evento : dataMap.keySet()) {
			//per ogni scommessa, cerco il record relativo alle quote dell'Exchange
			Map<Scommessa,List<AbstractInputRecord>> inputRecordEventoScommessaMap = dataMap.get(evento);
			for(Scommessa scommessa : inputRecordEventoScommessaMap.keySet()) {
				List<AbstractInputRecord> eventoScommessaRecordList = inputRecordEventoScommessaMap.get(scommessa);
				AbstractInputRecord exchangeRecord = findExchangeRecord(inputRecordEventoScommessaMap.get(scommessa));
				//Se trovato
				if(exchangeRecord != null) {
					List<RecordOutput> outputRecordsList = verifyRequirementsAndMapOddsComparison(eventoScommessaRecordList,exchangeRecord);
					mappedOutputRecord.addAll(outputRecordsList);			
				}
			}
		}
		return mappedOutputRecord;
			
	}

	private List<RecordOutput> verifyRequirementsAndMapOddsComparison(List<AbstractInputRecord> eventoScommessaRecordList, AbstractInputRecord exchangeRecord) {
		
		List<RecordOutput> outputRecordList = new ArrayList<RecordOutput>();
		
		for(AbstractInputRecord record : eventoScommessaRecordList) {
			//Confronto solo il record exchange con tutti quelli dei bookmaker
			if(record != exchangeRecord) {
				double rating = getRatingByScommessaPair(record, exchangeRecord);
				//se il rating è sufficientemente alto
				if(rating >= new Double(BETFAIR_EXCHANGE_MINIMUM_RATING_AMOUNT).doubleValue()) {
					RecordOutput recordOutput = mapBookVsBookRecordOutput(record, exchangeRecord, rating);
					outputRecordList.add(recordOutput);
				}
			}
		}
		return outputRecordList;
	}

	private AbstractInputRecord findExchangeRecord(List<AbstractInputRecord> scommessaRecordList) {
		AbstractInputRecord exchangeRecord = null;
		for(AbstractInputRecord record : scommessaRecordList) {
			if("Betfair Exchange".equals(record.getBookmakerName())) {
				exchangeRecord = record;
			}
		}
		return exchangeRecord;
	}

	@Override
	protected RecordOutput mapBookVsBookRecordOutput(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2, double rating) {
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
		recordOutput.setSport(scommessaInputRecord1.getSport().getCode());
		BetfairExchangeInputRecord exchangeRecord = (BetfairExchangeInputRecord)scommessaInputRecord2;
		recordOutput.setLiquidità(exchangeRecord.getLiquidita());
		return recordOutput;
	}

	@Override
	protected double getRatingByScommessaPair(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2) {
		return (new RatingCalculatorBookMakerExchangeOdds()).calculateRating(scommessaInputRecord1.getQuota(), scommessaInputRecord2.getQuota());
	}
}
