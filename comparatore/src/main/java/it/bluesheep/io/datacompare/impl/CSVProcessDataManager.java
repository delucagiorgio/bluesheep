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
			for(String eventoTxOdds : eventiTxOddsMap.keySet()) { 
				String[] splittedEventoKey = eventoTxOdds.split("\\|");
				SimpleDateFormat bet365Sdf = new SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.UK);
				Date dataOraEvento = null;
				try {
					dataOraEvento = bet365Sdf.parse(splittedEventoKey[0]);
				} catch (ParseException e) {
					logger.warning("Event with keyEvento " + eventoTxOdds + " cannot be parsed on date : error is " + e.getMessage());
				}
				String sport = splittedEventoKey[1];
				String[] partecipantiSplitted = splittedEventoKey[2].split(" vs ");
				String partecipante1 = partecipantiSplitted[0];
				if("Napoli".equals(partecipante1)) {
					System.out.print("TROVATO\n");
				}
				String partecipante2 = partecipantiSplitted[1];
				
				CSVInputRecord csvRecord = (CSVInputRecord) record;
				
				if(dataOraEvento != null && csvRecord.isSameEventAbstractInputRecord(dataOraEvento, sport, partecipante1, partecipante2)) {
					Map<Scommessa, List<AbstractInputRecord>> mapScommessaRecord = eventiTxOddsMap.get(eventoTxOdds);
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
		return csvEventListUpdatedInfo;
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
