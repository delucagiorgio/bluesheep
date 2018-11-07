package it.bluesheep.telegrambot.button;

public enum TextButtonCommand {
    
	SHOW_ACTIVE_PREF("Visualizza preferenze di notifica attive"),
	ENABLE_DISABLE_PREF("Attiva/Disattiva preferenze di notifica"),
    ADD_PREF("Aggiungi preferenze di notifica"),
    DEL_PREF("Rimuovi preferenze di notifica"),
    MOD_PREF("Modifica preferenze di notifica");
	
	private String code;
	
	private TextButtonCommand(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
