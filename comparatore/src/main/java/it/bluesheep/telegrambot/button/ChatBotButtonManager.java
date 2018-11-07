package it.bluesheep.telegrambot.button;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import it.bluesheep.database.entities.Bookmaker;
import it.bluesheep.telegrambot.ChatBotCommand;

public class ChatBotButtonManager {

	private ChatBotButtonManager() {}

	
	public static List<List<InlineKeyboardButton>> getOneColumnCommandsAvailable(Set<ChatBotCommand> chatBotCommandToBeDisplayed){
		
		List<List<InlineKeyboardButton>> listOfCommandButtonOneColumn = new ArrayList<List<InlineKeyboardButton>>();
		
		for(ChatBotCommand command : chatBotCommandToBeDisplayed) {
			List<InlineKeyboardButton> showInline = new ArrayList<InlineKeyboardButton>();
			InlineKeyboardButton showButton = ChatBotButtonManager.getCorrectChatBotButtonInfosOneColumn(command).getButtonTelegram();
	        if(showButton != null) {
	        	showInline.add(showButton);
	        	listOfCommandButtonOneColumn.add(showInline);
	        }
		}
		
        return listOfCommandButtonOneColumn;
	}
	
	public static List<List<InlineKeyboardButton>> getAvailableFilterButton(Set<ChatBotCommand> chatBotCommandToBeDisplayed){
		
		List<List<InlineKeyboardButton>> listOfCommandButtonOneColumn = new ArrayList<List<InlineKeyboardButton>>();
		
		for(ChatBotCommand command : chatBotCommandToBeDisplayed) {
			List<InlineKeyboardButton> showInline = new ArrayList<InlineKeyboardButton>();
			InlineKeyboardButton showButton = ChatBotButtonManager.getCorrectChatBotButtonInfosOneColumn(command).getButtonTelegram();
	        if(showButton != null) {
	        	showInline.add(showButton);
	        	listOfCommandButtonOneColumn.add(showInline);
	        }
		}
		
        return listOfCommandButtonOneColumn;
	}

	
	private static ChatBotButton getCorrectChatBotButtonInfosOneColumn(ChatBotCommand command) {
		switch(command) {
		
			case ADD_PREFERENCE_BONUS_ABUSING:
				return new ChatBotButton(CallbackDataType.ADD_PREF, TextButtonCommand.ADD_PREF);
				
			case DELETE_PREFERENCE_BONUS_ABUSING:
				return new ChatBotButton(CallbackDataType.DEL_PREF, TextButtonCommand.DEL_PREF);

			case ENABLE_DISABLE_PREFERENCES_BONUS_ABUSING:
				return new ChatBotButton(CallbackDataType.ENABLE_DISABLE_PREF, TextButtonCommand.ENABLE_DISABLE_PREF);

			case SHOW_PREFERENCES_BONUS_ABUSING:
				return new ChatBotButton(CallbackDataType.SHOW_ACTIVE_PREF, TextButtonCommand.SHOW_ACTIVE_PREF);
			
			case MOD_PREFERENCES_BONUS_ABUSING:
				return new ChatBotButton(CallbackDataType.MOD_PREF, TextButtonCommand.MOD_PREF);
				default:
					return null;
		}
	}
	
	public static List<List<InlineKeyboardButton>> getBookmakerButtonListTwoColumns(List<Bookmaker> bookmakerAvailable) {
		
		List<List<InlineKeyboardButton>> listOfCommandButtonOneColumn = new ArrayList<List<InlineKeyboardButton>>();
		
		List<InlineKeyboardButton> showInline = new ArrayList<InlineKeyboardButton>() ;
		
		for(Bookmaker command : bookmakerAvailable) {
			boolean newColumn = showInline.size() % 2 == 0;
			showInline = newColumn ? new ArrayList<InlineKeyboardButton>() : listOfCommandButtonOneColumn.get(listOfCommandButtonOneColumn.size() - 1);
			InlineKeyboardButton showButton = new ChatBotButton(CallbackDataType.BOOKMAKER, command.getBookmakerName()).getButtonTelegram();
	        if(showButton != null) {
	        	showInline.add(showButton);
	        	if(newColumn) {
		        	listOfCommandButtonOneColumn.add(showInline);
	        	}
	        }
		}
		
		return listOfCommandButtonOneColumn;
	}
}
