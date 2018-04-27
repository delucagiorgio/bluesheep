package it.bluesheep.io.datacompare.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.output.subtype.RecordBookmakerVsBookmakerOdds;
import it.bluesheep.entities.util.ScommessaUtilManager;
import it.bluesheep.entities.util.rating.impl.RatingCalculatorBookmakersOdds;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.AbstractProcessDataManager;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;

/**
 * Classe utilizzata per definire i metodi su cui si basa la comparazione di quote tra i vari
 * bookmaker di TxOdds. Il fine è quello di processare una determinata quota con la sua
 * opposta per poi valutarne la giustezza d'abbinamento tramite il calcolo del rating (> 70%) 
 * @author Giorgio De Luca
 *
 */
public class TxOddsProcessDataManager extends AbstractProcessDataManager {

	private static final String TXODDS_MINIMUM_RATING_AMOUT = "0.8";
	
	@Override
	public List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport) {
				
		List<RecordOutput> mappedOutputRecord = new ArrayList<RecordOutput>();	
		
		//per ogni evento in input
		for(String evento : dataMap.keySet()) {
			//per ogni tipo scommessa, cerco le scommesse opposte relative allo stesso evento e le comparo con 
			//quella in analisi: valuto il rating e se è maggiore del 70%
			Map<Scommessa,List<AbstractInputRecord>> inputRecordEventoScommessaMap = dataMap.get(evento);
			Map<Scommessa,Scommessa> processedScommessaTypes = new HashMap<Scommessa, Scommessa>();
			Scommessa oppositeScommessa = null;

			
			for(Scommessa scommessa : inputRecordEventoScommessaMap.keySet()) {
				List<AbstractInputRecord> temp = inputRecordEventoScommessaMap.get(scommessa);
				if(!sport.equals(temp.get(0).getSport())) {
					break;
				}
				if((Sport.CALCIO.equals(sport) && 
						!ScommessaUtilManager.getScommessaListCalcio3WayOdds().contains(scommessa)) ||
						(Sport.TENNIS.equals(sport) && 
								ScommessaUtilManager.getScommessaListTennis2WayOdds().contains(scommessa))) {
					
					oppositeScommessa = ScommessaUtilManager.getOppositeScommessaByScommessa(scommessa, sport);
					if(oppositeScommessa != null && !isAlreadyProcessedScommessaTypes(scommessa,oppositeScommessa,processedScommessaTypes)) {
						List<RecordOutput> outputRecordsList = verifyRequirementsAndMapOddsComparison(temp,inputRecordEventoScommessaMap.get(oppositeScommessa));
						mappedOutputRecord.addAll(outputRecordsList);
						processedScommessaTypes.put(scommessa, oppositeScommessa);
					}
				}
			}
			
		}

		return mappedOutputRecord;
	}

	/**
	 * GD - 19/04/18
	 * Verifica se la coppia di scommesse è stata già processata in passato
	 * @param scommessa scommessa1
	 * @param oppositeScommessa scommessa2
	 * @param processedScommessaTypes mappa di storico delle coppie di scommesse già processate
	 * @return true, se già processate, false altrimenti
	 */
	private boolean isAlreadyProcessedScommessaTypes(Scommessa scommessa, Scommessa oppositeScommessa, Map<Scommessa, Scommessa> processedScommessaTypes) {
		return processedScommessaTypes.get(scommessa) != null || processedScommessaTypes.get(oppositeScommessa) != null;
	}

	/**
	 * GD - 18/04/18
	 * Verifica che due liste di scommesse su stesso evento e tipo scommessa opposto siano comparabili secondo i criteri specificati
	 * dal business (rating maggiore del 70%) e se così valida la coppia di record e li mappa in un record di output aggiungendolo
	 * ad una lista
	 * @param scommessaInputDataRecordList lista di quote sulla scommessa di iterazione principale
	 * @param oppositeScommessaInputDataRecordList lista delle quote con scommessa opposta alla scommessa in analisi
	 * @return tutti i record mappati secondo il record di output che superano un rating del 70%
	 */
	private List<RecordOutput> verifyRequirementsAndMapOddsComparison(List<AbstractInputRecord> scommessaInputDataRecordList, List<AbstractInputRecord> oppositeScommessaInputDataRecordList) {
		
		List<RecordOutput> outputRecordList = new ArrayList<RecordOutput>();
		
		if(scommessaInputDataRecordList != null && !scommessaInputDataRecordList.isEmpty() && 
				oppositeScommessaInputDataRecordList != null && !oppositeScommessaInputDataRecordList.isEmpty()) {
			//per ogni quota disponibile sulla scommessa tipo 1
			Iterator<AbstractInputRecord> itrScommessa = scommessaInputDataRecordList.iterator();
			while(itrScommessa.hasNext()) {
				
				AbstractInputRecord scommessaInputRecord = itrScommessa.next();
				
				//per ogni quota disponibile sulla scommessa tipo 2
				Iterator<AbstractInputRecord> itrOppositeScommessa = oppositeScommessaInputDataRecordList.iterator();
				while(itrOppositeScommessa.hasNext()) {
					
					AbstractInputRecord oppositeScommessaInputRecord = itrOppositeScommessa.next();
					
					if(!oppositeScommessaInputRecord.getBookmakerName().equalsIgnoreCase(scommessaInputRecord.getBookmakerName())) {		
						double rating = getRatingByScommessaPair(scommessaInputRecord, oppositeScommessaInputRecord);
						//System.out.println("Evalutating Rating: value is  = " + rating);
						//se le due quote in analisi raggiungono i termini di accettabilità, vengono mappate nel record di output
						if(rating  >= new Double(TXODDS_MINIMUM_RATING_AMOUT).doubleValue()) {
							RecordOutput outputRecord = mapRecordOutput(scommessaInputRecord,oppositeScommessaInputRecord,rating);
							outputRecordList.add(outputRecord);
						}
					}
				}
			}
		}
		
		return outputRecordList;
	}

	@Override
	protected RecordOutput mapRecordOutput(AbstractInputRecord scommessaInputRecord, AbstractInputRecord oppositeScommessaInputRecord, double rating) {
		RecordBookmakerVsBookmakerOdds output = new RecordBookmakerVsBookmakerOdds();		
		output.setBookmakerName1(scommessaInputRecord.getBookmakerName());
		output.setBookmakerName2(oppositeScommessaInputRecord.getBookmakerName());
		output.setCampionato(scommessaInputRecord.getCampionato());
		output.setDataOraEvento(scommessaInputRecord.getDataOraEvento());
		output.setEvento(scommessaInputRecord.getPartecipante1() + "|" + scommessaInputRecord.getPartecipante2());
		output.setQuotaScommessaBookmaker1(scommessaInputRecord.getQuota());
		output.setQuotaScommessaBookmaker2(oppositeScommessaInputRecord.getQuota());
		output.setRating(rating * 100);
		output.setScommessaBookmaker1(scommessaInputRecord.getTipoScommessa().getCode());
		output.setScommessaBookmaker2(oppositeScommessaInputRecord.getTipoScommessa().getCode());
		output.setSport(scommessaInputRecord.getSport().getCode());
		output = (RecordBookmakerVsBookmakerOdds) translateFieldAboutCountry(output);
		return output;
	}

	@Override
	protected double getRatingByScommessaPair(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2)  {
		return (new RatingCalculatorBookmakersOdds()).calculateRating(scommessaInputRecord1.getQuota(), scommessaInputRecord2.getQuota());
	}
}
