package it.bluesheep.io.datacompare.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.record.BetfairExchangeInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.output.subtype.RecordBookmakerVsExchangeOdds;
import it.bluesheep.entities.util.rating.impl.RatingCalculatorBookMakerExchangeOdds;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.AbstractProcessDataManager;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.io.datacompare.util.ICompareInformationEvents;

public class BetfairExchangeProcessDataManager extends AbstractProcessDataManager implements ICompareInformationEvents{
	
	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds
					(List<AbstractInputRecord> exchangeList, ChiaveEventoScommessaInputRecordsMap eventiTxOddsMap) throws Exception{
		for(AbstractInputRecord record : exchangeList) {
			for(String eventoTxOdds : eventiTxOddsMap.keySet()) {
				String[] splittedEventoKey = eventoTxOdds.split("\\|");
				SimpleDateFormat sdf = new SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.UK);
				Date dataOraEvento = null;
				try {
					dataOraEvento = sdf.parse(splittedEventoKey[0]);
				} catch (ParseException e) {
					logger.warning("Event with keyEvento " + eventoTxOdds + " cannot be parsed on date : error is " + e.getMessage());
				}
				String sport = splittedEventoKey[1];
				String[] partecipantiSplitted = splittedEventoKey[2].split(" vs ");
				String partecipante1 = partecipantiSplitted[0];
				String partecipante2 = partecipantiSplitted[1];
				
				BetfairExchangeInputRecord exchangeRecord = (BetfairExchangeInputRecord) record;
				
				if(dataOraEvento != null && (exchangeRecord.isSameEventAbstractInputRecord(dataOraEvento, sport, partecipante1, partecipante2) || 
											exchangeRecord.isSameEventSecondaryMatch(dataOraEvento, sport, partecipante1, partecipante2))) {
					Map<Scommessa, List<AbstractInputRecord>> mapScommessaRecord = eventiTxOddsMap.get(eventoTxOdds);
					List<Scommessa> scommessaSet = new ArrayList<Scommessa>(mapScommessaRecord.keySet());
					AbstractInputRecord bookmakerRecord = mapScommessaRecord.get(scommessaSet.get(0)).get(0); 
					exchangeRecord.setCampionato(bookmakerRecord.getCampionato());
					exchangeRecord.setDataOraEvento(bookmakerRecord.getDataOraEvento());
					exchangeRecord.setKeyEvento(bookmakerRecord.getKeyEvento());
					exchangeRecord.setPartecipante1(bookmakerRecord.getPartecipante1());
					exchangeRecord.setPartecipante2(bookmakerRecord.getPartecipante2());
					break;
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
				if(eventoScommessaRecordList != null && 
						!eventoScommessaRecordList.isEmpty() && 
						!eventoScommessaRecordList.get(0).getSport().equals(sport)) {
					break;
				}
				AbstractInputRecord exchangeRecord = findExchangeRecord(inputRecordEventoScommessaMap.get(scommessa));
				//Se trovato
				if(exchangeRecord != null) {
					List<RecordOutput> outputRecordsList = verifyRequirementsAndMapOddsComparison(eventoScommessaRecordList,exchangeRecord);
					mappedOutputRecord.addAll(outputRecordsList);			
				}
			}
		}
		
		logger.info("Comparison completed successfully. Total events are " + dataMap.keySet().size() + ". Total comparison elaborated for sport " + sport + " are " + mappedOutputRecord.size());
		
		return mappedOutputRecord;
			
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
			if(record != exchangeRecord) {
				double rating1 = getRatingByScommessaPair(record, exchangeRecord);
				//se il rating1 è sufficientemente alto
				if(rating1 >= new Double(BlueSheepComparatoreMain.getProperties().getProperty("BETFAIR_MIN_THRESHOLD")).doubleValue() &&
				   rating1 <= new Double(BlueSheepComparatoreMain.getProperties().getProperty("BETFAIR_MAX_THRESHOLD")).doubleValue()) {
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
	private AbstractInputRecord findExchangeRecord(List<AbstractInputRecord> scommessaRecordList) {
		AbstractInputRecord exchangeRecord = null;
		for(AbstractInputRecord record : scommessaRecordList) {
			if("Betfair Exchange".equals(record.getBookmakerName())) {
				exchangeRecord = record;
				break;
			}
		}
		return exchangeRecord;
	}

	@Override
	protected RecordOutput mapRecordOutput(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2, double rating) {
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
		recordOutput = (RecordBookmakerVsExchangeOdds) translateFieldAboutCountry(recordOutput);
		return recordOutput;
	}

	@Override
	protected double getRatingByScommessaPair(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2) {
		return (new RatingCalculatorBookMakerExchangeOdds()).calculateRating(scommessaInputRecord1.getQuota(), scommessaInputRecord2.getQuota());
	}
}
