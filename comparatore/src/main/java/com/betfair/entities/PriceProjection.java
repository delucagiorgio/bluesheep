package com.betfair.entities;

import java.util.Set;

import com.betfair.enums.types.PriceData;


public class PriceProjection {
	private Set<PriceData> priceData;
	private ExBestOfferOverRides exBestOfferOverRides;
	private boolean virtualise;
	private boolean rolloverStakes;

	public Set<PriceData> getPriceData() {
		return priceData;
	}

	public void setPriceData(Set<PriceData> priceData) {
		this.priceData = priceData;
	}

	public ExBestOfferOverRides getExBestOfferOverRides() {
		return exBestOfferOverRides;
	}

	public void setExBestOfferOverRides(
			ExBestOfferOverRides exBestOfferOverRides) {
		this.exBestOfferOverRides = exBestOfferOverRides;
	}

	public boolean isVirtualise() {
		return virtualise;
	}

	public void setVirtualise(boolean virtualise) {
		this.virtualise = virtualise;
	}

	public boolean isRolloverStakes() {
		return rolloverStakes;
	}

	public void setRolloverStakes(boolean rolloverStakes) {
		this.rolloverStakes = rolloverStakes;
	}

}
