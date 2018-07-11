package it.bluesheep.io.datacompare.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.record.BetfairExchangeInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.AbstractProcessDataManager;
import it.bluesheep.io.datacompare.multithreadcompare.OddsComparisonSplitter;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.io.datacompare.util.ICompareInformationEvents;

public class BetfairExchangeProcessDataManager extends AbstractProcessDataManager implements ICompareInformationEvents{
		
	protected BetfairExchangeProcessDataManager() {
		super();
	}
	
	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds
					(List<AbstractInputRecord> exchangeList, ChiaveEventoScommessaInputRecordsMap eventiTxOddsMap) throws Exception{
		for(AbstractInputRecord record : exchangeList) {
			String[] splittedEventoKeyRecord = record.getKeyEvento().split("\\|");
			String key = splittedEventoKeyRecord[1];
			Map<Date, Map<String, Map<Scommessa,List<AbstractInputRecord>>>> dateMap = eventiTxOddsMap.get(Sport.valueOf(key));
			for(Date date : dateMap.keySet()) {
				if(record.compareDate(date, record.getDataOraEvento())) {
					for(String eventoTxOdds : dateMap.get(date).keySet()) {
						String[] splittedEventoKey = eventoTxOdds.split("\\|");
						String sport = splittedEventoKey[1];
						String[] partecipantiSplitted = splittedEventoKey[2].split(" vs ");
						String partecipante1 = partecipantiSplitted[0];
						String partecipante2 = partecipantiSplitted[1];
						
						BetfairExchangeInputRecord exchangeRecord = (BetfairExchangeInputRecord) record;
						
						if(exchangeRecord.isSameEventAbstractInputRecord(date, sport, partecipante1, partecipante2) || 
													exchangeRecord.isSameEventSecondaryMatch(date, sport, partecipante1, partecipante2)) {
							Map<Scommessa, List<AbstractInputRecord>> mapScommessaRecord = dateMap.get(date).get(eventoTxOdds);
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
			}
		}
		return exchangeList;
	}

	@Override
	public List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap sportMap, Sport sport) {
		
		List<RecordOutput> mappedOutputRecord = new ArrayList<RecordOutput>();	
		OddsComparisonSplitter oddsComparisonSplitter = new OddsComparisonSplitter();
		mappedOutputRecord = oddsComparisonSplitter.startComparisonOdds(sportMap, sport, "BETFAIR");
		
		return mappedOutputRecord;
			
	}

}
