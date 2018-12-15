package it.bluesheep.database.entities;

import java.sql.Timestamp;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import it.bluesheep.util.BlueSheepConstants;

public class TelegramUser extends AbstractBlueSheepEntity {

	private String userName;
	private Long chatId;
	private Timestamp registrationDate;
	private Boolean active;
	private Long lastMessageId;
	private String bluesheepUsername;
	private boolean bypassedControlAccount;
	
	private TelegramUser(String userName, Long chatId) {
		super();
		this.chatId = chatId;
		this.userName = userName;
		registrationDate = null;
		active = false;
	}

	private TelegramUser(String userName, String bluesheepUsername, Long chatId, Boolean active, boolean bypassedControlAccount, Timestamp date, Long id, Long lastMessageId, Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, updateTime);
		this.chatId = chatId;
		this.bluesheepUsername = bluesheepUsername;
		this.userName = userName;
		registrationDate = date;
		this.active = active;
		this.lastMessageId = lastMessageId;
		this.bypassedControlAccount = bypassedControlAccount;
	}
	
	public static TelegramUser getBlueSheepTelegramUserFromBlueSheepInfo(BlueSheepUserInfo bs, boolean active) {
		return new TelegramUser(bs.getTelegramUsername(), 
								bs.getBluesheepUsername(), 
								null, 
								active, 
								false,
								new Timestamp(System.currentTimeMillis()),
								1L, 
								null,
								new Timestamp(System.currentTimeMillis()),
								null);
	}
	
	public static TelegramUser getBlueSheepTelegramUserFromDatabaseInfo(String userName, String bluesheepUsername, Long chatId, Boolean active, boolean bypassedControlAccount, Timestamp date, Long id, Long lastMessageId, Timestamp createTime, Timestamp updateTime) {
		return new TelegramUser(userName, bluesheepUsername, chatId, active, bypassedControlAccount, date, id, lastMessageId, createTime, updateTime);
	}
	
	public static TelegramUser getBlueSheepTelegramUserFromNewInfo(String userName, Long chatId) {
		return new TelegramUser(userName, chatId);
	}

	public String getUserName() {
		return userName;
	}

	public Long getChatId() {
		return chatId;
	}

	public Timestamp getRegistrationDate() {
		return registrationDate;
	}

	public Boolean isActive() {
		return active;
	}
	
	@Override
	public String toString() {
		return userName + BlueSheepConstants.REGEX_CSV + 
				"CHAT_ID=" + chatId + BlueSheepConstants.REGEX_CSV +
				active + BlueSheepConstants.REGEX_CSV +
				registrationDate;
				
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public static TelegramUser getTelegramUserFromMessage(TelegramUser userDB, Message message) {
		return TelegramUser.getBlueSheepTelegramUserFromNewInfo(userDB.getUserName(), message.getChatId());
	}
	
	public static TelegramUser getTelegramUserFromMessage(Message message) {
		return TelegramUser.getBlueSheepTelegramUserFromNewInfo(message.getFrom().getUserName(), message.getChatId());
	}
	
	public static TelegramUser getTelegramUserFromUserTelegram(User user, Message message) {
		return TelegramUser.getBlueSheepTelegramUserFromNewInfo(user.getUserName(), message.getChatId());
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

	public void setRegistrationDate(Timestamp registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getBluesheepUsername() {
		return bluesheepUsername;
	}

	public boolean isBypassedControlAccount() {
		return bypassedControlAccount;
	}
}