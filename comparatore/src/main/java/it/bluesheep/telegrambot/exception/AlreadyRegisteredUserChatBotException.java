package it.bluesheep.telegrambot.exception;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.entities.TelegramUser;

public class AlreadyRegisteredUserChatBotException extends BluesheepChatBotException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyRegisteredUserChatBotException(TelegramUser user) {
		super("🤨" + System.lineSeparator() + ArbsUtil.getTelegramBoldString("ATTENZIONE "+ user.getFirstName()) + 
				": sei già stato inserito nel nostro database con uno specifico ID come utente attivo. Utilizza il comando /menu per visualizzare le opzioni che hai."
				+ System.lineSeparator()
				+ "La registrazione ci risulta avvenuta in data " + new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date(user.getRegistrationDate()))
				+ System.lineSeparator() 
				+ "La tua sottoscrizione è correttamente riconosciuta ed il servizio di segnalazione è abilitato all'invio dei messaggi alla tua chat." + "✔️" + System.lineSeparator() + " Per ulteriori chiarimenti, contattaci su " 
				+ ArbsUtil.getTelegramInlineURLAlias("Facebook", "https://www.facebook.com/BlueSheepMatched/"), user);
	}
	
}
