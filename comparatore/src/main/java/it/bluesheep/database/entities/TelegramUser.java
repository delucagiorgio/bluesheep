package it.bluesheep.database.entities;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import it.bluesheep.util.BlueSheepConstants;

public class TelegramUser extends AbstractBlueSheepEntity {

	private String firstName;
	private String lastName;
	private Long chatId;
	private Long registrationDate;
	private Boolean active;
	private Long lastMessageId;
	
	private TelegramUser(String firstName, String lastName, Long chatId) {
		super();
		this.chatId = chatId;
		this.firstName = firstName;
		this.lastName = lastName;
		registrationDate = System.currentTimeMillis();
		active = false;
	}

	private TelegramUser(String firstName, String lastName, Long chatId, Boolean active, Long date, Long id, Long lastMessageId) {
		super(id);
		this.chatId = chatId;
		this.firstName = firstName;
		this.lastName = lastName;
		registrationDate = date;
		this.active = active;
		this.lastMessageId = lastMessageId;
	}
	
	public static TelegramUser getBlueSheepTelegramUserFromDatabaseInfo(String firstName, String lastName, Long chatId, Boolean active, Long date, Long id, Long lastMessageId) {
		return new TelegramUser(firstName, lastName, chatId, active, date, id, lastMessageId);
	}
	
	public static TelegramUser getBlueSheepTelegramUserFromNewInfo(String firstName, String lastName, Long chatId) {
		return new TelegramUser(firstName, lastName, chatId);
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Long getChatId() {
		return chatId;
	}

	public Long getRegistrationDate() {
		return registrationDate;
	}

	public Boolean isActive() {
		return active;
	}
	
	@Override
	public String toString() {
		return firstName + BlueSheepConstants.REGEX_CSV + 
				lastName + BlueSheepConstants.REGEX_CSV +
				"CHAT_ID=" + chatId + BlueSheepConstants.REGEX_CSV +
				active + BlueSheepConstants.REGEX_CSV +
				registrationDate;
				
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public static TelegramUser getTelegramUserFromMessage(Message message) {
		return TelegramUser.getBlueSheepTelegramUserFromNewInfo(message.getFrom().getFirstName(), message.getFrom().getLastName(), message.getChatId());
	}
	
	public static TelegramUser getTelegramUserFromUserTelegram(User user, Message message) {
		return TelegramUser.getBlueSheepTelegramUserFromNewInfo(user.getFirstName(), user.getLastName(), message.getChatId());
	}
	
	public boolean sameRecord(TelegramUser user) {
		return super.sameRecord(user);
	}

	public Long getLastMessageId() {
		return lastMessageId;
	}
	
	public void setLastMessageId(Long messageId) {
		this.lastMessageId = messageId;
	}

	@Override
	public String getTelegramButtonText() {
		return null;
	}
}
