package it.bluesheep.telegrambot.message.util;

import it.bluesheep.arbitraggi.util.ArbsUtil;

public enum TextOptionBookmakerCommand {
	
	ADD_PREF("Scegli, tra i bookmaker disponibili, su quale desideri ricevere una notifica"),
	ENABLE_DISABLE_PREF("Scegli il bookmaker relativo a una tua preferenza da attivare o disattivare"),
	SHOW_ACTIVE_PREF("Scegli il bookmaker relativo ad una tua preferenza nello storico di cui vuoi visualizzare i dettagli"),
	DEL_PREF("Scegli il bookmaker relativo ad una tua preferenza nello storico che vuoi eliminare. " + ArbsUtil.getTelegramBoldString("ATTENZIONE: l'azione non è reversibile!")),
	MOD_PREF("Scegli il bookmaker relativo ad una tua preferenza nello storico di cui vuoi modificare i parametri di filtro");
	
	private String text;
	
	TextOptionBookmakerCommand(String text){
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text;
	}

}
