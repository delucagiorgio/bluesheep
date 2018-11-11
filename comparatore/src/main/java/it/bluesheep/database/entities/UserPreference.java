package it.bluesheep.database.entities;

public class UserPreference extends AbstractBlueSheepEntity {
	
	private Bookmaker bookmaker;
	private TelegramUser user;
	private Double rating;
	private Double rf;
	private Double liquidita;
	private String event;
	private String championship;
	private Double minOddValue;
	private boolean active;
	
	private UserPreference(Bookmaker bookmaker, TelegramUser user, Double rating, Double rf, Double liquidita, String event, String championship, Double minOddValue, boolean active) {
		super();
		this.bookmaker = bookmaker;
		this.user = user;
		this.rating = rating;
		this.rf = rf;
		this.liquidita = liquidita;
		this.event = event;
		this.championship = championship;
		this.minOddValue = minOddValue;
		this.active = active;
	}
	
	private UserPreference(Bookmaker bookmaker, TelegramUser user, Double rating, Double rf, Double liquidita, String event, String championship, Double minOddValue, boolean active, long id) {
		super(id);
		this.bookmaker = bookmaker;
		this.user = user;
		this.rating = rating;
		this.rf = rf;
		this.liquidita = liquidita;
		this.event = event;
		this.championship = championship;
		this.minOddValue = minOddValue;
		this.active = active;
	}
	
	public static UserPreference getBlueSheepUserPreferenceFromDatabaseInfo(Bookmaker bookmaker, TelegramUser user, Double rating, Double rf, Double liquidita, String event, String championship, Double minOddValue, boolean active, long id) {
		return new UserPreference(bookmaker, user, rating, rf, liquidita, event, championship, minOddValue, active, id);
	}
	
	public static UserPreference getBlueSheepUserPreferenceFromUserInfo(Bookmaker bookmaker, TelegramUser user, Double rating, Double rf, Double liquidita, String event, String championship, Double minOddValue, boolean active) {
		return new UserPreference(bookmaker, user, rating, rf, liquidita, event, championship, minOddValue, active);
	}

	public Bookmaker getBookmaker() {
		return bookmaker;
	}

	public void setBookmaker(Bookmaker bookmaker) {
		this.bookmaker = bookmaker;
	}

	public TelegramUser getUser() {
		return user;
	}

	public void setUser(TelegramUser user) {
		this.user = user;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Double getRf() {
		return rf;
	}

	public void setRf(Double rf) {
		this.rf = rf;
	}

	public Double getLiquidita() {
		return liquidita;
	}

	public void setLiquidita(Double liquidita) {
		this.liquidita = liquidita;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getChampionship() {
		return championship;
	}

	public void setChampionship(String championship) {
		this.championship = championship;
	}

	public Double getMinOddValue() {
		return minOddValue;
	}

	public void setMinOddValue(Double minOddValue) {
		this.minOddValue = minOddValue;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean sameRecord(UserPreference userPreference) {
		return super.sameRecord(userPreference);
	}

	@Override
	public String getTelegramButtonText() {
		return bookmaker.getBookmakerName();
	}

}
