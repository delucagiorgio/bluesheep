package it.bluesheep.arbitraggi.imagegeneration;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import it.bluesheep.arbitraggi.entities.ArbsRecord;
import it.bluesheep.arbitraggi.util.urlshortener.TinyUrlShortener;
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
	private Set<String> linkBook;

	
	public Event(ArbsRecord arbsRecord, String extractionTime) {
		this.participant1 = arbsRecord.getParticipant1();
		this.participant2 = arbsRecord.getParticipant2();
		this.date = arbsRecord.getDate().toString();
		this.country = arbsRecord.getCountry();
		this.championship = arbsRecord.getChampionship();
		this.setExtractionTime(extractionTime);
		this.sport = arbsRecord.getSport();
		if(linkBook == null) {
			linkBook = new HashSet<String>();
		}
		String linkBook1 = arbsRecord.getLink1();
		String linkBook2 = arbsRecord.getLink2();
		try {
			if(!"null".equals(linkBook1) && linkBook1 != null) {
				linkBook1 = TinyUrlShortener.getShortenedURLFromLongURL(linkBook1);
			}
			if(!"null".equals(linkBook2)  && linkBook2 != null) {
				linkBook2 = TinyUrlShortener.getShortenedURLFromLongURL(linkBook2);
			}
		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
		}
		if(linkBook1 != null && !"null".equals(linkBook1) && !linkBook1.isEmpty() &&  linkBook2 != null && !"null".equals(linkBook2) && !linkBook2.isEmpty()) {
			this.getLinkBook().add(arbsRecord.getBookmaker1() + BlueSheepConstants.KEY_SEPARATOR + linkBook1);
			this.getLinkBook().add(arbsRecord.getBookmaker2() + BlueSheepConstants.KEY_SEPARATOR + linkBook2);
		}
	}
	
    public abstract void addRecord(ArbsRecord arbsRecord);
    
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

	public Set<String> getLinkBook() {
		return linkBook;
	}
}