package it.bluesheep.telegrambot.exception;

import it.bluesheep.database.entities.TelegramUser;

public class TooOldMessageUsedException extends BluesheepChatBotException {

	private static final long serialVersionUID = 1L;
	
	public TooOldMessageUsedException(TelegramUser user) {
		super("⚠️ Non interagire con un vecchio messaggio! Utilizza il comando /menu per visualizzare le azioni che puoi compiere", user);
	}

}
