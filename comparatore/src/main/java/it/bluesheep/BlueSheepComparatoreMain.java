package it.bluesheep;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.output.subtype.RecordBookmakerVsExchangeOdds;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.IProcessDataManager;
import it.bluesheep.io.datacompare.impl.ExchangeProcessDataManager;
import it.bluesheep.io.datacompare.impl.TxOddsProcessDataManager;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.io.datainput.IInputDataManager;
import it.bluesheep.io.datainput.operationmanager.impl.BookmakerVsBookmakerInputDataManagerImpl;
import it.bluesheep.io.datainput.operationmanager.impl.ExchangeVsBookmakerInputDataManagerImpl;

public class BlueSheepComparatoreMain {
	
	private static Properties properties = new Properties(); 

    static {
        try {
            InputStream in = BlueSheepComparatoreMain.class.getResourceAsStream("/bluesheepComparatore.properties");
            properties.load(in);
            in.close();
        } catch (IOException exception) {
            System.out.println("Error loading the properties file: " + exception.toString());
            System.exit(-1);
        }
    }
	
	public static void main(String[] args) {
		//inizializzo le variabili necessarie per effettuare tutte le chiamate
		IInputDataManager inputDataManager;
		IProcessDataManager processDataManager;
		ChiaveEventoScommessaInputRecordsMap eventoScommessaRecordMap = new ChiaveEventoScommessaInputRecordsMap();
		
		
		//interrogazione di TxOdds
		inputDataManager = new BookmakerVsBookmakerInputDataManagerImpl();
		List<AbstractInputRecord> txOddsMappedRecordsFromJson = new ArrayList<AbstractInputRecord>();

		//Per ogni sport
		for(Sport sport : Sport.values()) {
			List<AbstractInputRecord> txOddsMappedRecordsFromJsonBySport = inputDataManager.processAllData(sport);	
			for(AbstractInputRecord record : txOddsMappedRecordsFromJsonBySport) {
				eventoScommessaRecordMap.addToMapEventoScommessaRecord(record);
			}
			txOddsMappedRecordsFromJson.addAll(txOddsMappedRecordsFromJsonBySport);
		}
		//Avvio comparazione quote tabella 1
		List<RecordOutput> tabella1OutputList = new ArrayList<RecordOutput>();
		processDataManager = new TxOddsProcessDataManager();
		for(Sport sport : Sport.values()) {
			try{
				tabella1OutputList.addAll(processDataManager.compareOdds(eventoScommessaRecordMap, sport));
			}catch(Exception e) {
				e.printStackTrace(System.out);
			}
		}
		
		for(RecordOutput output : tabella1OutputList) {
			System.out.println(output.getEvento() + ";" + output.getCampionato() + ";" + output.getDataOraEvento() + ";" +
							   output.getSport() + ";" + output.getBookmakerName1() + ";" + 
							   output.getQuotaScommessaBookmaker1() + ";" + output.getScommessaBookmaker1() + ";" +
							   output.getBookmakerName2() + ";" + output.getQuotaScommessaBookmaker2() + ";" + 
							   output.getScommessaBookmaker2() + ";" + output.getRating());
		}
		
		//Interrogazione Betfair
		inputDataManager = new ExchangeVsBookmakerInputDataManagerImpl();
		processDataManager = new ExchangeProcessDataManager();
		
		List<AbstractInputRecord> betfairMappedRecordsFromJson = new ArrayList<AbstractInputRecord>();
		
		//Per ogni sport
		for(Sport sport : Sport.values()) {
			betfairMappedRecordsFromJson = inputDataManager.processAllData(sport);	
			betfairMappedRecordsFromJson = ((ExchangeProcessDataManager) processDataManager).compareAndCollectSameEventsFromExchangeAndBookmakers(betfairMappedRecordsFromJson, txOddsMappedRecordsFromJson);
			for(AbstractInputRecord record : betfairMappedRecordsFromJson) {
				eventoScommessaRecordMap.addToMapEventoScommessaRecord(record);
			}
		}
		
		//Avvio comparazione quote tabella 2
		List<RecordOutput> tabella2OutputList = new ArrayList<RecordOutput>();
		processDataManager = new ExchangeProcessDataManager();
		for(Sport sport : Sport.values()) {
			try{
				tabella2OutputList.addAll(processDataManager.compareOdds(eventoScommessaRecordMap, sport));
			}catch(Exception e) {
				e.printStackTrace(System.out);
			}
		}
		
		for(RecordOutput output : tabella2OutputList) {
			System.out.println(output.getEvento() + ";" + output.getCampionato() + ";" + output.getDataOraEvento() + ";" +
							   output.getSport() + ";" + output.getBookmakerName1() + ";" + 
							   output.getQuotaScommessaBookmaker1() + ";" + output.getScommessaBookmaker1() + ";" +
							   output.getBookmakerName2() + ";" + output.getQuotaScommessaBookmaker2() + ";" + 
							   output.getScommessaBookmaker2() + ";" + output.getRating() + ";" + ((RecordBookmakerVsExchangeOdds)output).getLiquidità());
		}
	}
	
	public static Properties getProperties() {
		return properties;
	}
}