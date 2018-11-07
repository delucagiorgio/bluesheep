package it.bluesheep.telegrambot.exception;

import org.telegram.telegrambots.meta.api.objects.Message;

import it.bluesheep.database.entities.TelegramUser;

public abstract class BluesheepChatBotException extends Exception {

	protected static final long serialVersionUID = 1L;
	protected TelegramUser user;
	protected Message markupOriginMessage;


	public BluesheepChatBotException(String messageError, TelegramUser user) {
		super(messageError);
		this.user = user;
	}
	
	public TelegramUser getUser() {
		return user;
	}
	
	public Message getMarkupOriginMessage() {
		return markupOriginMessage;
	}
	
}
