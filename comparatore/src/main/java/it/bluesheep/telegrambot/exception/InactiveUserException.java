package it.bluesheep.telegrambot.exception;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.entities.TelegramUser;

public class InactiveUserException extends BluesheepChatBotException {

	private static final long serialVersionUID = 1L;
	
	public InactiveUserException(TelegramUser user) {
		super("❌ La tua utenza risulta disabilitata: è per caso scaduto il tuo abbonamento a Blue Sheep? " 
				+ System.lineSeparator() 
				+ "Scrivici in chat su " + ArbsUtil.getTelegramInlineURLAlias("Facebook", "https://www.facebook.com/BlueSheepMatched/") 
				+ " o sulla mail info@bluesheep.it se hai dubbi a riguardo!", user);
	}
}
