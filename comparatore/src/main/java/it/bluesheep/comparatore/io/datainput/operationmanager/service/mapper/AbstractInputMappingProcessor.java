package it.bluesheep.comparatore.io.datainput.operationmanager.service.mapper;

import java.util.List;
import java.util.logging.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.json.AbstractBluesheepJsonConverter;

public abstract class AbstractInputMappingProcessor {

	protected static Logger logger;
	protected AbstractBluesheepJsonConverter jsonConverter;
	
	protected AbstractInputMappingProcessor() {
		logger = (new BlueSheepLogger(AbstractInputMappingProcessor.class)).getLogger();
	}

	
	public abstract List<AbstractInputRecord> mapInputRecordIntoAbstractInputRecord(String jsonString, Scommessa scommessaTipo, Sport sport);

}