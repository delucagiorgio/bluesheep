package it.bluesheep.scraper;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.LogManager;

import org.apache.log4j.Logger;

public class ScraperMain {
	
	static Logger logger = Logger.getLogger(ScraperMain.class);

	
	public static void main(String[] args) {
		
		//reset all log predefined
		LogManager.getLogManager().reset();
		
		if (args != null && args.length > 0) {
			for (String filename : args) {
				logger.info("Starting scraping script " + filename);
				createFileJsonFromPHPScript(filename);
				logger.info("Scraping script " + filename + " execution terminated");

			}
		}
		logger.info("Scraping process terminated");

	}

	private static void createFileJsonFromPHPScript(String filename) {
		try {
			String line;
			StringBuilder output = new StringBuilder();
			Process p = Runtime.getRuntime().exec("php " + "/Users/giorgio/Desktop/BLUESHEEP/codes/" + filename + ".php");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

			while ((line = input.readLine()) != null) {
				output.append(line);
			}

			input.close();

			PrintWriter starVegasPath = new PrintWriter(
					new FileOutputStream("/Users/giorgio/Desktop/BLUESHEEP/codes/" + filename + ".json"));
			starVegasPath.write(output.toString());
			starVegasPath.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
