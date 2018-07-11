package it.bluesheep.io.datacompare.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.record.CSVInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.AbstractProcessDataManager;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.io.datacompare.util.ICompareInformationEvents;

public class CSVProcessDataManager extends AbstractProcessDataManager implements ICompareInformationEvents {
	
	protected CSVProcessDataManager() {
		super();
	}

	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds(
			List<AbstractInputRecord> csvEventList, ChiaveEventoScommessaInputRecordsMap eventiTxOddsMap)
			throws Exception {
		
		List<AbstractInputRecord> csvEventListUpdatedInfo = new ArrayList<AbstractInputRecord>();
		for(AbstractInputRecord record : csvEventList) {
			String[] splittedEventoKeyRecord = record.getKeyEvento().split("\\|");
			String key = splittedEventoKeyRecord[1];
			Map<Date, Map<String, Map<Scommessa,List<AbstractInputRecord>>>> dataMap = eventiTxOddsMap.get(Sport.valueOf(key));
			for(Date date : dataMap.keySet()) {
				if(record.compareDate(date, record.getDataOraEvento())) {
					for(String eventoTxOdds : dataMap.get(date).keySet()) { 
						String[] splittedEventoKey = eventoTxOdds.split("\\|");
						String sport = splittedEventoKey[1];
						String[] partecipantiSplitted = splittedEventoKey[2].split(" vs ");
						String partecipante1 = partecipantiSplitted[0];
						String partecipante2 = partecipantiSplitted[1];
						
						CSVInputRecord csvRecord = (CSVInputRecord) record;
						
						if(csvRecord.isSameEventAbstractInputRecord(date, sport, partecipante1, partecipante2)) {
							Map<Scommessa, List<AbstractInputRecord>> mapScommessaRecord = dataMap.get(date).get(eventoTxOdds);
							List<Scommessa> scommessaSet = new ArrayList<Scommessa>(mapScommessaRecord.keySet());
							AbstractInputRecord bookmakerRecord = mapScommessaRecord.get(scommessaSet.get(0)).get(0); 
							AbstractInputRecord csvRecordCopy = new CSVInputRecord(csvRecord); 
							csvRecordCopy.setCampionato(bookmakerRecord.getCampionato());
							csvRecordCopy.setDataOraEvento(bookmakerRecord.getDataOraEvento());
							csvRecordCopy.setKeyEvento(bookmakerRecord.getKeyEvento());
							csvRecordCopy.setPartecipante1(bookmakerRecord.getPartecipante1());
							csvRecordCopy.setPartecipante2(bookmakerRecord.getPartecipante2());
							csvRecordCopy.setBookmakerName(csvRecord.getBookmakerName());
							csvRecordCopy.setQuota(csvRecord.getQuota());
							csvRecordCopy.setTipoScommessa(csvRecord.getTipoScommessa());
							csvEventListUpdatedInfo.add(csvRecordCopy);
							break;
						}
					}
				}
			}
		}
		return csvEventListUpdatedInfo;
	}

	@Override
	public List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport) throws Exception {
		throw new Exception("Incorrect implementation of getRatingByScommessaPair");
		//Viene lasciato fare al processManager di TxOdds : tratterà gli eventi di Bet365 come un normale Bookmaker in più
	}

}
