package it.bluesheep.telegrambot.message.util;

import java.util.Arrays;
import java.util.List;
/**
 * I filtri "utente" 
 * @author giorgio
 *
 */
public enum ChatBotFilterCommand {
	BOOKMAKER_BONUS_ABUSING("bk"),
	RF_BONUS_ABUSING("rf"),
	RATING_BONUS_ABUSING("rt"),
	EVENT_BONUS_ABUSING("ev"),
	MINVALUEODD_BONUS_ABUSING("mo"),
	SIZE_BONUS_ABUSING("sz"),
	RF_TYPE_BONUS_ABUSING("rft");
	
	private String code;
	
	ChatBotFilterCommand(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public static List<ChatBotFilterCommand> getAllAddFilters() {
		return Arrays.asList(RATING_BONUS_ABUSING, RF_BONUS_ABUSING, EVENT_BONUS_ABUSING, MINVALUEODD_BONUS_ABUSING, SIZE_BONUS_ABUSING, RF_TYPE_BONUS_ABUSING);
	}
	
}
