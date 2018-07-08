package it.bluesheep.io.datacompare.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds(
			List<AbstractInputRecord> csvEventList, ChiaveEventoScommessaInputRecordsMap eventiTxOddsMap)
			throws Exception {
		
		List<AbstractInputRecord> csvEventListUpdatedInfo = new ArrayList<AbstractInputRecord>();
		for(AbstractInputRecord record : csvEventList) {
			String[] splittedEventoKeyRecord = record.getKeyEvento().split("\\|");
			String sport = splittedEventoKeyRecord[1];
			SimpleDateFormat sdf = new SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.UK);
			Date dataOraEvento = null;
			try {
				dataOraEvento = sdf.parse(splittedEventoKeyRecord[0]);

				Map<Date, Map<String, Map<Scommessa, List<AbstractInputRecord>>>> dateMap = eventiTxOddsMap.get(Sport.valueOf(sport));
				Map<String, Map<Scommessa, List<AbstractInputRecord>>> eventoByDateMap = dateMap.get(dataOraEvento);
				
				if(eventoByDateMap != null) {
					for(String eventoTxOdds : eventoByDateMap.keySet()) { 
						String[] splittedEventoKey = eventoTxOdds.split("\\|");
						String[] partecipantiSplitted = splittedEventoKey[2].split(" vs ");
						String partecipante1 = partecipantiSplitted[0];
						String partecipante2 = partecipantiSplitted[1];
						
						CSVInputRecord csvRecord = (CSVInputRecord) record;
						
						if(dataOraEvento != null && csvRecord.isSameEventAbstractInputRecord(dataOraEvento, sport, partecipante1, partecipante2)) {
							Map<Scommessa, List<AbstractInputRecord>> mapScommessaRecord = eventoByDateMap.get(eventoTxOdds);
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
			} catch (ParseException e) {
				logger.warning("Event with keyEvento " + record.getKeyEvento() + " cannot be parsed on date : error is " + e.getMessage());
			}
			
		}
		return csvEventListUpdatedInfo;
	}

	@Override
	public List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport) throws Exception {
		throw new Exception("Incorrect implementation of getRatingByScommessaPair");
		//Viene lasciato fare al processManager di TxOdds : tratterà gli eventi del CSV come un normale Bookmaker in più
	}

}
