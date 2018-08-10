package com.betfair.api;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

import com.betfair.util.RescriptResponseHandler;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;

public class HttpUtil {

    private static final String HTTP_HEADER_X_APPLICATION = "X-Application";
    private static final String HTTP_HEADER_X_AUTHENTICATION = "X-Authentication";
    private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HTTP_HEADER_ACCEPT = "Accept";
    private static final String HTTP_HEADER_ACCEPT_CHARSET = "Accept-Charset";
    private static Logger logger;

    public HttpUtil() {
        super();
        logger = Logger.getLogger(HttpUtil.class);
    }

    private String sendPostRequest(String param, String appKey, String ssoToken, String URL, ResponseHandler<String> reqHandler) {
        HttpPost post = new HttpPost(URL);
        String resp = null;
        try {
            post.setHeader(HTTP_HEADER_CONTENT_TYPE, BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_APPLICATION_JSON));
            post.setHeader(HTTP_HEADER_ACCEPT, BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_APPLICATION_JSON));
            post.setHeader(HTTP_HEADER_ACCEPT_CHARSET, BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.ENCODING_UTF_8));
            post.setHeader(HTTP_HEADER_X_APPLICATION, appKey);
            post.setHeader(HTTP_HEADER_X_AUTHENTICATION, ssoToken);

            post.setEntity(new StringEntity(param, BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.ENCODING_UTF_8)));

            HttpClient httpClient = new DefaultHttpClient();

            HttpParams httpParams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, Integer.valueOf(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_TIMEOUT)));
            HttpConnectionParams.setSoTimeout(httpParams, Integer.valueOf(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_TIMEOUT)));

            resp = httpClient.execute(post, reqHandler);

        } catch (Exception e) {
            logger.error("Error occurred during HTTP request execution: error is " + e.getMessage(), e);
        }

        return resp;
    }

    public String sendPostRequestRescript(String param, String operation, String appKey, String ssoToken) {
        String apiNgURL = BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_BASE_URL) + 
        		BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_RESCRIPT_SUFFIX) + operation;
        return sendPostRequest(param, appKey, ssoToken, apiNgURL, new RescriptResponseHandler());
    }
}
