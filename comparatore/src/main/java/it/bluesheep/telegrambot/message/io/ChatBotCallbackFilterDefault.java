package it.bluesheep.telegrambot.message.io;

import it.bluesheep.telegrambot.message.util.ChatBotFilterCommand;

public class ChatBotCallbackFilterDefault extends ChatBotCallbackFilter {

	protected ChatBotCallbackFilterDefault(ChatBotFilterCommand filter, String value, String callbackInfo, int pageIndex) {
		super(filter, value, callbackInfo, true);
		this.pageNumber = pageIndex;
	}
}
