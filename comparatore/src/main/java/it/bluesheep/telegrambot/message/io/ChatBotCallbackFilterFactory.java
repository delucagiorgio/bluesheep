package it.bluesheep.telegrambot.message.io;

import it.bluesheep.telegrambot.message.util.ChatBotFilterCommand;

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
