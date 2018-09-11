package it.bluesheep.comparatore.io.datacompare.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.record.Bet365InputRecord;
import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.AbstractProcessDataManager;
import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.comparatore.io.datacompare.util.ICompareInformationEvents;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.AbstractBlueSheepService;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepSharedResources;

public class Bet365ProcessDataManager extends AbstractProcessDataManager implements ICompareInformationEvents {
	
	protected Bet365ProcessDataManager() {
		super();
		this.logger = Logger.getLogger(Bet365ProcessDataManager.class);
		service = Service.BET365_SERVICENAME;
	}
	
	@Override
	public List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport, AbstractBlueSheepService bluesheepServiceType) throws Exception {
		throw new Exception("Incorrect implementation of compareOdds");
		//Viene lasciato fare al processManager di TxOdds : tratterà gli eventi di Bet365 come un normale Bookmaker in più
	}

	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds(List<AbstractInputRecord> bookmakerList, ChiaveEventoScommessaInputRecordsMap sportMap) throws Exception {
		logger.info("Start matching informartion for Bet365 on TxOdds events : "
				+ "input size Bet365 events is " + bookmakerList.size() 
				+ "; input size TxOdds events is " + sportMap.keySet().size());
		int matchedCountEvents = 0;
		for(AbstractInputRecord record : bookmakerList) {
			String[] splittedEventoKeyRecord = record.getKeyEvento().split("\\|");
			String key = splittedEventoKeyRecord[1];
			Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> dataMap = sportMap.get(Sport.valueOf(key));
			List<Date> dateList = new ArrayList<Date>(dataMap.keySet());
			for(Date date : dateList) {
				if((Sport.TENNIS.equals(record.getSport()) && AbstractInputRecord.compareDate(date, record.getDataOraEvento())) || 
						(Sport.CALCIO.equals(record.getSport()) && date.equals(record.getDataOraEvento()))) {
					List<String> eventoKeyList = new ArrayList<String>(dataMap.get(date).keySet());
					for(String eventoTxOdds : eventoKeyList) {
						String[] splittedEventoKey = eventoTxOdds.split("\\|");
						String sport = splittedEventoKey[1];
						String[] partecipantiSplitted = splittedEventoKey[2].split(BlueSheepConstants.REGEX_VERSUS);
						String partecipante1 = partecipantiSplitted[0];
						String partecipante2 = partecipantiSplitted[1];
						
						Bet365InputRecord bet365Record = (Bet365InputRecord) record;
						AbstractInputRecord exchangeRecord = BlueSheepSharedResources.findExchangeRecord(record);
						
						if(bet365Record.isSameEventAbstractInputRecord(date, sport, partecipante1, partecipante2) ||
								bet365Record.isSameEventSecondaryMatch(date, sport, partecipante1, partecipante2)) {
							Map<Scommessa, Map<String, AbstractInputRecord>> mapScommessaRecord = dataMap.get(date).get(eventoTxOdds);
							List<Scommessa> scommessaSet = new ArrayList<Scommessa>(mapScommessaRecord.keySet());
							Map<String, AbstractInputRecord> bookmakerRecordMap = mapScommessaRecord.get(scommessaSet.get(0));
							List<String> bookmakerSet = new ArrayList<String>(bookmakerRecordMap.keySet());
							if(!bookmakerSet.isEmpty()) {
								AbstractInputRecord bookmakerRecord = bookmakerRecordMap.get(bookmakerSet.get(0)); 
								bet365Record.setCampionato(bookmakerRecord.getCampionato());
								if(exchangeRecord != null) {
									bet365Record.setDataOraEvento(exchangeRecord.getDataOraEvento());
								}else {
									bet365Record.setDataOraEvento(bookmakerRecord.getDataOraEvento());
								}
								bet365Record.setPartecipante1(bookmakerRecord.getPartecipante1());
								bet365Record.setPartecipante2(bookmakerRecord.getPartecipante2());
								bet365Record.setKeyEvento("" + bet365Record.getDataOraEvento() + BlueSheepConstants.REGEX_PIPE + 
										bet365Record.getSport() + BlueSheepConstants.REGEX_PIPE + 
										bet365Record.getPartecipante1() + BlueSheepConstants.REGEX_VERSUS +
										bet365Record.getPartecipante2());
								matchedCountEvents++;
							}
							break;
						}
					}
				}
			}
		}
		
		logger.info("Matching process completed. Matched events are " + matchedCountEvents + ": events Bet365 = " + bookmakerList.size());
		
		return bookmakerList;
	}

}
