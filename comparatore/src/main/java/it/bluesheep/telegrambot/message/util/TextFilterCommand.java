package it.bluesheep.telegrambot.message.util;

public enum TextFilterCommand {
	
	RF("RF"),
	RATING("Rating"),
	EVENT("Evento"),
	MINVALUEODD("Quota minima"), 
	CHAMPIONSHIP("Campionato"),
	SIZE("Liquidità");

	private String code;
	
	TextFilterCommand(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static TextFilterCommand getTextFilterCommandByChatBotFilterCommand(ChatBotFilterCommand chatBotFilterList) {
		switch(chatBotFilterList) {
		case CHAMPIONSHIP_BONUS_ABUSING:
			return TextFilterCommand.CHAMPIONSHIP;
		
		case EVENT_BONUS_ABUSING:
			return EVENT;
			
		case MINVALUEODD_BONUS_ABUSING:
			return MINVALUEODD;
			
		case RATING_BONUS_ABUSING:
			return RATING;
			
		case RF_BONUS_ABUSING:
			return RF;
			
		case SIZE_BONUS_ABUSING:
			return SIZE;
			
		default:
			return null;
		}
	}
}
