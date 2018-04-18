package it.bluesheep.entities.input;

import java.util.Date;

import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;

public abstract class AbstractInputRecord {

	protected Date dataOraEvento;
	protected Sport sport;
	protected String partecipante1;
	protected String partecipante2;
	protected String campionato;
	protected String keyEvento;
	protected String bookmakerName;
	protected double quota;
	protected Scommessa tipoScommessa;
	
	public AbstractInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1, String partecipante2) {
		this.dataOraEvento = dataOraEvento;
		this.campionato = campionato;
		this.partecipante1 = partecipante1;
		this.partecipante2 = partecipante2;
		this.sport = sport;
		this.keyEvento = "" + this.dataOraEvento + ":" + this.sport + ":" + this.partecipante1 + " vs " + this.partecipante2 
				+ ":" + this.campionato;
	}
	
	public AbstractInputRecord(AbstractInputRecord record) {
		this.dataOraEvento = record.getDataOraEvento();
		this.sport = record.getSport();
		this.partecipante1 = record.getPartecipante1();
		this.partecipante2 = record.getPartecipante2();
		this.campionato = record.getCampionato();
		this.keyEvento = record.getKeyEvento();
	}

	public Date getDataOraEvento() {
		return dataOraEvento;
	}

	public void setDataOraEvento(Date dataOraEvento) {
		this.dataOraEvento = dataOraEvento;
	}

	public Sport getSport() {
		return sport;
	}

	public void setSport(Sport sport) {
		this.sport = sport;
	}

	public String getPartecipante1() {
		return partecipante1;
	}

	public void setPartecipante1(String partecipante1) {
		this.partecipante1 = partecipante1;
	}

	public String getPartecipante2() {
		return partecipante2;
	}

	public void setPartecipante2(String partecipante2) {
		this.partecipante2 = partecipante2;
	}

	public String getCampionato() {
		return campionato;
	}

	public void setCampionato(String campionato) {
		this.campionato = campionato;
	}

	public String getKeyEvento() {
		return keyEvento;
	}

	public void setKeyEvento(String keyEvento) {
		this.keyEvento = keyEvento;
	}

	public String getBookmakerName() {
		return bookmakerName;
	}

	public void setBookmakerName(String bookmakerName) {
		this.bookmakerName = bookmakerName;
	}

	public double getQuota() {
		return quota;
	}

	public void setQuota(double quota) {
		this.quota = quota;
	}

	public Scommessa getTipoScommessa() {
		return tipoScommessa;
	}

	public void setTipoScommessa(Scommessa tipoScommessa) {
		this.tipoScommessa = tipoScommessa;
	}
	
	
	
}
