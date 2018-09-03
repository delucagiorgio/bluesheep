package it.bluesheep.entities.output.subtype;

import it.bluesheep.entities.output.RecordOutput;

public class RecordBookmakerVsBookmakerOdds extends RecordOutput {

	private double rating2;
	private double liquidita;
	
	public RecordBookmakerVsBookmakerOdds() {
		super();
		liquidita = -1D;
	}

	public double getRating2() {
		return rating2;
	}

	public void setRating2(double rating2) {
		this.rating2 = rating2;
	}

	public double getLiquidita() {
		return liquidita;
	}

	public void setLiquidita(double liquidita) {
		this.liquidita = liquidita;
	}
	
}
