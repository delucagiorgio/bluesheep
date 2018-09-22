package it.bluesheep.comparatore.entities.output.subtype;

import it.bluesheep.comparatore.entities.output.RecordOutput;

public class RecordBookmakerVsBookmakerOdds extends RecordOutput {

	private double rating2;
	
	public RecordBookmakerVsBookmakerOdds() {
		super();
		this.liquidita1 = -1;
		this.liquidita2 = -1;
	}

	public double getRating2() {
		return rating2;
	}

	public void setRating2(double rating2) {
		this.rating2 = rating2;
	}
	
}
