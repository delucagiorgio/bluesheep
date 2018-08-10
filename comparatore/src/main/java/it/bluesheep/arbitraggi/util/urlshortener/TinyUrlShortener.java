package it.bluesheep.arbitraggi.util.urlshortener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TinyUrlShortener {
	
	private static Map<String, String> urlShortenerMap;
	
	static {
		if(urlShortenerMap == null) {
			urlShortenerMap = new ConcurrentHashMap<String, String>();
		}
	}
	
	private TinyUrlShortener() {}
	
	public static String getShortenedURLFromLongURL(String longUrl) throws IOException {
        String returnUrl = longUrl;

		if(longUrl != null && !"null".equalsIgnoreCase(longUrl)) {
			String shortedURL = urlShortenerMap.get(longUrl);
			
			if(shortedURL == null) {
				Map<String, String> params = new HashMap<String, String>(); 
		        params.put("url", longUrl); 
	        	returnUrl = NetUtil.doPost("http://tinyurl.com/api-create.php", params);
		        
		        if(returnUrl != null) {
		        	urlShortenerMap.put(longUrl, returnUrl);
		        }
		        
			}else {
				returnUrl = shortedURL;
			}
		}
        return returnUrl;
	}

}
