package it.bluesheep.telegrambot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

public class TelegramBotHandler extends TelegramLongPollingBot {
	
	private static Logger logger;
	private static TelegramBotHandler instance;
	
	private TelegramBotHandler() {
		super();
		logger = Logger.getLogger(TelegramBotHandler.class);
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
		
    	String firstNameUser = null;
    	String lastNameUser = null;
    	Long chatId = null;
        String message_text = null;
		if(!updateContainer.hasMessage() || updateContainer.getMessage().getFrom() != null && !updateContainer.getMessage().getFrom().getBot()) {
	        if (updateContainer.hasMessage() && updateContainer.getMessage().hasText()) {
	        	
	        	firstNameUser = updateContainer.getMessage().getFrom().getFirstName();
	        	lastNameUser = updateContainer.getMessage().getFrom().getLastName();
	        	chatId = updateContainer.getMessage().getChatId();
	            message_text = updateContainer.getMessage().getText();
	
	        	logger.info("Received a message from " + firstNameUser + " " + lastNameUser + ", chat_id: " + chatId + "; Text = " + message_text);
	            
	            if ("/menu".equals(message_text) && isRegisteredUser(updateContainer.getMessage())) {
	                SendMessage message = new SendMessage() // Create a message object object
	                        .setChatId(chatId)
	                        .setText("Ciao " + firstNameUser + " " + lastNameUser + ", benvenuto! Io sono BSBot." + System.lineSeparator() 
	                        		+ "Clicca sull'operazione che ti interessa eseguire");
	                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
	                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
	                List<InlineKeyboardButton> showInline = new ArrayList<InlineKeyboardButton>();
	                InlineKeyboardButton showButton = new InlineKeyboardButton().setText("Visualizza preferenze di notifica attive").setCallbackData("COMMAND_show_active_prefs");
	                showInline.add(showButton);
	               
	                List<InlineKeyboardButton> enableDisableInline = new ArrayList<InlineKeyboardButton>();
	                InlineKeyboardButton enableDisableButton = new InlineKeyboardButton().setText("Attiva/Disattiva preferenze di notifica").setCallbackData("COMMAND_enable_disable_prefs");
	                enableDisableInline.add(enableDisableButton);
	                
	                List<InlineKeyboardButton> addInline = new ArrayList<InlineKeyboardButton>();
	                InlineKeyboardButton addButton = new InlineKeyboardButton().setText("Aggiungi preferenze di notifica").setCallbackData("COMMAND_add_prefs");
	                addInline.add(addButton);
	                
	                List<InlineKeyboardButton> removeInline = new ArrayList<InlineKeyboardButton>();
	                InlineKeyboardButton removeButton = new InlineKeyboardButton().setText("Rimuovi preferenze di notifica").setCallbackData("COMMAND_delete_active_prefs");
	                removeInline.add(removeButton);
	                
	                // Set the keyboard to the markup
	                rowsInline.add(showInline);
	                rowsInline.add(enableDisableInline);
	                rowsInline.add(addInline);
	                rowsInline.add(removeInline);
	                
	                // Add it to the message
	                markupInline.setKeyboard(rowsInline);
	                message.setReplyMarkup(markupInline);
	                try {
	                    execute(message); // Sending our message object to user
	                } catch (Exception e) {
	                	logger.error(e.getMessage(), e);
	                }
	            } else if(message_text.equals("/registrazione") && !isBlockedUser(updateContainer.getMessage())){
	            	
	            } else {
	            	SendMessage message = new SendMessage() // Create a message object object
	                        .setChatId(chatId)
	                        .setText("Impossibile interagire. Contattare l'assistenza all'indirizzo mail info@bluesheep.it");
	                try {
	                    execute(message); // Sending our message object to user
	                } catch (Exception e) {
	                	logger.error(e.getMessage(), e);
	                }
	            }
	
	        } else if (updateContainer.hasCallbackQuery()) {
	        	
	        	firstNameUser = updateContainer.getCallbackQuery().getFrom().getFirstName();
	        	lastNameUser = updateContainer.getCallbackQuery().getFrom().getLastName();
	        	chatId = updateContainer.getCallbackQuery().getMessage().getChatId();
	            message_text = updateContainer.getCallbackQuery().getData();
	            
	        	logger.info("Received a message from " + firstNameUser + " " + lastNameUser + ", chat_id: " + chatId + "; Text = " + message_text);
	
	            // Set variables
	            long chat_id = updateContainer.getCallbackQuery().getMessage().getChatId();
	    		
	    		//TODO: qui vanno tutte le possibili selezioni (radice dell'albero delle scelte)
	            if (message_text.equals("COMMAND_show_active_prefs")) {
	                String answer = "TEST_OK";
	                SendMessage new_message = new SendMessage()
	                        .setChatId(chat_id)
	                        .setText(answer);
	                try {
	                    execute(new_message); 
	                } catch (Exception e) {
	                	logger.error(e.getMessage(), e);
	                }
	            }
	        }
        }
	}

	private boolean isBlockedUser(Message message) {
		return false;
	}

	private boolean isRegisteredUser(Message message) {
		return true;
	}

	@Override
	public String getBotToken() {
		return BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.TELEGRAMBOTKEY);
	}
	
	public void stopExecution() {
		logger.info("Trying to stop TelegramBotChat handler for shutdown request");
		super.exe.shutdown();
		try {
			logger.info("Waiting for 5 minutes for telegram handler shutdown");
			boolean correctlyTerminated = super.exe.awaitTermination(5, TimeUnit.MINUTES);
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
