package it.bluesheep.database.entities;

import java.sql.Timestamp;

public class UserPreferenceNotification extends AbstractBlueSheepEntity {

	private UserPreference userPreferenceId;
	private TelegramUser userId;
	private int progId;
	private String notificationKey;
	private boolean currentNotificationGroup;
	
	public UserPreferenceNotification(UserPreference userPreference, TelegramUser user, long id, int prodId, Timestamp createTimestamp, Timestamp updateTimestamp, String notificationKey, boolean currentNotificationGroup) {
		super(id, createTimestamp, updateTimestamp);
		this.progId = prodId;
		this.userId = user;
		this.userPreferenceId = userPreference;
		this.notificationKey = notificationKey;
		this.currentNotificationGroup = currentNotificationGroup;
	}
	
	@Override
	public String getTelegramButtonText() {
		return null;
	}

	public UserPreference getUserPreference() {
		return userPreferenceId;
	}

	public TelegramUser getUser() {
		return userId;
	}

	public int getProdId() {
		return progId;
	}

	public String getNotificationKey() {
		return notificationKey;
	}

	public boolean isCurrentNotificationGroup() {
		return currentNotificationGroup;
	}

}
