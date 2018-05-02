package it.bluesheep.util.json;

import org.json.JSONArray;
import org.json.JSONObject;

public class Bet365BluesheepJsonConverter extends AbstractBluesheepJsonConverter {

	private Bet365BluesheepJsonConverter() {
		super();
	}
	
	public synchronized static AbstractBluesheepJsonConverter getBet365BluesheepJsonConverter() {
		if(instance == null) {
			instance = new Bet365BluesheepJsonConverter();
		}
		return instance;
	}

	@Override
	public JSONArray getAllMatchesFromJSONObjectRoot(JSONObject obj) {
		
		return null;
	}

}
