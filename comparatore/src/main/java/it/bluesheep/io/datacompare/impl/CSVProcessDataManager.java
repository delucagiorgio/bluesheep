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
		for(AbstractInputRecord record : csvEventList) {
			for(String eventoTxOdds : eventiTxOddsMap.keySet()) {
				String[] splittedEventoKey = eventoTxOdds.split("\\|");
				SimpleDateFormat bet365Sdf = new SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.ENGLISH);
				Date dataOraEvento = null;
				try {
					dataOraEvento = bet365Sdf.parse(splittedEventoKey[0]);
				} catch (ParseException e) {
					logger.warning("Event with keyEvento " + eventoTxOdds + " cannot be parsed on date : error is " + e.getMessage());
				}
				String sport = splittedEventoKey[1];
				String[] partecipantiSplitted = splittedEventoKey[2].split(" vs ");
				String partecipante1 = partecipantiSplitted[0];
				String partecipante2 = partecipantiSplitted[1];
				
				CSVInputRecord csvRecord = (CSVInputRecord) record;
				
				if(dataOraEvento != null && csvRecord.isSameEventAbstractInputRecord(dataOraEvento, sport, partecipante1, partecipante2)) {
					Map<Scommessa, List<AbstractInputRecord>> mapScommessaRecord = eventiTxOddsMap.get(eventoTxOdds);
					List<Scommessa> scommessaSet = new ArrayList<Scommessa>(mapScommessaRecord.keySet());
					AbstractInputRecord bookmakerRecord = mapScommessaRecord.get(scommessaSet.get(0)).get(0); 
					csvRecord.setCampionato(bookmakerRecord.getCampionato());
					csvRecord.setDataOraEvento(bookmakerRecord.getDataOraEvento());
					csvRecord.setKeyEvento(bookmakerRecord.getKeyEvento());
					csvRecord.setPartecipante1(bookmakerRecord.getPartecipante1());
					csvRecord.setPartecipante2(bookmakerRecord.getPartecipante2());
					break;
				}
			}
		}
		return csvEventList;
	}

	@Override
	public List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport) throws Exception {
		throw new Exception("Incorrect implementation of getRatingByScommessaPair");
		//Viene lasciato fare al processManager di TxOdds : tratterà gli eventi di Bet365 come un normale Bookmaker in più
	}

	@Override
	protected double getRatingByScommessaPair(AbstractInputRecord scommessaInputRecord1,
			AbstractInputRecord scommessaInputRecord2) throws Exception {
		throw new Exception("Incorrect implementation of getRatingByScommessaPair");
		//Viene lasciato fare al processManager di TxOdds : tratterà gli eventi di Bet365 come un normale Bookmaker in più
	}

	@Override
	protected RecordOutput mapRecordOutput(AbstractInputRecord scommessaInputRecord1,
			AbstractInputRecord scommessaInputRecord2, double rating) throws Exception {
		throw new Exception("Incorrect implementation of mapRecordOutput");
		//Viene lasciato fare al processManager di TxOdds : tratterà gli eventi di Bet365 come un normale Bookmaker in più
	}

}
