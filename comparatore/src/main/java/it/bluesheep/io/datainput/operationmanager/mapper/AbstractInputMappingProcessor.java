package it.bluesheep.io.datainput.operationmanager.mapper;

import java.util.List;
import java.util.logging.Logger;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.util.BlueSheepLogger;

public abstract class AbstractInputMappingProcessor {

	protected static Logger logger;
	
	protected AbstractInputMappingProcessor() {
		logger = (new BlueSheepLogger(AbstractInputMappingProcessor.class)).getLogger();
	}

	
	public abstract List<AbstractInputRecord> mapInputRecordIntoAbstractInputRecord(String jsonString, Scommessa scommessaTipo, Sport sport);

}
