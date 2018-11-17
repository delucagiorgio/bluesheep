package it.bluesheep.telegrambot.message.io;

import it.bluesheep.telegrambot.message.util.ChatBotFilterCommand;

public class ChatBotCallbackFilterDefault extends ChatBotCallbackFilter {

	protected ChatBotCallbackFilterDefault(ChatBotFilterCommand filter, String value, String callbackInfo, int pageIndex, boolean isIdFilter) {
		super(filter, value, callbackInfo, isIdFilter);
		this.pageNumber = pageIndex;
	}
}
