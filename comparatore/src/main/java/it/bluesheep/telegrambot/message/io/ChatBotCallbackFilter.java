package it.bluesheep.telegrambot.message.io;

import it.bluesheep.telegrambot.message.util.ChatBotFilterCommand;
import it.bluesheep.util.BlueSheepConstants;

public abstract class ChatBotCallbackFilter{

	//Il filtro corrispondente
	private ChatBotFilterCommand filter;
	//Il valore video del comando 
	private String value;
	//La specifica callback del filtro
	private String specificCallbackData;
	//Specifica se il filtro è una tastiera o meno
	protected boolean keyboardMode = false;
	//Specifica se il filtro è relativo ad un identificativo di entità
	protected boolean isIdFilter;
	
	protected int pageNumber;

	
	protected ChatBotCallbackFilter(ChatBotFilterCommand filter, String value, String callbackInfo, boolean idFilter) {
		this.filter = filter;
		this.value = value;
		this.specificCallbackData = filter.getCode() + BlueSheepConstants.REGEX_CSV + callbackInfo;
		this.isIdFilter = idFilter;
	}

	public ChatBotFilterCommand getFilter() {
		return filter;
	}

	public String getValue() {
		return value;
	}

	public String getSpecificCallbackData() {
		return specificCallbackData;
	}

	public void setSpecificCallbackData(String callbackInfo) {
		this.specificCallbackData = callbackInfo;
	}

	public boolean isIdFilter() {
		return isIdFilter;
	}

	public void setIdFilter(boolean isIdFilter) {
		this.isIdFilter = isIdFilter;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
}
