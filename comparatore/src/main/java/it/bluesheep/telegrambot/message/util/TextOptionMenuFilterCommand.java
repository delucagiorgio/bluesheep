package it.bluesheep.telegrambot.message.util;

public enum TextOptionMenuFilterCommand {

	EVENT("Scegli su quale, tra gli eventi disponibili, vuoi riceve le segnalazioni"),
	RF_TYPE("Imposta il corretto rimborso specificato nell'offerta che vuoi sfruttare"),
	RF_VALUE("Scegli il valore minimo del Risk Free Rating (RF)"),
	RATING("Scegli il valore minimo del rating che deve essere presente nella segnalazione"),
	MINODDVALUE("Scegli il valore minimo di quota che deve essere presente nella segnalazione"),
	SIZE("Scegli il valore minimo di liquidità che deve essere presente nella segnalazione");
	
	private String text;
	
	TextOptionMenuFilterCommand(String text){
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text;
	}
	
}
