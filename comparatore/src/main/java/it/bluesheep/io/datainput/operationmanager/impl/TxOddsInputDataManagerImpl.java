package it.bluesheep.io.datainput.operationmanager.impl;

import java.util.ArrayList;
import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.util.ScommessaUtilManager;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.operationmanager.mapper.AbstractInputMappingProcessor;
import it.bluesheep.io.datainput.operationmanager.mapper.TxOddsInputMappingProcessor;
import it.bluesheep.serviceapi.impl.TxOddsApiImpl;

public final class TxOddsInputDataManagerImpl extends InputDataManagerImpl {
	
	private AbstractInputMappingProcessor processor;
	
	public TxOddsInputDataManagerImpl() {
		super();
		processor = new TxOddsInputMappingProcessor();
		apiServiceInterface = new TxOddsApiImpl();
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
	
		try {
			//esegui mapping secondo TXODDS
			abstractInputRecordsList = processor.mapInputRecordIntoAbstractInputRecord(jsonString, tipoScommessa, sport);
		}catch(Exception e) {
			System.out.println(jsonString);
			e.printStackTrace();
		}
		logger.info("Mapping JSON completed : events mapped from input JSON are " + abstractInputRecordsList.size());
		
		return abstractInputRecordsList;
	}

	@Override
	protected List<Scommessa> getCombinazioniSportScommessa(Sport sport) {
		List<Scommessa> scommessaList = new ArrayList<Scommessa>();
		
		if(sport.equals(Sport.TENNIS)) {
			scommessaList = ScommessaUtilManager.getScommessaListTennis2WayOdds();
		}else if(sport.equals(Sport.CALCIO)) {
			scommessaList = ScommessaUtilManager.getScommessaListCalcioAllOdds();
		}
		
		return scommessaList;
	}
	
	
}