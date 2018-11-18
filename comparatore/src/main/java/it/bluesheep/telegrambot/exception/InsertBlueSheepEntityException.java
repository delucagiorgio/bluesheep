package it.bluesheep.telegrambot.exception;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.entities.TelegramUser;

public class InsertBlueSheepEntityException extends BluesheepChatBotException {
	
	private static final long serialVersionUID = 1L;

	public InsertBlueSheepEntityException(TelegramUser user) {
		super(ArbsUtil.getTelegramBoldString("ATTENZIONE "+ user.getUserName()) + 
				" :  c'è un problema con la registrazione del tuo account. Scrivici in chat su " 
				+ ArbsUtil.getTelegramInlineURLAlias("Facebook", "https://www.facebook.com/BlueSheepMatched/") 
				+ " o sulla mail info@bluesheep.it. ", user);
	}

}
