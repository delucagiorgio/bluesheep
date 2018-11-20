package it.bluesheep.telegrambot.message.button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.entities.AbstractBlueSheepEntity;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.telegrambot.exception.AskToUsException;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackCommand;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackFilter;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackFilterFactory;
import it.bluesheep.telegrambot.message.util.CallbackDataType;
import it.bluesheep.telegrambot.message.util.ChatBotCommand;
import it.bluesheep.telegrambot.message.util.ChatBotCommandUtilManager;
import it.bluesheep.telegrambot.message.util.ChatBotFilterCommand;
import it.bluesheep.telegrambot.message.util.ChatBotFilterCommandUtilManager;
import it.bluesheep.telegrambot.message.util.TextButtonCommand;
import it.bluesheep.telegrambot.message.util.TextFilterCommand;
/**
 * Classe di utilità per creare le tabelle di bottoni "utente".
 * @author giorgio
 *
 */
public class ChatBotButtonManager {
	
	private ChatBotButtonManager() {}

	/**
	 * GD - 18/11/2018
	 * Crea una tabella di una colonna con i comandi disponibili per l'utente
	 * @param chatBotCommandToBeDisplayed Insieme di comandi disponibili per l'utente
	 * @return la tabella di bottoni
	 */
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
	
	/**
	 * GD - 18/11/2018
	 * Ritorna il corretto bottono dato un certo comando
	 * @param command il comando di cui si vuole il bottone
	 * @return il bottone con le specifiche del comando
	 */
	private static ChatBotButton getCorrectChatBotButtonInfosOneColumn(ChatBotCommand command) {
		switch(command) {
		
			case ADD_PREFERENCE_BONUS_ABUSING:
				return new ChatBotButton(CallbackDataType.ADD_PREF, TextButtonCommand.ADD_PREF);
				
			case DELETE_PREFERENCE_BONUS_ABUSING:
				return new ChatBotButton(CallbackDataType.DEL_PREF, TextButtonCommand.DEL_PREF);

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
	 * @param entityAvailable i bookmaker disponibili alla visualizzazione
	 * @param columnNumber numero di colonne da mostrare
	 * @param pageIndex l'indice della pagina
	 * @param maxRow il numero di righe massimo per i valori delle entità da mostrare
	 * @return i pulsanti cliccabili dall'utente, ognuno con il preciso valore da mostrare e con il corrispettivo callback corretto.
	 */
	public static List<List<InlineKeyboardButton>> getBlueSheepEntityButtonListNColumns(ChatBotCallbackCommand commandList, List<? extends AbstractBlueSheepEntity> entityAvailable, int columnNumber, int pageIndex, int maxRow) {
		int entityCount = entityAvailable.size();
		boolean pagination = false;
		
		if(commandList.getLastChatBotCallbackFilter() != null) {
			pagination = commandList.getLastChatBotCallbackFilter().isPagination();
		}
		if(entityCount > maxRow * columnNumber) {
			pagination = true;
		}
		
		return createTableMessage(entityAvailable, commandList, maxRow, columnNumber, pagination, pageIndex);
	}
	
	/**
	 * GD - 18/11/2018 
	 * Crea una tabella di bottoni rispetto ai valori e alla configurazione passata tramite parametri
	 * @param availableEntity le entità disponibili
	 * @param commandList la lista di comandi disponibili
	 * @param maxRow le righe massime di bottoni relativi alle entità
	 * @param columnNumber le colonne da mostrare
	 * @param pagination se paginata o meno 
	 * @param pageIndex l'indice della pagina da mostrare
	 * @return la tabella di bottoni
	 */
	private static List<List<InlineKeyboardButton>> createTableMessage(List<? extends AbstractBlueSheepEntity> availableEntity, ChatBotCallbackCommand commandList, int maxRow,
			int columnNumber, boolean pagination, int pageIndex) {
		
		int pageNumber = pageIndex;
		
		boolean partialResult = false;
		boolean isId = commandList.getLastChatBotCallbackFilter() != null && commandList.getLastChatBotCallbackFilter().isIdFilter();
		boolean existsPreviousPage = pageIndex > 0;
		
		ChatBotCallbackCommand firstLabelButton = null;
		ChatBotCallbackCommand lastLabelButton = null;
		
		List<String> buttonTextLabel = null;

		if(pagination) { // Se il risultato è paginato
			
			// Se il valore di filtro NON è un id del record ed è  O un comando di navigazione O una prima vista della tastiera
			if(!isId && (commandList.getNavigationCommand() != null || commandList.getFilterCommandsList() == null)) {
				
				//Calcolo tutte le possibili iniziali dei record
				buttonTextLabel = getKeyboardFromInitialChar(availableEntity);
				// se NON stanno tutti nella tastiera e l'attuale pagina non è l'ultima
				if((pageNumber + 1) * maxRow * columnNumber < buttonTextLabel.size()) { 
					//I bottoni sono iniziali e contengono la pagina corrente
					partialResult = true;
				}
				buttonTextLabel = buttonTextLabel.subList(pageNumber * maxRow * columnNumber, Math.min(maxRow * columnNumber + (maxRow * columnNumber * pageNumber), buttonTextLabel.size()));
			}
			
			else { // Se il valore di filtro è un id del record
				buttonTextLabel = getListEntityValues(availableEntity);
				if((pageNumber + 1) * maxRow * columnNumber < buttonTextLabel.size()) { // se NON stanno tutti nella tastiera
					//I bottoni sono iniziali e contengono la pagina corrente
					partialResult = true;
				}
				isId = true;
				buttonTextLabel = buttonTextLabel.subList(pageNumber * maxRow * columnNumber, Math.min(maxRow * columnNumber + (maxRow * columnNumber * pageNumber), buttonTextLabel.size()));
			}
		}else {
			buttonTextLabel = getListEntityValues(availableEntity);
			buttonTextLabel = buttonTextLabel.subList(0, Math.min(maxRow * columnNumber, buttonTextLabel.size()));
			isId = true;
		}
		
		if(commandList.getNavigationCommand() != null) {
			commandList.setNavigationCommand(null);
			if(!ChatBotFilterCommand.getAllAddFilters().contains(commandList.getLastChatBotCallbackFilter().getFilter())) {
				commandList.removeLastChatBotCallbackFilter();
			}
		}
		
		Collections.sort(buttonTextLabel);
		
		List<List<InlineKeyboardButton>> table = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> showInline = new ArrayList<InlineKeyboardButton>() ;
		ChatBotCallbackCommand copyRecord = null;
		for (int i = 0 ; i < buttonTextLabel.size(); i++) {
			copyRecord = new ChatBotCallbackCommand(commandList);
			ChatBotCallbackFilter ccf = null;
			if(commandList.getFilterCommandsList() == null) {
				copyRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>());
			}else {
				copyRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>(commandList.getFilterCommandsList()));
				ccf = copyRecord.getSameChatBotCallbackFilter(commandList.getLastChatBotCallbackFilter());
				if(ccf != null 
						&& (commandList.getNavigationCommand() == null 
							|| 
							!ChatBotCommandUtilManager.getNavigationChatBotCommand().contains(commandList.getNavigationCommand()))) {
					copyRecord.removeLastChatBotCallbackFilter();
				}
			}
			List<ChatBotCallbackFilter> listFilter = new ArrayList<ChatBotCallbackFilter>();
			
			boolean newColumn = showInline.size() % columnNumber == 0;
			showInline = newColumn ? new ArrayList<InlineKeyboardButton>() : table.get(table.size() - 1);
			
			ChatBotFilterCommand filterType = null;
			if(commandList.getLastChatBotCallbackFilter() != null && ccf != null) {
				filterType = ccf.getFilter();
			}else {
				//Caso base. Si passa sempre prima per il bookmaker
				filterType = ChatBotFilterCommand.BOOKMAKER_BONUS_ABUSING;
			}
			
			String displayValue = buttonTextLabel.get(i);
			ChatBotCallbackFilter filter = ChatBotCallbackFilterFactory
					.getCorrectChatBotCallbackFilterFactory(pagination, 
															filterType,
															displayValue,  
															+ (pagination ? 1 : 0) + ":" 
																	+ (partialResult ? 1 : 0) + ":" 
																	+ (isId ? 1 : 0) + ":" 
																	+ pageNumber + ":" 
																	+ displayValue, 
															partialResult, isId, pageIndex);
			listFilter.add(filter);
			
			if(copyRecord.getFilterCommandsList() == null) {
				copyRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>());
			}
			
			copyRecord.getFilterCommandsList().add(filter);
			InlineKeyboardButton showButton = new ChatBotButton(copyRecord).getButtonTelegram();
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
		
		List<InlineKeyboardButton> pageButton = new ArrayList<InlineKeyboardButton>();
		//Se il risultato è parziale e non è la prima pagina, mostro il pulsante <<
		if(existsPreviousPage) {
			ChatBotCallbackCommand previousRecord = new ChatBotCallbackCommand(firstLabelButton);
			previousRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>(firstLabelButton.getFilterCommandsList()));
			previousRecord.getLastChatBotCallbackFilter().setPageNumber(previousRecord.getLastChatBotCallbackFilter().getPageNumber() - 1);
			previousRecord.setNavigationCommand(ChatBotCommand.PREVIOUS_PAGE);
			
			InlineKeyboardButton previousPageButton = new ChatBotButton(previousRecord).getButtonTelegram();
			pageButton.add(previousPageButton);
		}
		
		//Se il risultato è parziale, mostro il pulsante >>
		if(partialResult) {
			
			ChatBotCallbackCommand nextRecord = new ChatBotCallbackCommand(lastLabelButton);
			nextRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>(lastLabelButton.getFilterCommandsList()));
			nextRecord.getLastChatBotCallbackFilter().setPageNumber(nextRecord.getLastChatBotCallbackFilter().getPageNumber() + 1);
			nextRecord.setNavigationCommand(ChatBotCommand.NEXT_PAGE);

			InlineKeyboardButton nextPageButton = new ChatBotButton(nextRecord).getButtonTelegram();
			pageButton.add(nextPageButton);
		}
		
		if(pageButton.size() + showInline.size() > columnNumber) {
			table.add(pageButton);
			showInline = new ArrayList<InlineKeyboardButton>();
		}else {
			showInline.addAll(pageButton);
		}
		
		if(isId 
				&& ChatBotFilterCommand.BOOKMAKER_BONUS_ABUSING.equals(lastLabelButton.getLastChatBotCallbackFilter().getFilter()) 
				&& ChatBotCommand.ADD_PREFERENCE_BONUS_ABUSING.equals(lastLabelButton.getRootCommand())) {
			if(showInline.size() + 1 > columnNumber) {
				showInline = new ArrayList<InlineKeyboardButton>();
				table.add(showInline);
			}
			ChatBotCallbackCommand backToKeyboardRecord = new ChatBotCallbackCommand(lastLabelButton);
			String initialChar = backToKeyboardRecord.getLastChatBotCallbackFilter().getValue();
			backToKeyboardRecord.getLastChatBotCallbackFilter().setValue(initialChar.substring(0, 1));
			backToKeyboardRecord.setNavigationCommand(ChatBotCommand.BACK_TO_KEYBOARD);
			
			InlineKeyboardButton previousPageButton = new ChatBotButton(backToKeyboardRecord).getButtonTelegram();
			showInline.add(previousPageButton);
		}
		
		if(!ChatBotFilterCommand.RF_BONUS_ABUSING.equals(lastLabelButton.getLastChatBotCallbackFilter().getFilter())) {
			List<InlineKeyboardButton> backToMenu = new ArrayList<InlineKeyboardButton>();
			ChatBotCallbackCommand backToMenuRecord = new ChatBotCallbackCommand(firstLabelButton);
			backToMenuRecord.setRootCommand(ChatBotCommand.MENU);
			backToMenuRecord.setFilterCommandsList(null);
			backToMenuRecord.setNavigationCommand(ChatBotCommand.BACK_TO_MENU_BONUS_ABUSING);
			
			InlineKeyboardButton backToMenuKeyButton = new ChatBotButton(backToMenuRecord).getButtonTelegram();
			backToMenu.add(backToMenuKeyButton);
			table.add(backToMenu);
		}
		
		return table;
	}
		
	/**
	 * GD - 18/11/2018
	 * Fornisce la lista di valori secondo il testo da mostrare sul bottone "utente"
	 * @param availableEntity le entità disponibili
	 * @return la lista di valori delle entità
	 */
	private static List<String> getListEntityValues(List<? extends AbstractBlueSheepEntity> availableEntity) {
		List<String> entityValues = new ArrayList<String>();
		
		for(AbstractBlueSheepEntity entity : availableEntity) {
			entityValues.add(entity.getTelegramButtonText());
		}
		
		return entityValues;
	}


	/**
	 * GD - 18/11/2018
	 * Crea una lista ordinata rispetto alle iniziali delle entità passate come parametro
	 * @param availableEntity le  entità disponibili
	 * @return l'insieme delle iniziali 
	 */
	private static List<String> getKeyboardFromInitialChar(List<? extends AbstractBlueSheepEntity> availableEntity) {
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

	/**
	 * GD - 18/11/2018
	 * Crea una tabella di bottoni rispetto ai valori del filtri e alla configurazione passata tramite parametri
	 * @param upDB la preferenza utente 
	 * @param command il comando generato dalla callback del messaggio ricevuto
	 * @param maxRow il numero di righe massimo per i valori dei filtri
	 * @param columnNumber il numero di colonne da mostrare
	 * @param receivedMessage il messaggio ricevuto
	 * @param userMessage l'utente DB
	 * @param pageIndex la pagina da mostrare
	 * @param toBeModified se la visualizzazione è per la creazione o per la modifica
	 * @return il messaggio da mostrare
	 * @throws AskToUsException se nel flusso si verifica qualche errore
	 */
	public static EditMessageText getAvailableFilterListButton(UserPreference upDB, ChatBotCallbackCommand command, int maxRow, int columnNumber, Message receivedMessage, TelegramUser userMessage, int pageIndex, boolean toBeModified) throws AskToUsException {
		
		List<ChatBotFilterCommand> availableFilter = ChatBotFilterCommandUtilManager.getChatBotFilterCommandListFromUserPreference(upDB, toBeModified);
		
		boolean pagination = availableFilter.size() > maxRow * columnNumber;
		boolean partialResult = false;
		boolean existsPreviousPage = command.getLastChatBotCallbackFilter() != null ? command.getLastChatBotCallbackFilter().getPageNumber() > 0 : false;
		boolean isId = false;
		
		ChatBotCallbackCommand firstLabelButton = null;
		ChatBotCallbackCommand lastLabelButton = null;
		
		if(pagination) { // Se il risultato è paginato
			if((pageIndex + 1) * maxRow * columnNumber < availableFilter.size()) { // se NON stanno tutti nella tastiera successiva
				//I bottoni sono iniziali e contengono la pagina corrente
				partialResult = true;
			}
			availableFilter = availableFilter.subList(pageIndex * maxRow * columnNumber, Math.min(maxRow * columnNumber + (maxRow * columnNumber * pageIndex), availableFilter.size()));
		}else {
			availableFilter = availableFilter.subList(0, Math.min(maxRow * columnNumber, availableFilter.size()));
		}
		
		if(command.getNavigationCommand() != null) {
			command.setNavigationCommand(null);
			command.removeLastChatBotCallbackFilter();
		}
		
		EditMessageText message = null;

		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> table = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> showInline = new ArrayList<InlineKeyboardButton>() ;
		ChatBotCallbackCommand copyRecord = null;
		for (int i = 0 ; i < availableFilter.size(); i++) {
			
			copyRecord = new ChatBotCallbackCommand(command);
			copyRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>());

			if(command.getFilterCommandsList() != null) {
				copyRecord.getFilterCommandsList().addAll(command.getFilterCommandsList());
			}
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
																				+ pageIndex + ":"
																				+ displayValue, 
																		partialResult, 
																		isId,
																		pageIndex);
			copyRecord.getFilterCommandsList().add(filter);
			
			InlineKeyboardButton showButton = new ChatBotButton(copyRecord).getButtonTelegram();
			if (showButton != null) {
				showInline.add(showButton);
				if (newColumn) {
					table.add(showInline);
				}
			}
			
			if(firstLabelButton == null){
				firstLabelButton = new ChatBotCallbackCommand(copyRecord);
			}
		}	
		
		//Crea i pulsanti di navigazione
		if(availableFilter != null && !availableFilter.isEmpty()) {
			lastLabelButton = new ChatBotCallbackCommand(copyRecord);
		
			List<InlineKeyboardButton> pageButton = new ArrayList<InlineKeyboardButton>();
			//Se il risultato è parziale e non è la prima pagina, mostro il pulsante <<
			if(existsPreviousPage) {
				ChatBotCallbackCommand previousRecord = new ChatBotCallbackCommand(firstLabelButton);
				previousRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>(firstLabelButton.getFilterCommandsList()));
				previousRecord.getLastChatBotCallbackFilter().setPageNumber(previousRecord.getLastChatBotCallbackFilter().getPageNumber() - 1);
				previousRecord.setNavigationCommand(ChatBotCommand.PREVIOUS_PAGE);
				
				InlineKeyboardButton previousPageButton = new ChatBotButton(previousRecord).getButtonTelegram();
				pageButton.add(previousPageButton);
			}
			
			//Se il risultato è parziale, mostro il pulsante >>
			if(partialResult) {
				
				ChatBotCallbackCommand nextRecord = new ChatBotCallbackCommand(lastLabelButton);
				nextRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>(lastLabelButton.getFilterCommandsList()));
				nextRecord.getLastChatBotCallbackFilter().setPageNumber(nextRecord.getLastChatBotCallbackFilter().getPageNumber() + 1);
				nextRecord.setNavigationCommand(ChatBotCommand.NEXT_PAGE);
	
				InlineKeyboardButton nextPageButton = new ChatBotButton(nextRecord).getButtonTelegram();
				pageButton.add(nextPageButton);
			}
			
			if(!pageButton.isEmpty()) {
				if(pageButton.size() + showInline.size() > columnNumber) {
					table.add(pageButton);
					showInline = new ArrayList<InlineKeyboardButton>();
				}else {
					showInline.addAll(pageButton);
				}
			}
		}
		
		//Bottone di conferma per salvare i filtri
		if(upDB.atLeastOneFilterSet() && !upDB.isActive()) {
			List<InlineKeyboardButton> confirmButton = new ArrayList<InlineKeyboardButton>();
			ChatBotCallbackCommand confirmRecord = new ChatBotCallbackCommand(command);
			confirmRecord.setFilterCommandsList(new ArrayList<ChatBotCallbackFilter>());
			if(command.getFilterCommandsList() != null) {
				confirmRecord.getFilterCommandsList().addAll(command.getFilterCommandsList());
			}
			confirmRecord.getLastChatBotCallbackFilter().setPageNumber(confirmRecord.getLastChatBotCallbackFilter().getPageNumber());
			confirmRecord.setNavigationCommand(ChatBotCommand.CONFIRM_CHANGE_BONUS_ABUSING);
			
			InlineKeyboardButton confirmKeyButton = new ChatBotButton(confirmRecord).getButtonTelegram();
			confirmButton.add(confirmKeyButton);
			table.add(confirmButton);
		}
		
		//Tasto per tornare al menu
		List<InlineKeyboardButton> backToMenuButton = new ArrayList<InlineKeyboardButton>();
		ChatBotCallbackCommand backToMenuRecord = new ChatBotCallbackCommand(command);
		backToMenuRecord.setRootCommand(ChatBotCommand.MENU);
		backToMenuRecord.setFilterCommandsList(null);
		backToMenuRecord.setNavigationCommand(ChatBotCommand.BACK_TO_MENU_BONUS_ABUSING);
		
		InlineKeyboardButton backToMenuKeyButton = new ChatBotButton(backToMenuRecord).getButtonTelegram();
		backToMenuButton.add(backToMenuKeyButton);
		table.add(backToMenuButton);
		
		if (!table.isEmpty()) {
			String text = "✅ Hai selezionato " +  ArbsUtil.getTelegramBoldString(command.getLastChatBotCallbackFilter().getValue()) + "."
					+ System.lineSeparator()  
					+ "Ora scegli quale altro filtro impostare oppure torna al menu principale: non perderai i dati finora salvati!";
			if(!upDB.isActive()) {		
				text = text + System.lineSeparator()
					+ "⚠️" + ArbsUtil.getTelegramBoldString("Attualmente la tua preferenza è inattiva") 
					+ " ma puoi sempre modificarla/attivarla dal menù iniziale in un altro momento";
			}
			
			if(upDB.atLeastOneFilterSet()) {
				text = text + System.lineSeparator() 
					+ "🔎I filtri attualmente impostati sono i seguenti: " + System.lineSeparator() +
					upDB.toString();
			}
			
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
