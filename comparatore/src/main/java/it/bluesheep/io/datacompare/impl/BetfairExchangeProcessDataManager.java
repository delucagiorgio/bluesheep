package it.bluesheep.io.datacompare.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
		
	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds
					(List<AbstractInputRecord> exchangeList, ChiaveEventoScommessaInputRecordsMap eventiTxOddsMap) throws Exception{
		for(AbstractInputRecord record : exchangeList) {
			String[] splittedEventoKeyRecord = record.getKeyEvento().split("\\|");
			String sport = splittedEventoKeyRecord[1];
			SimpleDateFormat sdf = new SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.UK);
			Date dataOraEvento = null;
			try {
				dataOraEvento = sdf.parse(splittedEventoKeyRecord[0]);
			} catch (ParseException e) {
				logger.warning("Event with keyEvento " + record.getKeyEvento() + " cannot be parsed on date : error is " + e.getMessage());
			}
			Map<Date, Map<String, Map<Scommessa, List<AbstractInputRecord>>>> dataMap = eventiTxOddsMap.get(Sport.valueOf(sport));
			for(Date date : dataMap.keySet()) {
				for(String eventoTxOdds : dataMap.get(date).keySet()) {
					String[] partecipantiSplitted = splittedEventoKeyRecord[2].split(" vs ");
					String partecipante1 = partecipantiSplitted[0];
					String partecipante2 = partecipantiSplitted[1];
					
					BetfairExchangeInputRecord exchangeRecord = (BetfairExchangeInputRecord) record;
					
					if(exchangeRecord != null && dataOraEvento != null && (exchangeRecord.isSameEventAbstractInputRecord(dataOraEvento, sport, partecipante1, partecipante2) || 
												exchangeRecord.isSameEventSecondaryMatch(dataOraEvento, sport, partecipante1, partecipante2))) {
						Map<Scommessa, List<AbstractInputRecord>> mapScommessaRecord = dataMap.get(date).get(eventoTxOdds);
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
