package it.bluesheep.io.datacompare.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.record.Bet365InputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.ComparatoreConstants;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.AbstractProcessDataManager;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.io.datacompare.util.ICompareInformationEvents;

public class Bet365ProcessDataManager extends AbstractProcessDataManager implements ICompareInformationEvents {

	
	@Override
	public List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport) throws Exception {
		throw new Exception("Incorrect implementation of compareOdds");
		//Viene lasciato fare al processManager di TxOdds : tratterà gli eventi di Bet365 come un normale Bookmaker in più
	}

	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds(List<AbstractInputRecord> bookmakerList, ChiaveEventoScommessaInputRecordsMap sportMap) throws Exception {
		logger.log(Level.INFO, "Start matching informartion for Bet365 on TxOdds events : "
				+ "input size Bet365 events is " + bookmakerList.size());
		int matchedCountEvents = 0;
		for(AbstractInputRecord record : bookmakerList) {
			String[] splittedEventoKeyRecord = record.getKeyEvento().split("\\|");
			String key = splittedEventoKeyRecord[1];
			Map<Date, Map<String, Map<Scommessa,List<AbstractInputRecord>>>> dataMap = sportMap.get(Sport.valueOf(key));
			for(Date date : dataMap.keySet()) {
				if(record.compareDate(date, record.getDataOraEvento())) {
					for(String eventoTxOdds : dataMap.get(date).keySet()) {
						String[] splittedEventoKey = eventoTxOdds.split("\\|");
						String sport = splittedEventoKey[1];
						String[] partecipantiSplitted = splittedEventoKey[2].split(ComparatoreConstants.REGEX_VERSUS);
						String partecipante1 = partecipantiSplitted[0];
						String partecipante2 = partecipantiSplitted[1];
						
						Bet365InputRecord bet365Record = (Bet365InputRecord) record;
						
						if(bet365Record.isSameEventAbstractInputRecord(date, sport, partecipante1, partecipante2) ||
								bet365Record.isSameEventSecondaryMatch(date, sport, partecipante1, partecipante2)) {
							Map<Scommessa, List<AbstractInputRecord>> mapScommessaRecord = dataMap.get(date).get(eventoTxOdds);
							List<Scommessa> scommessaSet = new ArrayList<Scommessa>(mapScommessaRecord.keySet());
							AbstractInputRecord bookmakerRecord = mapScommessaRecord.get(scommessaSet.get(0)).get(0); 
							bet365Record.setCampionato(bookmakerRecord.getCampionato());
							bet365Record.setDataOraEvento(bookmakerRecord.getDataOraEvento());
							bet365Record.setKeyEvento(bookmakerRecord.getKeyEvento());
							bet365Record.setPartecipante1(bookmakerRecord.getPartecipante1());
							bet365Record.setPartecipante2(bookmakerRecord.getPartecipante2());
							matchedCountEvents++;
							break;
						}
					}
				}
			}
		}
		
		logger.log(Level.INFO, "Matching process completed. Matched events are " + matchedCountEvents + ": events Bet365 = " + bookmakerList.size());
		
		return bookmakerList;
	}

}
