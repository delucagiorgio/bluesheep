package it.bluesheep.io.datainput.operationmanager.impl;

import java.util.ArrayList;
import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.EventoBetfairMercatoTipoScommessaMap;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.operationmanager.mapper.AbstractInputMappingProcessor;
import it.bluesheep.io.datainput.operationmanager.mapper.BetfairInputMappingProcessor;

public final class ExchangeVsBookmakerInputDataManagerImpl extends InputDataManagerImpl {

	private AbstractInputMappingProcessor processor;
	private EventoBetfairMercatoTipoScommessaMap eventoBetfairMercatoTipoScommessaMap;
	
	public ExchangeVsBookmakerInputDataManagerImpl() {
		super();
		processor = new BetfairInputMappingProcessor();
		eventoBetfairMercatoTipoScommessaMap = new EventoBetfairMercatoTipoScommessaMap();
	}
	
	@Override
	public String getDataFromService(Scommessa scommessa, Sport sport) { 
		String inputJson = "";
		
		//ottenere gli eventi relativi alla scommessa da voler analizzare e del determinato sport
		
		//parsare i dati JSON ritornati per mappare i dati nelle classi EventoBetfair
		
		//aggiungere gli eventi alla mappa eventoBetfairMercatoTipoScommessaMap
		
		//per ogni evento si vada a prendere il relativo marketId e lo si inserisca nella mappa, tramite la funzione 
		//eventoBetfairMercatoTipoScommessaMap.addEventoBetfairMercatoByTipoScommessa(evento, scommessaTipo, marketId);
		
		//ottenere tutti i dati relativi ai marketId collezionati e ritornare il JSON contenente i dati delle quote
		
		
		
//		InputStream inStream = null;
//		BufferedReader br = null;
//		try {
//			inStream = BookmakerVsBookmakerInputDataManagerImpl.class.getResourceAsStream("/JSONExmapleRequestMatchODDS_TEST.txt");
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

	@Override
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa, Sport sport) {
		
		List<AbstractInputRecord> returnItemsList = new ArrayList<AbstractInputRecord>();
		
		returnItemsList = processor.mapInputRecordIntoAbstractInputRecord(jsonString, tipoScommessa, sport);
		
		return returnItemsList;
	}

}
