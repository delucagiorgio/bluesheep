package it.bluesheep.database.entities;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import it.bluesheep.arbitraggi.util.ArbsUtil;
import it.bluesheep.comparatore.entities.util.ScommessaUtilManager;

public class PBOdd extends AbstractOddEntity {

	private Timestamp dataOraEvento;
	private String sport;
	private String evento;
	private String campionato;
	private Double rating1;
	private String bookmakerName1;
	private String scommessaBookmaker1;
	private Double quotaScommessaBookmaker1;
	private String bookmakerName2;
	private String scommessaBookmaker2;
	private Double quotaScommessaBookmaker2;
	private String nazione;
	private String linkBook1;
	private String linkBook2;
	private Double netProfit;
	private Double liquidita2;

	public PBOdd(Timestamp dataOraEvento, String sport, String evento, String campionato, Double rating1,
			String bookmakerName1, String scommessaBookmaker1, Double quotaScommessaBookmaker1,
			String bookmakerName2, String scommessaBookmaker2, Double quotaScommessaBookmaker2, String nazione,
			String linkBook1, String linkBook2, Double netProfit, Double liquidita2, long id,
			Timestamp createTime, Timestamp updateTime) {
		super(id, createTime, updateTime);
		this.bookmakerName1 = bookmakerName1;
		this.bookmakerName2 = bookmakerName2;
		this.campionato = campionato;
		this.dataOraEvento = dataOraEvento;
		this.evento = evento;
		this.linkBook1 = linkBook1;
		this.linkBook2 = linkBook2;
		this.liquidita2 = liquidita2;
		this.nazione = nazione;
		this.netProfit = netProfit;
		this.quotaScommessaBookmaker1 = quotaScommessaBookmaker1;
		this.quotaScommessaBookmaker2 = quotaScommessaBookmaker2;
		this.rating1 = rating1 / 100D;
		this.sport = sport;
		this.scommessaBookmaker1 = scommessaBookmaker1;
		this.scommessaBookmaker2 = scommessaBookmaker2;

	}
	
	public String getNotificationKey() {
		return bookmakerName1 + ";" +
				bookmakerName2 + ";" +
				campionato + ";" +
				evento + ";" +
				sport;
				
	}

	public Timestamp getDataOraEvento() {
		return dataOraEvento;
	}

	public String getSport() {
		return sport;
	}

	public String getEvento() {
		return evento;
	}

	public String getCampionato() {
		return campionato;
	}

	public Double getRating1() {
		return rating1;
	}

	public String getBookmakerName1() {
		return bookmakerName1;
	}

	public String getScommessaBookmaker1() {
		return scommessaBookmaker1;
	}

	public Double getQuotaScommessaBookmaker1() {
		return quotaScommessaBookmaker1;
	}

	public String getBookmakerName2() {
		return bookmakerName2;
	}

	public String getScommessaBookmaker2() {
		return scommessaBookmaker2;
	}

	public Double getQuotaScommessaBookmaker2() {
		return quotaScommessaBookmaker2;
	}

	public String getNazione() {
		return nazione;
	}

	public String getLinkBook1() {
		return linkBook1;
	}

	public String getLinkBook2() {
		return linkBook2;
	}

	public Double getNetProfit() {
		return netProfit;
	}

	public Double getLiquidita2() {
		return liquidita2;
	}

	@Override
	public String getTelegramButtonText() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Double rating1_100 = getRating1() * 100D;
		DecimalFormat df = new DecimalFormat("#.##");
		
		return  ArbsUtil.getTelegramBoldString("Rating") + ": " + df.format(rating1_100) + "%" + System.lineSeparator()  + 
				ArbsUtil.getTelegramBoldString("Data evento") + ": " + sdf.format(getDataOraEvento()) + System.lineSeparator() +
				ArbsUtil.getTelegramBoldString("Evento") + ": " + getEvento() + System.lineSeparator() + 
				ArbsUtil.getTelegramBoldString("Book1")  + ": " + getBookmakerName1() + System.lineSeparator() +
				ArbsUtil.getTelegramBoldString("Book2") + ": " + getBookmakerName2() + System.lineSeparator() +
				ArbsUtil.getTelegramBoldString("Scommessa1") + ": " + ScommessaUtilManager.getScommessaByCodeTelegramMessage(getScommessaBookmaker1()) + System.lineSeparator() +
				ArbsUtil.getTelegramBoldString("Scommessa2") + ": " + ScommessaUtilManager.getScommessaByCodeTelegramMessage(getScommessaBookmaker2()) + System.lineSeparator() +
				ArbsUtil.getTelegramBoldString("Quota1") + ": " + getQuotaScommessaBookmaker1() + System.lineSeparator() +
				ArbsUtil.getTelegramBoldString("Quota2") + ": " + getQuotaScommessaBookmaker2() + System.lineSeparator() +
				ArbsUtil.getTelegramBoldString("Campionato") + ": " + getCampionato() + System.lineSeparator() + 
				ArbsUtil.getTelegramBoldString("Sport") + ": " + getSport();
	}	

}
