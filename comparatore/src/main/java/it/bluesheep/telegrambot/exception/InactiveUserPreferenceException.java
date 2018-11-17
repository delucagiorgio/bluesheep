package it.bluesheep.telegrambot.exception;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.entities.TelegramUser;

public class InactiveUserPreferenceException extends BluesheepChatBotException {

	public InactiveUserPreferenceException(TelegramUser user) {
		super(ArbsUtil.getTelegramBoldString("ATTENZIONE " + user.getFirstName()) + ": la preferenza richiesta risulta inattiva!" 
				+ System.lineSeparator() + "Ricorda che non puoi avere più di due preferenze di segnalazione attive"
				+ System.lineSeparator() + "Torna al /menu per visualizzare le azioni disponibili", user);
	}

	private static final long serialVersionUID = 1L;

}
