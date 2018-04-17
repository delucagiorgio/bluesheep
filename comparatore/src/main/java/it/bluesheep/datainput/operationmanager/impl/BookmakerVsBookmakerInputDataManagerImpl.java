package it.bluesheep.datainput.operationmanager.impl;

import java.util.List;

import it.bluesheep.datainput.operationmanager.InputDataManagerImpl;
import it.bluesheep.datainput.operationmanager.util.TxOddsInputMappingProcessor;
import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;

public class BookmakerVsBookmakerInputDataManagerImpl extends InputDataManagerImpl{ 
	
	private static final String TX_ODDS_API = "TX_ODDS";
	
	@Override
	public String getDataFromService() {
		
		return null;
	}

	@Override
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, String serviceName, Scommessa tipoScommessa) {
		List<AbstractInputRecord> abstractInputRecordsList = null;
		
		if(TX_ODDS_API.equals(serviceName)) {
			//esegui mapping secondo TXODDS
			abstractInputRecordsList = TxOddsInputMappingProcessor.mapInputRecordIntoAbstractInputRecord(jsonString, tipoScommessa);
		}
		
		return abstractInputRecordsList;
	}

}
