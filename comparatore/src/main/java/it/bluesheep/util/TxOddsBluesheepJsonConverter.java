package it.bluesheep.util;

import org.json.JSONArray;
import org.json.JSONObject;

public class TxOddsBluesheepJsonConverter extends AbstractBluesheepJsonConverter {
	
	private TxOddsBluesheepJsonConverter() {
		super();
	}
	
	public synchronized static AbstractBluesheepJsonConverter getTxOddsBluesheepJsonConverter() {
		if(instance == null) {
			instance = new TxOddsBluesheepJsonConverter();
		}
		return instance;
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
	
	@Override
	public JSONArray getAllMatchesFromJSONObjectRoot(JSONObject obj) {
		return getChildNodeArrayByKey(obj, "match");
	}

}
