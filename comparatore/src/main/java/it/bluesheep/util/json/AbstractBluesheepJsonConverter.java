package it.bluesheep.util.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

public abstract class AbstractBluesheepJsonConverter {
	
	protected static AbstractBluesheepJsonConverter instance;
		
	/**
	 * GD - 17/04/18 
	 * Ritorna l'oggetto JSON relativo ad un nodo specifico dell'attuale stringa JSON in analisi
	 * @param obj il JSON in analisi
	 * @param childNodeKey il nome del nodo JSON richiesto come oggetto di ritorno
	 * @return l'oggetto JSON con chiave pari a "childNode", null se non esiste o non è un JSONObject
	 */
	public JSONObject getChildNodeByKey(JSONObject obj, String childNodeKey) {
		
		//costruisce l'oggetto JSON relativo al field passato come parametro (ChildNode)
		JSONObject returnObj = obj.optJSONObject(childNodeKey);
		
		return returnObj;
	}
	
	/**
	 * GD - 17/04/18 
	 * Ritorna una collezione di oggetti JSON relativa ad una specifica chiave dell'attuale stringa JSON in analisi,
	 * null se il tipo restituito non è un ArrayJSON o se la chiave non esiste
	 * @param obj l'oggetto JSON in analisi
	 * @param childNodeKey la chiave di cui si vuole ottenere la collezione di dati
	 * @return la collezione di dati avente chiave childNodeKey, null se il tipo restituito non è un ArrayJSON o se la chiave non esiste
	 */
	public JSONArray getChildNodeArrayByKey(JSONObject obj, String childNodeKey) {
		
		//costruisce un JSONArray relativo ad una collezione presente in un oggetto JSON
		JSONArray arrayNodes = obj.optJSONArray(childNodeKey);
		
		//se il nodo figlio non è un array allora è un oggetto, lo aggiungo ad un array e ritorno l'oggetto
		if(arrayNodes == null) {
			JSONObject objectNode = obj.optJSONObject(childNodeKey);
			arrayNodes = new JSONArray();
			if(objectNode != null) {
				arrayNodes.put(objectNode.toMap());
			}
		}
		
		return arrayNodes;
	}
	
	/**
	 * GD - 22/04/18
	 * Metodo che ritorna l'oggetto JSON su cui è possibile successivamente operare sui vari dati
	 * @param jsonString la string Json che si vuole parsare
	 * @return l'oggetto JSONObject costruito sulla stringa passata come parametro
	 */
	public static JSONObject convertFromJSON(String jsonString) {
		return new JSONObject(jsonString);
	}
	
	/**
	 * GD - 22/04/18
	 * Metodo che ritorna la stringa JSON relativa all'oggetto passato come parametro
	 * @param obj l'oggetto da convertire in JSON
	 * @return la stringa JSON relativa all'oggetto passato come parametro
	 */
	public static String convertToJSON(Object obj) {
		return JSONWriter.valueToString(obj);
	}
	
	/**
	 * GD - 17/04/18 
	 * Ritorna la collezione di partite relativa all'oggetto passato come parametro
	 * @param obj l'oggetto di cui si vogliono ottenere le informazioni sulle partite
	 * @return la collezione di partite relativa all'oggetto JSON passato come parametro
	 */
	public abstract JSONArray getAllMatchesFromJSONObjectRoot(JSONObject obj);

	
}
