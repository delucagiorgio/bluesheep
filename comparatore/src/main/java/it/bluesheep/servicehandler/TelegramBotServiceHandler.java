package it.bluesheep.servicehandler;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import it.bluesheep.telegrambot.TelegramBotHandler;

public final class TelegramBotServiceHandler extends AbstractBlueSheepService {

	private static Logger logger;
	private static TelegramBotServiceHandler instance;

	private TelegramBotServiceHandler() {
		logger = Logger.getLogger(TelegramBotServiceHandler.class);
	}
	
	protected static synchronized TelegramBotServiceHandler getTelegramBotServiceHandlerInstance() {
		if(instance == null) {
			instance = new TelegramBotServiceHandler();
		}
		return instance;
	}
	
	@Override
	public void run() {
		
		logger.info("TelegramBotChat handler starting");
		
        TelegramBotsApi botApi = new TelegramBotsApi();
        
        try {
            botApi.registerBot(TelegramBotHandler.getTelegramBotHandlerInstance());
    		logger.info("TelegramBotChat handler started");
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        	instance = null;
        }
	}
}
