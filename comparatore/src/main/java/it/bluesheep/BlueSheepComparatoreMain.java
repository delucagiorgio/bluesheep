package it.bluesheep;

import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.IProcessDataManager;
import it.bluesheep.io.datainput.IInputDataManager;
import it.bluesheep.io.datainput.operationmanager.impl.ExchangeVsBookmakerInputDataManagerImpl;

public class BlueSheepComparatoreMain {

	public static void main(String[] args) {
		
		//inizializzo le variabili necessarie per effettuare tutte le chiamate
		IInputDataManager inputDataManager;
		IProcessDataManager processDataManager;
		ChiaveEventoScommessaInputRecordsMap inputRecordMap = new ChiaveEventoScommessaInputRecordsMap();
		
		//interrogazione di TxOdds
		
		inputDataManager = new ExchangeVsBookmakerInputDataManagerImpl();
//		processDataManager = new TxOddsProcessDataManager();
		
		for(Sport sport : Sport.values()) {
			
			List<AbstractInputRecord> mappedRecordsFromJSONRequest = inputDataManager.processAllData(sport);
			for(AbstractInputRecord record : mappedRecordsFromJSONRequest) {
				inputRecordMap.addToMapEventoScommessaRecord(record);
			}
		}
		
//		List<RecordOutput> recordToOutputList = new ArrayList<RecordOutput>();
//		
//		try{
//			for(Sport sport : Sport.values()) {
//				recordToOutputList.addAll(processDataManager.compareOdds(inputRecordMap, sport));
//			}
//		}catch(Exception e) {
//			e.printStackTrace(System.out);
//		}
//		
//		for(RecordOutput output : recordToOutputList) {
//			System.out.println(output.getEvento() + ";" + output.getCampionato() + ";" + output.getDataOraEvento() + ";" +
//							   output.getSport() + ";" + output.getBookmakerName1() + ";" + 
//							   output.getQuotaScommessaBookmaker1() + ";" + output.getScommessaBookmaker1() + ";" +
//							   output.getBookmakerName2() + ";" + output.getQuotaScommessaBookmaker2() + ";" + 
//							   output.getScommessaBookmaker2() + ";" + output.getRating());
//		}
		
		

	}

}
