package it.bluesheep.telegrambot;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
import it.bluesheep.database.ConnectionPool;
import it.bluesheep.database.dao.impl.AbstractDAO;
import it.bluesheep.database.dao.impl.BookmakerDAO;
import it.bluesheep.database.dao.impl.TelegramUserDAO;
import it.bluesheep.database.dao.impl.UserPreferenceDAO;
import it.bluesheep.database.dao.manager.TelegramUserDatabaseManager;
import it.bluesheep.database.entities.AbstractBlueSheepEntity;
import it.bluesheep.database.entities.Bookmaker;
import it.bluesheep.database.entities.TelegramUser;
import it.bluesheep.database.entities.UserPreference;
import it.bluesheep.database.exception.BlueSheepDatabaseException;
import it.bluesheep.database.exception.MoreThanOneResultException;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.telegrambot.exception.AlreadyActiveBookmakerPreference;
import it.bluesheep.telegrambot.exception.AlreadyRegisteredUserChatBotException;
import it.bluesheep.telegrambot.exception.AskToUsException;
import it.bluesheep.telegrambot.exception.BluesheepChatBotException;
import it.bluesheep.telegrambot.exception.InactiveBookmakerRequestException;
import it.bluesheep.telegrambot.exception.NoUserNameSet;
import it.bluesheep.telegrambot.exception.NotPermittedOperationException;
import it.bluesheep.telegrambot.message.button.ChatBotButtonManager;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackCommand;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackFilter;
import it.bluesheep.telegrambot.message.io.ChatBotCallbackFilterFactory;
import it.bluesheep.telegrambot.message.util.AcceptedUserInput;
import it.bluesheep.telegrambot.message.util.ChatBotCommand;
import it.bluesheep.telegrambot.message.util.ChatBotCommandUtilManager;
import it.bluesheep.telegrambot.message.util.ChatBotFilterCommand;
import it.bluesheep.telegrambot.message.util.ChatBotFilterCommandUtilManager;
import it.bluesheep.telegrambot.message.util.TextFilterCommand;
import it.bluesheep.telegrambot.message.util.UserPreferenceFilterUtil;
import it.bluesheep.util.BlueSheepConstants;

public class TelegramBotHandler extends TelegramLongPollingBot {
	
	private static Logger logger = Logger.getLogger(TelegramBotHandler.class);
	private static TelegramBotHandler instance;
	private Connection connection;
	private static final int maxRow = 3;
	
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
				connection = ConnectionPool.getConnection();
			try {
				
				if(updateContainer.hasMessage() 
						&& updateContainer.getMessage().getFrom() != null 
						&& updateContainer.getMessage().getFrom().getUserName() == null) {
					throw new NoUserNameSet(TelegramUser.getTelegramUserFromMessage(updateContainer.getMessage()));
				}
				
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
		        				+ userMessage.getUserName() 
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
		        	
		        } else if (updateContainer.hasCallbackQuery() ) {

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
		        		logger.info("Received a message from " + userDB.getUserName() + ", chat_id: " + userDB.getChatId() + "; Text = " + updateContainer.getCallbackQuery().getData());
		        	
		        		manageResponse(receivedMessage, userDB, updateContainer.getCallbackQuery());
		        	}else {
		        		sendErrorMessage(new AskToUsException(TelegramUser.getTelegramUserFromMessage(receivedMessage)), userDB);
		        	}
		        	
		    		connection.commit();
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
			} catch (NoUserNameSet e) {
				sendErrorMessage(e, e.getUser());
				logger.warn(e.getMessage(), e);
				connection.rollback();
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		
		if(tempUser != null && tempUser.getLastMessageId() != null && lastSentMessageIdDB != null
				&& tempUser.getLastMessageId().longValue() > lastSentMessageIdDB.longValue()) {
			executeDeleteMessage(lastSentMessageIdDB, tempUser);
		}
		
	}

	private void manageResponse(Message receivedMessage, TelegramUser userMessage, CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
		try { 
	        ChatBotCallbackCommand command = ChatBotCallbackCommand.getChatBotCallbackCommandFromCallbackData(callbackData);
	        
			if(command != null) {
	        	if(command.getFilterCommandsList() == null) {
	        		if(ChatBotCommand.MENU.equals(command.getRootCommand())) {
	        			SendMessage message = getMenuMessageSendMessage(userMessage, receivedMessage, null);
	        			if(message != null) {
	        				sendStandardMessage(message, userMessage);
	        			}
	        		}
	        		
	        		else{
	        			List<Bookmaker> bookmakerAvailable = null;
	        			List<UserPreference> userPreferenceList = UserPreferenceDAO.getUserPreferenceDAOInstance(connection).getRelatedUserPreferenceFromUser(userMessage);
	        			//Aggiungi preferenza mostra tutti i possibili bookmaker attivi su cui impostare una notifica
	        			if(ChatBotCommand.ADD_PREFERENCE_BONUS_ABUSING.equals(command.getRootCommand())) {
		        			bookmakerAvailable = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getAllActiveBookmakerOrderedByName();
	        			}
	        			
	        			//Modifica preferenza mostra tutti i bookmaker presenti nelle preferenze attive
	        			else if(ChatBotCommand.MOD_PREFERENCES_BONUS_ABUSING.equals(command.getRootCommand())) {
		        			bookmakerAvailable = new ArrayList<Bookmaker>();
		        			
		        			for(UserPreference up : userPreferenceList) {
		        				if(up.getBookmaker().isActive()) {
		        					bookmakerAvailable.add(up.getBookmaker());
		        				}
		        			}
	        			}
	        			
	        			//Elimina preferenza e attiva/disattiva preferenza mostrano i bookmaker presenti nelle preferenze
	        			else if(ChatBotCommand.DELETE_PREFERENCE_BONUS_ABUSING.equals(command.getRootCommand())
//	        					|| ChatBotCommand.ENABLE_DISABLE_PREFERENCES_BONUS_ABUSING.equals(command.getRootCommand())
	        					) {
		        			bookmakerAvailable = new ArrayList<Bookmaker>();
		        			
		        			for(UserPreference up : userPreferenceList) {
	        					bookmakerAvailable.add(up.getBookmaker());
		        			}
	        			}
	        			
	        			//Se è stato valorizzata la lista di bookmaker e non è vuota
	        			if(bookmakerAvailable != null && !bookmakerAvailable.isEmpty()) {
	        				showBookmakerButtons(command, userMessage, receivedMessage, bookmakerAvailable, 0);
	        			}
	        			
	        			//Mostra preferenze mostra le preferenze attualmente attive
	        			else if(ChatBotCommand.SHOW_PREFERENCES_BONUS_ABUSING.equals(command.getRootCommand()) 
	        					&& userPreferenceList != null && !userPreferenceList.isEmpty()) {
	        				showActivePreferenceMessage(userPreferenceList, userMessage, receivedMessage, command);
	        			}else {
	        				throw new AskToUsException(userMessage);
	        			}
	        		}
	        		
		        }else if(command.getFilterCommandsList().size() > 0){
		        	
		        	//Qui vanno tutte analizzate tutte le possibili scelte fatte da una delle radici degli alberi delle scelte
		        	
            		boolean toBeModified = ChatBotCommandUtilManager.getModificationCommand().contains((command.getRootCommand()));
		        	
            		if(ChatBotCommandUtilManager.getNewOrModifyCommand().contains(command.getRootCommand())) {
	            		
			        	//Se si tratta del filtro bookmaker
			        	if(ChatBotFilterCommand.BOOKMAKER_BONUS_ABUSING.equals(command.getLastChatBotCallbackFilter().getFilter())) {
				        	
			        		//Indietro di uno step
				        	if(ChatBotCommand.BACK_TO_KEYBOARD.equals(command.getNavigationCommand())) {
				        		command.setNavigationCommand(null);
				        		command.removeLastChatBotCallbackFilter();
				        		List<Bookmaker> bookmakerAvailable = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getAllActiveBookmakerOrderedByName();
				        		showBookmakerButtons(command, userMessage, receivedMessage, bookmakerAvailable, 0);
				        	}
				        	
				        	else if(ChatBotCommand.CONFIRM_CHANGE_BONUS_ABUSING.equals(command.getNavigationCommand())) {
				        		
				        		String bookmakerName = command.getLastChatBotCallbackFilter().getValue();
		        				
		        				Bookmaker bookmaker = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getBookmakerFromBookmakerName(bookmakerName);
				        		
	        					UserPreference up = UserPreferenceDAO.getUserPreferenceDAOInstance(connection).getUserPreferenceFromUserAndBookmaker(userMessage, bookmaker);
	        					if(up.atLeastOneFilterSet()) {
					        		up.setActive(true);
		        					UserPreferenceDAO.getUserPreferenceDAOInstance(connection).activateUserPreference(up);
		        					
		        		        	//Se si tratta di una conferma di modifica
	        		        		String text = "Le tue modifiche sono state salvate" + System.lineSeparator() 
	        		        					  + "Scegli tra le operazioni disponibili quella che puoi eseguire";
	        		        		SendMessage message = getMenuMessageSendMessage(userMessage, receivedMessage, text);
	        		        		sendStandardMessage(message, userMessage);
	        					}
				        	}
				        	
				        	//Pagina precedente
				        	else if(ChatBotCommand.PREVIOUS_PAGE.equals(command.getNavigationCommand())) {
			        	        ChatBotCallbackFilter filter = command.getLastChatBotCallbackFilter();
				        		String initialChar = null;
		        				if(filter.isIdFilter()) {
		        					initialChar = filter.getValue().substring(0, 1);
		        				}
			        			List<Bookmaker> bookmakerPage = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getLikeBookmakerNameByInitalChar(initialChar);
				        		showBookmakerButtons(command, userMessage, receivedMessage, bookmakerPage, filter.getPageNumber() - 1);
				        	}
				        	
				        	//Pagina successiva
				        	else if(ChatBotCommand.NEXT_PAGE.equals(command.getNavigationCommand())) {
			        	        ChatBotCallbackFilter filter = command.getLastChatBotCallbackFilter();
				        		String initialChar = null;
		        				if(filter.isIdFilter()) {
		        					initialChar = filter.getValue().substring(0, 1);
		        				}
			        			List<Bookmaker> bookmakerPage = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getLikeBookmakerNameByInitalChar(initialChar);
				        		showBookmakerButtons(command, userMessage, receivedMessage, bookmakerPage, filter.getPageNumber() + 1);
				        	}
				    		//Se il comando iniziale è ancora disponibile per l'utente
				        	else if(controlRootCommandPermitted(userMessage, command)){
					            String bookmakerName = command.getLastChatBotCallbackFilter().getValue();
					            
					            //Controllo congiunto bookmaker e utente-preferenza
					            List<Bookmaker> bookmakerList = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getLikeBookmakerNameByInitalChar(bookmakerName);
					            
					            //Se l'insieme dei bookmaker è una lista e contiene un solo elemento e il filtro è impostato su ID
					            if(bookmakerList != null && 
					            		bookmakerList.size() == 1 &&
						            	command.getLastChatBotCallbackFilter().isIdFilter()) {
					            	Bookmaker bookmaker = bookmakerList.get(0);
						            //Prendo tutte le preferenze dell'utente per assicurarmi che il limite sia rispettato 
						            //e che non stia cercando di aggiungere preferenze oltre il limite concesso
						            List<UserPreference> userPreferenceBookmaker = UserPreferenceDAO.getUserPreferenceDAOInstance(connection).getUserPreferenceFromUser(userMessage);
						            
					            	//Controllo se esiste già una preferenza su quel bookmaker
					            	if(userPreferenceBookmaker != null) {
					            		boolean alreadyPresentBookmaker = false;
					            		for(UserPreference up : userPreferenceBookmaker) {
					            			alreadyPresentBookmaker = alreadyPresentBookmaker || up.getBookmaker().sameRecord(bookmaker);
					            		}
					            		
					            		if(!alreadyPresentBookmaker || toBeModified) {
					            			UserPreference upDB =  null;
					            			if(!toBeModified) {
					            				//Insert della nuova preferenza
					            				UserPreferenceDAO.getUserPreferenceDAOInstance(connection).insertRow(UserPreference.getBlueSheepUserPreferenceFromUserInfo(bookmaker, userMessage, null, null, null, null, null, false, null));
					            			}
					            			//Mostro i filtri impostabili
					            			upDB = UserPreferenceDAO.getUserPreferenceDAOInstance(connection).getUserPreferenceFromUserAndBookmaker(userMessage, bookmaker);
					            			goToFilterPreferences(bookmaker, userMessage, receivedMessage, upDB, command, toBeModified);
					            		}
					            		//Il bookmaker selezionato è stato già selezionato
					            		else {
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
					            //ci sono più bookmaker da mostrare, si visualizza la scelta paginata
					            else if(bookmakerList != null && !bookmakerList.isEmpty()) {
					            	
					            	//Mostra la stessa paginazione di prima, però con i valori 
					        		showBookmakerButtons(command, userMessage, receivedMessage, bookmakerList, 0);
					            }
					            
					            //Il bookmaker non è più presente o non è più attivo
					            else {
					    			SendMessage message = getMenuMessageSendMessage(new InactiveBookmakerRequestException(userMessage), userMessage, callbackQuery.getMessage(), "Ecco le operazioni che puoi eseguire");
						            if(message != null) {
						            	sendStandardMessage(message, userMessage);
						            }else {
						            	logger.warn("No command available to reply. No message is sent");
						            }
					            }
				    		}
				    		//l'operazione non è concessa, dato che i dati nel DB non la permettono
				    		else {
				    			SendMessage message = getMenuMessageSendMessage(new NotPermittedOperationException(userMessage, callbackQuery.getMessage()), userMessage, receivedMessage, "Ecco le operazioni che puoi eseguire");
					            if(message != null) {
					            	sendStandardMessage(message, userMessage);
					            }else {
					            	logger.warn("No command available to reply. No message is sent");
					            }
				    		}
		        		}
			        	
			        	//Se invece l'ultimo dato della callback è un filtro diverso dal bookmaker
			        	else if(ChatBotFilterCommand.getAllAddFilters().contains(command.getLastChatBotCallbackFilter().getFilter())) {
	        				ChatBotCallbackFilter filter = command.getFilterCommandsList().get(0);
	        				String bookmakerName = filter.getValue();
	        				
	        				Bookmaker bookmaker = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getBookmakerFromBookmakerName(bookmakerName);
	
	        				if(bookmaker != null) {
	        					UserPreference up = UserPreferenceDAO.getUserPreferenceDAOInstance(connection).getUserPreferenceFromUserAndBookmaker(userMessage, bookmaker);
	
	        					if(up != null) {
	        					//La prima callback è per forza da bookmaker, quindi non sarà disponibile alcun ramo del if else.
	        					//Viene mostrato il menù rispettivamente al comando fornito dalle possili scelte dei rami if else.
	        					
			        			//Mostra in base alla tipologia di filtro, i valori disponibili
	        						
			        				//Conferma delle modifiche
			        				if(up.atLeastOneFilterSet() 
			        						&& command.getNavigationCommand() != null 
			        						&& ChatBotCommand.CONFIRM_CHANGE_BONUS_ABUSING.equals(command.getNavigationCommand())) {
			        					
			        					up.setActive(true);
			        					UserPreferenceDAO.getUserPreferenceDAOInstance(connection).activateUserPreference(up);
			        					
			        		        	//Se si tratta di una conferma di modifica
		        		        		String text = "Le tue modifiche sono state salvate" + System.lineSeparator() 
		        		        					  + "Scegli tra le operazioni disponibili quella che puoi eseguire";
		        		        		SendMessage message = getMenuMessageSendMessage(userMessage, receivedMessage, text);
		        		        		sendStandardMessage(message, userMessage);
			        					
			        				}else{
			        					
			        					UserPreference updatedUP = UserPreferenceFilterUtil.updateUserPreferenceWithFilterInformationFromCallback(up, command, userMessage, connection);
			        					UserPreferenceDAO.getUserPreferenceDAOInstance(connection).updateUserPreferenceFilters(updatedUP);
			        					
			        					//Se il record non è oggetto di modifica ed 
				        				//è di tipo RF_TYPE allora deve essere successivamente mostrato per forza il valore di RF richiesto
				        				//Controlla che non sia un controllo di navigazione
				        				if(ChatBotFilterCommand.RF_TYPE_BONUS_ABUSING.equals(command.getLastChatBotCallbackFilter().getFilter())
			        						&& (command.getNavigationCommand() == null 
			        							|| 
			        							!ChatBotCommandUtilManager.getNavigationChatBotCommand().contains(command.getNavigationCommand()))
			        						&& command.getLastChatBotCallbackFilter().isIdFilter()){
				        					command.removeLastChatBotCallbackFilter();
			        						command.getFilterCommandsList().add(ChatBotCallbackFilterFactory.getCorrectChatBotCallbackFilterFactory(true, ChatBotFilterCommand.RF_BONUS_ABUSING, "", "", false, true, 0));
			        						showAvailableFilterValues(command, receivedMessage, userMessage, updatedUP);
				        				}else {
	
				        					//Se è presente almeno un filtro settato e c'è più di un filtro(c'è stata una selezione) e
				        					//non è presente un comando di navigazione, visualizza il menu dei filtri
				        					if(!TextFilterCommand.isTextFilterCommand(command.getLastChatBotCallbackFilter().getValue())
				        							&& updatedUP.atLeastOneFilterSet() 
				        							&& command.getFilterCommandsList().size() > 1
				        							&& (command.getNavigationCommand() == null 
				        							|| 
				        							!ChatBotCommandUtilManager.getNavigationChatBotCommand().contains(command.getNavigationCommand()))) {
					        					command.removeLastChatBotCallbackFilter();
						            			goToFilterPreferences(bookmaker, userMessage, receivedMessage, updatedUP, command, toBeModified);
				        					}else {
				        						//Prende il filtro da aggiornare
				        						ChatBotCallbackFilter filterToRemove = command.getLastChatBotCallbackFilter();
				        						
				        			        	//Pagina precedente
				        			        	if(ChatBotCommand.PREVIOUS_PAGE.equals(command.getNavigationCommand())) {
				        		        	        filterToRemove = command.getLastChatBotCallbackFilter();
				        		        	        filterToRemove.setPageNumber(filterToRemove.getPageNumber() - 1);
				        			        	}
				        			        	
				        			        	//Pagina successiva
				        			        	else if(ChatBotCommand.NEXT_PAGE.equals(command.getNavigationCommand())) {
				        		        	        filterToRemove = command.getLastChatBotCallbackFilter();
				        		        	        filterToRemove.setPageNumber(filterToRemove.getPageNumber() + 1);
	
				        			        	}
				        			        	
				        			        	//Mostro i valori del filtro rispetto ai parametri settati
				        			        	showAvailableFilterValues(command, receivedMessage, userMessage, updatedUP);
				        					}
				        				}
				        			}
	        					}
	        				}
				        }
            		}
			        	//Se si tratta di cancellazione, attivazione/disattivazione di una preferenza
		        	else if(ChatBotCommandUtilManager.getDeleteOrActivactionCommand().contains(command.getRootCommand())) {
				        	
				        if(ChatBotCommand.DELETE_PREFERENCE_BONUS_ABUSING.equals(command.getRootCommand()) 
				        		&& ChatBotFilterCommand.BOOKMAKER_BONUS_ABUSING.equals(command.getLastChatBotCallbackFilter().getFilter())) {
				        		
			        		String bookmakerName = command.getLastChatBotCallbackFilter().getValue();
			        		
			        		Bookmaker bookmaker = BookmakerDAO.getBlueSheepBookmakerDAOInstance(connection).getBookmakerFromBookmakerName(bookmakerName);
			        		
			        		UserPreferenceDAO.getUserPreferenceDAOInstance(connection).removeUserPreferenceByBookmakerAndUser(bookmaker, userMessage);
			        		
	    		        	//Conferma di modifica
			        		String text = "La tua preferenza relativa al bookmaker " 
			        					  + ArbsUtil.getTelegramBoldString(bookmaker.getBookmakerName())
			        					  + " è stata cancellata!"
			        					  + System.lineSeparator() 
			        					  + "Scegli tra le operazioni disponibili quella che puoi eseguire";
			        		SendMessage message = getMenuMessageSendMessage(userMessage, receivedMessage, text);
			        		sendStandardMessage(message, userMessage);
			        		
			        	}
//				        else if(ChatBotCommand.ENABLE_DISABLE_PREFERENCES_BONUS_ABUSING.equals(command.getRootCommand())) {
//			        		
//			        	}
		        	}else {
		        		throw new AskToUsException(userMessage);
		        	}
        		}else {
        			throw new AskToUsException(userMessage);
        		}
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

	private void showActivePreferenceMessage(List<UserPreference> userPreferenceList, TelegramUser userMessage,
			Message receivedMessage, ChatBotCallbackCommand command) {
		
		String text = "";
		int i = 0;
		for(UserPreference up : userPreferenceList){
			if(up.isActive()) {
				i++;
				text = text + "#" + i + System.lineSeparator() + System.lineSeparator();
				text = text + up.getUserPreferenceManifest() + System.lineSeparator();
				
			}
		}
		
		text = text + "Ricorda che il numero massimo di preferenze attive consentito è " + BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.CHAT_BOT_MAX_PREF) + "."
				+ System.lineSeparator() + "Torna al menù con il comando /menu";
		
		SendMessage message = new SendMessage(userMessage.getChatId(), text);
		sendStandardMessage(message, userMessage);
		
	}

	private void showAvailableFilterValues(ChatBotCallbackCommand command, Message receivedMessage, TelegramUser userMessage, UserPreference up) throws AskToUsException {
		
		AbstractDAO<? extends AbstractBlueSheepEntity> dao = ChatBotFilterCommandUtilManager.getCorrectDAOByChatBotCallackCommand(command, connection);
		
		List<? extends AbstractBlueSheepEntity> returnFilterRow = dao.getAllActiveRows();
		
		if(returnFilterRow != null && !returnFilterRow.isEmpty()) {
			int columnNumber = 2;
			ChatBotFilterCommand lastFilter = command.getLastChatBotCallbackFilter().getFilter();
			if(ChatBotFilterCommand.EVENT_BONUS_ABUSING.equals(lastFilter)) {
				columnNumber = 1;
			}
			String text = ChatBotFilterCommandUtilManager.getCorrectTextFromChatBotFilterCommand(command.getLastChatBotCallbackFilter().getFilter());
			collectResultAndSendMessage(command, returnFilterRow, columnNumber, command.getLastChatBotCallbackFilter().getPageNumber(), receivedMessage, userMessage, text);
		}
		
		else { 
			throw new AskToUsException(userMessage);
		}
	}

	private void showBookmakerButtons(ChatBotCallbackCommand command, TelegramUser userMessage, Message receivedMessage, List<? extends AbstractBlueSheepEntity> bookmakerAvailable, int pageIndex) throws AskToUsException {
        
		String textOfMessage = ChatBotCommandUtilManager.getCorrectTextFromChatBotCommand(command.getRootCommand());
		collectResultAndSendMessage(command, bookmakerAvailable, 2, pageIndex, receivedMessage, userMessage, textOfMessage);
		
	}

	private void collectResultAndSendMessage(ChatBotCallbackCommand command, List<? extends AbstractBlueSheepEntity> entityAvailable, int columnNumber, int pageIndex, Message receivedMessage, TelegramUser userMessage, String textOfMessage) throws AskToUsException {
		EditMessageText new_message = null;
        
        List<List<InlineKeyboardButton>> listOfCommandButton = ChatBotButtonManager.getBlueSheepEntityButtonListNColumns(command, entityAvailable, columnNumber, pageIndex, maxRow);
        
		if (!listOfCommandButton.isEmpty()) {
			
			if(entityAvailable.size() > maxRow * columnNumber) {
	        	textOfMessage = textOfMessage + "." 
	        			+  System.lineSeparator() 
	        			+ "Utilizza i pulsanti di navigazione " 
	        			+ ArbsUtil.getTelegramBoldString(">>") 
	        			+ " o " + ArbsUtil.getTelegramBoldString("<<") 
	        			+ " per muoverti tra le varie pagine di valori disponibili";
			}
			
			InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
			new_message = new EditMessageText() // Create a message object object
					.setChatId(receivedMessage.getChatId())	
					.setMessageId(receivedMessage.getMessageId())
					.setText(textOfMessage);
			
			// Add it to the message
			markupInline.setKeyboard(listOfCommandButton);
			new_message.setReplyMarkup(markupInline);
			
            sendStandardMessage(new_message, userMessage);
		}else {
			throw new AskToUsException(userMessage);
		}
	}

	private SendMessage getMenuMessageSendMessage(BluesheepChatBotException exception,
			TelegramUser userMessage, Message message, String text) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		return getMenuMessageSendMessage(userMessage, message, exception.getMessage() + System.lineSeparator() + text);
	}

	private void goToFilterPreferences(Bookmaker bookmaker, TelegramUser userMessage, 
			Message receivedMessage, UserPreference upDB, ChatBotCallbackCommand command, boolean toBeModified) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		
		//Crea il messaggio con i filtri
		EditMessageText message = getFilterMenuMessage(userMessage, receivedMessage, upDB, command, toBeModified);
		
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
		
		    		logger.info("Registration completed for user " + userMessage.getUserName() + ", CHAT_ID=" + userMessage.getChatId());
		    		
		    		SendMessage message = getMenuMessageSendMessage(userMessage, receivedMessage, null);
		            if(message != null) {
		            	sendStandardMessage(message, userDB);
		            }else {
		            	logger.warn("No command available to reply. No message is sent");
		            }
				}else {
					throw new AlreadyRegisteredUserChatBotException(userDB);
				}
			}else {
				throw new NotPermittedOperationException(userMessage, receivedMessage);
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
	
	private EditMessageText getFilterMenuMessage(TelegramUser userMessage, Message receivedMessage, UserPreference upDB, ChatBotCallbackCommand command, boolean toBeModified) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		EditMessageText message = null;
		if(command != null && command.getFilterCommandsList() != null) {
			
			//Menu di ogni possibile scelta dell'albero
			message = ChatBotButtonManager.getAvailableFilterListButton(upDB, command, 3, 2, receivedMessage, userMessage, command.getLastChatBotCallbackFilter().getPageNumber(), toBeModified);
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
					message.setText("Ciao "
								+ userMessage.getUserName() 
								+ ", benvenuto! Io sono Blue Sheep Bot." + System.lineSeparator()
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
	
	private void updateLastMessageIdUser(Message lastMessageSent, TelegramUser userDB) throws AskToUsException {
		userDB.setLastMessageId(new Long(lastMessageSent.getMessageId()));
		try {
			TelegramUserDAO.getBlueSheepTelegramUserDAOInstance(connection).updateLastMessageSent(userDB);
		} catch (SQLException e) {
			throw new AskToUsException(userDB);
		}
	}
	
	private void updateLastMessageIdUser(EditMessageText lastMessageSent, TelegramUser userDB) throws AskToUsException {
		userDB.setLastMessageId(new Long(lastMessageSent.getMessageId()));
		try {
			TelegramUserDAO.getBlueSheepTelegramUserDAOInstance(connection).updateLastMessageSent(userDB);
		} catch (SQLException e) {
			throw new AskToUsException(userDB);
		}
	}
	
	private boolean controlRootCommandPermitted(TelegramUser user, ChatBotCallbackCommand command) throws AlreadyRegisteredUserChatBotException, AskToUsException, SQLException {
		Set<ChatBotCommand> availableChatBotCommandForUser = TelegramUserDatabaseManager.getAvailableActionForUser(user, connection);
		
		return availableChatBotCommandForUser.contains(command.getRootCommand());
	}
	
	private void executeDeleteMessage(Long messageId, TelegramUser userDB) {
		DeleteMessage deleteMessage = new DeleteMessage().setChatId(userDB.getChatId()).setMessageId(messageId.intValue());
		try {
			execute(deleteMessage);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
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

}
