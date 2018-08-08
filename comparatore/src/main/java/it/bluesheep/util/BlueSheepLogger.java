package it.bluesheep.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;

public class BlueSheepLogger {
	
	private Logger logger;
	
	public BlueSheepLogger(Class<?> loggerClass) {
		logger = Logger.getLogger(loggerClass.getSimpleName());
		logger.setUseParentHandlers(false);
		try {
			setInitialConfig(loggerClass.getSimpleName());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * GD - Setta la configurazione base del logger
	 * @throws SecurityException
	 * @throws IOException
	 */
	private synchronized void setInitialConfig(String name) throws SecurityException, IOException {
		String loggingMode = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.LOGGING_MODE_HANDLER);
        Handler handler = null;
        
        if(logger.getHandlers() == null || logger.getHandlers().length == 0) {
	        if(BlueSheepConstants.LOG_CONSOLE.equalsIgnoreCase(loggingMode)) {
	     		handler = new ConsoleHandler();
	     	}else if(BlueSheepConstants.LOG_FILE_OUTPUT.equalsIgnoreCase(loggingMode)){
	     		
	     		SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
	     		SimpleDateFormat sdfFileDir = new SimpleDateFormat("yyyyMMdd");
	     		SimpleDateFormat sdfWeekFileDir = new SimpleDateFormat("yyyyMM");
	     		
	     		String fileDateFormatString = sdfFile.format(DirectoryFileUtilManager.TODAY);
	     		String fileDirDateFormatString = sdfFileDir.format(DirectoryFileUtilManager.TODAY);
	     		String weekFileDirDateFormatString = sdfWeekFileDir.format(DirectoryFileUtilManager.TODAY) + "_" + DirectoryFileUtilManager.WEEK_OF_MONTH;
	     		
	     		String logFileNamePrefix = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.LOG_PREFIX_FILENAME);
	     		String logOutputFileName = logFileNamePrefix + fileDateFormatString + ".log";
	     		String logOutputPath = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.LOGGING_PATH);
	     		
	     		String weekLogOutputPath = logOutputPath + "/" + weekFileDirDateFormatString + "/";
	     		String fileWeekLogOutputPath = weekLogOutputPath + "/" + fileDirDateFormatString + "/";
	     		
	     		DirectoryFileUtilManager.verifyDirectoryAndCreatePathIfNecessary(fileWeekLogOutputPath);
	     		
	     		handler = new FileHandler(fileWeekLogOutputPath + logOutputFileName, true);
	     	}
	        
	     	handler.setFormatter(new SimpleFormatter() {
	     		private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
	     		private final String separatore = " - ";
	     		
	     		@Override
	     	    public synchronized String format(LogRecord record) {
	     			StringBuilder returnString = new StringBuilder();
	     			returnString.append("[" + df.format(new Date(record.getMillis())) + "]").append(separatore);
	     			returnString.append("[" + record.getLevel().getName() + "]").append(separatore);
	     			returnString.append(record.getSourceClassName()).append(separatore).append(record.getSourceMethodName()).append(separatore);
	     			returnString.append(record.getMessage());
	     			
	     			
	     			if(record.getThrown() != null) {
	     				returnString.append(" Exception trown details : ");
	     				returnString.append("\n");
	     				StringWriter sw = new StringWriter();
	     				record.getThrown().printStackTrace(new PrintWriter(sw));
	     				returnString.append(sw.toString());
	     			}
	     			returnString.append("\n");
	     			return returnString.toString();
	     		}

	     	});
			
	     	String loggerLevel = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.LOGGING_LEVEL_OUTPUT);
	    	
			handler.setLevel(Level.parse(loggerLevel));
	    	handler.setEncoding(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.ENCODING_UTF_8));
	    	
	    	logger.setLevel(Level.parse(loggerLevel));       
	    	logger.addHandler(handler); 
        }
	}

	public Logger getLogger() {
		return logger;
	}
}
