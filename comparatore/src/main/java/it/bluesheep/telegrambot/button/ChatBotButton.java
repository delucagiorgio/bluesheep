package it.bluesheep.telegrambot.button;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import it.bluesheep.util.BlueSheepConstants;

public class ChatBotButton {

	private InlineKeyboardButton buttonTelegram;
	
	protected ChatBotButton(CallbackDataType callbackType, TextButtonCommand textButtonCommandIT) {
		buttonTelegram = new InlineKeyboardButton();
		buttonTelegram.setText(textButtonCommandIT.getCode());
		buttonTelegram.setCallbackData(callbackType.getCode());
	}
	
	public ChatBotButton(CallbackDataType bookmaker, String textButtonCommandIT) {
		buttonTelegram = new InlineKeyboardButton();
		buttonTelegram.setText(textButtonCommandIT);
		buttonTelegram.setCallbackData(bookmaker.getCode() + BlueSheepConstants.REGEX_CSV + textButtonCommandIT);
	}

	public InlineKeyboardButton getButtonTelegram() {
		return buttonTelegram;
	}
	
}
