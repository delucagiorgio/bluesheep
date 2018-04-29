package it.bluesheep.util.json;

import org.json.JSONArray;
import org.json.JSONObject;

public class BetfairBluesheepJsonConverter extends AbstractBluesheepJsonConverter {
	
	private BetfairBluesheepJsonConverter() {
		super();
	}
	
	public synchronized static AbstractBluesheepJsonConverter getBetfairBluesheepJsonConverter() {
		if(instance == null) {
			instance = new BetfairBluesheepJsonConverter();
		}
		return instance;
	}

	@Override
	public JSONArray getAllMatchesFromJSONObjectRoot(JSONObject obj) {
		
		return null;
	}
	
}
