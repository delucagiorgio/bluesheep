package it.bluesheep.arbitraggi.imagegeneration;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;


/**
 * Converte Html in png
 * @author Fabio
 *
 */
public class Html2PngConverter {
	
	private WebDriver driver;  
	private static Logger logger = Logger.getLogger(Html2PngConverter.class);
	private final static String LOGGING_FILE_FAKE = "../xhtml/logs.txt";


	public Html2PngConverter() {		

//		System.setProperty("webdriver.gecko.driver", "/Users/giorgio/Downloads/geckodriver");	

		System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
		System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, LOGGING_FILE_FAKE);
	}
	
	public void convert (String inputFile, String outputFileName) {
		try {
	        logger.info("Start taking a screenshot for input file : " + inputFile);
	        long startTime = System.currentTimeMillis();
//			String pathToHtmlLocation = "/Users/giorgio/git/bluesheep";
			String pathToHtmlLocation = "/home/bluesheep/java";
	        String htmlLocation = "file://" + pathToHtmlLocation + inputFile.substring(2);

			FirefoxBinary firefoxBinary = new FirefoxBinary();
			firefoxBinary.addCommandLineOptions("--headless");
			FirefoxOptions firefoxOptions = new FirefoxOptions();
	        firefoxOptions.setBinary(firefoxBinary);
	        LogManager.getLogManager().reset();

	    	// Apri Firefox
	        driver = new FirefoxDriver(firefoxOptions);
	        // Vai alla pagina
	    	driver.get(htmlLocation);
	    	// Metti a tutto schermo
	        driver.manage().window().maximize();
	        //driver.manage().window().setSize(new Dimension(1200, 675));
	        
	        
	        // Fai lo screen
	        takeScreenshot(outputFileName);
	        

	        // Chiudi il browser
//	        driver.close();
	        driver.quit();
	        logger.info("Screenshot executed in " + (startTime - System.currentTimeMillis()) + " ms");

	        HtmlFileHandler htmlFileHandler = new HtmlFileHandler();
	        htmlFileHandler.delete(LOGGING_FILE_FAKE);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	 private void takeScreenshot(String outputFileName) {
	        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	        try {
	        	File f = new File(outputFileName);
				FileUtils.copyFile(scrFile, f);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
	    }
}