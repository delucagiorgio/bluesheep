package it.bluesheep.util.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import it.bluesheep.BlueSheepComparatoreMain;

public class ZipUtil {
	
	private static final String EXTENSION_FILE_LOG = ".log";
	private static final String EXTENSION_FILE_ZIP = ".zip";
	private static final String ZIPPED_FILE_PREFIX = "ZIPPED_LOG_";
	
	public void zipLastRunLogFiles() throws IOException {
		Date today = new Date(System.currentTimeMillis());
 		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
 		String fileLogPrefix = BlueSheepComparatoreMain.getProperties().getProperty("LOG_PREFIX_FILENAME");
 		String fileLogPath = BlueSheepComparatoreMain.getProperties().getProperty("LOGGING_PATH");
 		String filenameTodayDateString = fileLogPrefix + sdf.format(today);
 		
 		File dirLogFiles = new File(fileLogPath);
 		File [] logFiles = dirLogFiles.listFiles(new FilenameFilter() {
 		    @Override
 		    public boolean accept(File dir, String name) {
 		        return name.startsWith(filenameTodayDateString) && 
 		        		name.endsWith(EXTENSION_FILE_LOG);
 		    }
 		});
 		
 		if(logFiles != null && logFiles.length != 0) {
	 		SimpleDateFormat sdfLog = new SimpleDateFormat("yyyyMMdd_HHmm");
	 		File zipLog = new File(fileLogPath + ZIPPED_FILE_PREFIX + fileLogPrefix + sdfLog.format(today) + EXTENSION_FILE_ZIP);
	 		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipLog));
	 		
	 		for(File logFile : logFiles) {
		 		ZipEntry entryZip = new ZipEntry(logFile.getName());
		 		out.putNextEntry(entryZip);
		
		 		byte[] data = Files.readAllBytes(logFile.toPath());
		 		
		 		out.write(data, 0, data.length);
		 		out.closeEntry();
	 		}
	 		out.close();
	 		
	 		int sizeFileList = logFiles.length;
	 		for(int i = 0; i < sizeFileList; i++) {
	 			logFiles[i].delete();
	 		}
 		}
	}

}
