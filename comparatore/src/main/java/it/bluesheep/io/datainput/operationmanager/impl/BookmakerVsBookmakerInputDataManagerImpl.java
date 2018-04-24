package it.bluesheep.io.datainput.operationmanager.impl;

import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.operationmanager.mapper.AbstractInputMappingProcessor;
import it.bluesheep.io.datainput.operationmanager.mapper.TxOddsInputMappingProcessor;
import it.bluesheep.service.api.impl.TxOddsApiImpl;

public final class BookmakerVsBookmakerInputDataManagerImpl extends InputDataManagerImpl{ 
	
	private AbstractInputMappingProcessor processor;
	
	public BookmakerVsBookmakerInputDataManagerImpl() {
		super();
		processor = new TxOddsInputMappingProcessor();
	}
	
	@Override
	public List<String> getDataFromService(Scommessa scommessa, Sport sport) {
		
		apiServiceInterface = new TxOddsApiImpl();
		
		//qui si eseguirà un comando (ad esempio "getData(Scommessa scommessa, Sport sport)") 
		//che restituirà la stringa JSON della chiamata 
		
		return null;
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
