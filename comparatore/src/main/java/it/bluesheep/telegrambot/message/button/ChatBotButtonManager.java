package it.bluesheep.telegrambot.message.button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import it.bluesheep.database.entities.AbstractBlueSheepEntity;
import it.bluesheep.database.entities.Bookmaker;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.telegrambot.exception.AskToUsException;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackCommand;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackFilter;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackFilterFactory;
import it.bluesheep.telegrambot.message.util.CallbackDataType;
import it.bluesheep.telegrambot.message.util.ChatBotCommand;
import it.bluesheep.telegrambot.message.util.ChatBotFilterCommand;
import it.bluesheep.telegrambot.message.util.TextButtonCommand;
import it.bluesheep.telegrambot.message.util.TextFilterCommand;

public class ChatBotButtonManager {
	
	private static Logger logger = Logger.getLogger(ChatBotButtonManager.class);

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
				
			case NEXT_PAGE:
				return new ChatBotButton(CallbackDataType.NEXT_PAGE, TextButtonCommand.NEXT_PAGE);
				
			case PREVIOUS_PAGE:
				return new ChatBotButton(CallbackDataType.PREVIOUS_PAGE, TextButtonCommand.PREVIOUS_PAGE);
				
			case BACK_TO_MENU_BONUS_ABUSING:
				return new ChatBotButton(CallbackDataType.BACK_TO_MENU, TextButtonCommand.BACK_TO_MENU);
				
			case CONFIRM_CHANGE_BONUS_ABUSING:
				return new ChatBotButton(CallbackDataType.CONFIRM, TextButtonCommand.CONFIRM);
				
			case BACK_TO_KEYBOARD: 
				return new ChatBotButton(CallbackDataType.BACK_TO_KEYBOARD, TextButtonCommand.BACK_TO_KEYBOARD);
				default:
					return null;
		}
	}
	
	/**
	 * GD - 08/11/2018
	 * Restituisce i pulsanti cliccabili dall'utente, ognuno con il preciso valore da mostrare e con il corrispettivo callback corretto.
	 * "commandList" deve contenere solo la rootCommand ADD_PREFERENCE
	 * @param commandList la callback del messaggio
	 * @param bookmakerAvailable i bookmaker disponibili alla visualizzazione
	 * @param columnNumber numero di colonne da mostrare
	 * @param pageIndex l'indice della pagina
	 * @return i pulsanti cliccabili dall'utente, ognuno con il preciso valore da mostrare e con il corrispettivo callback corretto.
	 */
	public static List<List<InlineKeyboardButton>> getBookmakerButtonListNColumns(ChatBotCallbackCommand commandList, List<Bookmaker> bookmakerAvailable, int columnNumber, int pageIndex) {
		int bookmakerCount = bookmakerAvailable.size();
		int maxRow = 3;
		boolean pagination = false;
		
		if(bookmakerCount > maxRow * columnNumber) {
			pagination = true;
		}
		
		return createTableMessage(bookmakerAvailable, commandList, maxRow, columnNumber, pagination, pageIndex);
	}
	
	private static List<List<InlineKeyboardButton>> createTableMessage(List<Bookmaker> availableEntity, ChatBotCallbackCommand commandList, int maxRow,
			int columnNumber, boolean pagination, int pageIndex) {
		
		int pageNumber = pageIndex;
		
		boolean partialResult = false;
		boolean isId = commandList.getLastChatBotCallbackFilter() != null && commandList.getLastChatBotCallbackFilter().isIdFilter();
		boolean existsPreviousPage = pageIndex > 0;
		
		ChatBotCallbackCommand firstLabelButton = null;
		ChatBotCallbackCommand lastLabelButton = null;
		
		List<String> buttonTextLabel = null;

		if(pagination) { // Se il risultato è paginato
			
			if(!isId) { // Se il valore di filtro NON è un id del record
				
				//Calcolo tutte le possibili iniziali dei record
				buttonTextLabel = getKeyboardFromInitialChar(availableEntity);
				if(buttonTextLabel.size() > maxRow * columnNumber || availableEntity.size() > maxRow * columnNumber) { // se NON stanno tutti nella tastiera
					//I bottoni sono iniziali e contengono la pagina corrente
					partialResult = true;
					buttonTextLabel = buttonTextLabel.subList(0, Math.min(maxRow * columnNumber, buttonTextLabel.size()));
				}else {
					buttonTextLabel = getListEntityValues(availableEntity);
					if(buttonTextLabel.size() > maxRow * columnNumber) { // se NON stanno tutti nella tastiera
						//I bottoni sono caratteri iniziali e contengono la pagina corrente
						partialResult = true;
						buttonTextLabel = buttonTextLabel.subList(0, Math.min(maxRow * columnNumber, buttonTextLabel.size()));
					}
					isId = true;
				}
			}else { // Se il valore di filtro è un id del record
				buttonTextLabel = getListEntityValues(availableEntity);
				if(buttonTextLabel.size() > maxRow * columnNumber) { // se NON stanno tutti nella tastiera
					//I bottoni sono iniziali e contengono la pagina corrente
					partialResult = true;
					buttonTextLabel = buttonTextLabel.subList(0, Math.min(maxRow * columnNumber, buttonTextLabel.size()));
				}
			}
		}else {
			buttonTextLabel = getListEntityValues(availableEntity);
			buttonTextLabel = buttonTextLabel.subList(0, Math.min(maxRow * columnNumber, buttonTextLabel.size()));
			isId = true;
		}
		
		logger.info("ButtonTextLabel size = " + buttonTextLabel.size() + "; availableEntity Size = " + availableEntity.size());
		
		
		Collections.sort(buttonTextLabel);
		
		logger.info("pagination = " + pagination + "; partialResult = " + partialResult + "; isId = " + isId + "; pageNumber = " + pageNumber);
		
		List<List<InlineKeyboardButton>> table = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> showInline = new ArrayList<InlineKeyboardButton>() ;
		ChatBotCallbackCommand copyRecord = null;
		for (int i = 0 ; i < buttonTextLabel.size(); i++) {
			copyRecord = new ChatBotCallbackCommand(commandList);
			copyRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>());
			List<ChatBotCallbackFilter> listFilter = new ArrayList<ChatBotCallbackFilter>();
			
			boolean newColumn = showInline.size() % columnNumber == 0;
			showInline = newColumn ? new ArrayList<InlineKeyboardButton>() : table.get(table.size() - 1);
			
			String displayValue = buttonTextLabel.get(i);
			ChatBotCallbackFilter filter = ChatBotCallbackFilterFactory
					.getCorrectChatBotCallbackFilterFactory(pagination, 
															ChatBotFilterCommand.BOOKMAKER_BONUS_ABUSING, 
															displayValue,  
															+ (pagination ? 1 : 0) + ":" 
																	+ (partialResult ? 1 : 0) + ":" 
																	+ (isId ? 1 : 0) + ":" 
																	+ pageNumber + ":" 
																	+ displayValue, 
															partialResult, isId, pageIndex);
			listFilter.add(filter);
			copyRecord.getFilterCommandsList().add(filter);
			InlineKeyboardButton showButton = new ChatBotButton(copyRecord).getButtonTelegram();
			
			System.out.println("-------------------------------------");
			
			System.out.println(showButton.getText());
			System.out.println(showButton.getCallbackData());
			
			System.out.println("-------------------------------------");
			
			if (showButton != null) {
				showInline.add(showButton);
				if (newColumn) {
					table.add(showInline);
				}
			}
			
			if(firstLabelButton == null) {
				firstLabelButton = new ChatBotCallbackCommand(copyRecord);
			}
		}	
		
		lastLabelButton = new ChatBotCallbackCommand(copyRecord);
		
		if(partialResult && existsPreviousPage || showInline.size() >= columnNumber) {
			showInline = new ArrayList<InlineKeyboardButton>();
		}
		
		//Se il risultato è parziale e non è la prima pagina, mostro il pulsante <<
		if(existsPreviousPage) {
			ChatBotCallbackCommand previousRecord = new ChatBotCallbackCommand(firstLabelButton);
			previousRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>(firstLabelButton.getFilterCommandsList()));
			previousRecord.getLastChatBotCallbackFilter().setPageNumber(previousRecord.getLastChatBotCallbackFilter().getPageNumber() - 1);
			previousRecord.setNavigationCommand(ChatBotCommand.PREVIOUS_PAGE);
			
			InlineKeyboardButton previousPageButton = new ChatBotButton(previousRecord).getButtonTelegram();
			showInline.add(previousPageButton);
		}
		
		//Se il risultato è parziale, mostro il pulsante >>
		if(partialResult) {
			
			ChatBotCallbackCommand nextRecord = new ChatBotCallbackCommand(lastLabelButton);
			nextRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>(lastLabelButton.getFilterCommandsList()));
			nextRecord.setNavigationCommand(ChatBotCommand.NEXT_PAGE);
			nextRecord.getLastChatBotCallbackFilter().setPageNumber(nextRecord.getLastChatBotCallbackFilter().getPageNumber() + 1);

			InlineKeyboardButton nextPageButton = new ChatBotButton(nextRecord).getButtonTelegram();
			
			showInline.add(nextPageButton);
		}
		
		if(showInline.size() >= columnNumber && isId) {
			table.add(showInline);
			showInline = new ArrayList<InlineKeyboardButton>();
		}else if(!isId) {
			table.add(showInline);
		}
		
		if(isId) {
			ChatBotCallbackCommand backToKeyboardRecord = new ChatBotCallbackCommand(lastLabelButton);
			backToKeyboardRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>(lastLabelButton.getFilterCommandsList()));
			backToKeyboardRecord.setNavigationCommand(ChatBotCommand.BACK_TO_KEYBOARD);
			
			InlineKeyboardButton previousPageButton = new ChatBotButton(backToKeyboardRecord).getButtonTelegram();
			showInline.add(previousPageButton);
			table.add(showInline);
		}
		
		return table;
	}
					
	private static List<String> getListEntityValues(List<Bookmaker> availableEntity) {
		List<String> entityValues = new ArrayList<String>();
		for(AbstractBlueSheepEntity entity : availableEntity) {
			entityValues.add(entity.getTelegramButtonText());
		}
		return entityValues;
	}


	private static List<String> getKeyboardFromInitialChar(List<Bookmaker> availableEntity) {
		Set<String> entityValuesSet = new HashSet<String>();
		
		//Trova il giusto accoppiamento di suffissi rispetto alle entità disponibili considerando il numero di pulsanti
		if(availableEntity != null && !availableEntity.isEmpty()) {
			for(AbstractBlueSheepEntity entity : availableEntity) {
				entityValuesSet.add(entity.getTelegramButtonText().substring(0, 1));
			}
		}
		List<String> listValuesSet = new ArrayList<String>(entityValuesSet);
		Collections.sort(listValuesSet);
		
		return listValuesSet;
	}


	public static EditMessageText getAvailableFilterListButton(UserPreference newUP_DB, ChatBotCallbackCommand command, int maxRow, int columnNumber, Message receivedMessage, TelegramUser userMessage, int pageIndex) throws AskToUsException {
		
		List<ChatBotFilterCommand> availableFilter = new ArrayList<ChatBotFilterCommand>();
		
		//1.Se la liquidità non è impostata
		if(newUP_DB.getLiquidita() == null) {
			availableFilter.add(ChatBotFilterCommand.SIZE_BONUS_ABUSING);
		}
		
		//2.Se il campionato non è impostato e l'evento non è impostato (solo uno dei due)
		if(newUP_DB.getChampionship() == null && newUP_DB.getEvent() == null) {
			availableFilter.add(ChatBotFilterCommand.CHAMPIONSHIP_BONUS_ABUSING);
			availableFilter.add(ChatBotFilterCommand.EVENT_BONUS_ABUSING);
		}
		
		//3.Se la quota minima non è impostata
		if(newUP_DB.getMinOddValue() == null) {
			availableFilter.add(ChatBotFilterCommand.MINVALUEODD_BONUS_ABUSING);
		}
		
		//4.Se il rating non è impostato e l' RF non è impostato (solo uno dei due)
		if(newUP_DB.getRating() == null && newUP_DB.getRf() == null) {
			availableFilter.add(ChatBotFilterCommand.RF_BONUS_ABUSING);
			availableFilter.add(ChatBotFilterCommand.RATING_BONUS_ABUSING);
		}
		
		//Controllo se almeno un filtro è settato (-1 per il bookmaker che risulta già scelto)
		boolean noFilterSet = availableFilter.size() == ChatBotFilterCommand.values().length - 1;
		boolean pagination = false;
		boolean partialResult = false;
		boolean isId = true;
		
		EditMessageText message = null;

		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> table = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> showInline = new ArrayList<InlineKeyboardButton>() ;
		for (int i = 0 ; i < availableFilter.size(); i++) {
			ChatBotCallbackCommand copyRecord = new ChatBotCallbackCommand(command);
			copyRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>(command.getFilterCommandsList()));
			List<ChatBotCallbackFilter> listFilter = new ArrayList<ChatBotCallbackFilter>();
			boolean newColumn = showInline.size() % columnNumber == 0;
			showInline = newColumn ? new ArrayList<InlineKeyboardButton>() : table.get(table.size() - 1);
			TextFilterCommand displayValue = TextFilterCommand.getTextFilterCommandByChatBotFilterCommand(availableFilter.get(i));
			ChatBotCallbackFilter filter = ChatBotCallbackFilterFactory
								.getCorrectChatBotCallbackFilterFactory(pagination, 
																		availableFilter.get(i), 
																		displayValue.getCode(), 
																		+ (pagination ? 1 : 0) + ":" 
																				+ (partialResult ? 1 : 0) + ":" 
																				+ (isId ? 1 : 0) + ":" 
																				+ displayValue, 
																		partialResult, 
																		isId,
																		pageIndex);
			listFilter.add(filter);
			copyRecord.getFilterCommandsList().add(filter);
			InlineKeyboardButton showButton = new ChatBotButton(copyRecord).getButtonTelegram();
			System.out.println("-------------------------------------");
			
			System.out.println(showButton.getText());
			System.out.println(showButton.getCallbackData());
			
			System.out.println("-------------------------------------");
			if (showButton != null) {
				showInline.add(showButton);
				if (newColumn) {
					table.add(showInline);
				}
			}
		}	
		
		if(!noFilterSet) {
			showInline = new ArrayList<InlineKeyboardButton>();
			Set<ChatBotCommand> chatBotCommand = new HashSet<ChatBotCommand>();
			chatBotCommand.add(ChatBotCommand.CONFIRM_CHANGE_BONUS_ABUSING);
			List<List<InlineKeyboardButton>> confirmButton = ChatBotButtonManager.getOneColumnCommandsAvailable(chatBotCommand);
			table.add(confirmButton.get(0));
		}
		
		if (!table.isEmpty()) {
			String text = "Hai selezionato " +  command.getLastChatBotCallbackFilter().getValue() + "."
					+ System.lineSeparator()  
					+ "Ora scegli quale filtro impostare oppure torna al menu principale: non perderai i dati finora salvati!"
					+ System.lineSeparator() 
					+ "Attualmente il tuo filtro è inattivo ma puoi sempre modificarlo dal menù iniziale, se vuoi completare l'operaione in un altro momento";
			message = new EditMessageText() // Create a message object object
					.setChatId(receivedMessage.getChatId())
					.setText(text)
					.setMessageId(receivedMessage.getMessageId());
			// Add it to the message
			markupInline.setKeyboard(table);
			message.setReplyMarkup(markupInline);
		
		}else {			
			throw new AskToUsException(userMessage);
		}
		
		return message;
	}
}
