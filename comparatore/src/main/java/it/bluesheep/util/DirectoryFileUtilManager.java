package it.bluesheep.util;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * La classe è stata pensata come riferimento unico per le variabili di tempo. Tutte le classi che hanno la necessità
 * di far riferimento all'istante di tempo in cui l'esecuzione è in corso dovrebbero utilizzare questa classe e 
 * aggiungere o modificare le informazioni presenti al suo interno.
 * @author Giorgio De Luca
 *
 */
public class DirectoryFileUtilManager {
	
	public static final int WEEK_OF_MONTH = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
	public static Date TODAY = new Date();
	
	/**
	 * GD - 30/04/18
	 * Il metodo controlla se dato un path esistono tutte le directory che lo compongono: nel caso 
	 * l'albero di cartella non è completo rispetto al path passato come parametro, crea le cartelle mancanti
	 * con lo stesso nome del path-parametro
	 * @param fileWeekLogOutputPath path da verificare e nel caso da creare
	 */
	public static void verifyDirectoryAndCreatePathIfNecessary(String fileWeekLogOutputPath) {
		TODAY = new Date();
		File directory = new File(fileWeekLogOutputPath);
		if(!directory.exists()) {
			directory.mkdirs();
		}
	}
}