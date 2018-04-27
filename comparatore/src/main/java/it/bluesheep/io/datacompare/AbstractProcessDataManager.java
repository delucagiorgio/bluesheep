package it.bluesheep.io.datacompare;

import java.util.List;

import it.bluesheep.entities.input.AbstractInputRecord;
import it.bluesheep.entities.output.RecordOutput;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datacompare.util.ChiaveEventoScommessaInputRecordsMap;
import it.bluesheep.util.TranslatorUtil;

public abstract class AbstractProcessDataManager implements IProcessDataManager {

	@Override
	public abstract List<RecordOutput> compareOdds(ChiaveEventoScommessaInputRecordsMap dataMap, Sport sport);
	
	/**
	 * GD - 18/04/18
	 * Metodo che verifica il rating in base al minimo richiesto da business
	 * @param scommessaInputRecord1 record scommessa
	 * @param scommessaInputRecord2 record scommessa opposta
	 * @return true, se il rating è >= al valore richiesto dal business, false altrimenti
	 */
	protected abstract double getRatingByScommessaPair(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2);
	
	/**
	 * GD - 25/04/18
	 * Mappa il record di output partendo dalle informazioni mappate input 
	 * @param scommessaInputRecord1 record 1
	 * @param scommessaInputRecord2 record 2
	 * @param rating il rating tra le due scommesse
	 * @return il record di output con le informazioni relative alle due scommesse e al loro rating
	 */
	protected abstract RecordOutput mapRecordOutput(AbstractInputRecord scommessaInputRecord1, AbstractInputRecord scommessaInputRecord2, double rating);
	
	protected RecordOutput translateFieldAboutCountry(RecordOutput recordOutput) {
		String campionato = recordOutput.getCampionato();
		if(campionato.startsWith("FB") || campionato.startsWith("WFB")) {
			String[] splittedCampionato = campionato.split(" ");
			if(splittedCampionato != null) {
				String countryCodeFootball = splittedCampionato[0].substring(splittedCampionato[0].length() - 3, splittedCampionato[0].length());
				if("INT".equalsIgnoreCase(countryCodeFootball)) {
					String[] playersArray = recordOutput.getEvento().split("|");
					String player1 = playersArray[0];
					String player2 = playersArray[1];
					String eventoNew = getTraduzioneByPlayer(player1) + "|" + getTraduzioneByPlayer(player2);
					recordOutput.setEvento(eventoNew);
				}else {
					String nation = getTraduzioneByNationCode(countryCodeFootball);
					recordOutput.setNazione(nation);
				}
			}
		}
		return recordOutput;
	}
	
	private String getTraduzioneByPlayer(String playerName) {
		return TranslatorUtil.getItalianTranslation(playerName);
	}
	
	private String getTraduzioneByNationCode(String nationCode) {
		return TranslatorUtil.getNationTranslation(nationCode);
	}

}
