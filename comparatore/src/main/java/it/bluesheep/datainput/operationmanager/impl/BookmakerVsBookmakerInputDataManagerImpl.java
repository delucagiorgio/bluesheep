package it.bluesheep.datainput.operationmanager.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import comparatore.test.json.TestJsonConversion;
import it.bluesheep.datainput.operationmanager.InputDataManagerImpl;
import it.bluesheep.datainput.operationmanager.dataprocessor.AbstractInputMappingProcessor;
import it.bluesheep.datainput.operationmanager.dataprocessor.TxOddsInputMappingProcessor;
import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;

public final class BookmakerVsBookmakerInputDataManagerImpl extends InputDataManagerImpl{ 
	
	private AbstractInputMappingProcessor processor;
	
	public BookmakerVsBookmakerInputDataManagerImpl() {
		super();
		processor = new TxOddsInputMappingProcessor();
	}
	
	@Override
	public String getDataFromService(Scommessa scommessa, Sport sport) {
		String inputJson = "";
		InputStream inStream = null;
		BufferedReader br = null;
		try {
			inStream = TestJsonConversion.class.getResourceAsStream("/CALCIO_TOTAL.txt");
			br = new BufferedReader(new InputStreamReader(inStream));
			String inputLine = br.readLine();
			while(inputLine != null) {
				inputJson = inputJson + inputLine;
				inputLine = br.readLine();
			}
			br.close();
			inStream.close();
		}catch(Exception e) {
			System.out.println("Exception is " + e.getMessage());
		}
		return inputJson;
	}

	/**
	 * GD - 17/04/18
	 * Metodo che dati in input il JSON da parsare e il tipo di scommessa di cui si vogliono ottenere le quote,
	 * crea una lista di AbstractInputRecord contenente i dati necessari
	 * @param jsonString il JSON da parsare
	 * @param tipoScommessa la tipologia di scommessa per la quale si vogliono ottenere i risultati
	 * @return una lista di AbstractInputRecord contenente i dati relativi al tipo di scommessa scelto
	 */
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport) {

		List<AbstractInputRecord> abstractInputRecordsList = null;
	
		//esegui mapping secondo TXODDS
		abstractInputRecordsList = processor.mapInputRecordIntoAbstractInputRecord(jsonString, tipoScommessa, sport);
		
		return abstractInputRecordsList;
	}

}
