package it.bluesheep.telegrambot.message.io;

import java.util.List;

import it.bluesheep.telegrambot.message.util.ChatBotCommand;

public abstract class ChatBotCallback {

	//Identifica l'azione di partenza
	private ChatBotCommand rootCommand;
	//Identifica la lista di filtri applicati alla sequenza di interazioni
	private List<ChatBotCallbackFilter> filterCommandsList;
	//Identifica il comando di navigazione, se presente
	private ChatBotCommand navigationCommand;
	
	protected ChatBotCallback(ChatBotCommand callback, List<ChatBotCallbackFilter> filterList, ChatBotCommand navigationCommand) {
		this.rootCommand = callback;
		this.filterCommandsList = filterList;
		this.navigationCommand = navigationCommand;
	}

	protected ChatBotCallback(ChatBotCommand callback, String callbackData, ChatBotCommand navigationCommand) {
		this.rootCommand = callback;
		this.navigationCommand = navigationCommand;
	}

	public ChatBotCallback(ChatBotCallbackCommand callbackCommand, ChatBotCommand navigationCommand) {
		this.rootCommand = callbackCommand.getRootCommand();
		this.filterCommandsList = callbackCommand.getFilterCommandsList();
		this.navigationCommand = navigationCommand;
	}

	public ChatBotCommand getRootCommand() {
		return rootCommand;
	}

	public void setRootCommand(ChatBotCommand rootCommand) {
		this.rootCommand = rootCommand;
	}
	
	public ChatBotCallbackFilter getLastChatBotCallbackFilter() {
		return filterCommandsList != null ? filterCommandsList.get(filterCommandsList.size() - 1) : null;
	}

	public List<ChatBotCallbackFilter> getFilterCommandsList() {
		return filterCommandsList;
	}

	public void setFilterCommandsList(List<ChatBotCallbackFilter> filterCommandsList) {
		this.filterCommandsList = filterCommandsList;
	}

	public ChatBotCommand getNavigationCommand() {
		return navigationCommand;
	}

	public void setNavigationCommand(ChatBotCommand navigationCommand) {
		this.navigationCommand = navigationCommand;
	}
	
	public void removeLastChatBotCallbackFilter() {
		ChatBotCallbackFilter filter = getLastChatBotCallbackFilter();
		if(filter != null) {
			filterCommandsList.remove(filter);
			if(filterCommandsList.isEmpty()) {
				filterCommandsList = null;
			}
		}
	}
	
	public ChatBotCallbackFilter getSameChatBotCallbackFilter(ChatBotCallbackFilter filter) {
		if(filterCommandsList != null && !filterCommandsList.isEmpty()) {
			for(ChatBotCallbackFilter ccf : filterCommandsList) {
				if(ccf.getFilter().equals(filter.getFilter())) {
					return ccf;
				}
			}
		}
		
		return null;
		
	}
	
}
