package it.bluesheep.database.entities;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import it.bluesheep.arbitraggi.util.ArbsUtil;

public class UserPreference extends AbstractBlueSheepEntity {
	
	private Bookmaker bookmaker;
	private TelegramUser user;
	private Double ratingValue;
	private Double rfValue;
	private Double liquidita;
	private String event;
	private Double minOddValue;
	private boolean active;
	private Double rfType;
	
	private UserPreference(Bookmaker bookmaker, TelegramUser user, Double rating, Double rf, Double liquidita, String event, Double minOddValue, boolean active, Double rfType) {
		super();
		this.bookmaker = bookmaker;
		this.user = user;
		this.ratingValue = rating;
		this.rfValue = rf;
		this.liquidita = liquidita;
		this.event = event;
		this.minOddValue = minOddValue;
		this.active = active;
		this.rfType = rfType;
	}
	
	private UserPreference(Bookmaker bookmaker, TelegramUser user, Double rating, Double rf, Double liquidita, String event, Double minOddValue, boolean active, long id, Timestamp createTime, Timestamp updateTime, Double rfType) {
		super(id, createTime, updateTime);
		this.bookmaker = bookmaker;
		this.user = user;
		this.ratingValue = rating;
		this.rfValue = rf;
		this.liquidita = liquidita;
		this.event = event;
		this.minOddValue = minOddValue;
		this.active = active;
		this.rfType = rfType;
	}
	
	public static UserPreference getBlueSheepUserPreferenceFromDatabaseInfo(Bookmaker bookmaker, TelegramUser user, Double rating, Double rf, Double liquidita, String event, Double minOddValue, boolean active, long id, Timestamp createTime, Timestamp updateTime, Double rfType) {
		return new UserPreference(bookmaker, user, rating, rf, liquidita, event, minOddValue, active, id, createTime, updateTime, rfType);
	}
	
	public static UserPreference getBlueSheepUserPreferenceFromUserInfo(Bookmaker bookmaker, TelegramUser user, Double rating, Double rf, Double liquidita, String event, Double minOddValue, boolean active, Double rfType) {
		return new UserPreference(bookmaker, user, rating, rf, liquidita, event, minOddValue, active, rfType);
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
		return ratingValue;
	}

	public void setRating(Double rating) {
		this.ratingValue = rating;
	}

	public Double getRfValue() {
		return rfValue;
	}

	public void setRfValue(Double rf) {
		this.rfValue = rf;
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

	public Double getRfType() {
		return rfType;
	}

	public void setRfType(Double rfType) {
		this.rfType = rfType;
	}
	
	public boolean atLeastOneFilterSet() {
		return event != null || liquidita != null || minOddValue != null || ratingValue != null || (rfType != null && rfValue != null);
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.##");
		
		return (event != null ? ArbsUtil.getTelegramBoldString("Evento") + ": " + event + System.lineSeparator() : "") + 
			   (liquidita != null ? ArbsUtil.getTelegramBoldString("Liquidità") + ": " + df.format(liquidita) + System.lineSeparator(): "") +
			   (minOddValue != null ? ArbsUtil.getTelegramBoldString("Quota minima") + ": " + df.format(minOddValue) + System.lineSeparator() : "") +
			   (ratingValue != null ? ArbsUtil.getTelegramBoldString("Rating") + ": " + df.format(ratingValue * 100) + "%" + System.lineSeparator() : "") +
			   (rfType != null ? ArbsUtil.getTelegramBoldString("Percentuale rimborso") + ": " + df.format(rfType * 100) + "%" + System.lineSeparator() : "") +
			   (rfValue != null ? ArbsUtil.getTelegramBoldString("Rating RF") + ": " + df.format(rfValue * 100) + "%" + System.lineSeparator() : "") ;
					
	}
	
	public String getUserPreferenceManifest() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		DecimalFormat df = new DecimalFormat("#.##");
		
		return "Preferenza creata il " + sdf.format(getCreateTime()) + System.lineSeparator()
			   + ArbsUtil.getTelegramBoldString("Bookmaker") + ": " + bookmaker.getBookmakerName() + System.lineSeparator()
			   + (event != null ? ArbsUtil.getTelegramBoldString("Evento") + ": " + event + System.lineSeparator() : "") 
			   + (liquidita != null ? ArbsUtil.getTelegramBoldString("Liquidità") + ": " + df.format(liquidita) + System.lineSeparator(): "")
			   + (minOddValue != null ? ArbsUtil.getTelegramBoldString("Quota minima") + ": " + df.format(minOddValue) + System.lineSeparator() : "")
			   + (ratingValue != null ? ArbsUtil.getTelegramBoldString("Rating") + ": " + df.format(ratingValue * 100) + "%" + System.lineSeparator() : "")
			   + (rfType != null ? ArbsUtil.getTelegramBoldString("Percentuale rimborso") + ": " + df.format(rfType * 100) + "%" + System.lineSeparator() : "")
			   + (rfValue != null ? ArbsUtil.getTelegramBoldString("Rating RF") + ": " + df.format(rfValue * 100) + "%" + System.lineSeparator() : "")
			   ;
	}
	
}
