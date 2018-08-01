package it.bluesheep.util.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.entities.util.ComparatoreConstants;
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
	
	public void zipLastRunLogFiles() throws IOException {
		
		//Formato della directory dei giorni
 		SimpleDateFormat sdfFileDir = new SimpleDateFormat("yyyyMMdd");
 		//Formato della directory delle settimane relative al mese
 		SimpleDateFormat sdfWeekFileDir = new SimpleDateFormat("yyyyMM");
 		
 		String fileDirDateFormatString = sdfFileDir.format(DirectoryFileUtilManager.TODAY);
 		String weekFileDirDateFormatString = sdfWeekFileDir.format(DirectoryFileUtilManager.TODAY) + "_" + DirectoryFileUtilManager.WEEK_OF_MONTH;
 		
 		String logOutputPath = BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.LOGGING_PATH);
 		
 		String weekLogOutputPath = logOutputPath + "/" + weekFileDirDateFormatString + "/";
 		String fileWeekLogOutputPath = weekLogOutputPath + "/" + fileDirDateFormatString + "/";
		
 		String fileLogPrefix = BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.LOG_PREFIX_FILENAME);
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
	 		String outputFileName = fileWeekLogOutputPath + ZIPPED_FILE_PREFIX + fileLogPrefix + sdfLog.format(DirectoryFileUtilManager.TODAY) + EXTENSION_FILE_ZIP;
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

}
