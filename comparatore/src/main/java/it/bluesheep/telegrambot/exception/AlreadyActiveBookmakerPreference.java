package it.bluesheep.telegrambot.exception;

import org.telegram.telegrambots.meta.api.objects.Message;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.entities.Bookmaker;
import it.bluesheep.database.entities.TelegramUser;

public class AlreadyActiveBookmakerPreference extends BluesheepChatBotException {

	public AlreadyActiveBookmakerPreference(TelegramUser user, Message message, Bookmaker bookmaker) {
		super("‼️" + " Preferenza sul bookmaker " + ArbsUtil.getTelegramBoldString(bookmaker.getBookmakerName()) + " già attiva." + 
				System.lineSeparator(), user);
		this.markupOriginMessage = message;
	}

	private static final long serialVersionUID = 1L;

}
