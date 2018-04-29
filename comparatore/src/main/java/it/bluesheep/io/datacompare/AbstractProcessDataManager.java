package it.bluesheep.io.datacompare;

import java.util.List;
import java.util.logging.Logger;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.TranslatorUtil;

public abstract class AbstractProcessDataManager implements IProcessDataManager {

	protected static Logger logger;
	
	protected AbstractProcessDataManager() {
		logger = (new BlueSheepLogger(AbstractProcessDataManager.class)).getLogger();	
	}

	
	@Override
	public abstract List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport);
	
	/**
	 * GD - 18/04/18
	 * Metodo che verifica il rating1 in base al minimo richiesto da business
	 * @param scommessaInputRecord1 record scommessa
	 * @param scommessaInputRecord2 record scommessa opposta
	 * @return true, se il rating1 è >= al valore richiesto dal business, false altrimenti
	 */
	protected abstract double getRatingByScommessaPair(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2);
	
	/**
	 * GD - 25/04/18
	 * Mappa il record di output partendo dalle informazioni mappate input 
	 * @param scommessaInputRecord1 record 1
	 * @param scommessaInputRecord2 record 2
	 * @param rating1 il rating1 tra le due scommesse
	 * @return il record di output con le informazioni relative alle due scommesse e al loro rating1
	 */
	protected abstract RecordOutput mapRecordOutput(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2, double rating);
	
	protected RecordOutput translateFieldAboutCountry(RecordOutput recordOutput) {
		String campionato = recordOutput.getCampionato();
		if(campionato.startsWith("FB") || campionato.startsWith("WFB")) {
			String[] splittedCampionato = campionato.split(" ");
			if(splittedCampionato != null) {
				int startIndex = 2;
				if(campionato.startsWith("WFB")) {
					startIndex = 3;
				}
				String countryCodeFootball = splittedCampionato[0].substring(startIndex, splittedCampionato[0].length());
				String nation = getTraduzioneByNationCode(countryCodeFootball);
				if("INT".equalsIgnoreCase(countryCodeFootball)) {
					String[] eventoSplitted = recordOutput.getEvento().split("|");
					String partecipante1 = getTraduzioneItaliana(eventoSplitted[0]);
					String partecipante2 = getTraduzioneItaliana(eventoSplitted[1]);
					recordOutput.setEvento(partecipante1 + "|" + partecipante2); 
				}
				recordOutput.setNazione(nation);
			}
		}
		return recordOutput;
	}
	
	private String getTraduzioneByNationCode(String nationCode) {
		return TranslatorUtil.getNationTranslation(nationCode);
	}

	private String getTraduzioneItaliana(String toBeTranslatedString) {
		return TranslatorUtil.getItalianTranslation(toBeTranslatedString);
	}

}
