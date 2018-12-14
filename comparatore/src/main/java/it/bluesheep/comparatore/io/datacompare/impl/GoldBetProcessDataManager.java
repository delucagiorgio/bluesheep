package it.bluesheep.comparatore.io.datacompare.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.record.GoldBetInputRecord;
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

public class GoldBetProcessDataManager extends AbstractProcessDataManager implements ICompareInformationEvents {

	protected GoldBetProcessDataManager() {
		super();
		service = Service.GOLDBET_SERVICENAME;
	}
	
	@Override
	public List<ArbsRecord> compareThreeWayOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport,
			AbstractBlueSheepService bluesheepServiceType) throws Exception {
		throw new Exception("Incorrect implementation of compareThreeWayOdds");
	}

	@Override
	public List<RecordOutput> compareTwoWayOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport,
			AbstractBlueSheepService bluesheepServiceType) throws Exception {
		throw new Exception("Incorrect implementation of compareTwoWayOdds");
	}

	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds(List<AbstractInputRecord> bookmakerInputList) throws Exception {
		ChiaveEventoScommessaInputRecordsMap eventiTxOddsMap = BlueSheepSharedResources.getEventoScommessaRecordMap();
		List<AbstractInputRecord> listExchangeRecordListCopy = new ArrayList<AbstractInputRecord>(BlueSheepSharedResources.getExchangeRecordsList());
		List<AbstractInputRecord> goldBetEventListUpdatedInfo = new ArrayList<AbstractInputRecord>();
		for(AbstractInputRecord record : bookmakerInputList) {
			String[] splittedEventoKeyRecord = record.getKeyEvento().split("\\|");
			String key = splittedEventoKeyRecord[1];
			Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> dataMap = eventiTxOddsMap.get(Sport.valueOf(key));
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
						
						GoldBetInputRecord goldBetRecord = (GoldBetInputRecord) record;
						AbstractInputRecord exchangeRecord = BlueSheepSharedResources.findExchangeRecord(record, listExchangeRecordListCopy);
						
						if(goldBetRecord.isSameEventAbstractInputRecord(date, sport, partecipante1, partecipante2)) {
							Map<Scommessa, Map<String, AbstractInputRecord>> mapScommessaRecord = dataMap.get(date).get(eventoTxOdds);
							List<Scommessa> scommessaSet = new ArrayList<Scommessa>(mapScommessaRecord.keySet());
							Map<String, AbstractInputRecord> bookmakerRecordMap = mapScommessaRecord.get(scommessaSet.get(0));
							List<String> bookmakerList = new ArrayList<String>(bookmakerRecordMap.keySet());
							if(!bookmakerList.isEmpty()) {
								AbstractInputRecord bookmakerRecord = bookmakerRecordMap.get(bookmakerList.get(0)); 
								AbstractInputRecord goldBetRecordCopy = new GoldBetInputRecord(goldBetRecord); 
								goldBetRecordCopy.setCampionato(bookmakerRecord.getCampionato());
								if(exchangeRecord != null) {
									goldBetRecordCopy.setDataOraEvento(exchangeRecord.getDataOraEvento());
								}else {
									goldBetRecordCopy.setDataOraEvento(bookmakerRecord.getDataOraEvento());
								}
								goldBetRecordCopy.setPartecipante1(bookmakerRecord.getPartecipante1());
								goldBetRecordCopy.setPartecipante2(bookmakerRecord.getPartecipante2());
								goldBetRecordCopy.setKeyEvento("" + goldBetRecordCopy.getDataOraEvento() + BlueSheepConstants.REGEX_PIPE + 
										goldBetRecordCopy.getSport() + BlueSheepConstants.REGEX_PIPE + 
										goldBetRecordCopy.getPartecipante1()+ BlueSheepConstants.REGEX_VERSUS + 
										goldBetRecordCopy.getPartecipante2());
								goldBetRecordCopy.setBookmakerName(goldBetRecord.getBookmakerName());
								goldBetRecordCopy.setQuota(goldBetRecord.getQuota());
								goldBetRecordCopy.setTipoScommessa(goldBetRecord.getTipoScommessa());
								goldBetEventListUpdatedInfo.add(goldBetRecordCopy);
							}
							break;
						}
					}
				}
			}
		}
		return goldBetEventListUpdatedInfo;
	}

}
