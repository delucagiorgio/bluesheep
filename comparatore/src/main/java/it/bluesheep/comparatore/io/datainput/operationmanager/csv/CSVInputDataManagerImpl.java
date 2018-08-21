package it.bluesheep.comparatore.io.datainput.operationmanager.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.bluesheep.comparatore.entities.input.AbstractInputRecord;
import it.bluesheep.comparatore.entities.input.record.CSVInputRecord;
import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datainput.IInputDataManager;
import it.bluesheep.comparatore.io.datainput.operationmanager.service.impl.InputDataManagerImpl;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepSharedResources;

/**
 * 
 * @author Giorgio De Luca	
 *
 */
public class CSVInputDataManagerImpl extends InputDataManagerImpl implements IInputDataManager{

	private static Long updateFrequencyDiff;
	
	private static final Integer SPORT = 0;
	private static final Integer CAMPIONATO = 1;
	private static final Integer DATA_ORA_EVENTO = 2;
	private static final Integer PARTECIPANTE1 = 3;
	private static final Integer PARTECIPANTE2 = 4;
	private static final Integer SCOMMESSA = 5;
	private static final Integer QUOTA = 6;
	private static final Integer BOOKMAKER = 7;
	
	private Map<Integer, Map<Integer, String>> idLineMapKeyValues;
	private String csvFilenamePath;
	
	
	public CSVInputDataManagerImpl(Sport sport) {
		super(sport);
		this.logger = Logger.getLogger(CSVInputDataManagerImpl.class);
		idLineMapKeyValues = new HashMap<Integer, Map<Integer, String>>();
		csvFilenamePath = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.CSV_ODDS_PATH_INPUT_FILE);
		updateFrequencyDiff = Long.valueOf(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.UPDATE_FREQUENCY)) * 1000L * 60L;
		this.serviceName = Service.CSV_SERVICENAME;
	}
	
	/**
	 * GD - 02/05/18
	 * Avvia il processo di input dati da CSV
	 * @return una lista di record pronti per la comparazione quote
	 */
	@Override
	public List<AbstractInputRecord> processAllData() {
		List<AbstractInputRecord> returnList = new ArrayList<AbstractInputRecord>();

		if(BlueSheepSharedResources.getCsvToBeProcessed()) {
		
			logger.info("Starting CSV input process");
			
			
			//ottengo tutte le linee del csv
			try {
				idLineMapKeyValues = getLines();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			
			for(Integer idLine : idLineMapKeyValues.keySet()) {
				AbstractInputRecord record = mapSplittedInfoIntoAbstractInputRecord(idLineMapKeyValues.get(idLine), idLine);
				if(record != null) {
					returnList.add(record);
				}
			}
			
			BlueSheepSharedResources.setCsvToBeProcessed(Boolean.FALSE);
			
		}else {
			logger.warn("CSV input data is suspended since data haven't changed from last run");
		}
		return returnList;
	}

	/**
	 * GD - 02/05/18 
	 * Mappa le informazioni della linea di CSV nel record di generico per essere processato nell'applicazione
	 * @param map la mappa key-value
	 * @param id l'id del record
	 * @return il record generico pronto per essere processato dall'applicazione, null se qualcosa va storto
	 */
	private AbstractInputRecord mapSplittedInfoIntoAbstractInputRecord(Map<Integer, String> map, Integer id) {
		String dataOraEventoString = map.get(DATA_ORA_EVENTO);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");
		AbstractInputRecord record = null;
		Date processingDate = new Date();
		try {
			Date dataOraEvento = sdf.parse(dataOraEventoString);
	
			if(dataOraEvento != null && (dataOraEvento.getTime() - processingDate.getTime() > updateFrequencyDiff)) {
				Sport sport = getCorrectSport(map.get(SPORT), id);
				if(sport.equals(this.sport)) {
					Scommessa scommessa = getCorrectScommessa(map.get(SCOMMESSA), id);
					String campionato = map.get(CAMPIONATO);
					String bookmaker = map.get(BOOKMAKER);
					String partecipante1 = map.get(PARTECIPANTE1);
					String partecipante2 = map.get(PARTECIPANTE2);
					double quota = new Double(map.get(QUOTA));
					record = new CSVInputRecord(dataOraEvento, sport, campionato, partecipante1, partecipante2, id.toString());
					record.setBookmakerName(bookmaker);
					record.setTipoScommessa(scommessa);
					record.setQuota(quota);
					record.setSource(serviceName);
				}
			}
		} catch (ParseException e) {
			logger.error("Line " + id + ": " + e.getMessage(), e);
		}	
		
		return record;
	}

	/**
	 * GD - 02/05/18
	 * Mappa la scommessa in base alla stringa passata. Se non valida, logga il valore passato e i valori accettati
	 * @param string la stringa utente contenente la scommessa
	 * @param id l'id del record
	 * @return l'entità Scommessa associata, null se non valida
	 */
	private Scommessa getCorrectScommessa(String string, Integer id) {
		Scommessa scommessa = null;
		if(Scommessa.NESSUNGOAL_U0X5.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.NESSUNGOAL_U0X5;
		}else if(Scommessa.ALPIU1GOAL_U1X5.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.ALPIU1GOAL_U1X5;
		}else if(Scommessa.ALPIU2GOAL_U2X5.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.ALPIU2GOAL_U2X5;
		}else if(Scommessa.ALPIU3GOAL_U3X5.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.ALPIU3GOAL_U3X5;
		}else if(Scommessa.ALPIU4GOAL_U4X5.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.ALPIU4GOAL_U4X5;
		}else if(Scommessa.ALMENO1GOAL_O0X5.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.ALMENO1GOAL_O0X5;
		}else if(Scommessa.ALMENO2GOAL_O1X5.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.ALMENO2GOAL_O1X5;
		}else if(Scommessa.ALMENO3GOAL_O2X5.getCode().equalsIgnoreCase(string)) {	
			scommessa = Scommessa.ALMENO3GOAL_O2X5;
		}else if(Scommessa.ALMENO4G0AL_O3X5.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.ALMENO4G0AL_O3X5;
		}else if(Scommessa.ALMENO5GOAL_O4X5.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.ALMENO5GOAL_O4X5;
		}else if(Scommessa.ENTRAMBISEGNANO_GOAL.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.ENTRAMBISEGNANO_GOAL;
		}else if(Scommessa.NESSUNOSEGNA_NOGOAL.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.NESSUNOSEGNA_NOGOAL;
		}else if(Scommessa.SFIDANTE1VINCENTE_1.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.SFIDANTE1VINCENTE_1;
		}else if(Scommessa.SFIDANTE2VINCENTE_2.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.SFIDANTE2VINCENTE_2;
		}else if(Scommessa.PAREGGIO_X.getCode().equalsIgnoreCase(string)) {
			scommessa = Scommessa.PAREGGIO_X;
		}else {
			logger.warn("Attention: Line with ID = " + id + " has no valid SCOMMESSA value field. Value inserted is = " + string + "; values accepted are = " + Scommessa.values().toString());
		}
		return scommessa;
	}

	/**
	 * GD - 02/05/18
	 * Mappa lo sport in base alla stringa passata. Se non valida, logga il valore passato e i valori accettati
	 * @param string la stringa utente contenente lo sports
	 * @param id l'id del record
	 * @return l'entità Sport associata, null se non valido
	 */
	private Sport getCorrectSport(String string, Integer id) {
		Sport sport = null;
		if(Sport.CALCIO.getCode().equalsIgnoreCase(string)) {
			sport = Sport.CALCIO;
		}else if (Sport.TENNIS.getCode().equalsIgnoreCase(string)) {
			sport = Sport.TENNIS;
		}else {
			logger.warn("Attention: Line with ID = " + id + " has no valid SPORT value field. Value inserted is = " + string + "; values accepted are = " + Sport.values().toString());
		}
		return sport;
	}

	/**
	 * GD - 02/05/2018
	 * Ottiene tutte le linee presenti all'interno del CSV e le memorizza in una mappa chiave-valore-idLinea
	 * @return la mappa con le informazioni relative a tutte le linee presenti nel file
	 * @throws IOException
	 */
	private Map<Integer, Map<Integer, String>> getLines() throws IOException {
		
		Map<Integer, Map<Integer, String>> mapToBeReturned = new HashMap<Integer, Map<Integer, String>>();;
		BufferedReader br = null;
		

		br = new BufferedReader(new FileReader(csvFilenamePath));
		String line = br.readLine();
		Integer i = new Integer(0);
		while(line != null) {
			try {
				logger.debug("Reading line : " + line);
				Map<Integer, String> keyValuesMap = getKeyValuesMapFromLine(line);
				mapToBeReturned.put(i, keyValuesMap);
				i = new Integer(i + 1);
			}catch(Exception e) {
				logger.error("Exception occurred during getLines in CSVInputDataManagerImpl : exception is :" + e.getMessage(), e);
			}
			line = br.readLine();
		}
		if(br != null) {
			br.close();
		}
		
		logger.info("CSV parsing process completed");
		
		return mapToBeReturned;
	}

	/**
	 * GD - 02/05/18
	 * Mappa i valori presenti all'interno del record CSV in una mappa associando ad ogni campo una posizione
	 * @param line la stringa da leggere
	 * @return la mappa chiave-valore
	 */
	private Map<Integer, String> getKeyValuesMapFromLine(String line) {
		
		Map<Integer, String> keyValueMap = new HashMap<Integer, String>();
		
		if(line != null) {
			String[] splittedLine = line.split(BlueSheepConstants.REGEX_CSV);
			keyValueMap.put(BOOKMAKER, splittedLine[BOOKMAKER]);
			keyValueMap.put(CAMPIONATO, splittedLine[CAMPIONATO]);
			keyValueMap.put(DATA_ORA_EVENTO, splittedLine[DATA_ORA_EVENTO]);
			keyValueMap.put(PARTECIPANTE1, splittedLine[PARTECIPANTE1]);
			keyValueMap.put(PARTECIPANTE2, splittedLine[PARTECIPANTE2]);
			keyValueMap.put(QUOTA, splittedLine[QUOTA]);
			keyValueMap.put(SCOMMESSA, splittedLine[SCOMMESSA]);
			keyValueMap.put(SPORT, splittedLine[SPORT]);
		}
		return keyValueMap;
	}

	@Override
	public List<AbstractInputRecord> mapJsonToAbstractInputRecord(String jsonString, Scommessa tipoScommessa,
			Sport sport) {
		return null;
	}

	@Override
	protected List<Scommessa> getCombinazioniSportScommessa(Sport sport) {
		return null;
	}
}