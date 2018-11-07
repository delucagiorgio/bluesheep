package it.bluesheep.telegrambot.exception;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.entities.TelegramUser;

public class AskToUsException extends BluesheepChatBotException {

	private static final long serialVersionUID = 1L;
	
	public AskToUsException(TelegramUser user) {
		super("🚧 " + ArbsUtil.getTelegramBoldString("ATTENZIONE "+ user.getFirstName()) + 
				" 🚧" + System.lineSeparator() + "C'è un problema. Scrivici in chat su " 
				+ ArbsUtil.getTelegramInlineURLAlias("Facebook", "https://www.facebook.com/BlueSheepMatched/") 
				+ " o sulla mail info@bluesheep.it. ", user);
		}

}
