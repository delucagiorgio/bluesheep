package it.bluesheep.util.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;
import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.DirectoryFileUtilManager;

/**
 * Classe di util per organizzare secondo una certa logica i file di Log delle varie esecuzioni dell'applicazione
 * -30/04/18 : la politica di aggregazione dei file prevede di raggruppare i file secondo la gerarchia di cartelle seguente
 * 				LOGGING_PATH/yyyyMM_idSettimana/yyyyMMdd/outputFileName
 * 
 * NB:
 * -outputFilename = fileLogPath + ZIPPED_FILE_PREFIX + fileLogPrefix + sdfLog.format(today) + EXTENSION_FILE_ZIP;
 * @author Giorgio De Luca
 *
 */
public class ZipUtil {
	
	private static final String EXTENSION_FILE_LOG = ".log";
	private static final String EXTENSION_FILE_ZIP = ".zip";
	private static final String ZIPPED_FILE_PREFIX = "ZIPPED_LOG_";
	private static final String EXTENSION_FILE_JSON = ".json";
	private Logger logger;
	
	public ZipUtil() {
		logger = (new BlueSheepLogger(ZipUtil.class)).getLogger();
	}
	
	public void zipLastRunLogFiles() throws IOException {
		Date now = new Date();
		//Formato della directory dei giorni
 		SimpleDateFormat sdfFileDir = new SimpleDateFormat("yyyyMMdd");
 		//Formato della directory delle settimane relative al mese
 		SimpleDateFormat sdfWeekFileDir = new SimpleDateFormat("yyyyMM");
 		
 		String fileDirDateFormatString = sdfFileDir.format(now);
 		String weekFileDirDateFormatString = sdfWeekFileDir.format(now) + "_" + DirectoryFileUtilManager.WEEK_OF_MONTH;
 		
 		String logOutputPath = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.LOGGING_PATH);
 		
 		String weekLogOutputPath = logOutputPath + "/" + weekFileDirDateFormatString + "/";
 		String fileWeekLogOutputPath = weekLogOutputPath + "/" + fileDirDateFormatString + "/";
		
 		String fileLogPrefix = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.LOG_PREFIX_FILENAME);
 		String filenameTodayDateString = fileLogPrefix + fileDirDateFormatString;
 		
 		File dirLogFiles = new File(fileWeekLogOutputPath);
 		File [] logFiles = dirLogFiles.listFiles(new FilenameFilter() {
 		    @Override
 		    public boolean accept(File dir, String name) {
 		        return name.startsWith(filenameTodayDateString) && 
 		        		name.endsWith(EXTENSION_FILE_LOG);
 		    }
 		});
 		
 		if(logFiles != null && logFiles.length != 0) {
	 		SimpleDateFormat sdfLog = new SimpleDateFormat("yyyyMMdd_HHmm");
	 		String outputFileName = fileWeekLogOutputPath + ZIPPED_FILE_PREFIX + fileLogPrefix + sdfLog.format(now) + EXTENSION_FILE_ZIP;
	 		File zipLog = new File(outputFileName);
	 		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipLog));
	 		
	 		for(File logFile : logFiles) {
		 		ZipEntry entryZip = new ZipEntry(logFile.getName());
		 		out.putNextEntry(entryZip);
		
		 		byte[] data = Files.readAllBytes(logFile.toPath());
		 		
		 		out.write(data, 0, data.length);
		 		out.closeEntry();
	 		}
	 		out.close();
	 		
	 		//rimuovo i file "*.log" presenti nella directory
	 		int sizeFileList = logFiles.length;
	 		for(int i = 0; i < sizeFileList; i++) {
	 			logFiles[i].delete();
	 		}
 		}
	}
	
	public void zipOldJsonFiles(String pathOutfileFile) {
		Date now = new Date();
		//Formato della directory dei giorni
 		SimpleDateFormat sdfFileDir = new SimpleDateFormat("yyyy-MM-dd");
 		String fileDirDateFormatString = sdfFileDir.format(now);
 		
 		String directoryToMoveZippedFile = pathOutfileFile + fileDirDateFormatString + "/";
 		
 		DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary(directoryToMoveZippedFile);
 		
 		File dirJsonFiles = new File(pathOutfileFile);
 		File [] jsonFilesList = dirJsonFiles.listFiles(new FilenameFilter() {
 		    @Override
 		    public boolean accept(File dir, String name) {
 		        return name.endsWith(EXTENSION_FILE_JSON);
 		    }
 		});
 		File latestFile = getLatestFile(jsonFilesList);
 		
 		zipFilesExceptNewOne(latestFile, jsonFilesList, directoryToMoveZippedFile);

 		int jsonFileListSize = jsonFilesList.length;
 		
 		for(int i = 0; i < jsonFileListSize; i++) {
 			if(jsonFilesList[i] != latestFile) {
 				jsonFilesList[i].delete();
 			}
 		}
	}

	/**
	 * GD - 08/08/2018
	 * Ritorna il file creato più recentemente data una lista di file
	 * @param jsonFilesList la lista di file
	 * @return il file più recente a livello di creazione
	 */
	private File getLatestFile(File[] jsonFilesList) {
 		SimpleDateFormat fileSdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
 		File latestFile = null;
 		Date dateOfLatestFile = null;
 		for(File fileJson : jsonFilesList) {
 			try {
				Date dateOfFile = fileSdf.parse(fileJson.getName());
				if(latestFile == null || dateOfLatestFile.before(dateOfFile)) {
					latestFile = fileJson;
					dateOfLatestFile = dateOfFile;
				}
			} catch (ParseException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);			
			}
 		}
		return latestFile;
	}

	/**
	 * GD - 08/08/2018
	 * Zippa nel percorso passato come parametro i file JSON che sono presenti nella directory, ad esclusione del più recente.
	 * @param latestFile l'ultimo file prodotto
	 * @param jsonFilesList i file presenti nella directory
	 * @param directoryToMoveZippedFile la directory dove spostare i file zippati
	 */
	private void zipFilesExceptNewOne(File latestFile, File[] jsonFilesList, String directoryToMoveZippedFile) {
 		try {
 	 		ZipOutputStream out = null;
	 		for(File jsonFile : jsonFilesList) {
	 			if(latestFile != jsonFile) {
		 	 		String outputFileName = directoryToMoveZippedFile + ZIPPED_FILE_PREFIX + 
		 	 				jsonFile.getName().substring(0, jsonFile.getName().length() - 5) + EXTENSION_FILE_ZIP;
		 	 		
		 	 		File zipJson = new File(outputFileName);
					out = new ZipOutputStream(new FileOutputStream(zipJson));
			 		ZipEntry entryZip = new ZipEntry(jsonFile.getName());
			 		out.putNextEntry(entryZip);
			 		
			 		byte[] data = Files.readAllBytes(jsonFile.toPath());
			 		out.write(data, 0, data.length);
			 		out.closeEntry();
	 			}
	 		}
	 		if(out != null) {
	 			out.close();
	 		}
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);			
		}
	}
}
