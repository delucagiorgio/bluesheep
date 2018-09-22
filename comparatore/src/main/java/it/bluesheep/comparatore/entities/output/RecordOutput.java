package it.bluesheep.comparatore.entities.output;

import java.util.Date;

public abstract class RecordOutput {

	protected Date dataOraEvento;
	protected String sport;
	protected String evento;
	protected String campionato;
	protected double rating1;
	protected String bookmakerName1;
	protected String scommessaBookmaker1;
	protected double quotaScommessaBookmaker1;
	protected String bookmakerName2;
	protected String scommessaBookmaker2;
	protected double quotaScommessaBookmaker2;
	protected String nazione;
	protected String linkBook1;
	protected String linkBook2;
	protected double netProfit;
	protected double liquidita1;
	protected double liquidita2;
	
	public Date getDataOraEvento() {
		return dataOraEvento;
	}
	
	public void setDataOraEvento(Date dataOraEvento) {
		this.dataOraEvento = dataOraEvento;
	}
	
	public String getSport() {
		return sport;
	}
	
	public void setSport(String sport) {
		this.sport = sport;
	}
	
	public String getEvento() {
		return evento;
	}
	
	public void setEvento(String evento) {
		this.evento = evento;
	}
	
	public String getCampionato() {
		return campionato;
	}
	
	public void setCampionato(String campionato) {
		this.campionato = campionato;
	}
	
	public double getRating() {
		return rating1;
	}
	
	public void setRating(double rating) {
		this.rating1 = rating;
	}
	
	public String getBookmakerName1() {
		return bookmakerName1;
	}
	
	public void setBookmakerName1(String bookmakerName1) {
		this.bookmakerName1 = bookmakerName1;
	}
	
	public String getScommessaBookmaker1() {
		return scommessaBookmaker1;
	}
	
	public void setScommessaBookmaker1(String scommessaBookmaker1) {
		this.scommessaBookmaker1 = scommessaBookmaker1;
	}
	
	public double getQuotaScommessaBookmaker1() {
		return quotaScommessaBookmaker1;
	}
	
	public void setQuotaScommessaBookmaker1(double quotaScommessaBookmaker1) {
		this.quotaScommessaBookmaker1 = quotaScommessaBookmaker1;
	}
	
	public String getBookmakerName2() {
		return bookmakerName2;
	}
	
	public void setBookmakerName2(String bookmakerName2) {
		this.bookmakerName2 = bookmakerName2;
	}
	
	public String getScommessaBookmaker2() {
		return scommessaBookmaker2;
	}
	
	public void setScommessaBookmaker2(String scommessaBookmaker2) {
		this.scommessaBookmaker2 = scommessaBookmaker2;
	}
	
	public double getQuotaScommessaBookmaker2() {
		return quotaScommessaBookmaker2;
	}
	
	public void setQuotaScommessaBookmaker2(double quotaScommessaBookmaker2) {
		this.quotaScommessaBookmaker2 = quotaScommessaBookmaker2;
	}

	public String getNazione() {
		return nazione;
	}

	public void setNazione(String nazione) {
		this.nazione = nazione;
	}

	public String getLinkBook1() {
		return linkBook1;
	}

	public void setLinkBook1(String linkBook1) {
		this.linkBook1 = linkBook1;
	}

	public String getLinkBook2() {
		return linkBook2;
	}

	public void setLinkBook2(String linkBook2) {
		this.linkBook2 = linkBook2;
	}

	public double getNetProfit() {
		return netProfit;
	}

	public void setNetProfit(double netProfit) {
		this.netProfit = netProfit;
	}

	public double getLiquidita1() {
		return liquidita1;
	}

	public void setLiquidita1(double liquidita) {
		this.liquidita1 = liquidita;
	}
	
	public double getLiquidita2() {
		return liquidita2;
	}

	public void setLiquidita2(double liquidita) {
		this.liquidita2 = liquidita;
	}
	
}
