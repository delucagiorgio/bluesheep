package com.betfair.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import it.bluesheep.servicehandler.BlueSheepServiceHandlerManager;
import it.bluesheep.util.BlueSheepConstants;
 
 
public class HttpClientNonInteractiveLoginSSO {
 
    private static int port = 443; 
    private static Logger logger;
    
    public HttpClientNonInteractiveLoginSSO() {
    	logger = Logger.getLogger(HttpClientNonInteractiveLoginSSO.class);
    }
    
    public String login() throws Exception {
 
        HttpClient httpClient = new DefaultHttpClient();
        String responseString = null;
        String jsonSessionToken = null;
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            KeyManager[] keyManagers = getKeyManagers("pkcs12", new FileInputStream(new File(BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_CERTIFICATE_PATH))), BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_PASSWORD));
            ctx.init(keyManagers, null, new SecureRandom());
            SSLSocketFactory factory = new SSLSocketFactory(ctx, new StrictHostnameVerifier());
 
            ClientConnectionManager manager = httpClient.getConnectionManager();
            manager.getSchemeRegistry().register(new Scheme("https", port, factory));
            HttpPost httpPost = new HttpPost("https://identitysso.betfair.it/api/certlogin");
            
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_USERNAME)));
            nvps.add(new BasicNameValuePair("password", BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_PASSWORD)));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
 
            httpPost.setHeader("X-Application", BlueSheepServiceHandlerManager.getProperties().getProperty(BlueSheepConstants.BETFAIR_APPKEY));
            httpPost.setHeader("Accept","application/json");
            httpPost.setHeader("Connection", "keep-alive");
            
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
 
            if (entity != null) {
                responseString = EntityUtils.toString(entity);
            }
            
            jsonSessionToken = (new JSONObject(responseString)).getString(BlueSheepConstants.BETFAIR_SESSION_TOKEN_STRING);
        }catch(Exception e) {
        	logger.error(e.getMessage(), e);
        }finally {
            httpClient.getConnectionManager().shutdown();
        }
        
        return jsonSessionToken;
    }
 
 
 
    protected static KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());
        return kmf.getKeyManagers();
    }
}

