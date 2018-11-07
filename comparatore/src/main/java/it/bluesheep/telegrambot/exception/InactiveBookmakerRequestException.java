package it.bluesheep.telegrambot.exception;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.entities.TelegramUser;

public class InactiveBookmakerRequestException extends BluesheepChatBotException {

	public InactiveBookmakerRequestException(TelegramUser user) {
		super("❌" + ArbsUtil.getTelegramBoldString(" Bookmaker inattivo") +" ❌" 
				+ System.lineSeparator() + 
				"Reindirizzamento al menu principale.", user);
	}

	private static final long serialVersionUID = 1L;

}
