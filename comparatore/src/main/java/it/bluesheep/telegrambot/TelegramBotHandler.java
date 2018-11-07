package it.bluesheep.telegrambot;

import java.sql.Connection;
import java.sql.SQLException;
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
import it.bluesheep.database.dao.impl.BookmakerDAO;
import it.bluesheep.database.dao.impl.TelegramUserDAO;
import it.bluesheep.database.dao.impl.UserPreferenceDAO;
import it.bluesheep.database.dao.manager.TelegramUserDatabaseManager;
import it.bluesheep.database.entities.Bookmaker;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.database.exception.BlueSheepDatabaseException;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.telegrambot.button.CallbackDataType;
import it.bluesheep.telegrambot.button.ChatBotButtonManager;
import it.bluesheep.telegrambot.exception.AlreadyActiveBookmakerPreference;
import it.bluesheep.telegrambot.exception.AlreadyRegisteredUserChatBotException;
import it.bluesheep.telegrambot.exception.AskToUsException;
import it.bluesheep.telegrambot.exception.BluesheepChatBotException;
import it.bluesheep.telegrambot.exception.InactiveBookmakerRequestException;
import it.bluesheep.telegrambot.exception.NotPermittedOperationException;
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
		try {
			connection = BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().connectToDatabase();

			try {
				if(connection != null && !updateContainer.hasMessage() || (updateContainer.getMessage().getChatId().equals(new Long(51337759)) && updateContainer.getMessage().getFrom() != null && !updateContainer.getMessage().getFrom().getBot())) {
		        	
			        if (updateContainer.hasMessage() && updateContainer.getMessage().hasText()) {
			        	receivedMessage = updateContainer.getMessage();
			        	userMessage = TelegramUser.getTelegramUserFromMessage(receivedMessage);
			        	tempUser = TelegramUserDAO.getBlueSheepTelegramUserDAOInstance(connection).getUserFromMessage(receivedMessage);
			        	boolean registeredClient = tempUser != null && tempUser.isActive();
			        	userDB = registeredClient ? tempUser : null;
			        	logger.info("Received a message from " 
			        				+ userMessage.getFirstName() + " " 
			        			+ userMessage.getLastName() 
			        			+ ", chat_id: " 
			        				+ userMessage.getChatId() 
			        				+ "; Text = " 
			        			+ receivedMessage.getText() 
			        			+ "; :::REGISTERED USER = " 
			        			+ registeredClient + ":::");
			            
			        	Set<String> possibleCommands = AcceptedUserInput.getAvailableUserInputs();
			        	
			        	if(possibleCommands.contains(receivedMessage.getText())) {
				            manageUserInput(userMessage, receivedMessage, userDB);
			            } else {
			            	SendMessage message = new SendMessage() // Create a message object object
			                        .setChatId(userMessage.getChatId())
			                        .setText("Impossibile interagire");
			            	Message returnMessage = sendStandardMessage(message);
			            	if(returnMessage != null && userDB != null) {
			            		updateLastMessageIdUser(returnMessage, userDB);
			            	}
			            }
			    		
			        	connection.commit();
	
			        } else if (updateContainer.hasCallbackQuery() && updateContainer.getCallbackQuery().getMessage().getChatId().equals(new Long(51337759))) {
			        	receivedMessage = updateContainer.getCallbackQuery().getMessage();
			        	tempUser = TelegramUserDAO.getBlueSheepTelegramUserDAOInstance(connection)
								.getUserFromUser(TelegramUser
										.getTelegramUserFromUserTelegram(updateContainer.getCallbackQuery().getFrom(), receivedMessage));
			        	boolean registeredClient = tempUser != null && tempUser.isActive();
			        	userDB = registeredClient ? tempUser : null;
			        	
			        	if(userDB != null) {
			        		logger.info("Received a message from " + userDB.getFirstName() + " " + userDB.getLastName() + ", chat_id: " + userDB.getChatId() + "; Text = " + updateContainer.getCallbackQuery().getData());
			        	
			        		manageResponse(receivedMessage, userDB, updateContainer.getCallbackQuery().getData(), updateContainer.getCallbackQuery());
			        	}else {
			        		sendErrorMessage(new AskToUsException(TelegramUser.getTelegramUserFromMessage(receivedMessage)));
			        	}
			        	
			    		connection.commit();
			        }
				}
			}catch (AlreadyRegisteredUserChatBotException e) {
				logger.error("This exception should not exist!!!");
				connection.rollback();
			} catch (AskToUsException e) {
        		sendErrorMessage(e);
				logger.error(e.getMessage(), e);
        		connection.rollback();
			} catch (MoreThanOneResultException e) {
				connection.rollback();
				logger.error(e.getMessage(), e);
				sendErrorMessage(new AskToUsException(TelegramUser
						.getTelegramUserFromUserTelegram(updateContainer.getCallbackQuery().getFrom(), receivedMessage)));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			connection = BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().connectToDatabase();
			logger.warn("Database connection status is " + BlueSheepDatabaseManager.getBlueSheepDatabaseManagerInstance().getStatus());
		}
		
		if(tempUser != null && tempUser.getLastMessageId() != null && tempUser.getLastMessageId().longValue() != receivedMessage.getMessageId().longValue()) {
			executeDeleteMessage(tempUser.getLastMessageId(), receivedMessage);
		}
	}
	
	private void executeDeleteMessage(Long long1, Message receivedMessage) {
		DeleteMessage deleteMessage = new DeleteMessage().setChatId(receivedMessage.getChatId()).setMessageId(long1.intValue());
		try {
			execute(deleteMessage);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void manageResponse(Message receivedMessage, TelegramUser userMessage, String callbackData, CallbackQuery callbackQuery) {
        EditMessageText new_message = null;
		try {
			//TODO: qui vanno tutte le possibili selezioni (radice dell'albero delle scelte)
	        if (CallbackDataType.ADD_PREF.getCode().equals(callbackData)) {
	            List<Bookmaker> bookmakerAvailable = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getAllActiveBookmaker();
	            
	            List<List<InlineKeyboardButton>> listOfCommandButtonOneColumn = ChatBotButtonManager.getBookmakerButtonListTwoColumns(bookmakerAvailable);
	            
				if (!listOfCommandButtonOneColumn.isEmpty()) {
					InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
					new_message = new EditMessageText() // Create a message object object
							.setChatId(receivedMessage.getChatId())
							.setMessageId(receivedMessage.getMessageId())
							.setText("Seleziona il boomaker su cui vuoi impostare una notifica");
					// Add it to the message
					markupInline.setKeyboard(listOfCommandButtonOneColumn);
					new_message.setReplyMarkup(markupInline);
				}
	        }else if(CallbackDataType.BOOKMAKER.getCode().equals(callbackData.split(BlueSheepConstants.REGEX_CSV)[0])) {
	        	
	        	//Controllo operazione permmessa
	        	String callbackDataType = callbackData.split(BlueSheepConstants.REGEX_CSV)[0];
	    		Set<ChatBotCommand> availableChatBotCommandForUser = TelegramUserDatabaseManager.getAvailableActionForUser(userMessage, connection);
	    		
	    		if(availableChatBotCommandForUser.contains(ChatBotCommandUtilManager.getRootCommand(CallbackDataType.valueOf(callbackDataType)))){
		        	String[] splittedCallbackBookmaker = callbackData.split(BlueSheepConstants.REGEX_CSV);
		            String bookmakerName = splittedCallbackBookmaker[1];
		            
		            //Controllo congiunto bookmaker e utente-preferenza
		            Bookmaker bookmaker = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getActiveBookmakerFromBookmakerName(bookmakerName);
		            List<UserPreference> userPreferenceBookmaker = UserPreferenceDAO.getUserPreferenceDAOInstance(connection).getUserPreferenceFromUser(userMessage);
		            
		            if(bookmaker != null) {
		            	if(userPreferenceBookmaker != null) {
		            		boolean alreadyPresentBookmaker = false;
		            		for(UserPreference up : userPreferenceBookmaker) {
		            			if(up.getBookmaker().sameRecord(bookmaker)) {
		            				alreadyPresentBookmaker = true;
		            			}
		            		}
		            		
		            		if(!alreadyPresentBookmaker) {
		            			UserPreferenceDAO.getUserPreferenceDAOInstance(connection).insertRow(UserPreference.getBlueSheepUserPreferenceFromUserInfo(bookmaker, userMessage, null, null, null, null, null, null, false));
		            			userPreferenceBookmaker = UserPreferenceDAO.getUserPreferenceDAOInstance(connection).getUserPreferenceFromUser(userMessage);
		            			goToFilterPreferences(bookmakerName, userMessage, receivedMessage, callbackQuery, CallbackDataType.BOOKMAKER, userPreferenceBookmaker);
		            		}else {
				    			SendMessage message = getMenuMessageSendMessage(new AlreadyActiveBookmakerPreference(userMessage, callbackQuery.getMessage(), bookmaker), userMessage, callbackQuery.getMessage(), 
				    					"Accedi alla sezione di 'Modifica preferenze di notifica' per modificare le segnalazioni su " 
				    							+ ArbsUtil.getTelegramBoldString(bookmakerName) + " o scegli un altro comando");
					            if(message != null) {
					            	Message returnMessage = sendStandardMessage(message);
					            	if(returnMessage != null) {
					            		updateLastMessageIdUser(returnMessage, userMessage);
					            	}
					            }else {
					            	logger.warn("No command available to reply. No message is sent");
					            }
		            		}
		            	}
		            }else {
		    			SendMessage message = getMenuMessageSendMessage(new InactiveBookmakerRequestException(userMessage), userMessage, callbackQuery.getMessage(), "Ecco le operazioni che puoi eseguire");
			            if(message != null) {
			            	sendStandardMessage(message);
			            }else {
			            	logger.warn("No command available to reply. No message is sent");
			            }
		            }
	    		}else {
	    			backToMenuWithError(new NotPermittedOperationException(userMessage, callbackQuery.getMessage()));
	    			SendMessage message = getMenuMessageSendMessage(userMessage, receivedMessage, "Ecco le operazioni che puoi eseguire");
		            if(message != null) {
		            	sendStandardMessage(message);
		            }else {
		            	logger.warn("No command available to reply. No message is sent");
		            }
	    		}
	        }
		}catch(BluesheepChatBotException e) {
			Message message = sendErrorMessage(e);
			if(message != null) {
				updateLastMessageIdUser(message, userMessage);
			}
    		logger.info("Sending error message to user " + e.getUser().toString(), e);
		}catch(BlueSheepDatabaseException e) {
			Message message = sendErrorMessage(new NotPermittedOperationException(userMessage, receivedMessage));
			if(message != null) {
				updateLastMessageIdUser(message, userMessage);
			}
			logger.error(e.getMessage(), e);
		}catch(Exception e) {
			Message message = sendErrorMessage(new AskToUsException(userMessage));
			if(message != null) {
				updateLastMessageIdUser(message, userMessage);
			}
			logger.error(e.getMessage(), e);
		}
	
        if(new_message != null) {
            sendStandardMessage(new_message);
        }
	}

	private SendMessage getMenuMessageSendMessage(BluesheepChatBotException exception,
			TelegramUser userMessage, Message message, String text) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		return getMenuMessageSendMessage(userMessage, message, exception.getMessage() + System.lineSeparator() + text);
	}

	private void goToFilterPreferences(String bookmakerName, TelegramUser userMessage, Message receivedMessage, CallbackQuery callbackQuery, CallbackDataType callbackDataType, List<UserPreference> userPreferenceBookmaker) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		EditMessageText message = getSelectionMenuMessageEditedText(userMessage, receivedMessage, callbackQuery, callbackDataType, userPreferenceBookmaker);
		if(message != null) {
			sendStandardMessage(message);
		}
	}

	private void manageUserInput(TelegramUser userMessage, Message receivedMessage, TelegramUser userDB) {
		try {
			if (userDB != null && AcceptedUserInput.MENU.getInputUser().equals(receivedMessage.getText())) {
	            
				SendMessage message = getMenuMessageSendMessage(userDB, receivedMessage, null);
				if(message != null) {
	            	Message returnMessage = sendStandardMessage(message);
	            	if(returnMessage != null) {
	            		updateLastMessageIdUser(returnMessage, userDB);
	            	}
	            }else {
	            	throw new NotPermittedOperationException(userMessage, receivedMessage);
	            }
	        } 
			
			else if((AcceptedUserInput.REGISTRATION.getInputUser().equals(receivedMessage.getText()) || 
					AcceptedUserInput.START.getInputUser().equals(receivedMessage.getText())) && 
					!isBlockedUser(userMessage)){
				if(userDB == null) {
		    		try {
						TelegramUserDatabaseManager.addUserArbsMap(userMessage, connection);
					} catch (SQLException | MoreThanOneResultException e) {
						throw new AskToUsException(userMessage);
					}
		
		    		logger.info("Registration completed for user " + userMessage.getFirstName() + " " + userMessage.getLastName() + ", CHAT_ID=" + userMessage.getChatId());
		    		
		    		SendMessage message = getMenuMessageSendMessage(userMessage, receivedMessage, null);
		            if(message != null) {
		            	sendStandardMessage(message);
		            }else {
		            	logger.warn("No command available to reply. No message is sent");
		            }
				}else {
					throw new AlreadyRegisteredUserChatBotException(userDB);
				}
			}
    	}catch(BluesheepChatBotException e) {
    		Message message = sendErrorMessage(e);
			if(message != null && userDB != null) {
				updateLastMessageIdUser(message, userDB);
			}
    		logger.info("Sending error message to user " + e.getUser().toString(), e);
    	} catch (SQLException e) {
    		logger.error(e.getMessage(), e);
    		Message message = sendErrorMessage(new AskToUsException(userMessage));
    		if(message != null && userDB != null) {
				updateLastMessageIdUser(message, userDB);
			}
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
	
	public Message sendErrorMessage(BluesheepChatBotException error) {
		
		Message returnMessage = null;
		
		SendMessage message = new SendMessage(error.getUser().getChatId(), error.getMessage()).setParseMode("Markdown");
		try {
			returnMessage = execute(message);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return returnMessage;
	}
	
	public void backToMenuWithError(BluesheepChatBotException error) {
		EditMessageText message = new EditMessageText().setMessageId(error.getMarkupOriginMessage().getMessageId())
													   .setParseMode("Markdown")
													   .setText(error.getMessage())
													   .setChatId(error.getUser().getChatId());
		try {
			execute(message);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void sendStandardMessage(String text, String chatId) {
		SendMessage message = new SendMessage(chatId, text).setParseMode("Markdown");
		try {
			execute(message);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public Message sendStandardMessage(SendMessage message) {
		Message returnMessage = null;
		try {
			message.setParseMode("Markdown");
			returnMessage = execute(message);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return returnMessage;
	}
	
	public void sendStandardMessage(EditMessageText message) {
		try {
			message.setParseMode("Markdown");
			execute(message);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}	
	
	private EditMessageText getSelectionMenuMessageEditedText(TelegramUser userMessage, Message receivedMessage, CallbackQuery callbackQuery, CallbackDataType callbackData, List<UserPreference> userPreferenceBookmaker) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		EditMessageText message = null;
		if(callbackData != null) {
			if(!CallbackDataType.getCommandCallbackDataType().contains(callbackData)) {
				//Menu di ogni possibile scelta dell'albero
				Set<ChatBotCommand> availableChatBotCommandForUser = TelegramUserDatabaseManager.getAvailableActionForUser(userMessage, connection);
				
				if (availableChatBotCommandForUser != null) {
					
					InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
					List<List<InlineKeyboardButton>> listOfCommandButtonOneColumn = ChatBotButtonManager.getOneColumnCommandsAvailable(availableChatBotCommandForUser);
		
					if (!listOfCommandButtonOneColumn.isEmpty()) {
						String text = "Hai selezionato " +  callbackQuery.getData().split(BlueSheepConstants.REGEX_CSV)[1];
						message = new EditMessageText() // Create a message object object
								.setChatId(receivedMessage.getChatId())
								.setText(text)
								.setMessageId(receivedMessage.getMessageId());
						// Add it to the message
						markupInline.setKeyboard(listOfCommandButtonOneColumn);
						message.setReplyMarkup(markupInline);
					}
				}else {
					throw new AskToUsException(userMessage);
				}
			}
		}else {			
			
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
		TelegramUserDAO.getBlueSheepTelegramUserDAOInstance(connection).updateLastMessageSent(lastMessageSent, userDB);
	}

}
