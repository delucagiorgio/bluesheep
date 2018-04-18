package it.bluesheep.io.datainput.operationmanager.impl;

import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.operationmanager.mapper.AbstractInputMappingProcessor;
import it.bluesheep.io.datainput.operationmanager.mapper.BetfairInputMappingProcessor;

public class ExchangeVsBookmakerInputDataManagerImpl extends InputDataManagerImpl {

	private AbstractInputMappingProcessor processor;
	
	public ExchangeVsBookmakerInputDataManagerImpl() {
		super();
		processor = new BetfairInputMappingProcessor();
	}
	
	@Override
	public String getDataFromService(Scommessa scommessa, Sport sport) { 
		// da valutare il tipo dell'oggetto ritornato (potremmo far tornare un oggetto generico)
		// e poi castarlo nel metodo della sotto classe che mappa il nostro AbstractInputRecord
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport) {
		// TODO Auto-generated method stub
		return null;
	}

}
