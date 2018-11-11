package it.bluesheep.telegrambot.message.button;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import it.bluesheep.telegrambot.message.io.ChatBotCallbackCommand;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackFilter;
import it.bluesheep.telegrambot.message.util.CallbackDataType;
import it.bluesheep.telegrambot.message.util.TextButtonCommand;
import it.bluesheep.util.BlueSheepConstants;

public class ChatBotButton {

	private InlineKeyboardButton buttonTelegram;
	
	protected ChatBotButton(CallbackDataType callbackType, TextButtonCommand textButtonCommandIT) {
		buttonTelegram = new InlineKeyboardButton();
		buttonTelegram.setText(textButtonCommandIT.getCode());
		buttonTelegram.setCallbackData(callbackType.getCode());
	}
	
	public ChatBotButton(CallbackDataType callbackDataType, String textButtonCommandIT) {
		buttonTelegram = new InlineKeyboardButton();
		buttonTelegram.setText(textButtonCommandIT);
		buttonTelegram.setCallbackData(callbackDataType.getCode() + BlueSheepConstants.REGEX_CSV + textButtonCommandIT);
	}
	
	public ChatBotButton(ChatBotCallbackCommand callbackData) {
		buttonTelegram = new InlineKeyboardButton();
		if(callbackData.getNavigationCommand() != null) {
			buttonTelegram.setText(TextButtonCommand.getTextButtonCommandByChatBotCommand(callbackData.getNavigationCommand()).getCode());
		}else {
			buttonTelegram.setText(callbackData.getLastChatBotCallbackFilter().getValue());
		}
		String str = callbackData.getRootCommand().getCode() + BlueSheepConstants.REGEX_COMMA;
		for(ChatBotCallbackFilter filter : callbackData.getFilterCommandsList()) {
			str = str + filter.getSpecificCallbackData();
			if(callbackData.getFilterCommandsList().indexOf(filter) + 1 != callbackData.getFilterCommandsList().size()) {
				str = str + BlueSheepConstants.REGEX_COMMA;
			}
		}
		if(callbackData.getNavigationCommand() != null) {
			str = str + BlueSheepConstants.REGEX_COMMA + callbackData.getNavigationCommand().getCode();
		}
		buttonTelegram.setCallbackData(str);
	}

	public InlineKeyboardButton getButtonTelegram() {
		return buttonTelegram;
	}
	
}
