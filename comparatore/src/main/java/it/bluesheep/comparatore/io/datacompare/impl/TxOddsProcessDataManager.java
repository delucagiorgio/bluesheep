package it.bluesheep.comparatore.io.datacompare.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.arbitraggi.entities.ArbsType;
import it.bluesheep.arbitraggi.entities.BetReference;
import it.bluesheep.arbitraggi.entities.ThreeOptionsArbsRecord;
import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.output.subtype.RecordBookmakerVsBookmakerOdds;
import it.bluesheep.comparatore.entities.util.ScommessaUtilManager;
import it.bluesheep.comparatore.entities.util.TranslatorUtil;
import it.bluesheep.comparatore.entities.util.comparevalue.CompareValueFactory;
import it.bluesheep.comparatore.entities.util.comparevalue.rating.RatingCalculatorBookmakersOdds;
import it.bluesheep.comparatore.entities.util.comparevalue.rating.RatingCalculatorFactory;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.AbstractProcessDataManager;
import it.bluesheep.comparatore.io.datacompare.util.BookmakerLinkGenerator;
import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.comparatore.io.datacompare.util.ICompareInformationEvents;
import it.bluesheep.comparatore.io.datacompare.util.ThresholdRatingFactory;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.AbstractBlueSheepService;
import it.bluesheep.servicehandler.ArbitraggiServiceHandler;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepSharedResources;

/**
 * Classe utilizzata per definire i metodi su cui si basa la comparazione di quote tra i vari
 * bookmaker di TxOdds. Il fine è quello di processare una determinata quota con la sua
 * opposta per poi valutarne la giustezza d'abbinamento tramite il calcolo del rating1 (> 70%) 
 * @author Giorgio De Luca
 *
 */
public class TxOddsProcessDataManager extends AbstractProcessDataManager implements ICompareInformationEvents{
	
	private double minThreshold;
	private double maxThreshold;
	private boolean controlValidityOdds;
	private long startComparisonTime;
	private long minutesOfOddValidity;
	private static double THREEWAY_NET_PROFIT;
	
	protected TxOddsProcessDataManager() {
		super();
		controlValidityOdds = false;
		startComparisonTime = System.currentTimeMillis();
		minutesOfOddValidity = new Long(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.MINUTES_ODD_VALIDITY)) * 60 * 1000L;
		service = Service.TXODDS_SERVICENAME;
		THREEWAY_NET_PROFIT = new Double(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.THREEWAY_NET_PROFIT));
	}
	
	@Override
	public List<RecordOutput> compareTwoWayOdds(ChiaveEventoScommessaInputRecordsMap sportMap, Sport sport, AbstractBlueSheepService bluesheepServiceType) {
		Map<Service, Map<String, Double>> mapThresholdMap = ThresholdRatingFactory.getThresholdMapByAbstractBlueSheepService(bluesheepServiceType);
		this.minThreshold = mapThresholdMap.get(Service.TXODDS_SERVICENAME).get(BlueSheepConstants.PP_MIN);
		this.maxThreshold = mapThresholdMap.get(Service.TXODDS_SERVICENAME).get(BlueSheepConstants.PP_MAX);
		
		if(bluesheepServiceType instanceof ArbitraggiServiceHandler) {
			controlValidityOdds = true;
		}
		
		List<RecordOutput> mappedOutputRecord = new ArrayList<RecordOutput>();
		//per ogni evento in input
		ChiaveEventoScommessaInputRecordsMap recordInputMap = BlueSheepSharedResources.getEventoScommessaRecordMap();
		Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> sportDateMap = recordInputMap.get(sport);
		if(sportDateMap != null) {
			List<Date> dateList = new ArrayList<Date>(sportDateMap.keySet());
			for(Date date : dateList) {
				List<String> eventoKeyList = new ArrayList<String>(sportDateMap.get(date).keySet());
				for(String evento : eventoKeyList) {
					//per ogni tipo scommessa, cerco le scommesse opposte relative allo stesso evento e le comparo con 
					//quella in analisi
					Map<Scommessa, Map<String, AbstractInputRecord>> inputRecordEventoScommessaMap = sportDateMap.get(date).get(evento);
					Map<Scommessa,Scommessa> processedScommessaTypes = new HashMap<Scommessa, Scommessa>();
					Scommessa oppositeScommessa = null;		
					List<Scommessa> scommessaList = new ArrayList<Scommessa>(inputRecordEventoScommessaMap.keySet());
					for(Scommessa scommessa : scommessaList) {
						Map<String, AbstractInputRecord> temp = inputRecordEventoScommessaMap.get(scommessa);
						if((Sport.CALCIO.equals(sport) && 
								!ScommessaUtilManager.getScommessaListCalcio3WayOdds().contains(scommessa)) ||
								(Sport.TENNIS.equals(sport) && 
										ScommessaUtilManager.getScommessaListTennis2WayOdds().contains(scommessa))) {
							
							oppositeScommessa = ScommessaUtilManager.getOppositeScommessaByScommessa(scommessa, sport);
							if(oppositeScommessa != null && !isAlreadyProcessedScommessaTypes(scommessa,oppositeScommessa,processedScommessaTypes)) {
								List<RecordOutput> outputRecordsList = verifyRequirementsAndMapOddsComparison(temp,inputRecordEventoScommessaMap.get(oppositeScommessa), bluesheepServiceType);
								mappedOutputRecord.addAll(outputRecordsList);
								processedScommessaTypes.put(scommessa, oppositeScommessa);
							}
						}
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
	 * dal business (rating1 maggiore del 70%) e se così valida la coppia di record e li mappa in un record di output aggiungendolo
	 * ad una lista
	 * @param bookmakerRecord1Map lista di quote sulla scommessa di iterazione principale
	 * @param bookmakerRecord2Map lista delle quote con scommessa opposta alla scommessa in analisi
	 * @return tutti i record mappati secondo il record di output che superano un rating1 del 70%
	 */
	private List<RecordOutput> verifyRequirementsAndMapOddsComparison(Map<String, AbstractInputRecord> bookmakerRecord1Map, Map<String, AbstractInputRecord> bookmakerRecord2Map, AbstractBlueSheepService bluesheepService) {
		
		List<RecordOutput> outputRecordList = new ArrayList<RecordOutput>();
		
		if(bookmakerRecord1Map != null && !bookmakerRecord1Map.isEmpty() && 
				bookmakerRecord2Map != null && !bookmakerRecord2Map.isEmpty()) {
			//per ogni quota disponibile sulla scommessa tipo 1
			List<String> bookmakerScommessaSet = new ArrayList<String>(bookmakerRecord1Map.keySet());
			List<String> bookmakerOppositeScommessaSet = new ArrayList<String>(bookmakerRecord2Map.keySet());

			for(String bookmakerScommessa : bookmakerScommessaSet) {
				//per ogni quota disponibile sulla scommessa tipo 2
				for(String bookmakerScommessaOpposite : bookmakerOppositeScommessaSet) {
					
					if(!bookmakerScommessa.equalsIgnoreCase(bookmakerScommessaOpposite) 
							&& !BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME.equalsIgnoreCase(bookmakerScommessaOpposite) 
							&& !BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME.equalsIgnoreCase(bookmakerScommessa)) { 
						
						AbstractInputRecord scommessaInputRecord = bookmakerRecord1Map.get(bookmakerScommessa);
						AbstractInputRecord oppositeScommessaInputRecord = bookmakerRecord2Map.get(bookmakerScommessaOpposite);
						
						List<AbstractInputRecord> orderedListByQuota = getOrderedQuotaList(scommessaInputRecord, oppositeScommessaInputRecord);
						
						double compareValue1 = CompareValueFactory.getCompareValueInterfaceByComparisonTypeAndService(service, bluesheepService).getCompareValue(orderedListByQuota.get(0).getQuota(), orderedListByQuota.get(1).getQuota());
						double rating2 = RatingCalculatorBookmakersOdds.calculateRatingApprox(orderedListByQuota.get(0).getQuota(), orderedListByQuota.get(1).getQuota());
						
						//se le due quote in analisi raggiungono i termini di accettabilità, vengono mappate nel record di output
						if(compareValue1 >= minThreshold && (
										(controlValidityOdds && ArbsUtil.validOddsRatio(orderedListByQuota.get(0).getQuota(), orderedListByQuota.get(1).getQuota(), service))
										||
										(!controlValidityOdds && 
										rating2 >= minThreshold &&
										compareValue1 <= maxThreshold && 
										rating2 <= maxThreshold)
								) &&
						   (!controlValidityOdds || 
								   (!scommessaInputRecord.getSource().equals(Service.CSV_SERVICENAME) && 
										   !oppositeScommessaInputRecord.getSource().equals(Service.CSV_SERVICENAME) &&
										   hasBeenRecentlyUpdated(scommessaInputRecord) && 
										   hasBeenRecentlyUpdated(oppositeScommessaInputRecord)
									)
						   )) {
							RecordOutput outputRecord = mapRecordOutput(orderedListByQuota.get(0), orderedListByQuota.get(1), 
									RatingCalculatorFactory.getRatingCalculator(service).getCompareValue(orderedListByQuota.get(0).getQuota(), 
																										 orderedListByQuota.get(1).getQuota()));
							((RecordBookmakerVsBookmakerOdds) outputRecord).setRating2(rating2 * 100);
							outputRecordList.add(outputRecord);
						}
					}
				}
			}
		}
		
		return outputRecordList;
	}
	
	private boolean hasBeenRecentlyUpdated(AbstractInputRecord scommessaInputRecord) {
		return startComparisonTime - scommessaInputRecord.getTimeInsertionInSystem() <= minutesOfOddValidity;
	}

	/**
	 * GD - 29/05/18
	 * Ordina i record di input in base al valore di quota
	 * @param scommessaInputRecord input1
	 * @param oppositeScommessaInputRecord input2
	 * @return la lista in cui il primo elemento è quello con la quota superiore, il secondo quello con la quota inferiore.
	 */
	private List<AbstractInputRecord> getOrderedQuotaList(AbstractInputRecord scommessaInputRecord,
			AbstractInputRecord oppositeScommessaInputRecord) {
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
	
	protected RecordOutput mapRecordOutput(AbstractInputRecord scommessaInputRecord, AbstractInputRecord oppositeScommessaInputRecord, double rating1) {
		RecordBookmakerVsBookmakerOdds output = new RecordBookmakerVsBookmakerOdds();
		
		output.setBookmakerName1(scommessaInputRecord.getBookmakerName());
		output.setBookmakerName2(oppositeScommessaInputRecord.getBookmakerName());
		output.setCampionato(scommessaInputRecord.getCampionato());
		output.setDataOraEvento(scommessaInputRecord.getDataOraEvento());
		output.setEvento(scommessaInputRecord.getPartecipante1() + BlueSheepConstants.REGEX_VERSUS + scommessaInputRecord.getPartecipante2());
		output.setQuotaScommessaBookmaker1(scommessaInputRecord.getQuota());
		output.setQuotaScommessaBookmaker2(oppositeScommessaInputRecord.getQuota());
		output.setRating(rating1 * 100);
		output.setScommessaBookmaker1(scommessaInputRecord.getTipoScommessa().getCode());
		output.setScommessaBookmaker2(oppositeScommessaInputRecord.getTipoScommessa().getCode());
		output.setSport(scommessaInputRecord.getSport().toString());
		output = (RecordBookmakerVsBookmakerOdds)TranslatorUtil.translateFieldAboutCountry(output);
		output.setLinkBook1(BookmakerLinkGenerator.getBookmakerLinkEvent(scommessaInputRecord));
		output.setLinkBook2(BookmakerLinkGenerator.getBookmakerLinkEvent(oppositeScommessaInputRecord));
		
		return output;
	}

	@Override
	public List<AbstractInputRecord> compareAndCollectSameEventsFromBookmakerAndTxOdds(List<AbstractInputRecord> bookmakerList, ChiaveEventoScommessaInputRecordsMap eventiTxOddsMap) throws Exception {
		for(AbstractInputRecord txOddsRecord : bookmakerList) {
			AbstractInputRecord exchangeRecord = BlueSheepSharedResources.findExchangeRecord(txOddsRecord);
			if(exchangeRecord != null) {
				txOddsRecord.setDataOraEvento(exchangeRecord.getDataOraEvento());
				txOddsRecord.setKeyEvento("" + txOddsRecord.getDataOraEvento() + BlueSheepConstants.REGEX_PIPE + 
						txOddsRecord.getSport() + BlueSheepConstants.REGEX_PIPE + 
						txOddsRecord.getPartecipante1()+ BlueSheepConstants.REGEX_VERSUS + 
						txOddsRecord.getPartecipante2());
			}
		}
		return bookmakerList;
	}

	@Override
	public List<ArbsRecord> compareThreeWayOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport, AbstractBlueSheepService bluesheepServiceType) throws Exception {
		
		List<ArbsRecord> threeWayRecordOutput = new ArrayList<ArbsRecord>();
		Set<String> bookmakerSet;
		
		Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> sportMap = dataMap.get(sport);
		if(sportMap != null) {
			Set<Date> dateSet = new HashSet<Date>(sportMap.keySet());
			for(Date date : dateSet) {
				Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>> eventoMap = sportMap.get(date);
				if(eventoMap != null) {
					Set<String> eventoSet = new HashSet<String>(eventoMap.keySet());
					for(String evento : eventoSet) {
						bookmakerSet = new HashSet<String>();
						Map<Scommessa, Map<String, AbstractInputRecord>> scommessaMap = eventoMap.get(evento);
						if(scommessaMap != null) {
							Map<String, AbstractInputRecord> homeWinRecordMap = scommessaMap.get(Scommessa.SFIDANTE1VINCENTE_1);
							Map<String, AbstractInputRecord> awayWinRecordMap = scommessaMap.get(Scommessa.SFIDANTE2VINCENTE_2);
							Map<String, AbstractInputRecord> drawRecordMap = scommessaMap.get(Scommessa.PAREGGIO_X);
							
							if(homeWinRecordMap != null && awayWinRecordMap != null && drawRecordMap != null) {
								List<AbstractInputRecord> homeWinRecordList = new ArrayList<AbstractInputRecord>(homeWinRecordMap.values());
								List<AbstractInputRecord> awayWinRecordList = new ArrayList<AbstractInputRecord>(awayWinRecordMap.values());
								List<AbstractInputRecord> drawRecordList = new ArrayList<AbstractInputRecord>(drawRecordMap.values());

								homeWinRecordList = orderByQuotaListDesc(homeWinRecordList);
								awayWinRecordList = orderByQuotaListDesc(awayWinRecordList);
								drawRecordList = orderByQuotaListDesc(drawRecordList);
								
								String keyEventoArbs = null;
								double maxStoredProfitRun  = -1;
								
								for(int i = 0; i < homeWinRecordList.size() && bookmakerSet.size() <= 8; i++) {
									AbstractInputRecord homeWinRecord = homeWinRecordList.get(i);
									if(!BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME.equalsIgnoreCase(homeWinRecord.getBookmakerName()) && 
											!Service.CSV_SERVICENAME.equals(homeWinRecord.getSource()) && hasBeenRecentlyUpdated(homeWinRecord)) {
										for(int j = 0; j < awayWinRecordList.size() && bookmakerSet.size() <= 8; j++) {
											AbstractInputRecord awayWinRecord = awayWinRecordList.get(j);
											if(!BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME.equalsIgnoreCase(awayWinRecord.getBookmakerName()) && 
													!Service.CSV_SERVICENAME.equals(awayWinRecord.getSource()) && hasBeenRecentlyUpdated(awayWinRecord)) {
												for(int k = 0; k < drawRecordList.size() && bookmakerSet.size() <= 8; k++) {
													AbstractInputRecord drawRecord = drawRecordList.get(k);
													if(!BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME.equalsIgnoreCase(drawRecord.getBookmakerName()) && 
															!Service.CSV_SERVICENAME.equals(drawRecord.getSource()) && hasBeenRecentlyUpdated(drawRecord)) {
														double netProfitCombination = ArbsUtil.getThreeWayNetProfit(homeWinRecord, awayWinRecord, drawRecord);
														

														if(netProfitCombination >= THREEWAY_NET_PROFIT) {
															
															if(!homeWinRecord.getBookmakerName().equalsIgnoreCase(awayWinRecord.getBookmakerName()) || 
																	!homeWinRecord.getBookmakerName().equalsIgnoreCase(drawRecord.getBookmakerName()) || 
																	!awayWinRecord.getBookmakerName().equalsIgnoreCase(drawRecord.getBookmakerName())) {
																Map<String, Double> threeWayStoredNetProfitMap = BlueSheepSharedResources.getArbsNetProfitHistoryMap().get(ArbsType.THREE_WAY);
																
																if(threeWayStoredNetProfitMap != null) {
																	Double maxNetProfitStored = BlueSheepSharedResources.getArbsNetProfitHistoryMap().get(ArbsType.THREE_WAY).get(ArbsUtil.getKeyArbsThreeWayFromInputRecord(homeWinRecord));
																	
																	if(maxNetProfitStored != null) {
																		//Esiste un record con questa chiave evento
																		
																		//Se la combinazione è maggiore del doppio di quella salvata o 
																		//è già stata trovata una combinazione con questa condizione vera
																		if(netProfitCombination >= (maxNetProfitStored * 2) || maxStoredProfitRun > 0) {
																			
																			//Dovrebbe inizializzarlo solo la prima volta, per ogni evento
																			//Il codice sotto si basa su questo assunto
																			if(maxStoredProfitRun < 0) {
																				maxStoredProfitRun = netProfitCombination;
																			}
																			
																			if(keyEventoArbs == null) {
																				keyEventoArbs = ArbsUtil.getKeyArbsThreeWayFromInputRecord(homeWinRecord);
																			}
																			
																			bookmakerSet.add(homeWinRecord.getBookmakerName());
																			bookmakerSet.add(awayWinRecord.getBookmakerName());
																			bookmakerSet.add(drawRecord.getBookmakerName());
																		}
																	}else {//Non esiste il record relativo a questo evento nella mappa dei profitti netti di tipo 3way
																		
																		//Dovrebbe inizializzarlo solo la prima volta, per ogni evento
																		//Il codice sotto si basa su questo assunto
																		if(maxStoredProfitRun < 0) {
																			maxStoredProfitRun = netProfitCombination;
																		}
																		
																		if(keyEventoArbs == null) {
																			keyEventoArbs = ArbsUtil.getKeyArbsThreeWayFromInputRecord(homeWinRecord);
																		}
																		bookmakerSet.add(homeWinRecord.getBookmakerName());
																		bookmakerSet.add(awayWinRecord.getBookmakerName());
																		bookmakerSet.add(drawRecord.getBookmakerName());
																	}
																}else { // Non esiste ancora nessun record nella mappa dei profitti netti di tipo 3way
																	
																	//Dovrebbe inizializzarlo solo la prima volta, per ogni evento
																	//Il codice sotto si basa su questo assunto
																	if(maxStoredProfitRun < 0) {
																		maxStoredProfitRun = netProfitCombination;
																	}
																	
																	if(keyEventoArbs == null) {
																		keyEventoArbs = ArbsUtil.getKeyArbsThreeWayFromInputRecord(homeWinRecord);
																	}
																	
																	bookmakerSet.add(homeWinRecord.getBookmakerName());
																	bookmakerSet.add(awayWinRecord.getBookmakerName());
																	bookmakerSet.add(drawRecord.getBookmakerName());
																}
															}
														}else {
															continue;
														}
													}
												}
											}
										}
									}
								}
								
								if(maxStoredProfitRun > 0 && keyEventoArbs != null) {
									Map<String, Double> threeWayNetProfitMap = BlueSheepSharedResources.getArbsNetProfitHistoryMap().get(ArbsType.THREE_WAY);
									if(threeWayNetProfitMap == null) {
										threeWayNetProfitMap = new HashMap<String, Double>();
										BlueSheepSharedResources.getArbsNetProfitHistoryMap().put(ArbsType.THREE_WAY, threeWayNetProfitMap);
									}
									threeWayNetProfitMap.put(keyEventoArbs, new Double(maxStoredProfitRun));
								}
								
								if(bookmakerSet.size() > 0) {
									
									for(String bookmaker : bookmakerSet) {
										AbstractInputRecord homeWinRecord = homeWinRecordMap.get(bookmaker);
										AbstractInputRecord awayWinRecord = awayWinRecordMap.get(bookmaker);
										AbstractInputRecord drawRecord = drawRecordMap.get(bookmaker);
										
										if(homeWinRecord != null && 
												awayWinRecord != null && 
												drawRecord != null && 
												!Service.CSV_SERVICENAME.equals(homeWinRecord.getSource()) && 
												!Service.CSV_SERVICENAME.equals(awayWinRecord.getSource()) && 
												!Service.CSV_SERVICENAME.equals(drawRecord.getSource()) && 
												BlueSheepSharedResources.getArbsNetProfitHistoryMap().get(ArbsType.THREE_WAY).containsKey(ArbsUtil.getKeyArbsThreeWayFromInputRecord(homeWinRecord))) {
											
											BetReference[] referenceArray = ArbsUtil.findReferenceFromBookmakerMap(homeWinRecordMap, awayWinRecordMap, drawRecordMap);

											ArbsRecord arbRecord = new ThreeOptionsArbsRecord(BlueSheepConstants.STATUS0_ARBS_RECORD, bookmaker, bookmaker, bookmaker, 
													homeWinRecord.getQuota(), drawRecord.getQuota(), awayWinRecord.getQuota(), 
													homeWinRecord.getTipoScommessa().getCode(), drawRecord.getTipoScommessa().getCode(), awayWinRecord.getTipoScommessa().getCode(), 
													date.toString(), homeWinRecord.getPartecipante1() + BlueSheepConstants.REGEX_VERSUS + homeWinRecord.getPartecipante2(),
													homeWinRecord.getCampionato(), sport.getCode(), 
													BookmakerLinkGenerator.getBookmakerLinkEvent(homeWinRecord), BookmakerLinkGenerator.getBookmakerLinkEvent(drawRecord), BookmakerLinkGenerator.getBookmakerLinkEvent(awayWinRecord),
													"",
													-1, -1, -1, false, false, false, false, false, false, referenceArray[0], referenceArray[1]);

											threeWayRecordOutput.add(arbRecord);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return threeWayRecordOutput;
	}

	private List<AbstractInputRecord> orderByQuotaListDesc(List<AbstractInputRecord> recordList) {
		Collections.sort(recordList, new Comparator<AbstractInputRecord>() {

			@Override
			public int compare(AbstractInputRecord o1, AbstractInputRecord o2) {
				return (new Double(o2.getQuota())).compareTo(new Double(o1.getQuota()));
			}
		});
		return recordList;
	}
}
