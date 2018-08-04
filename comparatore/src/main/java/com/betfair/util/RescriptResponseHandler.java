package com.betfair.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import it.bluesheep.util.BlueSheepLogger;
import it.bluesheep.util.BlueSheepConstants;

public class RescriptResponseHandler implements ResponseHandler<String> {
   
	private static Logger logger;

	public RescriptResponseHandler() {
		logger = (new BlueSheepLogger(RescriptResponseHandler.class)).getLogger();
	}
	
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        StatusLine statusLine = response.getStatusLine();
        logger.log(Level.CONFIG, "Response status line = " + statusLine);
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() != 200) {

            String s = entity == null ? null : EntityUtils.toString(entity, BlueSheepConstants.ENCODING_UTF_8);
            logger.log(Level.WARNING, "Call to api-ng failed");

            logger.log(Level.SEVERE, s);
            System.exit(0);

        }

        return entity == null ? null : EntityUtils.toString(entity,BlueSheepConstants.ENCODING_UTF_8);
    }
}
