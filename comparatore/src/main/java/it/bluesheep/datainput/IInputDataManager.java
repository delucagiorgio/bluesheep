package it.bluesheep.datainput;

import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.scommessa.Scommessa;

public interface IInputDataManager {

	public abstract String getDataFromService();
	
	public abstract List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, String serviceName, Scommessa tipoScommessa);

	
}
