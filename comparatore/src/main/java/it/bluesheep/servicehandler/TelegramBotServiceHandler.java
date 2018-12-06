package it.bluesheep.servicehandler;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;

import it.bluesheep.telegrambot.TelegramBotHandler;
import it.bluesheep.util.BlueSheepSharedResources;

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
		ApiContextInitializer.init();
        TelegramBotsApi botApi = new TelegramBotsApi();
        
        try {
            BotSession session = botApi.registerBot(TelegramBotHandler.getTelegramBotHandlerInstance());
    		BlueSheepSharedResources.setBotSession(session);
            logger.info("TelegramBotChat handler started");
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        	instance = null;
        }
	}
	
}
