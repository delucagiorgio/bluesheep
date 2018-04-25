package com.betfair.api;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.betfair.util.RescriptResponseHandler;

import it.bluesheep.BlueSheepComparatoreMain;

public class HttpUtil {

    private static final String HTTP_HEADER_X_APPLICATION = "X-Application";
    private static final String HTTP_HEADER_X_AUTHENTICATION = "X-Authentication";
    private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HTTP_HEADER_ACCEPT = "Accept";
    private static final String HTTP_HEADER_ACCEPT_CHARSET = "Accept-Charset";

    public HttpUtil() {
        super();
    }

    private String sendPostRequest(String param, String appKey, String ssoToken, String URL, ResponseHandler<String> reqHandler) {
        HttpPost post = new HttpPost(URL);
        String resp = null;
        try {
            post.setHeader(HTTP_HEADER_CONTENT_TYPE, BlueSheepComparatoreMain.getProperties().getProperty("APPLICATION_JSON"));
            post.setHeader(HTTP_HEADER_ACCEPT, BlueSheepComparatoreMain.getProperties().getProperty("APPLICATION_JSON"));
            post.setHeader(HTTP_HEADER_ACCEPT_CHARSET, BlueSheepComparatoreMain.getProperties().getProperty("ENCODING_UTF8"));
            post.setHeader(HTTP_HEADER_X_APPLICATION, appKey);
            post.setHeader(HTTP_HEADER_X_AUTHENTICATION, ssoToken);

            post.setEntity(new StringEntity(param, BlueSheepComparatoreMain.getProperties().getProperty("ENCODING_UTF8")));

            HttpClient httpClient = new DefaultHttpClient();

            HttpParams httpParams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, Integer.valueOf(BlueSheepComparatoreMain.getProperties().getProperty("TIMEOUT")));
            HttpConnectionParams.setSoTimeout(httpParams, Integer.valueOf(BlueSheepComparatoreMain.getProperties().getProperty("TIMEOUT")));

            resp = httpClient.execute(post, reqHandler);

        } catch (UnsupportedEncodingException unsupportedEncodingEx) {
            unsupportedEncodingEx.printStackTrace();
        } catch (ClientProtocolException clientProtocolEx) {
            clientProtocolEx.printStackTrace();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }

        return resp;
    }

    public String sendPostRequestRescript(String param, String operation, String appKey, String ssoToken) {
        String apiNgURL = BlueSheepComparatoreMain.getProperties().getProperty("BETFAIR_URL") + BlueSheepComparatoreMain.getProperties().getProperty("RESCRIPT_SUFFIX") + operation;
        return sendPostRequest(param, appKey, ssoToken, apiNgURL, new RescriptResponseHandler());
    }
}
