package it.bluesheep.telegrambot.message.util;
/**
 * I testi relativi ai filtri da mostrare
 * @author giorgio
 *
 */
public enum TextFilterCommand {
	
	RF("RF", "RF"),
	RATING("Rating", "RATING"),
	EVENT("Evento", "EVENT"),
	MINVALUEODD("Quota minima", "MINVALUEODD"), 
	SIZE("Liquidità", "SIZE");

	private String code;
	private String callbackCode;
	
	TextFilterCommand(String code, String callbackCode){
		this.code = code;
		this.callbackCode = callbackCode;
	}
	
	public String getCode() {
		return code;
	}
	
	public static TextFilterCommand getTextFilterCommandByChatBotFilterCommand(ChatBotFilterCommand chatBotFilterList) {
		switch(chatBotFilterList) {
		case EVENT_BONUS_ABUSING:
			return EVENT;
			
		case MINVALUEODD_BONUS_ABUSING:
			return MINVALUEODD;
			
		case RATING_BONUS_ABUSING:
			return RATING;
			
			//L'RF è un caso particolare in cui è previsto prima l'inserimento del rimborso, poi il valore del rating
		case RF_TYPE_BONUS_ABUSING:	
		case RF_BONUS_ABUSING:
			return RF;
			
		case SIZE_BONUS_ABUSING:
			return SIZE;
			
		default:
			return null;
		}
	}
	
	public static boolean isTextFilterCommand(String code) {
		for(TextFilterCommand tfc : TextFilterCommand.values()) {
			if(tfc.callbackCode.equals(code)) {
				return true;
			}
		}
		return false;
	}
}
