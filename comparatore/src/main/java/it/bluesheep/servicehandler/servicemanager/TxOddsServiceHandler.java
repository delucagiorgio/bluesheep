package it.bluesheep.servicehandler.servicemanager;

import java.util.List;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.BlueSheepSharedResources;

public final class TxOddsServiceHandler extends AbstractBlueSheepServiceHandler {

	
	protected TxOddsServiceHandler() {
		super();
		logger = (new BlueSheepLogger(TxOddsServiceHandler.class)).getLogger();
		serviceName = Service.TXODDS_SERVICENAME;
	}

	/**
	 * Consideriamo TxOdds come il principale fornitore di quote, non è dunque necessario
	 * processare i dati relativi all'evento, ma saranno la base per gli altri servizi.
	 */
	@Override
	protected void startProcessingDataTransformation(List<AbstractInputRecord> inputRecordList) {
		super.addToChiaveEventoScommessaMap(inputRecordList);
	}
	
	@Override
	protected void startProcess() {
		//Set the smallest timestamp of the last requests before starting the process
		BlueSheepSharedResources.setTxOddsUpdateTimestamp(BlueSheepSharedResources.getTxOddsNowMinimumUpdateTimestamp());
		
		super.startProcess();
	}

}
