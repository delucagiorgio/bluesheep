package it.bluesheep.util;

import it.bluesheep.comparatore.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;

public class BlueSheepSharedResources {
	
	private static ChiaveEventoScommessaInputRecordsMap eventoScommessaRecordMap = new ChiaveEventoScommessaInputRecordsMap();
	private static Long txOddsUpdateTimestamp = new Long(-1);
	private static Long txOddsNowMinimumUpdateTimestamp = new Long(-1);
	
	public static ChiaveEventoScommessaInputRecordsMap getEventoScommessaRecordMap() {
		return eventoScommessaRecordMap;
	}

	public static Long getTxOddsUpdateTimestamp() {
		return txOddsUpdateTimestamp;
	}

	public static void setTxOddsUpdateTimestamp(Long txOddsNowMinimumUpdateTimestamp) {
		BlueSheepSharedResources.txOddsUpdateTimestamp = txOddsNowMinimumUpdateTimestamp;
	}

	public static Long getTxOddsNowMinimumUpdateTimestamp() {
		return txOddsNowMinimumUpdateTimestamp;
	}

	public static void setTxOddsNowMinimumUpdateTimestamp(Long txOddsNowMinimumUpdateTimestamp) {
		BlueSheepSharedResources.txOddsNowMinimumUpdateTimestamp = txOddsNowMinimumUpdateTimestamp;
	}

}
