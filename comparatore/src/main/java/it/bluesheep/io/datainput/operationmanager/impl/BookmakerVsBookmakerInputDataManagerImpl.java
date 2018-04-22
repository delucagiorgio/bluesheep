package it.bluesheep.io.datainput.operationmanager.impl;

import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.operationmanager.mapper.AbstractInputMappingProcessor;
import it.bluesheep.io.datainput.operationmanager.mapper.TxOddsInputMappingProcessor;

public final class BookmakerVsBookmakerInputDataManagerImpl extends InputDataManagerImpl{ 
	
	private AbstractInputMappingProcessor processor;
	
	public BookmakerVsBookmakerInputDataManagerImpl() {
		super();
		processor = new TxOddsInputMappingProcessor();
	}
	
	@Override
	public String getDataFromService(Scommessa scommessa, Sport sport) {
		String inputJson = "";
//		InputStream inStream = null;
//		BufferedReader br = null;
//		try {
//			inStream = BookmakerVsBookmakerInputDataManagerImpl.class.getResourceAsStream("/CALCIO_TOTAL.txt");
//			br = new BufferedReader(new InputStreamReader(inStream));
//			String inputLine = br.readLine();
//			while(inputLine != null) {
//				inputJson = inputJson + inputLine;
//				inputLine = br.readLine();
//			}
//			br.close();
//			inStream.close();
//		}catch(Exception e) {
//			System.out.println("Exception is " + e.getMessage());
//		}
		return inputJson;
	}

	/**
	 * GD - 17/04/18
	 * Metodo che prende come dati in input il JSON da parsare e il tipo di scommessa di cui si vogliono ottenere le quote,
	 * crea una lista di AbstractInputRecord contenente i dati contenenti le informazioni di output
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
