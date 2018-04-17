package it.bluesheep.util;

import org.json.*;

public class TxOddsBluesheepJsonConverter {
	
	private static TxOddsBluesheepJsonConverter instance;
	
	private TxOddsBluesheepJsonConverter() {}
	
	public synchronized static TxOddsBluesheepJsonConverter getTxOddsBluesheepJsonConverter() {
		if(instance == null) {
			instance = new TxOddsBluesheepJsonConverter();
		}
		return instance;
	}
	
	/**
	 * GD - 17/04/18 
	 * Ritorna l'oggetto JSON relativo ad un nodo specifico dell'attuale stringa JSON in analisi
	 * @param obj il JSON in analisi
	 * @param childNodeKey il nome del nodo JSON richiesto come oggetto di ritorno
	 * @return l'oggetto JSON con chiave pari a "childNode"
	 */
	public JSONObject getChildNodeByKey(JSONObject obj, String childNodeKey) {
		
		//costruisce l'oggetto JSON relativo al field passato come parametro (ChildNode)
		JSONObject returnObj = obj.getJSONObject(childNodeKey);
		
		return returnObj;
	}
	
	/**
	 * GD - 17/04/18 
	 * Ritorna una collezione di oggetti JSON relativa ad una specifica chiave dell'attuale stringa JSON in analisi
	 * @param obj l'oggetto JSON in analisi
	 * @param childNodeKey la chiave di cui si vuole ottenere la collezione di dati
	 * @return la collezione di dati avente chiave childNodeKey
	 */
	public JSONArray getChildNodeArrayByKey(JSONObject obj, String childNodeKey) {
		
		//costruisce un JSONArray relativo ad una collezione presente in un oggetto JSON
		JSONArray arrayNodes = obj.getJSONArray(childNodeKey);
		
		return arrayNodes;
	}
	
	/**
	 * GD - 17/04/18 
	 * Ritorna l'oggetto "@attributes" relativo all'oggetto passato come parametro
	 * @param obj l'oggetto di cui si vogliono ottenere le informazioni "@attributes"
	 * @return l'oggetto "@attributes" relativo all'oggetto JSON passato come parametro
	 */
	public JSONObject getAttributesNodeFromJSONObject(JSONObject obj) {
		return getChildNodeByKey(obj, "@attributes");
	}
	
	/**
	 * GD - 17/04/18 
	 * Ritorna la collezione di "match" relativa all'oggetto passato come parametro
	 * @param obj l'oggetto di cui si vogliono ottenere le informazioni "match"
	 * @return la collezione di "match" relativa all'oggetto JSON passato come parametro
	 */
	public JSONArray getAllMatchesFromJSONObjectRoot(JSONObject obj) {
		return getChildNodeArrayByKey(obj, "match");
	}

}
