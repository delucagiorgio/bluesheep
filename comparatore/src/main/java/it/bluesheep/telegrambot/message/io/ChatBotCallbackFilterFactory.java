package it.bluesheep.telegrambot.message.io;

import it.bluesheep.telegrambot.message.util.ChatBotFilterCommand;
/**
 * Classe factory per creare un filtro di default con i valori specifici o un filtro paginato, contenente tutte le informazioni delle pagine
 * @author giorgio
 *
 */
public class ChatBotCallbackFilterFactory {
	
	private ChatBotCallbackFilterFactory() {}
	
	public static ChatBotCallbackFilter getCorrectChatBotCallbackFilterFactory(boolean pagination, ChatBotFilterCommand filter, String textValue, String callbackInfo, boolean keyboardMod, boolean isFilterId, int pageIndex) {
		if(pagination) {
			return new ChatBotCallbackFilterPaginated(filter, textValue, callbackInfo, pagination, isFilterId, pageIndex);
		}else {
			return new ChatBotCallbackFilterDefault(filter, textValue, callbackInfo, pageIndex, isFilterId);
		}
	}

}
