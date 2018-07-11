package it.bluesheep.io.datainput;

import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;

public interface IInputDataManager extends Runnable {
	
	public abstract List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport);

	public List<AbstractInputRecord> processAllData();

	
}
