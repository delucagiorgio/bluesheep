package it.bluesheep;

import java.util.ArrayList;
import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.IInputDataManager;
import it.bluesheep.io.datainput.operationmanager.impl.BookmakerVsBookmakerInputDataManagerImpl;

public class BlueSheepComparatoreMain {

	public static void main(String[] args) {
		
		//inizializzo le variabili necessarie per effettuare tutte le chiamate
		List<Scommessa> scommessaTipoList = new ArrayList<Scommessa>();
		IInputDataManager inputDataManager;
		ChiaveEventoScommessaInputRecordsMap inputRecordMap = new ChiaveEventoScommessaInputRecordsMap();
		
		for(Scommessa scommessa : Scommessa.values()) {
			scommessaTipoList.add(scommessa);
		}
		
		//interrogazione di TxOdds
		
		inputDataManager = new BookmakerVsBookmakerInputDataManagerImpl();
		
		for(Sport sport : Sport.values()) {
			
			List<AbstractInputRecord> mappedRecordsFromJSONRequest = inputDataManager.processAllData(sport, scommessaTipoList);
			for(AbstractInputRecord record : mappedRecordsFromJSONRequest) {
				inputRecordMap.addToMapEventoScommessaRecord(record);
			}
			
			System.out.print("Sport " + sport.getCode() + " successfully mapped");
		}
		
		

	}

}
