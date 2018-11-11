package it.bluesheep.telegrambot.message.io;

import it.bluesheep.telegrambot.message.util.ChatBotFilterCommand;

public class ChatBotCallbackFilterPaginated extends ChatBotCallbackFilter {

	protected ChatBotCallbackFilterPaginated(ChatBotFilterCommand filter, String value, String callbackInfo, boolean keyboardMode, boolean isFilterID, int pageIndex) {
		super(filter, value, callbackInfo, isFilterID);
		this.keyboardMode = keyboardMode;
		this.pageNumber = pageIndex;
	}

}
