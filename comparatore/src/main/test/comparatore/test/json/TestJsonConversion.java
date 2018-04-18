package comparatore.test.json;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

import it.bluesheep.entities.util.scommessa.Scommessa;
import it.bluesheep.entities.util.sport.Sport;
import it.bluesheep.io.datainput.IInputDataManager;
import it.bluesheep.io.datainput.operationmanager.impl.BookmakerVsBookmakerInputDataManagerImpl;

public class TestJsonConversion {
	
	@Test
	public void testJsonConversion() {
		String inputJson = "";
		InputStream inStream = null;
		BufferedReader br = null;
		try {
			inStream = TestJsonConversion.class.getResourceAsStream("/CALCIO_TOTAL.txt");
			br = new BufferedReader(new InputStreamReader(inStream));
			String inputLine = br.readLine();
			while(inputLine != null) {
				inputJson = inputJson + inputLine;
				inputLine = br.readLine();
			}
			br.close();
			inStream.close();
		}catch(Exception e) {
			System.out.println("Exception is " + e.getMessage());
		}
		
		
		
		IInputDataManager inputDataManager = new BookmakerVsBookmakerInputDataManagerImpl();
		
		inputDataManager.mapJsonToAbstractInputRecord(inputJson, Scommessa.ALMENO1GOAL_O0X5, Sport.CALCIO);	
	}

}
