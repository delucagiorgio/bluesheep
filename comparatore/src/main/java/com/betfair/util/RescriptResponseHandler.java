package com.betfair.util;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import it.bluesheep.util.BlueSheepLogger;

public class RescriptResponseHandler implements ResponseHandler<String> {
   
	private static Logger logger;
	private static final String ENCODING_UTF_8 = "UTF-8";

	public RescriptResponseHandler() {
		logger = (new BlueSheepLogger(RescriptResponseHandler.class)).getLogger();
	}
	
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        StatusLine statusLine = response.getStatusLine();
        logger.warning("Response status line = " + statusLine);
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() != 200) {

            String s = entity == null ? null : EntityUtils.toString(entity, ENCODING_UTF_8);
            logger.warning("Call to api-ng failed");

            logger.info("Error returned is " + s);
            System.exit(0);

        }

        return entity == null ? null : EntityUtils.toString(entity,ENCODING_UTF_8);
    }
}
