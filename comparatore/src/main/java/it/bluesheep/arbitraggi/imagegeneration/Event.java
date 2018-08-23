package it.bluesheep.arbitraggi.imagegeneration;

import java.util.ArrayList;
import java.util.List;

import it.bluesheep.util.BlueSheepConstants;

/**
 * Event contiene le informazioni di un evento generico e le formatta per generare un html
 * @author Fabio
 *
 */
public abstract class Event {

	private String extractionTime;
	private String participant1;
	private String participant2;
	private String date;
	private String sport;
	private String country;
	private String championship;
	private List<String> linkBook;

	
	public Event(String participant1, String participant2, String date, String sport, String country,
			String championship, String extractionTime)  {
		super();
		this.participant1 = participant1;
		this.participant2 = participant2;
		this.sport = sport;
		this.date = date;
		this.country = country;
		this.championship = championship;
		this.setExtractionTime(extractionTime);
		this.linkBook = new ArrayList<String>();
	}
	
	public abstract void addRecord(String bookmaker1, String oddsType1, String odd1, String money1, String bookmaker2, String oddsType2, String odd2, String money2);
	
	public String toHtml(int index, int tot) {
		
		final String bootstrapPath = "./bootstrap.min.css";
		final String mystylePath = "./mystyle.css";
		final String sheepPath = "./img/bluesheep.png";
		
		String result = 
		"<html xmlns=\"http://www.bluesheep.it\">" +
		  "<head>" +
		  "<meta charset=\"UTF-8\">" +
		    "<meta content=\"\" />" +
		    "<title>" +
		      "Bluesheep" +
		    "</title>" +
		    "<meta name=\"description\" content=\"Arbitraggi\" />" +
		      "<link rel=\"stylesheet\" href=\"" + bootstrapPath + "\" />" +
		      "<link rel=\"stylesheet\" href=\"" + mystylePath + "\" />" +
		      "<link href=\"https://fonts.googleapis.com/css?family=Karla\" rel=\"stylesheet\">" + 
		  "</head>" +
		  "<body>" +
		    "<div class=\"container\">" +
		      "<div class=\"row header\">" +
		        "<div class=\"col goat-container header-side-col\">";
				
				if (getExtractionTime() != null) {
					result += "<h4 class=\"extraction-time\">" + 
					          	"Ora estrazione: <br />" + getExtractionTime() + 
					          "</h4>";
				}
				result += 
				"<span class=\"helper\"></span>" +
		          "<img class=\"big-goat\" src=\"" + sheepPath + "\" alt=\"Bluesheep\" />" +
		          "<img class=\"big-goat\" src=\"" + sheepPath + "\" alt=\"Bluesheep\" />" +
		          "<img class=\"big-goat\" src=\"" + sheepPath + "\" alt=\"Bluesheep\" />" +
		          "<br />" +
		          "<br />" +
		          "<br />" +
		          "<br />" +
		        "</div>" +
		        "<div class=\"col title-container header-main-col\">" +
		          "<h1>" +
		            getParticipant1() + " - " + getParticipant2() +
		          "</h1>" +
		          "<h2>" +
		            getChampionship() + 
		          "</h2>" +
		          "<h4>" +
		            getDate() +
		          "</h4>" +
		        "</div>" +
		        "<div class=\"col ball-container header-side-col\">" +
			        "<h4 class=\"extraction-time\">" + 
			        "            Segnalazione numero: <br />" + index + "/" + tot + 
			        "</h4>   " +
			        "<span class=\"helper\"></span>";
		
		result += drawHeaderBallImage();
		result += 
		        "</div>" +
		      "</div>" +
		    "</div>" +
		    "<br />" +
		    "<br />";
		result += insertTables();
		result += 
				"</body>" +
				"</html>";		
		return result;
	}
	
	protected abstract String insertTables();
	public abstract String drawHeaderBallImage();

	@Override
	public String toString() {
		String result = "Event:\nPARTICIPANT 1 = " + this.participant1 + "\n" + 
								 "PARTICIPANT 2 = " + this.participant2 + "\n" + 
								 "DATE = " + this.date + "\n" + 
								 "COUNTRY = " + this.country + "\n" + 
								 "CHAMPIONSHIP = " + this.championship + "\n\n";

		return result;
		
	}
	
	public boolean isSameEvent(Event e) {		
		if (this.participant1.equals(e.participant1) &&
				this.participant2.equals(e.participant2) &&
				this.date.equals(e.date) &&
				this.sport.equals(e.sport) &&
				this.getClass().equals(e.getClass()) &&
				this.championship.equals(e.championship)) {
			return true;
		}
		
		return false;
	}

	public String getParticipant1() {
		return participant1;
	}

	public void setParticipant1(String participant1) {
		this.participant1 = participant1;
	}

	public String getParticipant2() {
		return participant2;
	}

	public void setParticipant2(String participant2) {
		this.participant2 = participant2;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getChampionship() {
		return championship;
	}

	public void setChampionship(String championship) {
		this.championship = championship;
	}

	public String getExtractionTime() {
		return extractionTime;
	}

	public void setExtractionTime(String extractionTime) {
		this.extractionTime = extractionTime;
	}
	
	public String getUnifiedKeyAndLinks() {
		return participant1 + BlueSheepConstants.REGEX_CSV + 
				participant2 + BlueSheepConstants.REGEX_CSV + 
				date;
				
	}

	public List<String> getLinkBook() {
		return linkBook;
	}
}