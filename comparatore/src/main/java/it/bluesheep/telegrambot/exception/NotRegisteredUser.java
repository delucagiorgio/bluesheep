package it.bluesheep.telegrambot.exception;

import org.telegram.telegrambots.meta.api.objects.Message;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.entities.TelegramUser;

public class NotRegisteredUser extends BluesheepChatBotException {

	public NotRegisteredUser(TelegramUser user, Message receivedMessage) {
		super("✋ " + ArbsUtil.getTelegramBoldString("ASPETTA " + user.getUserName()) 
		+ System.lineSeparator() + "Fin qui tutto bene, ma prima di utilizzare le funzionalità del nostro sistema è necessario che tu ti registri scrivendo /start (o cliccandoci qui direttamente)!" +
				System.lineSeparator() + "Fatto questo, ti assicuro che ci siamo! 😊" , user);
	}

	private static final long serialVersionUID = 1L;

}
