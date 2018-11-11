package it.bluesheep.telegrambot;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.database.BlueSheepDatabaseManager;
import it.bluesheep.database.ConnectionPool;
import it.bluesheep.database.dao.impl.BookmakerDAO;
import it.bluesheep.database.dao.impl.TelegramUserDAO;
import it.bluesheep.database.dao.impl.UserPreferenceDAO;
import it.bluesheep.database.dao.manager.TelegramUserDatabaseManager;
import it.bluesheep.database.entities.Bookmaker;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.database.exception.BlueSheepDatabaseException;
import it.bluesheep.database.exception.ConnectionPoolException;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.telegrambot.exception.AlreadyActiveBookmakerPreference;
import it.bluesheep.telegrambot.exception.AlreadyRegisteredUserChatBotException;
import it.bluesheep.telegrambot.exception.AskToUsException;
import it.bluesheep.telegrambot.exception.BluesheepChatBotException;
import it.bluesheep.telegrambot.exception.InactiveBookmakerRequestException;
import it.bluesheep.telegrambot.exception.NotPermittedOperationException;
import it.bluesheep.telegrambot.message.button.ChatBotButtonManager;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackCommand;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackFilter;
import it.bluesheep.telegrambot.message.util.AcceptedUserInput;
import it.bluesheep.telegrambot.message.util.ChatBotCommand;
import it.bluesheep.telegrambot.message.util.ChatBotFilterCommand;
import it.bluesheep.util.BlueSheepConstants;

public class TelegramBotHandler extends TelegramLongPollingBot {
	
	private static Logger logger = Logger.getLogger(TelegramBotHandler.class);
	private static TelegramBotHandler instance;
	private static Properties properties;
	private static Connection connection;
	
	private TelegramBotHandler() {
		super();
	}
	
	public static synchronized TelegramBotHandler getTelegramBotHandlerInstance() {
		if(instance == null) {
			instance = new TelegramBotHandler();
		}
		
		return instance;
	}
	
	@Override
	public String getBotUsername() {
		return BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.TELEGRAMBOTNAME);
	}

	@Override
	public void onUpdateReceived(Update updateContainer) {
			
    	Message receivedMessage = null;
    	TelegramUser userMessage = null; 
    	TelegramUser userDB = null;
    	TelegramUser tempUser = null;
    	Long lastSentMessageIdDB = null;
		try {
			try {
				connection = ConnectionPool.getConnectionPool().getConnection();
			} catch (ConnectionPoolException e1) {
				logger.error(e1.getMessage(), e1);
			}

			try {
				if(connection != null && !updateContainer.hasMessage() 
						|| (updateContainer.getMessage().getChatId().equals(new Long(51337759)) 
								&& updateContainer.getMessage().getFrom() != null 
								&& !updateContainer.getMessage().getFrom().getBot())) {
		        	
			        if (updateContainer.hasMessage() && updateContainer.getMessage().hasText()) {

			        	logger.info("Start user DB seach");
			        	receivedMessage = updateContainer.getMessage();
			        	userMessage = TelegramUser.getTelegramUserFromMessage(receivedMessage);
			        	tempUser = TelegramUserDAO.getBlueSheepTelegramUserDAOInstance(connection).getUserFromMessage(receivedMessage);
			        	boolean registeredClient = tempUser != null && tempUser.isActive();
			        	userDB = registeredClient ? tempUser : null;
			        	
			        	if(userDB != null && userDB.getLastMessageId() != null) {
			        		lastSentMessageIdDB = new Long(userDB.getLastMessageId().longValue());
			        	}
			        	
			        	logger.info("Received a message from " 
			        				+ userMessage.getFirstName() + " "  + userMessage.getLastName() 
			        				+ ", chat_id: " + userMessage.getChatId() 
			        				+ "; Text = " + receivedMessage.getText() 
			        				+ "; :::REGISTERED USER = " + registeredClient + ":::");
			            
			        	Set<String> possibleCommands = AcceptedUserInput.getAvailableUserInputs();
			        	
			        	if(possibleCommands.contains(receivedMessage.getText())) {
				            manageUserInput(userMessage, receivedMessage, userDB);
			            } else {
			            	SendMessage message = new SendMessage() // Create a message object object
			                        .setChatId(userMessage.getChatId())
			                        .setText("Impossibile interagire");
			            	sendStandardMessage(message, userDB);
			            }
			    		
			        	connection.commit();
	
			        } else if (updateContainer.hasCallbackQuery() && updateContainer.getCallbackQuery().getMessage().getChatId().equals(new Long(51337759))) {

			        	receivedMessage = updateContainer.getCallbackQuery().getMessage();
			        	tempUser = TelegramUserDAO.getBlueSheepTelegramUserDAOInstance(connection)
								.getUserFromUser(TelegramUser
										.getTelegramUserFromUserTelegram(updateContainer.getCallbackQuery().getFrom(), receivedMessage));
			        	boolean registeredClient = tempUser != null && tempUser.isActive();
			        	userDB = registeredClient ? tempUser : null;
			        	
			        	if(userDB != null && userDB.getLastMessageId() != null) {
			        		lastSentMessageIdDB = new Long(userDB.getLastMessageId().longValue());
			        	}
			        	
			        	if(userDB != null) {
			        		logger.info("Received a message from " + userDB.getFirstName() + " " + userDB.getLastName() + ", chat_id: " + userDB.getChatId() + "; Text = " + updateContainer.getCallbackQuery().getData());
			        	
			        		manageResponse(receivedMessage, userDB, updateContainer.getCallbackQuery());
			        	}else {
			        		sendErrorMessage(new AskToUsException(TelegramUser.getTelegramUserFromMessage(receivedMessage)), userDB);
			        	}
			    		connection.commit();
			        }
				}
			}catch (AlreadyRegisteredUserChatBotException e) {
				logger.error("This exception should not exist!!!");
				connection.rollback();
			} catch (AskToUsException e) {
        		sendErrorMessage(e, userDB);
				logger.error(e.getMessage(), e);
        		connection.rollback();
			} catch (MoreThanOneResultException e) {
				connection.rollback();
				logger.error(e.getMessage(), e);
				sendErrorMessage(new AskToUsException(TelegramUser
						.getTelegramUserFromUserTelegram(updateContainer.getCallbackQuery().getFrom(), receivedMessage)), userDB);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			try {
				connection = ConnectionPool.getConnectionPool().getConnection();
			} catch (ConnectionPoolException e1) {
				logger.error(e1.getMessage(), e1);
			}
			logger.warn("Database connection status is " + BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().getStatus());
		}
		
		if(tempUser != null && tempUser.getLastMessageId() != null && lastSentMessageIdDB != null
				&& tempUser.getLastMessageId().longValue() > lastSentMessageIdDB.longValue()) {
			executeDeleteMessage(lastSentMessageIdDB, tempUser);
		}
		
		try {
			ConnectionPool.getConnectionPool().releaseConnection(connection);
		} catch (ConnectionPoolException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void executeDeleteMessage(Long messageId, TelegramUser userDB) {
		DeleteMessage deleteMessage = new DeleteMessage().setChatId(userDB.getChatId()).setMessageId(messageId.intValue());
		try {
			execute(deleteMessage);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void manageResponse(Message receivedMessage, TelegramUser userMessage, CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
		try { 
	        ChatBotCallbackCommand command = ChatBotCallbackCommand.getChatBotCallbackCommandFromCallbackData(callbackData);
	        
			if(command != null) {
	        	if(command.getFilterCommandsList() == null) {
	        		//TODO: qui vanno tutte le possibili selezioni (radice degli alberi delle scelte)
	        		if (ChatBotCommand.ADD_PREFERENCE_BONUS_ABUSING.equals(command.getRootCommand())) {
	        			List<Bookmaker> bookmakerAvailable = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getAllActiveBookmakerOrderedByName();
		        		showAddPreferencesMenu(command, userMessage, receivedMessage, bookmakerAvailable, 0);
		        	}else {
		        		
		        	}
		        }else if(command.getFilterCommandsList().size() > 0){
		        	
		        	//Qui vanno tutte analizzate tutte le possibili scelte fatte da una delle radici degli alberi delle scelte
		        	
		        	
		        	//Indietro di uno step
		        	if(ChatBotCommand.BACK_TO_KEYBOARD.equals(command.getNavigationCommand())) {
		        		command.setNavigationCommand(null);
	        	        command.setFilterCommandsList(null);
		        		List<Bookmaker> bookmakerAvailable = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getAllActiveBookmakerOrderedByName();
		        		showAddPreferencesMenu(command, userMessage, receivedMessage, bookmakerAvailable, 0);
		        	}
		        	
		        	
		        	//Pagina precedente
		        	else if(ChatBotCommand.PREVIOUS_PAGE.equals(command.getNavigationCommand())) {
	        	        ChatBotCallbackFilter filter = command.getLastChatBotCallbackFilter();
		        		String initialChar = filter.getValue();
		        		command.setNavigationCommand(null);
		        		command.setFilterCommandsList(null);
		        		
		        		List<Bookmaker> bookmakerPage = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getBookmakerPageByInitialChar(initialChar.substring(0, 1), false);
		        		if(bookmakerPage == null) {
		        			bookmakerPage = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getAllActiveBookmakerOrderedByName();
		        		}
		        		Collections.reverse(bookmakerPage);
		        		showAddPreferencesMenu(command, userMessage, receivedMessage, bookmakerPage, filter.getPageNumber() - 1);
		        	}
		        	
		        	//Pagina successiva
		        	else if(ChatBotCommand.NEXT_PAGE.equals(command.getNavigationCommand())) {
	        	        ChatBotCallbackFilter filter = command.getLastChatBotCallbackFilter();
		        		String initialChar = filter.getValue();
		        		command.setNavigationCommand(null);
		        		command.setFilterCommandsList(null);
		        		
		        		List<Bookmaker> bookmakerPage = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getBookmakerPageByInitialChar(initialChar.substring(0, 1), true);
		        		if(bookmakerPage == null) {
		        			bookmakerPage = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getAllActiveBookmakerOrderedByName();
		        		}
		        		showAddPreferencesMenu(command, userMessage, receivedMessage, bookmakerPage, filter.getPageNumber() + 1);
		        	}
		        	
		        	
		        	
		        	else if(ChatBotFilterCommand.BOOKMAKER_BONUS_ABUSING.equals(command.getLastChatBotCallbackFilter().getFilter())) {
		        	
			        	//Controllo operazione permmessa
			    		Set<ChatBotCommand> availableChatBotCommandForUser = TelegramUserDatabaseManager.getAvailableActionForUser(userMessage, connection);
			    		
			    		if(availableChatBotCommandForUser.contains(command.getRootCommand())){
				            String bookmakerName = command.getLastChatBotCallbackFilter().getValue();
				            
				            //Controllo congiunto bookmaker e utente-preferenza
				            List<Bookmaker> bookmakerList = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getLikeBookmakerNameByInitalChar(bookmakerName);
				            
				            if(bookmakerList != null && bookmakerList.size() == 1 && command.getLastChatBotCallbackFilter().isIdFilter()) {
				            	Bookmaker bookmaker = bookmakerList.get(0);
					            //Prendo tutte le preferenze dell'utente per assicurarmi che il limite sia rispettato 
					            //e che non stia cercando di aggiungere preferenze oltre il limite concesso
					            List<UserPreference> userPreferenceBookmaker = UserPreferenceDAO.getUserPreferenceDAOInstance(connection).getUserPreferenceFromUser(userMessage);
					            
					            if(bookmaker != null) {
					            	//Controllo se esiste già una preferenza su quel bookmaker
					            	if(userPreferenceBookmaker != null) {
					            		boolean alreadyPresentBookmaker = false;
					            		for(UserPreference up : userPreferenceBookmaker) {
					            			alreadyPresentBookmaker = alreadyPresentBookmaker || up.getBookmaker().sameRecord(bookmaker);
					            		}
					            		
					            		if(!alreadyPresentBookmaker) {
					            			//Insert della nuova preferenza
					            			UserPreferenceDAO.getUserPreferenceDAOInstance(connection).insertRow(UserPreference.getBlueSheepUserPreferenceFromUserInfo(bookmaker, userMessage, null, null, null, null, null, null, false));
					            			
					            			//Mostro i filtri impostabili
					            			UserPreference newUP_DB = UserPreferenceDAO.getUserPreferenceDAOInstance(connection).getUserPreferenceFromUserAndBookmaker(userMessage, bookmaker);
					            			goToFilterPreferences(bookmaker, userMessage, receivedMessage, newUP_DB, command);
					            		}else {
							    			SendMessage message = getMenuMessageSendMessage(new AlreadyActiveBookmakerPreference(userMessage, receivedMessage, bookmaker), userMessage, callbackQuery.getMessage(), 
							    					"Accedi alla sezione di 'Modifica preferenze di notifica' per modificare le segnalazioni su " 
							    							+ ArbsUtil.getTelegramBoldString(bookmakerName) + " o scegli un altro comando");
								            if(message != null) {
								            	sendStandardMessage(message, userMessage);
								            }else {
								            	logger.warn("No command available to reply. No message is sent");
								            }
					            		}
					            	}
					            }
				            }else if(bookmakerList != null && !bookmakerList.isEmpty()) {
				            	
				            	//Mostra la stessa paginazione di prima, però con i valori 
				        		showAddPreferencesMenu(command, userMessage, receivedMessage, bookmakerList, 0);
				            }else {
				    			SendMessage message = getMenuMessageSendMessage(new InactiveBookmakerRequestException(userMessage), userMessage, callbackQuery.getMessage(), "Ecco le operazioni che puoi eseguire");
					            if(message != null) {
					            	sendStandardMessage(message, userMessage);
					            }else {
					            	logger.warn("No command available to reply. No message is sent");
					            }
				            }
			    		}else {
			    			SendMessage message = getMenuMessageSendMessage(new NotPermittedOperationException(userMessage, callbackQuery.getMessage()), userMessage, receivedMessage, "Ecco le operazioni che puoi eseguire");
				            if(message != null) {
				            	sendStandardMessage(message, userMessage);
				            }else {
				            	logger.warn("No command available to reply. No message is sent");
				            }
			    		}
			        }else if(ChatBotFilterCommand.getAllAddFilters().contains(command.getLastChatBotCallbackFilter().getFilter())) {
			        	//Mostra in base alla tipologia di filtro, i valori disponibili
			        	showAvailableFilterValues(command);
			        }
				}
			}else {
				throw new AskToUsException(userMessage);
			}
		}catch(BluesheepChatBotException e) {
			sendErrorMessage(e, userMessage);
    		logger.info("Sending error message to user " + e.getUser().toString(), e);
		}catch(BlueSheepDatabaseException e) {
			sendErrorMessage(new NotPermittedOperationException(userMessage, receivedMessage), userMessage);
		}catch(Exception e) {
			sendErrorMessage(new AskToUsException(userMessage), userMessage);
			logger.error(e.getMessage(), e);
		}
	}

	private void showAvailableFilterValues(ChatBotCallbackCommand command) {
		
//		AbstractDAO<? extends AbstractBlueSheepEntity> dao = ChatBotFilterCommandUtilManager.getCorrectDAOByChatBotCallackCommand(command, connection);
		
		
		
		
	}

	private void showAddPreferencesMenu(ChatBotCallbackCommand command, TelegramUser userMessage, Message receivedMessage, List<Bookmaker> bookmakerAvailable, int pageIndex) {
        EditMessageText new_message = null;
        
        List<List<InlineKeyboardButton>> listOfCommandButtonOneColumn = ChatBotButtonManager.getBookmakerButtonListNColumns(command, bookmakerAvailable, 2, pageIndex);
        
		if (!listOfCommandButtonOneColumn.isEmpty()) {
			InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
			new_message = new EditMessageText() // Create a message object object
					.setChatId(receivedMessage.getChatId())
					.setMessageId(receivedMessage.getMessageId())
					.setText("Seleziona il boomaker su cui vuoi impostare una notifica");
			// Add it to the message
			markupInline.setKeyboard(listOfCommandButtonOneColumn);
			new_message.setReplyMarkup(markupInline);
			
            sendStandardMessage(new_message, userMessage);
		}
		
	}

	private SendMessage getMenuMessageSendMessage(BluesheepChatBotException exception,
			TelegramUser userMessage, Message message, String text) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		return getMenuMessageSendMessage(userMessage, message, exception.getMessage() + System.lineSeparator() + text);
	}

	private void goToFilterPreferences(Bookmaker bookmaker, TelegramUser userMessage, 
			Message receivedMessage, UserPreference newUP_DB, ChatBotCallbackCommand command) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		
		//Crea il messaggio con i filtri
		EditMessageText message = getSelectionMenuMessageEditedText(userMessage, receivedMessage, newUP_DB, command);
		
		if(message != null) {
			sendStandardMessage(message, userMessage);
		}
	}

	private void manageUserInput(TelegramUser userMessage, Message receivedMessage, TelegramUser userDB) {
		try {
			if (userDB != null && AcceptedUserInput.MENU.getInputUser().equals(receivedMessage.getText())) {
	            
				SendMessage message = getMenuMessageSendMessage(userDB, receivedMessage, null);
				if(message != null) {
	            	sendStandardMessage(message, userDB);
	            }else {
	            	throw new NotPermittedOperationException(userMessage, receivedMessage);
	            }
	        } 
			
			else if((AcceptedUserInput.REGISTRATION.getInputUser().equals(receivedMessage.getText()) || 
					AcceptedUserInput.START.getInputUser().equals(receivedMessage.getText())) && 
					!isBlockedUser(userMessage)){
				if(userDB == null) {
		    		try {
		    			userDB = TelegramUserDatabaseManager.addUserArbsMap(userMessage, connection);
					} catch (SQLException | MoreThanOneResultException e) {
						throw new AskToUsException(userMessage);
					}
		
		    		logger.info("Registration completed for user " + userMessage.getFirstName() + " " + userMessage.getLastName() + ", CHAT_ID=" + userMessage.getChatId());
		    		
		    		SendMessage message = getMenuMessageSendMessage(userMessage, receivedMessage, null);
		            if(message != null) {
		            	sendStandardMessage(message, userDB);
		            }else {
		            	logger.warn("No command available to reply. No message is sent");
		            }
				}else {
					throw new AlreadyRegisteredUserChatBotException(userDB);
				}
			}
    	}catch(BluesheepChatBotException e) {
    		sendErrorMessage(e, userDB);
    		logger.info("Sending error message to user " + e.getUser().toString(), e);
    	} catch (SQLException e) {
    		logger.error(e.getMessage(), e);
    		sendErrorMessage(new AskToUsException(userMessage), userDB);
    	}
	}

	private boolean isBlockedUser(TelegramUser user) {
		return false;
	}

	@Override
	public String getBotToken() {
		return BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.TELEGRAMBOTKEY);
	}
	
	public void stopExecution() {
		try {
			connection.rollback();
			connection.close();
		} catch (SQLException e1) {
			logger.error(e1.getMessage(), e1);
		}
		
		logger.info("Trying to stop TelegramBotChat handler for shutdown request");
		super.exe.shutdown();
		try {
			logger.info("Waiting for 10 seconds for telegram handler shutdown");
			boolean correctlyTerminated = super.exe.awaitTermination(10, TimeUnit.SECONDS);
			if(!correctlyTerminated) {
				logger.warn("Telegram handler executor shutdown failed. Executor is forced to shutdown");
				super.exe.shutdownNow();
			}else {
				logger.info("Telegram handler executor shutdown completed successfully");
			}
			
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void sendErrorMessage(BluesheepChatBotException error, TelegramUser userDB) {
		
		Message returnMessage = null;
		
		SendMessage message = new SendMessage(error.getUser().getChatId(), error.getMessage()).setParseMode("Markdown");
		try {
			returnMessage = execute(message);
			if(returnMessage != null && userDB != null) {
				logger.info("Sending message to userID " + userDB.getId() + " from bot with id " + returnMessage.getMessageId());
				updateLastMessageIdUser(returnMessage, userDB);
			}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void sendStandardMessage(SendMessage message, TelegramUser userDB) {
		Message returnMessage = null;
		try {
			message.setParseMode("Markdown");
			returnMessage = execute(message);
        	
			if(returnMessage != null && userDB != null) {
				logger.info("Sending message to userID " + userDB.getId() + " from bot with id " + returnMessage.getMessageId());
        		updateLastMessageIdUser(returnMessage, userDB);
        	}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void sendStandardMessage(EditMessageText message, TelegramUser userDB) {
		try {
			message.setParseMode("Markdown");
			if(execute(message) != null) {
				logger.info("Sending message to userID " + userDB.getId() + " from bot with id " + message.getMessageId());
				updateLastMessageIdUser(message, userDB);
			}
			
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}	
	
	private EditMessageText getSelectionMenuMessageEditedText(TelegramUser userMessage, Message receivedMessage, UserPreference newUP_DB, ChatBotCallbackCommand command) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		EditMessageText message = null;
		if(command != null && command.getFilterCommandsList() != null) {
			
			//Menu di ogni possibile scelta dell'albero
			message = ChatBotButtonManager.getAvailableFilterListButton(newUP_DB, command, 3, 2, receivedMessage, userMessage, command.getLastChatBotCallbackFilter().getPageNumber());
		}
		
		
		return message;
	}
	
	private SendMessage getMenuMessageSendMessage(TelegramUser userMessage, Message receivedMessage, String text) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		SendMessage message = null;

		Set<ChatBotCommand> availableChatBotCommandForUser = TelegramUserDatabaseManager.getAvailableActionForUser(userMessage, connection);
		
		if (availableChatBotCommandForUser != null) {
			
			InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
			List<List<InlineKeyboardButton>> listOfCommandButtonOneColumn = ChatBotButtonManager.getOneColumnCommandsAvailable(availableChatBotCommandForUser);

			if (!listOfCommandButtonOneColumn.isEmpty()) {
				message = new SendMessage() // Create a message object object
						.setChatId(receivedMessage.getChatId());
				if(text == null) {
					message.setText("Ciao " + userMessage.getFirstName() + " " + userMessage.getLastName()
								+ ", benvenuto! Io sono BSBot." + System.lineSeparator()
								+ "Clicca sull'operazione che ti interessa eseguire");
				}else {
					message.setText(text);
				}
				// Add it to the message
				markupInline.setKeyboard(listOfCommandButtonOneColumn);
				message.setReplyMarkup(markupInline);
			}
		}else {
			throw new AskToUsException(userMessage);
		}
		return message;
	}

	public static Properties getProperties() {
		return properties;
	}
	
	public static void initializeProperties() {
		
	}
	
	private void updateLastMessageIdUser(Message lastMessageSent, TelegramUser userDB) {
		userDB.setLastMessageId(new Long(lastMessageSent.getMessageId()));
		TelegramUserDAO.getBlueSheepTelegramUserDAOInstance(connection).updateLastMessageSent(userDB);
	}
	
	private void updateLastMessageIdUser(EditMessageText lastMessageSent, TelegramUser userDB) {
		userDB.setLastMessageId(new Long(lastMessageSent.getMessageId()));
		TelegramUserDAO.getBlueSheepTelegramUserDAOInstance(connection).updateLastMessageSent(userDB);
	}

}
