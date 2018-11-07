package it.bluesheep.telegrambot.exception;

import org.telegram.telegrambots.meta.api.objects.Message;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.entities.TelegramUser;

public class NotPermittedOperationException extends BluesheepChatBotException {

	private static final long serialVersionUID = 1L;
	
	public NotPermittedOperationException(TelegramUser user, Message message) {
		super("❌ " + ArbsUtil.getTelegramBoldString("Operazione non permessa"), user);
		this.markupOriginMessage = message;
	}
}
