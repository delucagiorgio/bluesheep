package it.bluesheep.util.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

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
	
	private static final String EXTENSION_FILE_ZIP = ".zip";
	private static final String ZIPPED_FILE_PREFIX = "ZIPPED_JSON_";
	private static final String EXTENSION_FILE_JSON = ".json";
	private Logger logger;
	
	public ZipUtil() {
		logger = Logger.getLogger(ZipUtil.class);
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
				logger.error(e.getMessage(), e);			
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
			logger.error(e.getMessage(), e);			
		}
	}
}
