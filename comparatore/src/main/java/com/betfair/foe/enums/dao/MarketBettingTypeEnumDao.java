package com.betfair.foe.enums.dao;

import java.util.HashSet;
import java.util.Set;

import com.betfair.foe.enums.types.MarketBettingType;

/**
 * Classe per accedere velocemente ai valori di MarketBettingType secondo i criteri dei metodi
 * @author Giorgio De Luca
 *
 */
public class MarketBettingTypeEnumDao {
	
	private MarketBettingTypeEnumDao(){}
	
	/**
	 * GD - 22/04/18
	 * @return ritorna il set di stringhe contenente i codici relativi alle tipologie di scommesse richieste sul calcio
	 */
	public static Set<String> getCalcioExchangeOdds(){
		Set<String> marketTypeCalcio = new HashSet<String>();
		
		marketTypeCalcio.add(MarketBettingType.GOAL_NOGOAL.getCode());
		marketTypeCalcio.add(MarketBettingType.ODDS.getCode());
		marketTypeCalcio.addAll(getOverUnderExchangeOdds());
		
		return marketTypeCalcio;
	}
	
	/**
	 * GD - 22/04/18
	 * @return ritorna il set di stringhe contenente i codici relativi alle tipologie di scommesse richieste sul tennis
	 */
	public static Set<String> getTennisExchangeOdds(){
		Set<String> marketTypeTennis = new HashSet<String>();
		
		marketTypeTennis.add(MarketBettingType.ODDS.getCode());
		
		return marketTypeTennis;
	}
	
	/**
	 * GD - 22/04/18
	 * @return ritorna il set di stringhe contenente i codici relativi alle tipologie di scommesse richieste sul calcio
	 * di tipo Under/Over
	 */
	public static Set<String> getOverUnderExchangeOdds(){
		Set<String> marketTypeOverUnder = new HashSet<String>();
		
		marketTypeOverUnder.add(MarketBettingType.OVERUNDER_05.getCode());
		marketTypeOverUnder.add(MarketBettingType.OVERUNDER_15.getCode());
		marketTypeOverUnder.add(MarketBettingType.OVERUNDER_25.getCode());
		marketTypeOverUnder.add(MarketBettingType.OVERUNDER_35.getCode());
		marketTypeOverUnder.add(MarketBettingType.OVERUNDER_45.getCode());
		
		return marketTypeOverUnder;
	}

}
