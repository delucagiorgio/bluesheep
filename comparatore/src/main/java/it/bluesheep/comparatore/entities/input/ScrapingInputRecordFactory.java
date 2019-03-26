package it.bluesheep.comparatore.entities.input;

import it.bluesheep.comparatore.entities.input.record.BetflagInputRecord;
import it.bluesheep.comparatore.entities.input.record.GoldBetInputRecord;
import it.bluesheep.comparatore.entities.input.record.PinterBetInputRecord;
import it.bluesheep.comparatore.entities.input.record.StanleyBetInputRecord;
import it.bluesheep.comparatore.entities.input.record.StarVegasInputRecord;
import it.bluesheep.comparatore.serviceapi.Service;

public final class ScrapingInputRecordFactory {

	private ScrapingInputRecordFactory() {}
	
	public static AbstractScrapingInputRecord getCorrectScrapingInputRecord(Service service, AbstractScrapingInputRecord record) {
		switch(service) {
		case BETFLAG_SERVICENAME:
			return new BetflagInputRecord(record);
		case GOLDBET_SERVICENAME:
			return new GoldBetInputRecord(record);
		case PINTERBET_SERVICENAME:
			return new PinterBetInputRecord(record);
		case STANLEYBET_SERVICENAME:
			return new StanleyBetInputRecord(record);
		case STARVEGAS_SERVICENAME:
			return new StarVegasInputRecord(record);
			default:
				return null;
		}
	}
	
}
