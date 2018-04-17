package it.bluesheep.entities.output;

import java.sql.Timestamp;

import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;

public abstract class RecordOutput {

	protected Timestamp dataOraEvento;
	protected Sport sport;
	protected String evento;
	protected String campionato;
	protected double rating;
	protected double riskFreeRating;
	protected String bookmakerName1;
	protected Scommessa scommessaBookmaker1;
	protected double quotaScommessaBookmaker1;
	protected String bookmakerName2;
	protected Scommessa scommessaBookmaker2;
	protected double quotaScommessaBookmaker2;
	
	public Timestamp getDataOraEvento() {
		return dataOraEvento;
	}
	public void setDataOraEvento(Timestamp dataOraEvento) {
		this.dataOraEvento = dataOraEvento;
	}
	public Sport getSport() {
		return sport;
	}
	public void setSport(Sport sport) {
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
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public double getRiskFreeRating() {
		return riskFreeRating;
	}
	public void setRiskFreeRating(double riskFreeRating) {
		this.riskFreeRating = riskFreeRating;
	}
	public String getBookmakerName1() {
		return bookmakerName1;
	}
	public void setBookmakerName1(String bookmakerName1) {
		this.bookmakerName1 = bookmakerName1;
	}
	public Scommessa getScommessaBookmaker1() {
		return scommessaBookmaker1;
	}
	public void setScommessaBookmaker1(Scommessa scommessaBookmaker1) {
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
	public Scommessa getScommessaBookmaker2() {
		return scommessaBookmaker2;
	}
	public void setScommessaBookmaker2(Scommessa scommessaBookmaker2) {
		this.scommessaBookmaker2 = scommessaBookmaker2;
	}
	public double getQuotaScommessaBookmaker2() {
		return quotaScommessaBookmaker2;
	}
	public void setQuotaScommessaBookmaker2(double quotaScommessaBookmaker2) {
		this.quotaScommessaBookmaker2 = quotaScommessaBookmaker2;
	}
	
}
