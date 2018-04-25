package it.bluesheep.entities.input;

import java.util.Calendar;
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
	protected String filler;
	
	public AbstractInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1, String partecipante2, String filler) {	
		if(dataOraEvento != null) {
			Calendar.getInstance().setTime(dataOraEvento);
		}
		this.dataOraEvento = dataOraEvento;
		this.campionato = campionato;
		this.partecipante1 = partecipante1;
		this.partecipante2 = partecipante2;
		this.sport = sport;
		this.filler = filler;
		this.keyEvento = "" + this.dataOraEvento + ":" + this.sport + ":" + this.partecipante1 + " vs " + this.partecipante2;
	}
	
	public AbstractInputRecord(AbstractInputRecord record) {
		if(record.getDataOraEvento() != null) {
			Calendar.getInstance().setTime(record.getDataOraEvento());
		}
		this.dataOraEvento = record.getDataOraEvento();
		this.sport = record.getSport();
		this.partecipante1 = record.getPartecipante1();
		this.partecipante2 = record.getPartecipante2();
		this.campionato = record.getCampionato();
		this.keyEvento = record.getKeyEvento();
		this.filler = record.getFiller();
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

	public String getFiller() {
		return filler;
	}

	public void setFiller(String filler) {
		this.filler = filler;
	}
	
	/**
	 * GD - metodo utilizzato per comparare due record e capire se si riferiscono allo stesso evento
	 * @param abstractInputRecord record da comparare
	 * @return true, se sono lo stesso evento, false altrimenti
	 */
	public abstract boolean isSameEventAbstractInputRecord (AbstractInputRecord abstractInputRecord);

	
	protected boolean isSameScommessa(Scommessa tipoScommessa2, Scommessa tipoScommessa3) {
		return tipoScommessa2.getCode().equals(tipoScommessa3.getCode());
	}

	protected boolean compareSport(Sport sport2, Sport sport3) {
		return sport2.getCode() == sport3.getCode();
	}

	protected boolean compareDate(Date dataOraEvento2, Date dataOraEvento3) {
		return ((dataOraEvento2.getTime() >= dataOraEvento3.getTime()) && (dataOraEvento2.getTime() - dataOraEvento3.getTime() < 3600000))
				|| ((dataOraEvento3.getTime() >= dataOraEvento2.getTime()) && (dataOraEvento3.getTime() - dataOraEvento2.getTime() < 3600000));
	}

	protected boolean compareParticipants(String p11, String p12, String p21, String p22) {
		return (p11.contains(p21) || p21.contains(p11)) && (p12.contains(p22) || p22.contains(p12));
	}

	
}
