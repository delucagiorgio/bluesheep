package it.bluesheep.telegrambot.message.util;

import java.util.Arrays;
import java.util.List;

public enum ChatBotFilterCommand {
	BOOKMAKER_BONUS_ABUSING("bk"),
	RF_BONUS_ABUSING("rf"),
	RATING_BONUS_ABUSING("rt"),
	EVENT_BONUS_ABUSING("ev"),
	MINVALUEODD_BONUS_ABUSING("mo"),
	CHAMPIONSHIP_BONUS_ABUSING("cs"),
	SIZE_BONUS_ABUSING("sz");
	
	private String code;
	
	ChatBotFilterCommand(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public static List<ChatBotFilterCommand> getAllAddFilters() {
		return Arrays.asList(RF_BONUS_ABUSING, RATING_BONUS_ABUSING, EVENT_BONUS_ABUSING, MINVALUEODD_BONUS_ABUSING, CHAMPIONSHIP_BONUS_ABUSING, SIZE_BONUS_ABUSING);
	}
}
