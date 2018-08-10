package com.betfair.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import it.bluesheep.util.BlueSheepConstants;

public class RescriptResponseHandler implements ResponseHandler<String> {
   
	private static Logger logger;

	public RescriptResponseHandler() {
		logger = Logger.getLogger(RescriptResponseHandler.class);
	}
	
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        StatusLine statusLine = response.getStatusLine();
        logger.debug("Response status line = " + statusLine);
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() != 200) {
            String s = entity == null ? null : EntityUtils.toString(entity, BlueSheepConstants.ENCODING_UTF_8);
            logger.warn("Call to api-ng failed");
            logger.error(s);
        }

        return entity == null ? null : EntityUtils.toString(entity,BlueSheepConstants.ENCODING_UTF_8);
    }
}
