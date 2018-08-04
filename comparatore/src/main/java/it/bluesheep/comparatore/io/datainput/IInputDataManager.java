package it.bluesheep.comparatore.io.datainput;

import java.util.List;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;

public interface IInputDataManager extends Runnable {
	
	public abstract List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport);

	public List<AbstractInputRecord> processAllData();

	
}
