package it.bluesheep.telegrambot.exception;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.entities.TelegramUser;

public class NoUserNameSet extends BluesheepChatBotException {

	private static final long serialVersionUID = 1L;

	public NoUserNameSet(TelegramUser user) {
		super(ArbsUtil.getTelegramBoldString("ATTENZIONE") 
				+ System.lineSeparator() + "Per poter interagire è necessario settare lo username del proprio account Telegram." 
				+ System.lineSeparator() + "Puoi farlo direttamente dal tuo cellulare cliccando su _Impostazioni_, "
						+ "successivamente vai nella sezione del tuo account (dove c'è scritto il tuo nome e il tuo numero di telefono):"
						+ " qui troverai il campo username da settare! Ricordati di inserire lo stesso username anche sul nostro sito www.bluesheep.it." 
						+ System.lineSeparator() + "Successivamente utilizza il comando /registrazione per abilitare il servizio!", user);
	}
}
