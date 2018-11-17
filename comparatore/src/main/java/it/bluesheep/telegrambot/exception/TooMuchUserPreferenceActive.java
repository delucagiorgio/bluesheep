package it.bluesheep.telegrambot.exception;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.entities.UserPreference;

public class TooMuchUserPreferenceActive extends BluesheepChatBotException {

	private static final long serialVersionUID = 1L;

	public TooMuchUserPreferenceActive(TelegramUser user, UserPreference up) {
		super("‼️ " + ArbsUtil.getTelegramBoldString("ATTENZIONE " + user.getFirstName()) 
				+ System.lineSeparator() + System.lineSeparator()
				+ "La preferenza sul bookmaker " + ArbsUtil.getTelegramBoldString(up.getBookmaker().getBookmakerName())
				+ " non può essere attivata a causa del numero di preferenze già attive." + System.lineSeparator()
				+ "Utilizza il comando /menu per andare al menù iniziale e scegli di eliminare o disabilitare una preferenza già attiva per poter attivare nuove preferenze", user);
	}

}
