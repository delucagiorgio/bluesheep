package it.bluesheep.comparatore.entities.input;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import it.bluesheep.comparatore.entities.util.scommessa.Scommessa;
import it.bluesheep.comparatore.entities.util.sport.Sport;
import it.bluesheep.comparatore.io.datacompare.util.CosineSimilarityUtil;
import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

public abstract class AbstractInputRecord {

	protected Date dataOraEvento;
	protected Sport sport;
	protected String partecipante1;
	protected String partecipante2;
	protected String campionato;
	protected String keyEvento;
	protected String bookmakerName;
	protected double quota;
	protected Scommessa tipoScommessa;
	protected String filler;
	protected long timeOfInsertionInSystem;
	protected Service source;
	protected double liquidita;
	
	public AbstractInputRecord(Date dataOraEvento,Sport sport, String campionato, String partecipante1, String partecipante2, String filler) {	
		if(dataOraEvento != null) {
			Calendar.getInstance().setTime(dataOraEvento);
		}
		this.dataOraEvento = dataOraEvento;
		this.campionato = campionato;
		this.partecipante1 = partecipante1;
		this.partecipante2 = partecipante2;
		this.sport = sport;
		this.filler = filler;
		this.keyEvento = "" + this.dataOraEvento + BlueSheepConstants.REGEX_PIPE + this.sport + BlueSheepConstants.REGEX_PIPE + this.partecipante1 + BlueSheepConstants.REGEX_VERSUS + this.partecipante2;
		this.timeOfInsertionInSystem = System.currentTimeMillis();
	}
	
	public AbstractInputRecord(AbstractInputRecord record) {
		if(record.getDataOraEvento() != null) {
			Calendar.getInstance().setTime(record.getDataOraEvento());
		}
		this.dataOraEvento = record.getDataOraEvento();
		this.sport = record.getSport();
		this.partecipante1 = record.getPartecipante1();
		this.partecipante2 = record.getPartecipante2();
		this.campionato = record.getCampionato();
		this.keyEvento = record.getKeyEvento();
		this.filler = record.getFiller();
		this.timeOfInsertionInSystem = System.currentTimeMillis();
		this.liquidita = record.getLiquidita();
	}

	public Date getDataOraEvento() {
		return dataOraEvento;
	}

	public void setDataOraEvento(Date dataOraEvento) {
		this.dataOraEvento = dataOraEvento;
		this.keyEvento = "" + this.dataOraEvento + BlueSheepConstants.REGEX_PIPE + this.sport + BlueSheepConstants.REGEX_PIPE + this.partecipante1 + BlueSheepConstants.REGEX_VERSUS + this.partecipante2;
	}

	public Sport getSport() {
		return sport;
	}

	public void setSport(Sport sport) {
		this.sport = sport;
	}

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

	public String getCampionato() {
		return campionato;
	}

	public void setCampionato(String campionato) {
		this.campionato = campionato;
	}

	public String getKeyEvento() {
		return keyEvento;
	}

	public void setKeyEvento(String keyEvento) {
		this.keyEvento = keyEvento;
	}

	public String getBookmakerName() {
		return bookmakerName;
	}

	public void setBookmakerName(String bookmakerName) {
		this.bookmakerName = bookmakerName;
	}

	public double getQuota() {
		return quota;
	}

	public void setQuota(double quota) {
		this.quota = quota;
	}

	public Scommessa getTipoScommessa() {
		return tipoScommessa;
	}

	public void setTipoScommessa(Scommessa tipoScommessa) {
		this.tipoScommessa = tipoScommessa;
	}

	public String getFiller() {
		return filler;
	}

	public void setFiller(String filler) {
		this.filler = filler;
	}
	
	public long getTimeInsertionInSystem() {
		return timeOfInsertionInSystem;
	}

	public void setTimeInsertionInSystem(long time) {
		this.timeOfInsertionInSystem = time;
	}
	
	/**
	 * GD - metodo che compara la tipologia di scommessa
	 * @param tipoScommessa1 prima scommessa
	 * @param tipoScommessa2 seconda scommessa
	 * @return true, se le scommessa sono uguali, false altrimenti
	 */
	protected static boolean isSameScommessa(Scommessa tipoScommessa1, Scommessa tipoScommessa2) {
		return tipoScommessa1.getCode().equals(tipoScommessa2.getCode());
	}

	/**
	 * GD - metodo che compara la tipologia di sport
	 * @param sport1 primo sport
	 * @param sport2 secondo sport
	 * @return true, se gli sport sono gli stessi, false altrimenti
	 */
	public static boolean compareSport(Sport sport1, Sport sport2) {
		return sport1.getCode() == sport2.getCode();
	}

	/**
	 * GD - metodo che compara le date
	 * @param dataOraEvento1 prima data 
	 * @param dataOraEvento2 seconda data
	 * @return true, se le date sono identiche (con un errore di accettazione tra le due di UN'ORA), false altrimenti
	 */
	public static boolean compareDate(Date dataOraEvento1, Date dataOraEvento2) {
		return ((dataOraEvento1.getTime() >= dataOraEvento2.getTime()) && (dataOraEvento1.getTime() - dataOraEvento2.getTime() <= 3600000))
				|| ((dataOraEvento2.getTime() >= dataOraEvento1.getTime()) && (dataOraEvento2.getTime() - dataOraEvento1.getTime() <= 3600000));
	}

	/**
	 * GD - Metodo che compara i partecipanti
	 * @param exPartecipante1 partecipante1 del record di Exchange
	 * @param exPartecipante2 partecipante2 del record di Exchange
	 * @param bmPartecipante1 partecipante1 del record di Bookmaker
	 * @param bmPartecipante2 partecipante2 del record di Bookmaker
	 * @return
	 */
	public static boolean compareParticipants(String exPartecipante1, String exPartecipante2, String bmPartecipante1, String bmPartecipante2) {
		
		if(exPartecipante1 == null || exPartecipante2 == null || bmPartecipante1 == null || bmPartecipante2 == null) {
			return false;
		}
		
		boolean part1AllOrNoOneMinorCategory = allOrNoOneMinorCategory(exPartecipante1, bmPartecipante1);
		boolean part2AllOrNoOneMinorCategory = allOrNoOneMinorCategory(exPartecipante2, bmPartecipante2);
		
		if(!part1AllOrNoOneMinorCategory || !part2AllOrNoOneMinorCategory) {
			return false;
		}
		
		String participant11 = exPartecipante1.toLowerCase();
		String participant12 = exPartecipante2.toLowerCase();
		String participant21 = bmPartecipante1.toLowerCase();
		String participant22 = bmPartecipante2.toLowerCase();
		
		if ((participant11.contains(participant21) || participant21.contains(participant11)) && ((participant12.contains(participant22) || participant22.contains(participant12)))){
			return true;
		} else {
			
			String regex1 = null;
			String regex2 = null;
			
			if (participant11.contains(BlueSheepConstants.REGEX_COMMERCIAL_E) && participant12.contains(BlueSheepConstants.REGEX_COMMERCIAL_E)) {
				regex1 = BlueSheepConstants.REGEX_COMMERCIAL_E;
			}else if(participant11.contains(BlueSheepConstants.REGEX_SLASH) && participant12.contains(BlueSheepConstants.REGEX_SLASH)) {
				regex1 = BlueSheepConstants.REGEX_SLASH;
			}
			
			if (participant21.contains(BlueSheepConstants.REGEX_COMMERCIAL_E) && participant22.contains(BlueSheepConstants.REGEX_COMMERCIAL_E)) {
				regex2 = BlueSheepConstants.REGEX_COMMERCIAL_E;
			}else if(participant21.contains(BlueSheepConstants.REGEX_SLASH) && participant22.contains(BlueSheepConstants.REGEX_SLASH)) {
				regex2 = BlueSheepConstants.REGEX_SLASH;
			}
			
			if(regex1 != null && regex2 != null) {
				// bookmaker
				String memberb11 = participant11.split(regex1)[0].trim();
				String memberb12 = participant11.split(regex1)[1].trim();
				
				String memberb21 = participant12.split(regex1)[0].trim();
				String memberb22 = participant12.split(regex1)[1].trim();

				List<String> b1 = new ArrayList<String>();
				b1.add(memberb11);
				b1.add(memberb12);
				
				List<String> b2 = new ArrayList<String>();
				b2.add(memberb21);
				b2.add(memberb22);
				
				// betfair
				String memberbet11 = participant21.split(regex2)[0].trim();
				String memberbet12 = participant21.split(regex2)[1].trim();
				
				String memberbet21 = participant22.split(regex2)[0].trim();
				String memberbet22 = participant22.split(regex2)[1].trim();
				
				List<String> bet1 = new ArrayList<String>();
				bet1.add(memberbet11);
				bet1.add(memberbet12);
				
				List<String> bet2 = new ArrayList<String>();
				bet2.add(memberbet21);
				bet2.add(memberbet22);
							
				return equalLists(b1, bet1) && equalLists(b2, bet2);
				
			} else {
				return false;
			}
		}
	}
	
	private static boolean allOrNoOneMinorCategory(String playerBook1, String playerBook2) {
		Pattern patterMinorCategory = Pattern.compile("[uU][0-9][0-9]");
		boolean allTest = patterMinorCategory.matcher(playerBook1).find() && patterMinorCategory.matcher(playerBook2).find();
		boolean noTest = !patterMinorCategory.matcher(playerBook1).find() && !patterMinorCategory.matcher(playerBook2).find();

		return ((allTest && new Boolean(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.MINOR_CATEGORY_ONOFF))) || noTest);
	}

	private static boolean equalLists(List<String> one, List<String> two){     
	    if (one == null && two == null){
	        return true;
	    }

	    if((one == null && two != null) 
	      || one != null && two == null
	      || one.size() != two.size()){
	        return false;
	    }

	    //to avoid messing the order of the lists we will use a copy
	    //as noted in comments by A. R. S.
	    one = new ArrayList<String>(one); 
	    two = new ArrayList<String>(two);   

	    Collections.sort(one);
	    Collections.sort(two);      
	    return one.equals(two);
	}

	public boolean isSameEventSecondaryMatch(Date date, String sport, String partecipante1, String partecipante2) {
		if(partecipante1 != null && partecipante2 != null && 
				this.partecipante1 != null && this.partecipante2 != null &&
				this.sport.getCode().equals(sport) && compareDate(this.dataOraEvento, date)) {		
			CosineSimilarityUtil csu = new CosineSimilarityUtil();
			double cosSimPartecipant1 = csu.similarity(this.partecipante1, partecipante1);
			double cosSimPartecipant2 = csu.similarity(this.partecipante2, partecipante2);
			return cosSimPartecipant1 >= 0.8 && cosSimPartecipant2 >= 0.8;
		
		}
		return false;
	}

	public Service getSource() {
		return source;
	}

	public void setSource(Service source) {
		this.source = source;
	}

	public double getLiquidita() {
		return liquidita;
	}

	public void setLiquidita(double liquidita) {
		this.liquidita = liquidita;
	}
	
}
