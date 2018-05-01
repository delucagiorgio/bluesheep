package it.bluesheep.entities.input.util.betfair;

import java.util.Date;

public class EventoBetfair {
	
	private String partecipante1;
	private String partecipante2;
	private Date dataOraEvento;
	private String campionato;
	private String id;
	private String marketId;
	
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
	
	public Date getDataOraEvento() {
		return dataOraEvento;
	}
	
	public void setDataOraEvento(Date dataOraEvento) {
		this.dataOraEvento = dataOraEvento;
	}
	
	public String getCampionato() {
		return campionato;
	}
	
	public void setCampionato(String campionato) {
		this.campionato = campionato;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getMarketId() {
		return marketId;
	}

	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}

}
