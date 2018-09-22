package it.bluesheep.arbitraggi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.arbitraggi.entities.ArbsType;
import it.bluesheep.arbitraggi.entities.BetReference;
import it.bluesheep.arbitraggi.entities.ThreeOptionReference;
import it.bluesheep.arbitraggi.entities.ThreeOptionsArbsRecord;
import it.bluesheep.arbitraggi.entities.TwoOptionReference;
import it.bluesheep.arbitraggi.entities.TwoOptionsArbsRecord;
import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepSharedResources;

public class ArbsUtil {

	private static double RATIO_ODDS_VALIDITY = new Double((3.0/7.0));
	private static Logger logger = Logger.getLogger(ArbsUtil.class);
	
	private ArbsUtil() {};
	
	public static Map<String, Map<String, List<ArbsRecord>>> initializePreviousRunRecordsMap(List<String> linesFromFile){
		Map<String, Map<String, List<ArbsRecord>>> alreadySentArbsOdds = new TreeMap<String, Map<String, List<ArbsRecord>>>();

		//Per ogni linea calcolo la chiave del record e l'aggiungo alla mappa secondo i criteri
	   for(String line : linesFromFile) {
		   String[] splittedLine = line.split(BlueSheepConstants.KEY_SEPARATOR);
		   String typeOfArbs = splittedLine[0];
		   if(ArbsType.TWO_WAY.getCode().equalsIgnoreCase(typeOfArbs)) {
			   alreadySentArbsOdds = initializeTwoWayArbsRecordStored(splittedLine, alreadySentArbsOdds);
		   }else if(ArbsType.THREE_WAY.getCode().equalsIgnoreCase(typeOfArbs)) {
			   alreadySentArbsOdds = initializeThreeWayArbsRecordStored(splittedLine, alreadySentArbsOdds);
		   }
	   }
	   return alreadySentArbsOdds; 
	}
	
	private static Map<String, Map<String, List<ArbsRecord>>> initializeThreeWayArbsRecordStored(String[] splittedLine, Map<String, Map<String, List<ArbsRecord>>> alreadySentArbsOdds) {
		String runId = splittedLine[1];
		String eventoInfo = splittedLine[2];
		String odds = splittedLine[3];
		String status = getCorrectStatusChange(splittedLine[4]);
		String links = splittedLine[5];
//		String sizeLiquidita = splittedLine[6];
		
		Map<String, List<ArbsRecord>> keyArbsMap = alreadySentArbsOdds.get(status);
		if (keyArbsMap == null) {
			keyArbsMap = new HashMap<String, List<ArbsRecord>>();
			alreadySentArbsOdds.put(status, keyArbsMap);
		}
		List<ArbsRecord> keyArbsList = keyArbsMap.get(runId);
		if (keyArbsList == null) {
			keyArbsList = new ArrayList<ArbsRecord>();
		}
		
		String date = eventoInfo.split(BlueSheepConstants.REGEX_CSV)[1];
		String sport = eventoInfo.split(BlueSheepConstants.REGEX_CSV)[2];
		String eventoKey = "" + date + BlueSheepConstants.REGEX_PIPE + sport + BlueSheepConstants.REGEX_PIPE + eventoInfo.split(BlueSheepConstants.REGEX_CSV)[0];
		String bet1 = eventoInfo.split(BlueSheepConstants.REGEX_CSV)[6];
		String bet2 = eventoInfo.split(BlueSheepConstants.REGEX_CSV)[8];
		String bet3 = eventoInfo.split(BlueSheepConstants.REGEX_CSV)[10];
		
		BetReference[] betReferenceArray = findReferenceInMapFromString(eventoKey, date, sport, bet1, bet2, bet3);
		BetReference betRef = betReferenceArray[0];
		BetReference betAverage = betReferenceArray[1];
		
		keyArbsList.add(new ThreeOptionsArbsRecord(BlueSheepConstants.STATUSINVALID_ARBS_RECORD,
				eventoInfo.split(BlueSheepConstants.REGEX_CSV)[5], eventoInfo.split(BlueSheepConstants.REGEX_CSV)[7], eventoInfo.split(BlueSheepConstants.REGEX_CSV)[9],
				Double.parseDouble(odds.split(BlueSheepConstants.REGEX_CSV)[0]),
				Double.parseDouble(odds.split(BlueSheepConstants.REGEX_CSV)[1]),
				Double.parseDouble(odds.split(BlueSheepConstants.REGEX_CSV)[2]),
				eventoInfo.split(BlueSheepConstants.REGEX_CSV)[6], eventoInfo.split(BlueSheepConstants.REGEX_CSV)[8], eventoInfo.split(BlueSheepConstants.REGEX_CSV)[10],
				eventoInfo.split(BlueSheepConstants.REGEX_CSV)[1], eventoInfo.split(BlueSheepConstants.REGEX_CSV)[0],
				eventoInfo.split(BlueSheepConstants.REGEX_CSV)[4], eventoInfo.split(BlueSheepConstants.REGEX_CSV)[2],
				links.split(BlueSheepConstants.REGEX_CSV)[0], links.split(BlueSheepConstants.REGEX_CSV)[1], links.split(BlueSheepConstants.REGEX_CSV)[2],
				eventoInfo.split(BlueSheepConstants.REGEX_CSV)[3],
				-1,
				-1,
				-1,
//				Double.parseDouble(sizeLiquidita.split(BlueSheepConstants.REGEX_CSV)[0]),
//				Double.parseDouble(sizeLiquidita.split(BlueSheepConstants.REGEX_CSV)[1]),
//				Double.parseDouble(sizeLiquidita.split(BlueSheepConstants.REGEX_CSV)[2]),
				false, false, false, false, false, false,
				betRef, betAverage));
		keyArbsMap.put(runId, keyArbsList);
		
		return alreadySentArbsOdds;
	}

	private static Map<String, Map<String, List<ArbsRecord>>> initializeTwoWayArbsRecordStored(String[] splittedLine, Map<String, Map<String, List<ArbsRecord>>> alreadySentArbsOdds) {
		
		String runId = splittedLine[1];
		String eventoInfo = splittedLine[2];
		String odds = splittedLine[3];
		String status = getCorrectStatusChange(splittedLine[4]);
		String links = splittedLine[5];
		String sizeLiquidita = splittedLine[6];
		Map<String, List<ArbsRecord>> keyArbsMap = alreadySentArbsOdds.get(status);
		if (keyArbsMap == null) {
			keyArbsMap = new HashMap<String, List<ArbsRecord>>();
			alreadySentArbsOdds.put(status, keyArbsMap);
		}
		List<ArbsRecord> keyArbsList = keyArbsMap.get(runId);
		if (keyArbsList == null) {
			keyArbsList = new ArrayList<ArbsRecord>();
		}
		
		String date = eventoInfo.split(BlueSheepConstants.REGEX_CSV)[1];
		String sport = eventoInfo.split(BlueSheepConstants.REGEX_CSV)[2];
		String eventoKey = "" + date + BlueSheepConstants.REGEX_PIPE + sport + BlueSheepConstants.REGEX_PIPE + eventoInfo.split(BlueSheepConstants.REGEX_CSV)[0];
		String bet1 = eventoInfo.split(BlueSheepConstants.REGEX_CSV)[6];
		String bet2 = eventoInfo.split(BlueSheepConstants.REGEX_CSV)[8];


		BetReference[] betReferenceArray = findReferenceInMapFromString(eventoKey, date, sport, bet1, bet2, null);
		BetReference betRef = betReferenceArray[0];
		BetReference betAverage = betReferenceArray[1];

		keyArbsList.add(new TwoOptionsArbsRecord(BlueSheepConstants.STATUSINVALID_ARBS_RECORD,
				eventoInfo.split(BlueSheepConstants.REGEX_CSV)[5], eventoInfo.split(BlueSheepConstants.REGEX_CSV)[7],
				Double.parseDouble(odds.split(BlueSheepConstants.REGEX_CSV)[0]),
				Double.parseDouble(odds.split(BlueSheepConstants.REGEX_CSV)[1]),
				eventoInfo.split(BlueSheepConstants.REGEX_CSV)[6], eventoInfo.split(BlueSheepConstants.REGEX_CSV)[8],
				eventoInfo.split(BlueSheepConstants.REGEX_CSV)[1], eventoInfo.split(BlueSheepConstants.REGEX_CSV)[0],
				eventoInfo.split(BlueSheepConstants.REGEX_CSV)[4], eventoInfo.split(BlueSheepConstants.REGEX_CSV)[2],
				links.split(BlueSheepConstants.REGEX_CSV)[0], links.split(BlueSheepConstants.REGEX_CSV)[1],
				eventoInfo.split(BlueSheepConstants.REGEX_CSV)[3],
				Double.parseDouble(sizeLiquidita.split(BlueSheepConstants.REGEX_CSV)[0]),
				Double.parseDouble(sizeLiquidita.split(BlueSheepConstants.REGEX_CSV)[1]), false, false, false, false,
				betRef, betAverage));
		keyArbsMap.put(runId, keyArbsList);

		return alreadySentArbsOdds;
	}

	private static BetReference[] findReferenceInMapFromString(String eventoKey, String dateString, String sport, String bet1, String bet2, String bet3) {
		
		BetReference[] returnArray = new BetReference[2];
		
		ChiaveEventoScommessaInputRecordsMap map = BlueSheepSharedResources.getEventoScommessaRecordMap();
		Map<Date, Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>>> dateMap = map.get(Sport.valueOf(sport));
		if(dateMap != null) {
			Date date = null;
			SimpleDateFormat sdfInput = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
			try {
				date = sdfInput.parse(dateString);
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
			}
			if(date != null) {
				Map<String, Map<Scommessa, Map<String, AbstractInputRecord>>> eventoMap = dateMap.get(date);
				if(eventoMap != null) {
					Map<Scommessa, Map<String, AbstractInputRecord>> scommessaMap = eventoMap.get(eventoKey);
					if(scommessaMap != null) {
						Map<String, AbstractInputRecord> bookmakerBet1Map = scommessaMap.get(Scommessa.getScommessaByCode(bet1));
						Map<String, AbstractInputRecord> bookmakerBet2Map = scommessaMap.get(Scommessa.getScommessaByCode(bet2));
						Map<String, AbstractInputRecord> bookmakerBet3Map = null;
						if(bet3 != null) {
							bookmakerBet3Map = scommessaMap.get(Scommessa.getScommessaByCode(bet3));
						}

						if(bookmakerBet1Map != null && bookmakerBet2Map != null && (bet3 == null || bookmakerBet3Map != null)) {
							AbstractInputRecord ref1 = bookmakerBet1Map.get(BlueSheepConstants.BET365_BOOKMAKER_NAME);
							AbstractInputRecord ref2 = bookmakerBet2Map.get(BlueSheepConstants.BET365_BOOKMAKER_NAME);
							AbstractInputRecord ref3 = null;
							if(bet3 != null) {
								ref3 = bookmakerBet3Map.get(BlueSheepConstants.BET365_BOOKMAKER_NAME);
							}
						  
							if(ref1 != null && ref2 != null && (bet3 == null || ref3 != null)) {
								if(bet3 == null) {
									returnArray[0] = new TwoOptionReference(BlueSheepConstants.BET365_BOOKMAKER_NAME, ref1.getTipoScommessa().getCode(), StringUtils.substring("" + ref1.getQuota(), 0, 5), BlueSheepConstants.BET365_BOOKMAKER_NAME, ref2.getTipoScommessa().getCode(), StringUtils.substring("" + ref2.getQuota(), 0, 5));
								}else {
									returnArray[0] = new ThreeOptionReference(BlueSheepConstants.BET365_BOOKMAKER_NAME, ref1.getTipoScommessa().getCode(), StringUtils.substring("" + ref1.getQuota(), 0, 5), ref2.getBookmakerName(), ref2.getTipoScommessa().getCode(), StringUtils.substring("" + ref2.getQuota(), 0, 5), ref3.getBookmakerName(), ref3.getTipoScommessa().getCode(), StringUtils.substring("" + ref3.getQuota(), 0, 5));
								}
							}else {
								ref1 = bookmakerBet1Map.get(BlueSheepConstants.MARATHON_BET_BOOKMAKER_NAME);
								ref2 = bookmakerBet2Map.get(BlueSheepConstants.MARATHON_BET_BOOKMAKER_NAME);
								if(bet3 != null) {
									ref3 = bookmakerBet3Map.get(BlueSheepConstants.MARATHON_BET_BOOKMAKER_NAME);
								}
								
								if(ref1 != null && ref2 != null && (bet3 == null || ref3 != null)) {
									if(bet3 == null) {
										returnArray[0] = new TwoOptionReference(BlueSheepConstants.MARATHON_BET_BOOKMAKER_NAME, 
																				ref1.getTipoScommessa().getCode(), 
																				"" + ref1.getQuota(), 
																				BlueSheepConstants.MARATHON_BET_BOOKMAKER_NAME, 
																				ref2.getTipoScommessa().getCode(), 
																				"" + ref2.getQuota());
									}else {
										returnArray[0] = new ThreeOptionReference(BlueSheepConstants.MARATHON_BET_BOOKMAKER_NAME, 
																				  ref1.getTipoScommessa().getCode(), 
																				  StringUtils.substring("" + ref1.getQuota(), 0, 5), 
																				  ref2.getBookmakerName(), 
																				  ref2.getTipoScommessa().getCode(), 
																				  StringUtils.substring("" + ref2.getQuota(), 0, 5), 
																				  ref3.getBookmakerName(), 
																				  ref3.getTipoScommessa().getCode(), 
																				  StringUtils.substring("" + ref3.getQuota(), 0, 5));
									}
								}
							}
						  
							Set<String> bookmakerBet1List = new HashSet<String>(bookmakerBet1Map.keySet());
							Set<String> bookmakerBet2List = new HashSet<String>(bookmakerBet2Map.keySet());
							Set<String> bookmakerBet3List = new HashSet<String>();
							if(bet3 != null && bookmakerBet2Map != null) {
								bookmakerBet3List = new HashSet<String>(bookmakerBet3Map.keySet());
							}

							if(bookmakerBet1List.size() > 3 && bookmakerBet2List.size() > 3 && (bet3 == null || bookmakerBet3List.size() > 3)) {
								
								List<AbstractInputRecord> recordBet1List = new ArrayList<AbstractInputRecord>();
								for(String bookmaker : bookmakerBet1List) {
									AbstractInputRecord record = bookmakerBet1Map.get(bookmaker);
									if(record != null) {
										recordBet1List.add(record);
									}
								}
							  
								List<AbstractInputRecord> recordBet2List = new ArrayList<AbstractInputRecord>();
								for(String bookmaker : bookmakerBet2List) {
									AbstractInputRecord record = bookmakerBet2Map.get(bookmaker);
									if(record != null) {
										recordBet2List.add(record);
									}
								}
								
								List<AbstractInputRecord> recordBet3List = new ArrayList<AbstractInputRecord>();
								for(String bookmaker : bookmakerBet3List) {
									AbstractInputRecord record = bookmakerBet3Map.get(bookmaker);
									if(record != null) {
										recordBet3List.add(record);
									}
								}
								
								Collections.sort(recordBet1List, new Comparator<AbstractInputRecord>() {

									@Override
									public int compare(AbstractInputRecord o1, AbstractInputRecord o2) {
										return new Double(o1.getQuota()).compareTo(new Double(o2.getQuota()));
									}
								});
								
								Collections.sort(recordBet2List, new Comparator<AbstractInputRecord>() {

									@Override
									public int compare(AbstractInputRecord o1, AbstractInputRecord o2) {
										return new Double(o1.getQuota()).compareTo(new Double(o2.getQuota()));
									}
								});
								
								Collections.sort(recordBet3List, new Comparator<AbstractInputRecord>() {

									@Override
									public int compare(AbstractInputRecord o1, AbstractInputRecord o2) {
										return new Double(o1.getQuota()).compareTo(new Double(o2.getQuota()));
									}
								});
								
								int indexMediana1 = recordBet1List.size() / 2 + recordBet1List.size() % 2;
								int indexMediana2 = recordBet2List.size() / 2 + recordBet2List.size() % 2;
								int indexMediana3 = recordBet3List.size() / 2 + recordBet3List.size() % 2;
								
								if(bet3 == null) {
									returnArray[1] = new TwoOptionReference(recordBet1List.get(indexMediana1).getBookmakerName(), bet1, "" + StringUtils.substring("" + recordBet1List.get(indexMediana1).getQuota(), 0, 5), recordBet2List.get(indexMediana2).getBookmakerName(), bet2, "" + StringUtils.substring("" + recordBet2List.get(indexMediana2).getQuota(), 0, 5));
								}else {
									returnArray[1] = new ThreeOptionReference(recordBet1List.get(indexMediana1).getBookmakerName(),
											bet1, 
											"" + StringUtils.substring("" + recordBet1List.get(indexMediana1).getQuota(), 0, 5), 
											recordBet2List.get(indexMediana2).getBookmakerName(), 
											bet2, 
											"" + StringUtils.substring("" + recordBet2List.get(indexMediana2).getQuota(), 0, 5), 
											recordBet3List.get(indexMediana3).getBookmakerName(), 
											bet3, 
											StringUtils.substring("" + recordBet3List.get(indexMediana3).getQuota(), 0, 5));
								}
							}
						}
					}
				}
			}
		}
		return returnArray;
	}

	private static String getCorrectStatusChange(String string) {
		switch(string) {
		case BlueSheepConstants.STATUS0_ARBS_RECORD:
		case BlueSheepConstants.STATUS1_ARBS_RECORD:
			return BlueSheepConstants.STATUSINVALID_ARBS_RECORD;
		}
		return string;
	}

	public static String getKeyArbsTwoWayFromOutputRecord(RecordOutput record) {
		
		return record.getEvento() + BlueSheepConstants.REGEX_CSV +
				record.getDataOraEvento() + BlueSheepConstants.REGEX_CSV +
				record.getSport() + BlueSheepConstants.REGEX_CSV + 
				record.getNazione()+ BlueSheepConstants.REGEX_CSV + 
				record.getCampionato() + BlueSheepConstants.REGEX_CSV + 
				record.getBookmakerName1() + BlueSheepConstants.REGEX_CSV + 
				record.getScommessaBookmaker1() + BlueSheepConstants.REGEX_CSV + 
				record.getBookmakerName2() + BlueSheepConstants.REGEX_CSV + 
				record.getScommessaBookmaker2() + BlueSheepConstants.KEY_SEPARATOR +
				record.getQuotaScommessaBookmaker1() + BlueSheepConstants.REGEX_CSV + 
				record.getQuotaScommessaBookmaker2() + BlueSheepConstants.KEY_SEPARATOR + 
				record.getLinkBook1() + BlueSheepConstants.REGEX_CSV +
				record.getLinkBook2() + BlueSheepConstants.KEY_SEPARATOR + 
				record.getLiquidita1()+ BlueSheepConstants.REGEX_CSV +
				record.getLiquidita2();
	}


	
	public static String getScommessaStringURL(String scommessaBookmaker1) {
		String result = scommessaBookmaker1;
		if(result != null && (result.startsWith("U") || result.startsWith("O"))) {
			String tmp = result.substring(0,1);
			switch(tmp) {
			case "U":
				result = "Under " + result.substring(1) + "_";
				break;
			case "O":
				result = "Over " + result.substring(1) + "_";
				break;
			default:
				break;
			}
		}
		
		return result;
	}
	
	public static String getTelegramBoldString(String string) {
		return "*" + string + "*";
	}
	
	public static boolean validOddsRatio(double odd1, double odd2, Service service) {
		
		if(Service.BETFAIR_SERVICENAME.equals(service)) {
			odd2 = 1 / (1 - (1 / odd2));
		}
		
		double minOdd;
		double maxOdd;
		if(odd1 > odd2) {
			maxOdd = odd1;
			minOdd = odd2;
		}else {
			maxOdd = odd2;
			minOdd = odd1;
		}
		return minOdd / maxOdd >= RATIO_ODDS_VALIDITY;
	}

	public static double getThreeWayNetProfit(AbstractInputRecord homeWinRecord, AbstractInputRecord awayWinRecord, AbstractInputRecord drawRecord) {
		return ((1 / ((1 / homeWinRecord.getQuota()) + (1 / awayWinRecord.getQuota()) + (1 / drawRecord.getQuota()))) - 1) * 100;
	}

	public static BetReference[] findReferenceInMapFromOutputRecord(RecordOutput record) {
		BetReference[] returnArray = findReferenceInMapFromString("" + record.getDataOraEvento() + BlueSheepConstants.REGEX_PIPE + 
																  record.getSport() + BlueSheepConstants.REGEX_PIPE + 
																  record.getEvento(), record.getDataOraEvento().toString(), record.getSport(), record.getScommessaBookmaker1(), record.getScommessaBookmaker2(), null);
		return returnArray;
	}
	
	public static BetReference[] findReferenceInMapFromOutputRecord(ArbsRecord record) {
		String bet3 = null;
		if(record instanceof ThreeOptionsArbsRecord) {
			bet3 = ((ThreeOptionsArbsRecord) record).getBet3();
		}
		BetReference[] returnArray = findReferenceInMapFromString("" + record.getDate() + BlueSheepConstants.REGEX_PIPE + 
																  record.getSport() + BlueSheepConstants.REGEX_PIPE + 
																  record.getKeyEvento(), record.getDate(), record.getSport(), record.getBet1(), record.getBet2(), bet3);
		return returnArray;
	}
	
	public static BetReference[] findReferenceInMapFromString(String recordKey) {
		String[] splittedKey = recordKey.split(BlueSheepConstants.REGEX_CSV);
		BetReference[] betReferenceArray = ArbsUtil.findReferenceInMapFromString(splittedKey[1] + BlueSheepConstants.REGEX_PIPE + 
																				 splittedKey[2] + BlueSheepConstants.REGEX_PIPE + 
																				 splittedKey[0], splittedKey[1], splittedKey[2], splittedKey[6],  splittedKey[8], null);
		return betReferenceArray;
	}

	public static BetReference[] findReferenceFromBookmakerMap(Map<String, AbstractInputRecord> homeWinRecordMap,
			Map<String, AbstractInputRecord> awayWinRecordMap, Map<String, AbstractInputRecord> drawRecordMap) {
		BetReference[] returnArray = new BetReference[2];
		
		Set<String> bookmaker1Set = new HashSet<String>(homeWinRecordMap.keySet());
		Set<String> bookmaker2Set = new HashSet<String>(awayWinRecordMap.keySet());
		Set<String> bookmakerXSet = new HashSet<String>(drawRecordMap.keySet());
		
		if(bookmaker1Set.size() > 3 && bookmaker2Set.size() > 3 && bookmakerXSet.size() > 3) {
			List<AbstractInputRecord> record1List = new ArrayList<AbstractInputRecord>();
			for(String bookmaker : bookmaker1Set) {
				AbstractInputRecord record = homeWinRecordMap.get(bookmaker);
				if(record != null && !BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME.equalsIgnoreCase(bookmaker)) {
					record1List.add(record);
				}
			}
			
			List<AbstractInputRecord> recordXList = new ArrayList<AbstractInputRecord>();
			for(String bookmaker : bookmakerXSet) {
				AbstractInputRecord record = drawRecordMap.get(bookmaker);
				if(record != null && !BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME.equalsIgnoreCase(bookmaker)) {
					recordXList.add(record);
				}
			}
			
			List<AbstractInputRecord> record2List = new ArrayList<AbstractInputRecord>();
			for(String bookmaker : bookmaker2Set) {
				AbstractInputRecord record = awayWinRecordMap.get(bookmaker);
				if(record != null && !BlueSheepConstants.BETFAIR_EXCHANGE_BOOKMAKER_NAME.equalsIgnoreCase(bookmaker)) {
					record2List.add(record);
				}
			}
			
			Collections.sort(record1List, new Comparator<AbstractInputRecord>() {

				@Override
				public int compare(AbstractInputRecord o1, AbstractInputRecord o2) {
					// TODO Auto-generated method stub
					return new Double(o1.getQuota()).compareTo(new Double(o2.getQuota()));
				}
			});
			
			Collections.sort(record2List, new Comparator<AbstractInputRecord>() {

				@Override
				public int compare(AbstractInputRecord o1, AbstractInputRecord o2) {
					// TODO Auto-generated method stub
					return new Double(o1.getQuota()).compareTo(new Double(o2.getQuota()));
				}
			});
			
			Collections.sort(recordXList, new Comparator<AbstractInputRecord>() {

				@Override
				public int compare(AbstractInputRecord o1, AbstractInputRecord o2) {
					// TODO Auto-generated method stub
					return new Double(o1.getQuota()).compareTo(new Double(o2.getQuota()));
				}
			});
			
			int mediana1 = record1List.size() / 2 - record1List.size() % 2;
			int mediana2 = record2List.size() / 2 - record2List.size() % 2;
			int medianaX = recordXList.size() / 2 - recordXList.size() % 2;
			
			returnArray[1] = new ThreeOptionReference(record1List.get(mediana1).getBookmakerName(), Scommessa.SFIDANTE1VINCENTE_1.getCode(), StringUtils.substring("" + record1List.get(mediana1).getQuota(), 0, 5), 
					recordXList.get(medianaX).getBookmakerName(), Scommessa.PAREGGIO_X.getCode(), StringUtils.substring("" + recordXList.get(medianaX).getQuota(), 0, 5),
					record2List.get(mediana2).getBookmakerName(), Scommessa.SFIDANTE2VINCENTE_2.getCode(), StringUtils.substring("" + record2List.get(mediana2).getQuota(), 0, 5));
		}
		AbstractInputRecord ref1 = homeWinRecordMap.get(BlueSheepConstants.BET365_BOOKMAKER_NAME);
		AbstractInputRecord ref2 = awayWinRecordMap.get(BlueSheepConstants.BET365_BOOKMAKER_NAME);
		AbstractInputRecord refX = drawRecordMap.get(BlueSheepConstants.BET365_BOOKMAKER_NAME);
		
		if(ref1 != null && ref2 != null && refX != null) {
			returnArray[0] = new ThreeOptionReference(BlueSheepConstants.BET365_BOOKMAKER_NAME, 
					ref1.getTipoScommessa().getCode(), StringUtils.substring("" + ref1.getQuota(), 0, 5), 
					BlueSheepConstants.BET365_BOOKMAKER_NAME, refX.getTipoScommessa().getCode(), StringUtils.substring("" + refX.getQuota(), 0, 5),
					BlueSheepConstants.BET365_BOOKMAKER_NAME, ref2.getTipoScommessa().getCode(), StringUtils.substring("" + ref2.getQuota(), 0, 5));
		}else {
			ref1 = homeWinRecordMap.get(BlueSheepConstants.MARATHON_BET_BOOKMAKER_NAME);
			ref2 = awayWinRecordMap.get(BlueSheepConstants.MARATHON_BET_BOOKMAKER_NAME);
			refX = drawRecordMap.get(BlueSheepConstants.MARATHON_BET_BOOKMAKER_NAME);
			if(ref1 != null && ref2 != null && refX != null) {
				returnArray[0] = new ThreeOptionReference(BlueSheepConstants.MARATHON_BET_BOOKMAKER_NAME, 
						ref1.getTipoScommessa().getCode(), StringUtils.substring("" + ref1.getQuota(), 0, 5), 
						BlueSheepConstants.MARATHON_BET_BOOKMAKER_NAME, refX.getTipoScommessa().getCode(), StringUtils.substring("" + refX.getQuota(), 0, 5),
						BlueSheepConstants.MARATHON_BET_BOOKMAKER_NAME, ref2.getTipoScommessa().getCode(), StringUtils.substring("" + ref2.getQuota(), 0, 5));
			}
		}
		
		return returnArray;
	}
	
	public static ArbsRecord getArbsRecordFromStoredData(String storedData) {
		
		
		
		return null;
	}
	
}
