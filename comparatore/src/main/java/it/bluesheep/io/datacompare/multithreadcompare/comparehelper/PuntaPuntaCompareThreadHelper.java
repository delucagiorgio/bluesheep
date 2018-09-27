package it.bluesheep.io.datacompare.multithreadcompare.comparehelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.input.record.BetfairExchangeInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.output.subtype.RecordBookmakerVsBookmakerOdds;
import it.bluesheep.entities.util.ComparatoreConstants;
import it.bluesheep.entities.util.ScommessaUtilManager;
import it.bluesheep.entities.util.TranslatorUtil;
import it.bluesheep.entities.util.rating.impl.RatingCalculatorBookmakersOdds;
import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.util.BookmakerLinkGenerator;

public class PuntaPuntaCompareThreadHelper extends CompareThreadHelper {

	private double minThreshold;
	private double maxThreshold;
	
	protected PuntaPuntaCompareThreadHelper(Map<String, List<RecordOutput>> oddsComparisonThreadMap,
			List<Date> keyList,
			Map<Date, Map<String, Map<Scommessa, List<AbstractInputRecord>>>> dataMap,
			Sport sport) {
		super(oddsComparisonThreadMap, keyList, dataMap, sport);
		this.minThreshold = new Double(BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.PP_MIN_THRESHOLD)).doubleValue();
		this.maxThreshold = new Double(BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.PP_MAX_THRESHOLD)).doubleValue();
	}

	@Override
	public void run() {
		List<RecordOutput> mappedOutputRecord = new ArrayList<RecordOutput>();
		//per ogni evento in input
		for(Date date : keyList) {
			for(String evento : dateMap.get(date).keySet()) {
				//per ogni tipo scommessa, cerco le scommesse opposte relative allo stesso evento e le comparo con 
				//quella in analisi
				Map<Scommessa,List<AbstractInputRecord>> inputRecordEventoScommessaMap = dateMap.get(date).get(evento);
				Map<Scommessa,Scommessa> processedScommessaTypes = new HashMap<Scommessa, Scommessa>();
				Scommessa oppositeScommessa = null;			
				for(Scommessa scommessa : inputRecordEventoScommessaMap.keySet()) {
					List<AbstractInputRecord> temp = inputRecordEventoScommessaMap.get(scommessa);
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
		}
		oddsComparisonThreadMap.put("" + this.getId(), mappedOutputRecord);
		logger.log(Level.INFO, "Thread " + this.getId() + " completed execution. Mapped records are " + mappedOutputRecord.size());
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
	 * dal business (rating1 maggiore del 70%) e se così valida la coppia di record e li mappa in un record di output aggiungendolo
	 * ad una lista
	 * @param scommessaInputDataRecordList lista di quote sulla scommessa di iterazione principale
	 * @param oppositeScommessaInputDataRecordList lista delle quote con scommessa opposta alla scommessa in analisi
	 * @return tutti i record mappati secondo il record di output che superano un rating1 del 70%
	 */
	private List<RecordOutput> verifyRequirementsAndMapOddsComparison(List<AbstractInputRecord> scommessaInputDataRecordList, List<AbstractInputRecord> oppositeScommessaInputDataRecordList) {
		
		List<RecordOutput> outputRecordList = new ArrayList<RecordOutput>();
		
		if(scommessaInputDataRecordList != null && !scommessaInputDataRecordList.isEmpty() && 
				oppositeScommessaInputDataRecordList != null && !oppositeScommessaInputDataRecordList.isEmpty()) {
			//per ogni quota disponibile sulla scommessa tipo 1
			Iterator<AbstractInputRecord> itrScommessa = scommessaInputDataRecordList.iterator();
			while(itrScommessa.hasNext()) {
				
				AbstractInputRecord scommessaInputRecord = itrScommessa.next();
				boolean isScommessaInputRecordExchangeRecord = scommessaInputRecord instanceof BetfairExchangeInputRecord;
				boolean isScommessaInputRecordBackExchangeRecord = false;
				if(isScommessaInputRecordExchangeRecord) {
					isScommessaInputRecordBackExchangeRecord = !((BetfairExchangeInputRecord) scommessaInputRecord).isLayRecord();
				}
				
				if(!isScommessaInputRecordExchangeRecord || isScommessaInputRecordBackExchangeRecord) {
					//per ogni quota disponibile sulla scommessa tipo 2
					Iterator<AbstractInputRecord> itrOppositeScommessa = oppositeScommessaInputDataRecordList.iterator();
					while(itrOppositeScommessa.hasNext()) {
						
						AbstractInputRecord oppositeScommessaInputRecord = itrOppositeScommessa.next();
						
						boolean isOppositeScommessaInputRecordExchangeRecord = oppositeScommessaInputRecord instanceof BetfairExchangeInputRecord;
						boolean isOppositeScommessaInputRecordBackExchangeRecord = false;
	
						if(isOppositeScommessaInputRecordExchangeRecord) {
							isOppositeScommessaInputRecordBackExchangeRecord = !((BetfairExchangeInputRecord) oppositeScommessaInputRecord).isLayRecord();
						}
						
						if(!oppositeScommessaInputRecord.getBookmakerName().equalsIgnoreCase(scommessaInputRecord.getBookmakerName()) 
								&& (!isOppositeScommessaInputRecordExchangeRecord || isOppositeScommessaInputRecordBackExchangeRecord)) { 
							
							List<AbstractInputRecord> orderedListByQuota = getOrderedQuotaList(scommessaInputRecord, oppositeScommessaInputRecord);
							
							double maxOdd = orderedListByQuota.get(0).getQuota();
							double minOdd = orderedListByQuota.get(1).getQuota();;
							if((scommessaInputRecord.equals(orderedListByQuota.get(0)) && isScommessaInputRecordBackExchangeRecord) || 
									(oppositeScommessaInputRecord.equals(orderedListByQuota.get(0)) && isOppositeScommessaInputRecordBackExchangeRecord)) {
								double temp = orderedListByQuota.get(0).getQuota();
								maxOdd = (temp - 1) * 0.95 + 1;
							}else if((scommessaInputRecord.equals(orderedListByQuota.get(1)) && isScommessaInputRecordBackExchangeRecord) || 
									(oppositeScommessaInputRecord.equals(orderedListByQuota.get(1)) && isOppositeScommessaInputRecordBackExchangeRecord)) {
								double temp = orderedListByQuota.get(1).getQuota();
								minOdd = (temp - 1) * 0.95 + 1;
							}
							
							double rating1 = (new RatingCalculatorBookmakersOdds()).calculateRating(maxOdd, minOdd);
							double rating2 = (new RatingCalculatorBookmakersOdds()).calculateRatingApprox(maxOdd, minOdd);
							
							//se le due quote in analisi raggiungono i termini di accettabilità, vengono mappate nel record di output
							if(rating1 >= minThreshold && 
							   rating2 >= minThreshold &&
							   rating1 <= maxThreshold && 
							   rating2 <= maxThreshold
							   ) {
								RecordOutput outputRecord = mapRecordOutput(orderedListByQuota.get(0), orderedListByQuota.get(1), rating1);
								RecordBookmakerVsBookmakerOdds bookVsBookRecord = (RecordBookmakerVsBookmakerOdds) outputRecord;
								bookVsBookRecord.setRating2(rating2 * 100);
								outputRecordList.add(bookVsBookRecord);
							}
						}
					}
				}
			}
		}
		
		return outputRecordList;
	}
	
	@Override
	protected RecordOutput mapRecordOutput(AbstractInputRecord scommessaInputRecord, AbstractInputRecord oppositeScommessaInputRecord, double rating1) {
		RecordBookmakerVsBookmakerOdds output = new RecordBookmakerVsBookmakerOdds();		
		output.setBookmakerName1(scommessaInputRecord.getBookmakerName());
		output.setBookmakerName2(oppositeScommessaInputRecord.getBookmakerName());
		output.setCampionato(scommessaInputRecord.getCampionato());
		output.setDataOraEvento(scommessaInputRecord.getDataOraEvento());
		output.setEvento(scommessaInputRecord.getPartecipante1() + "|" + scommessaInputRecord.getPartecipante2());
		output.setQuotaScommessaBookmaker1(scommessaInputRecord.getQuota());
		output.setQuotaScommessaBookmaker2(oppositeScommessaInputRecord.getQuota());
		output.setRating(rating1 * 100);
		output.setScommessaBookmaker1(scommessaInputRecord.getTipoScommessa().getCode());
		output.setScommessaBookmaker2(oppositeScommessaInputRecord.getTipoScommessa().getCode());
		output.setSport(scommessaInputRecord.getSport().toString());
		output = (RecordBookmakerVsBookmakerOdds)TranslatorUtil.translateFieldAboutCountry(output);
		output.setLinkBook1(BookmakerLinkGenerator.getBookmakerLinkEvent(scommessaInputRecord));
		output.setLinkBook2(BookmakerLinkGenerator.getBookmakerLinkEvent(oppositeScommessaInputRecord));
		output.setLiquidita1(scommessaInputRecord.getLiquidita());
		output.setLiquidita2(oppositeScommessaInputRecord.getLiquidita());
		return output;
	}
	
	/**
	 * GD - 29/05/18
	 * Ordina i record di input in base al valore di quota.
	 * @param scommessaInputRecord input1
	 * @param oppositeScommessaInputRecord input2
	 * @return la lista in cui il primo elemento è quello con la quota superiore, il secondo quello con la quota inferiore.
	 */
	private List<AbstractInputRecord> getOrderedQuotaList(AbstractInputRecord scommessaInputRecord, AbstractInputRecord oppositeScommessaInputRecord) {
		List<AbstractInputRecord> returnList = new ArrayList<AbstractInputRecord>(2);
		
		if(scommessaInputRecord.getQuota() >= oppositeScommessaInputRecord.getQuota()) {
			returnList.add(0, scommessaInputRecord);
			returnList.add(1, oppositeScommessaInputRecord);
		}else {
			returnList.add(0, oppositeScommessaInputRecord);
			returnList.add(1, scommessaInputRecord);
		}
		
		return returnList;
	}

	@Override
	protected double getRatingByScommessaPair(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2){
		return 0;
	}

}
